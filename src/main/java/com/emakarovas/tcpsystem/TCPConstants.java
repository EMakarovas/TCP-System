package com.emakarovas.tcpsystem;

/**
 * Contains all the constants used in the TCP system.
 * @author Edgaras Makarovas
 *
 */
public final class TCPConstants {
	
	private TCPConstants() {}
	
	public static final String SERVER_IP = "127.0.0.1";
	public static final byte WRITER_GREETING = (byte) 0;
	public static final int SEQUENCE_END_VALUE = -1;
	public static final int SEQUENCE_REQUEST_VALUE = 0;
	/** A prefix added to TCP messages which indicate the KeepAlive period in seconds. */
	public static final String KEEP_ALIVE_MESSAGE_PREFIX = "KeepAlive:";

}
