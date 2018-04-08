package com.emakarovas.tcpsystem.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.emakarovas.tcpsystem.AbstractTCPComponent;
import com.emakarovas.tcpsystem.config.ConfigProvider;
import com.emakarovas.tcpsystem.connection.DefaultTCPConnection;
import com.emakarovas.tcpsystem.connection.TCPConnection;
import com.emakarovas.tcpsystem.connection.TCPConnectionException;

/**
 * An abstract class providing common behavior for connections between the {@link TCPServer} and a TCP client.
 * @author Edgaras Makarovas
 *
 */
public abstract class AbstractTCPServerToClientConnector extends AbstractTCPComponent implements Runnable {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractTCPServerToClientConnector.class);
	
	protected final AtomicBoolean connectRequested;

	protected AbstractTCPServerToClientConnector(ConfigProvider configProvider, int port) {
		super(configProvider, port);
		this.connectRequested = new AtomicBoolean(false);
	}
	
	@Override
	public void connect() {
		connectRequested.set(true);
	}

	@Override
	protected TCPConnection buildNewTCPConnection(ConfigProvider configProvider, int port) throws TCPConnectionException {
		try (final ServerSocket serverSocket = new ServerSocket(port)) {
			final Socket socket = serverSocket.accept();
			return new DefaultTCPConnection(configProvider, socket);
		} catch(IOException e) {
			final String msg = String.format("An exception was thrown when creating a new TCP connection to port %s.", port);
			LOGGER.warn(msg);
			throw new TCPConnectionException(msg, e);
		}
	}
	
	@Override
	public void run() {
		// wait until connection is requested
		while(!connectRequested.get()) {}
		super.connect();
		onConnect();
	}
	
	/**
	 * Called when the connection has been established.
	 */
	protected abstract void onConnect();

}
