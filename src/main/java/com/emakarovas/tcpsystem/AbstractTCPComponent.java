package com.emakarovas.tcpsystem;


import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.emakarovas.tcpsystem.config.ConfigProvider;
import com.emakarovas.tcpsystem.connection.TCPConnection;
import com.emakarovas.tcpsystem.connection.TCPConnection.InputListener;
import com.emakarovas.tcpsystem.connection.TCPConnectionException;

/**
 * An abstract implementation of a TCP component providing common behavior for concrete types.
 * @author Edgaras Makarovas
 *
 */
public abstract class AbstractTCPComponent implements TCPComponent {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractTCPComponent.class);
	
	protected final ConfigProvider configProvider;
	protected final int tcpPort;
	
	protected volatile TCPConnection connection;
	
	protected AbstractTCPComponent(ConfigProvider configProvider, int port) {
		this.configProvider = configProvider;
		this.tcpPort = port;
	}
	
	@Override
	public void connect() {
		connectIfNotConnected();
	}
	
	/**
	 * Establishes a {@link #connection} if currently not connected.
	 */
	protected void connectIfNotConnected() {
		if(connection==null || !connection.isConnectionAlive()) {
			this.connection = getNewTCPConnection(configProvider, tcpPort);
			connection.registerInputListener(new InputListener() {
				@Override public void onInputReceived(String content) {
					onTCPMessageReceived(content);
				}
			});
		}
	}
	
	/**
	 * Provides a common gateway to send messages through the underlying {@link #connection}. 
	 * This method handles the relevant exceptions and tries to reconnect if necessary.
	 * @param msg
	 */
	protected void sendMessage(String msg) {
		try {
			LOGGER.info("Sending message [{}].", msg);
			connection.send(msg);
		} catch (TCPConnectionException e) {
			final String errorMsg = String.format("An error occurred while sending the message [%s].", msg);
			LOGGER.warn(errorMsg, e);
			connectIfNotConnected();
			sendMessage(msg);
		}
	}
		
	/**
	 * Builds a {@link TCPConnection}, logging any errors and trying again until one is created.
	 * @param configProvider
	 * @param port
	 * @return
	 */
	protected TCPConnection getNewTCPConnection(ConfigProvider configProvider, int port) {
		try {
			return buildNewTCPConnection(configProvider, port);
		} catch (TCPConnectionException e) {
			final int retryInSeconds = configProvider.getRetryInSecondsOnDisconnect();
			final String msg = String.format("An error occurred while creating a new TCP connection. Retrying in %s seconds...", 
					retryInSeconds);
			LOGGER.error(msg, e);
			try {
				TimeUnit.SECONDS.sleep(retryInSeconds);
			} catch (InterruptedException e1) {
				LOGGER.warn("The thread was interrupted while waiting to establish a new TCP connection.");
			}
			return getNewTCPConnection(configProvider, port);
		}
	}
	
	/**
	 * Builds a {@link TCPConnection} by using the provided args, throwing a {@link TCPConnectionException}
	 * if any occur during the building process.
	 * @param configProvider
	 * @param port
	 * @return
	 * @throws TCPConnectionException
	 */
	protected abstract TCPConnection buildNewTCPConnection(ConfigProvider configProvider, int port) throws TCPConnectionException;

	/**
	 * Called whenever a new TCP message is received.
	 * @param msg
	 */
	protected abstract void onTCPMessageReceived(String msg);
	
}
