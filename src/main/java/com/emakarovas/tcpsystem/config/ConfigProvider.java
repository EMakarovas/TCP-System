package com.emakarovas.tcpsystem.config;

import java.nio.charset.Charset;

import com.emakarovas.tcpsystem.client.reader.TCPReader;
import com.emakarovas.tcpsystem.client.writer.TCPWriter;

/**
 * Provides configuration values for the TCP system.
 * @author Edgaras Makarovas
 *
 */
public interface ConfigProvider {

	/**
	 * Returns the TCP port to be used by the {@link TCPReader} of the TCP system.
	 * @return
	 */
	int getTcpPortReader();
	
	/**
	 * Returns the TCP port to be used by the {@link TCPWriter} of the TCP system.
	 * @return
	 */
	int getTcpPortWriter();
	
	/**
	 * Returns the encoding used by the TCP connections.
	 * @return
	 */
	Charset getTcpEncoding();
	
	/**
	 * Returns the number of seconds to wait before retrying to connect to the server.
	 * @return
	 */
	int getRetryInSecondsOnDisconnect();
	
}
