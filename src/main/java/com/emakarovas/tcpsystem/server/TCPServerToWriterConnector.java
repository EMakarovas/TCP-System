package com.emakarovas.tcpsystem.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.emakarovas.tcpsystem.TCPConstants;
import com.emakarovas.tcpsystem.client.writer.TCPWriter;
import com.emakarovas.tcpsystem.client.writer.TCPWriterInputMessage;
import com.emakarovas.tcpsystem.config.ConfigProvider;
import com.emakarovas.tcpsystem.dao.SequenceDAO;
import com.emakarovas.tcpsystem.util.StringUtil;

/**
 * Defines a connection between a {@link TCPServer} and a {@link TCPWriter}.
 * @author Edgaras Makarovas
 *
 */
public class TCPServerToWriterConnector extends AbstractTCPServerToClientConnector {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(TCPServerToWriterConnector.class);
	
	protected final BlockingQueue<List<Integer>> sequenceQueue;
	protected final String writerGreetingAsString;
	protected final AtomicBoolean writerHasGreeted;
	protected final SequenceDAO sequenceDAO;

	protected TCPServerToWriterConnector(ConfigProvider configProvider, SequenceDAO sequenceDAO, BlockingQueue<List<Integer>> sequenceQueue) {
		super(configProvider, configProvider.getTcpPortWriter());
		this.writerGreetingAsString = new String(new byte[] { TCPConstants.WRITER_GREETING }, configProvider.getTcpEncoding());
		this.writerHasGreeted = new AtomicBoolean(false);
		this.sequenceDAO = sequenceDAO;
		this.sequenceQueue = sequenceQueue;
	}

	@Override
	protected void onTCPMessageReceived(String msg) {
		if(writerGreetingAsString==null || writerHasGreeted==null) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				LOGGER.warn("The thread was interrupted while waiting for {} to fully load.", 
						TCPServerToWriterConnector.class.getSimpleName());
			}
		}
		if(writerGreetingAsString.equals(msg)) {
			this.writerHasGreeted.set(true);
		} else {
			final List<Integer> sequence = StringUtil.convertCommaSeparatedStringToIntegerList(msg);
			sequenceQueue.add(sequence);
			sequenceDAO.save(sequence);
		}
	}
	
	/**
	 * Listens to the input provided by the user and sends it to the {@link TCPWriter}.
	 */
	protected void sendContinuousUserInputToWriter() {
		// TODO check that this connection is not listening to user input already.
		try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
			String buffer = null;
			while((buffer = br.readLine())!=null) {
				final TCPWriterInputMessage inputMsg;
				try {
					inputMsg = new TCPWriterInputMessage(buffer);
				} catch(IllegalArgumentException e) {
					final String errorMsg = String.format("Could not read the provided input message [%s].", buffer);
					LOGGER.warn(errorMsg);
					throw new IllegalStateException(errorMsg, e);
				}
				this.sendMessage(inputMsg.asTCPMessage());
			}
		} catch (IOException e) {
			LOGGER.warn("An exception was thrown while listening to user input!");
			sendContinuousUserInputToWriter();
		}
	}

	@Override
	public void onConnect() {
		/*
		 *  TODO this method and class should be reworked if KeepAlive's were implemented,
		 *  the writerHasGreeted flag should be set to false whenever the connection was lost.
		 */
		// wait while writer hasn't greeted
		while(!writerHasGreeted.get()) {}
		sendContinuousUserInputToWriter();
	}

}
