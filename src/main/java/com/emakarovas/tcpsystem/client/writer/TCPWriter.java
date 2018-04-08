package com.emakarovas.tcpsystem.client.writer;

import java.nio.charset.Charset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.emakarovas.tcpsystem.TCPConstants;
import com.emakarovas.tcpsystem.client.AbstractTCPClient;
import com.emakarovas.tcpsystem.config.ConfigProvider;
import com.emakarovas.tcpsystem.config.PropertiesConfigProvider;

/**
 * Defines the TCP writer component.
 * @author Edgaras Makarovas
 *
 */
public class TCPWriter extends AbstractTCPClient {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(TCPWriter.class);
	
	protected final Charset encoding;

	protected TCPWriter(ConfigProvider configProvider) {
		super(configProvider, configProvider.getTcpPortWriter());
		this.encoding = configProvider.getTcpEncoding();
	}
	
	@Override
	public void connect() {
		super.connect();
		greetServer();
	}

	@Override
	protected void onTCPMessageReceived(String msg) {
		final TCPWriterInputMessage inputMsg;
		try {
			inputMsg = new TCPWriterInputMessage(msg);
		} catch(IllegalArgumentException e) {
			final String errorMsg = String.format("Could not read the provided input message [%s].", msg);
			LOGGER.warn(errorMsg);
			throw new IllegalStateException(errorMsg, e);
		}
		final TCPWriterOutputMessage outputMsg = new TCPWriterOutputMessage(inputMsg);
		this.sendMessage(outputMsg.asTCPMessage());
	}
	
	/**
	 * Sends the greeting message to the server.
	 */
	protected void greetServer() {
		final String content = new String(new byte[] { TCPConstants.WRITER_GREETING }, encoding);
		this.sendMessage(content);
	}
	
	public static void main(String[] args) {
		new TCPWriter(PropertiesConfigProvider.getInstance()).connect();
	}

}
