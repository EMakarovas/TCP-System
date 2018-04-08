package com.emakarovas.tcpsystem.server;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.emakarovas.tcpsystem.config.ConfigProvider;
import com.emakarovas.tcpsystem.config.PropertiesConfigProvider;
import com.emakarovas.tcpsystem.dao.DummyLoggingSequenceDAO;
import com.emakarovas.tcpsystem.dao.SequenceDAO;

/**
 * Defines the server of the TCP system.
 * @author Edgaras Makarovas
 *
 */
public class TCPServer {
	
	protected volatile TCPServerToReaderConnector readerConnector;
	protected volatile TCPServerToWriterConnector writerConnector;
	
	public TCPServer(ConfigProvider configProvider, SequenceDAO sequenceDAO) {
		final BlockingQueue<List<Integer>> sequenceQueue = new LinkedBlockingQueue<List<Integer>>();
		this.writerConnector = new TCPServerToWriterConnector(configProvider, sequenceDAO, sequenceQueue);
		startServerToComponentConnector(writerConnector);
		this.readerConnector = new TCPServerToReaderConnector(configProvider, sequenceQueue);
		startServerToComponentConnector(readerConnector);
	}
	
	/**
	 * Starts the provided {@link AbstractTCPServerToClientConnector}.
	 * @param connector
	 */
	private static final void startServerToComponentConnector(AbstractTCPServerToClientConnector connector) {
		final Thread connectorThread = new Thread(connector);
		connectorThread.start();
		connector.connect();
	}
	
	public static void main(String[] args) {
		new TCPServer(PropertiesConfigProvider.getInstance(), new DummyLoggingSequenceDAO());
	}
	
}
