package com.emakarovas.tcpsystem.server;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.emakarovas.tcpsystem.TCPConstants;
import com.emakarovas.tcpsystem.client.reader.TCPReader;
import com.emakarovas.tcpsystem.client.reader.TCPReaderMessage;
import com.emakarovas.tcpsystem.config.ConfigProvider;

/**
 * Defines a connection between a {@link TCPServer} and a {@link TCPReader}.
 * @author Edgaras Makarovas
 *
 */
public class TCPServerToReaderConnector extends AbstractTCPServerToClientConnector {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(TCPServerToReaderConnector.class);
	
	/**
	 * The maximum number of values that can be sent from a sequence.
	 */
	private static final int MAX_ITEMS_FROM_SEQUENCE_TO_SEND = 10;

	protected final BlockingQueue<List<Integer>> sequenceQueue;
	protected final Map<UUID, Sequence> sequenceByUuidMap;
	protected final BlockingQueue<UUID> pendingUuidQueue;
	
	protected TCPServerToReaderConnector(ConfigProvider configProvider, BlockingQueue<List<Integer>> sequenceQueue) {
		super(configProvider, configProvider.getTcpPortReader());
		this.sequenceQueue = sequenceQueue;
		this.sequenceByUuidMap = new ConcurrentHashMap<UUID, Sequence>();
		this.pendingUuidQueue = new LinkedBlockingQueue<UUID>();
	}

	@Override
	protected void onTCPMessageReceived(String msg) {
		
		final TCPReaderMessage readerMessage;
		try {
			readerMessage = new TCPReaderMessage(msg);
		} catch(IllegalArgumentException e) {
			final String errorMsg = String.format("Could not parse the provided message [%s].", msg);
			LOGGER.error(errorMsg);
			throw new IllegalStateException(errorMsg, e);
		}
		final UUID uuid = readerMessage.uuid;
		final boolean isUuidOnly = !msg.contains(",");
		if(isUuidOnly) {
			if (sequenceByUuidMap.containsKey(uuid)) {
				sendNextSequenceValue(uuid);
			} else {
				LOGGER.info("Sequence {} has been finished!", msg);
			}
		} else {
			pendingUuidQueue.add(uuid);
		}
	}
	
	/**
	 * Sends the next value of the sequence inside {@Link #sequenceByUuidMap} with the provided key.
	 * @param sequenceUuid
	 */
	protected void sendNextSequenceValue(UUID sequenceUuid) {
		final Sequence storedSequence = sequenceByUuidMap.get(sequenceUuid);
		final int responseValue;
		if(!storedSequence.canSendNext()) {
			sequenceByUuidMap.remove(sequenceUuid);
			responseValue = TCPConstants.SEQUENCE_END_VALUE;
		} else {
			responseValue = storedSequence.getNextValue();
		}
		final TCPReaderMessage responseMsg = new TCPReaderMessage(sequenceUuid, responseValue);
		this.sendMessage(responseMsg.asTCPMessage());
	}

	@Override
	public void onConnect() {
		while(true) {
			try {
				final UUID nextUuid = pendingUuidQueue.take();
				final List<Integer> nextSequence = sequenceQueue.take();
				sequenceByUuidMap.put(nextUuid, new Sequence(nextSequence));
				sendNextSequenceValue(nextUuid);
			} catch (InterruptedException e) {
				final String errorMsg = "The thread was interrupted while waiting for pending UUIDs.";
				LOGGER.warn(errorMsg, e);
				// try again
				run();
			}
		}
	}
	
	/**
	 * Defines a thread-safe sequence of integers.
	 * @author Edgaras Makarovas
	 *
	 */
	protected final class Sequence {
		
		final List<Integer> sequence;
		final AtomicInteger currentIndex;
		
		public Sequence(List<Integer> sequence) {
			this.sequence = sequence;
			this.currentIndex = new AtomicInteger(0);
		}
		
		boolean canSendNext() {
			return currentIndex.get()!=MAX_ITEMS_FROM_SEQUENCE_TO_SEND && currentIndex.get()!=sequence.size();
		}
		
		int getNextValue() {
			if(!canSendNext()) {
				throw new IllegalStateException("This sequence cannot provide any more values!");
			}
			return sequence.get(currentIndex.getAndIncrement());
		}

	}

}
