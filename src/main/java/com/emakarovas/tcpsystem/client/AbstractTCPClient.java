package com.emakarovas.tcpsystem.client;

import com.emakarovas.tcpsystem.AbstractTCPComponent;
import com.emakarovas.tcpsystem.config.ConfigProvider;
import com.emakarovas.tcpsystem.connection.TCPConnection;
import com.emakarovas.tcpsystem.connection.TCPConnectionException;

/**
 * An abstract implementation of a TCP client providing common behavior for concrete types.
 * @author Edgaras Makarovas
 *
 */
public abstract class AbstractTCPClient extends AbstractTCPComponent {

	protected AbstractTCPClient(ConfigProvider configProvider, int port) {
		super(configProvider, port);
	}
	
	@Override
	protected TCPConnection buildNewTCPConnection(ConfigProvider configProvider, int port) throws TCPConnectionException {
		return new TCPClientConnection(configProvider, port);
	}

}
