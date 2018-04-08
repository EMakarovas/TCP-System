package com.emakarovas.tcpsystem.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Optional;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An implementation of {@link ConfigProvider} which reads a .properties file to provide the config values.
 * @author Edgaras Makarovas
 *
 */
public class PropertiesConfigProvider implements ConfigProvider {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PropertiesConfigProvider.class);
	
	private static final String PROPERTIES_PATH = "/config.properties";
	
	private static final String TCP_PORT_READER_KEY = "tcp.port.reader";
	private static final String TCP_PORT_WRITER_KEY = "tcp.port.writer";
	private static final String TCP_ENCODING_KEY = "tcp.encoding";
	private static final String TCP_RETRY_ON_DISCONNECT_SECONDS_KEY = "tcp.retry_on_disconnect_seconds";
	
	private static volatile Optional<PropertiesConfigProvider> instanceOpt = Optional.empty();
	
	protected final int tcpPortReader;
	protected final int tcpPortWriter;
	protected final Charset tcpEncoding;
	protected final int retryOnDisconnectSeconds;
	
	protected PropertiesConfigProvider() throws IOException {
		try (final InputStream is = this.getClass().getResourceAsStream(PROPERTIES_PATH)) {
			final Properties props = new Properties();
			props.load(is);
			this.tcpPortReader = Integer.parseInt(props.getProperty(TCP_PORT_READER_KEY));
			this.tcpPortWriter = Integer.parseInt(props.getProperty(TCP_PORT_WRITER_KEY));
			this.tcpEncoding = Charset.forName(props.getProperty(TCP_ENCODING_KEY));
			this.retryOnDisconnectSeconds = Integer.parseInt(props.getProperty(TCP_RETRY_ON_DISCONNECT_SECONDS_KEY));
		}
	}
	
	/**
	 * Returns the singleton instance of {@link PropertiesConfigProvider}.
	 * @return
	 */
	public static final synchronized PropertiesConfigProvider getInstance() {
		if(!instanceOpt.isPresent()) {
			try {
				instanceOpt = Optional.of(new PropertiesConfigProvider());
			} catch (IOException e) {
				final String msg = String.format("An error occurred while creating the singleton instance of %s.", 
						PropertiesConfigProvider.class.getSimpleName());
				LOGGER.error(msg, e);
				throw new IllegalStateException(msg, e);
			}
		}
		return instanceOpt.get();
	}

	@Override
	public int getTcpPortReader() {
		return tcpPortReader;
	}

	@Override
	public int getTcpPortWriter() {
		return tcpPortWriter;
	}

	@Override
	public Charset getTcpEncoding() {
		return tcpEncoding;
	}

	@Override
	public int getRetryInSecondsOnDisconnect() {
		return retryOnDisconnectSeconds;
	}
	
}
