package com.emakarovas.tcpsystem.client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.emakarovas.tcpsystem.config.ConfigProvider;
import com.emakarovas.tcpsystem.connection.DefaultTCPConnection;
import com.emakarovas.tcpsystem.connection.TCPConnection;
import com.emakarovas.tcpsystem.connection.TCPConnectionException;

/**
 * An extension of {@link DefaultTCPConnection} used by {@link AbstractTCPClient}.
 * @author Edgaras Makarovas
 *
 */
public class TCPClientConnection extends DefaultTCPConnection implements TCPConnection {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(TCPClientConnection.class);
	
	private static final String SERVER_IP = "127.0.0.1";

	public TCPClientConnection(ConfigProvider configProvider, int port) throws TCPConnectionException {
		super(configProvider, buildConnectionSocket(port));
	}

	/**
	 * Builds a connection {@link Socket} with the given <i>tcpPort</i> and returns it.
	 * @param tcpPort
	 * @return
	 * @throws TCPConnectionException If an error occurs while creating the {@link Socket}.
	 */
	private static final Socket buildConnectionSocket(int tcpPort) throws TCPConnectionException {
		try {
			return new Socket(SERVER_IP, tcpPort);
		} catch (IOException e) {
			final String msg = String.format("Could not connect to the provided server IP %s!", SERVER_IP);
			LOGGER.error(msg, e);
			throw new TCPConnectionException(msg, e);
		}
	}

}
