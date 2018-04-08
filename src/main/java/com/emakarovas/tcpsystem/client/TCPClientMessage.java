package com.emakarovas.tcpsystem.client;

/**
 * Defines a message sent/received by the components of the TCP system.
 * @author Edgaras Makarovas
 *
 */
public interface TCPClientMessage {

	/**
	 * Returns this message formatted as a TCP system message.
	 * @return
	 */
	String asTCPMessage();
	
}
