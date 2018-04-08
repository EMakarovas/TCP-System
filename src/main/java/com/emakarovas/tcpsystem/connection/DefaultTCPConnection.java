package com.emakarovas.tcpsystem.connection;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.emakarovas.tcpsystem.config.ConfigProvider;

/**
 * An abstract implementation of {@link TCPConnection} providing common behavior.
 * @author Edgaras Makarovas
 *
 */
public class DefaultTCPConnection implements TCPConnection {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultTCPConnection.class);
		
	protected final Charset encoding;
	protected final Set<InputListener> inputListeners;
	protected final Socket socket;
	protected final AtomicBoolean connectionAlive;
	
	public DefaultTCPConnection(ConfigProvider configProvider, Socket socket) throws TCPConnectionException {
		this.encoding = configProvider.getTcpEncoding();
		this.inputListeners = new HashSet<InputListener>();
		this.socket = socket;
		this.connectionAlive = new AtomicBoolean(true);
		new Thread(new InputReader()).start();
	}

	@Override
	public synchronized void send(String content) throws TCPConnectionException {
		try {
			if(!connectionAlive.get()) {
				final String msg = String.format("The message [%s] could not be sent due to the connection being dead!", content);
				throw new TCPConnectionException(msg);
			}
			getSocketOutputStream().write(content.getBytes(encoding));
		} catch (IOException e) {
			final String msg = String.format("An error occurred while trying to send the content [%s].", content);
			LOGGER.error(msg, e);
			connectionAlive.set(false);
			throw new TCPConnectionException(msg, e);
		}
	}
	
	@Override
	public synchronized void registerInputListener(InputListener inputListener) {
		inputListeners.add(inputListener);
	}
	
	@Override
	public synchronized boolean isConnectionAlive() {
		return connectionAlive.get();
	}
	
	/**
	 * Returns the {@link InputStream} of the underlying {@link #socket} in a thread-safe manner.
	 * @return
	 * @throws IOException
	 * @throws TCPConnectionException 
	 */
	protected synchronized InputStream getSocketInputStream() throws IOException, TCPConnectionException {
		if(!connectionAlive.get()) {
			throw new TCPConnectionException("The InputStream could not be provided due to the connection being dead!");
		}
		return socket.getInputStream();
	}
	
	/**
	 * Returns the {@link OutputStream} of the underlying {@link #socket} in a thread-safe manner.
	 * @return
	 * @throws IOException
	 * @throws TCPConnectionException 
	 */
	protected synchronized OutputStream getSocketOutputStream() throws IOException, TCPConnectionException {
		if(!connectionAlive.get()) {
			throw new TCPConnectionException("The InputStream could not be provided due to the connection being dead!");
		}
		return socket.getOutputStream();
	}
	
	protected class InputReader implements Runnable {

		@Override
		public void run() {
			char[] buffer = new char[1024];
			int bytesRead = -1;
			try (final Reader r = new InputStreamReader(getSocketInputStream(), encoding)){
				while((bytesRead = r.read(buffer, 0, buffer.length)) >= 0) {
					final String content = new StringBuilder().append(buffer, 0, bytesRead).toString();
					LOGGER.info("Received message [{}].", content);
					for(InputListener inputListener : inputListeners) {
						inputListener.onInputReceived(content);
					}
				}
				connectionAlive.set(false);
			} catch (IOException | TCPConnectionException e) {
				LOGGER.error("An error occurred while reading the input!", e);
				connectionAlive.set(false);
			}
		}
		
	}

}
