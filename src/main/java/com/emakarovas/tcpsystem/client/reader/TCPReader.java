package com.emakarovas.tcpsystem.client.reader;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.emakarovas.tcpsystem.TCPConstants;
import com.emakarovas.tcpsystem.client.AbstractTCPClient;
import com.emakarovas.tcpsystem.config.ConfigProvider;
import com.emakarovas.tcpsystem.config.PropertiesConfigProvider;

/**
 * Defines the TCP reader client.
 * @author Edgaras Makarovas
 *
 */
public class TCPReader extends AbstractTCPClient {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(TCPReader.class);
	
	private static final int SEQUENCE_CAPACITY = 1000;
	
	protected final Map<UUID, ReaderSequence> sequenceMap;
	protected final Queue<UUID> completeSequenceUUIDQueue;
	protected final Set<UUID> inFlightSequenceUUIDSet;
	protected final Queue<UUID> newSequenceUUIDQueue;
	
	protected TCPReader(ConfigProvider configProvider) {
		super(configProvider, configProvider.getTcpPortReader());
		this.sequenceMap = new ConcurrentHashMap<UUID, ReaderSequence>();
		this.completeSequenceUUIDQueue =  new LinkedBlockingQueue<UUID>();
		this.inFlightSequenceUUIDSet = Collections.synchronizedSet(new HashSet<UUID>());
		this.newSequenceUUIDQueue = new LinkedBlockingQueue<UUID>();
		for(int i=0; i<SEQUENCE_CAPACITY; i++) {
			newSequenceUUIDQueue.add(UUID.randomUUID());
		}
	}
	
	@Override
	public void connect() {
		super.connect();
		new Thread(() -> {
			while(true) {
				requestNextSequence();
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					LOGGER.error("The next sequence requesting thread has been interrupted!");
				}
			}
		}).start();
	}
	
	/**
	 * Requests the next sequence.
	 */
	protected synchronized void requestNextSequence() {
		if(completeSequenceUUIDQueue.size()==SEQUENCE_CAPACITY) {
			final UUID oldestCompleteSequence = completeSequenceUUIDQueue.poll();
			sequenceMap.remove(oldestCompleteSequence);
		}
		if(inFlightSequenceUUIDSet.size()!=SEQUENCE_CAPACITY) {
			final UUID uuid = newSequenceUUIDQueue.poll();
			inFlightSequenceUUIDSet.add(uuid);
			sequenceMap.put(uuid, new ConcurrentReaderSequence(uuid));
			newSequenceUUIDQueue.add(UUID.randomUUID());
			this.sendMessage(new TCPReaderMessage(uuid, TCPConstants.SEQUENCE_REQUEST_VALUE).asTCPMessage());
		}
	}
	
	/**
	 * Requests a new sequence for the provided {@link UUID} from the server.
	 * @param uuid
	 */
	protected void requestSequenceForUUID(UUID uuid) {
		final TCPReaderMessage msg = new TCPReaderMessage(uuid, TCPConstants.SEQUENCE_REQUEST_VALUE);
		this.sendMessage(msg.asTCPMessage());
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
		final int value = readerMessage.value.get();
		final ReaderSequence sequence = sequenceMap.get(uuid);
		if(value==TCPConstants.SEQUENCE_END_VALUE) {
			sequence.setAsComplete();
			inFlightSequenceUUIDSet.remove(uuid);
			completeSequenceUUIDQueue.add(uuid);
			this.sendMessage(uuid.toString());
		} else {
			final Optional<Integer> lastValue = sequence.getLastValue();
			if(lastValue.isPresent() && lastValue.get()!=value-1) {
				final String errorMsg = String.format("Incorrect sequence value returned by server for UUID %s. "
						+ "Previous sequence value: [%s], new provided value: [%s].", uuid, lastValue.get(), value);
				LOGGER.warn(errorMsg);
			} else {
				sequence.addToSequence(value);
				this.sendMessage(uuid.toString());
			}
		}
	}
	
	public static void main(String[] args) {
		new TCPReader(PropertiesConfigProvider.getInstance()).connect();
	}

}
