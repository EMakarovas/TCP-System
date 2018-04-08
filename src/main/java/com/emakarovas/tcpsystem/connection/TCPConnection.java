package com.emakarovas.tcpsystem.connection;

/**
 * Defines a TCP connection used by the components of the TCP system.
 * @author Edgaras Makarovas
 *
 */
public interface TCPConnection {
	
	/**
	 * Sends the provided <i>content</i> through the connection.
	 * @param content
	 * @throws TCPConnectionException If there is a problem with the connection.
	 */
	void send(String content) throws TCPConnectionException;
	
	/**
	 * Returns true if the connection is alive; false otherwise.
	 * @return
	 */
	boolean isConnectionAlive();
	
	/**
	 * Registers the provided {@link InputListener} to listen to this {@link TCPConnection TCPConnection's} input.
	 * @param inputListener
	 */
	void registerInputListener(InputListener inputListener);
	
	/**
	 * Defines a listener of the input of a {@link TCPConnection}.
	 * @author Edgaras Makarovas
	 *
	 */
	public static interface InputListener {
		
		/**
		 * Is executed whenever the {@link TCPConnection} holding this listener receives new input.
		 * @param content
		 */
		void onInputReceived(String content);
		
	}

}
