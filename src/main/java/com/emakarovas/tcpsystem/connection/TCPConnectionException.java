package com.emakarovas.tcpsystem.connection;

/**
 * An {@link Exception} indicating that a problem occurred with a {@link TCPConnection}.
 * @author Edgaras Makarovas
 *
 */
public class TCPConnectionException extends Exception {

	private static final long serialVersionUID = -2281685242577666268L;
	
	public TCPConnectionException(String msg) {
		super(msg);
	}
	
	public TCPConnectionException(String msg, Throwable t) {
		super(msg, t);
	}

}
