package com.emakarovas.tcpsystem.client.reader;

import java.util.Optional;
import java.util.UUID;

import com.emakarovas.tcpsystem.client.TCPClientMessage;

/**
 * Defines a message sent to/expected by the {@link TCPReader}.
 * @author Edgaras Makarovas
 *
 */
public class TCPReaderMessage implements TCPClientMessage {

	public final UUID uuid;
	public final Optional<Integer> value;
	
	public TCPReaderMessage(UUID uuid, int value) {
		this.uuid = uuid;
		this.value = Optional.of(value);
	}
	
	/**
	 * Creates a {@link TCPReaderMessage} by parsing the provided message.
	 * @param serverStringMsg
	 * @throws IllegalArgumentException If the message does not follow the expected format "UUID,value".
	 */
	public TCPReaderMessage(String serverStringMsg) throws IllegalArgumentException {
		String[] separated = serverStringMsg.split(",");
		this.uuid = UUID.fromString(separated[0]);
		this.value = separated.length>1 ? Optional.of(Integer.parseInt(separated[1])) : Optional.empty();
	}

	@Override
	public String toString() {
		return "ReaderMessage [uuid=" + uuid + ", value=" + value + "]";
	}

	@Override
	public String asTCPMessage() {
		final String valueAddition = value.isPresent() ? "," + value.get() : "";
		return uuid.toString() + valueAddition;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TCPReaderMessage other = (TCPReaderMessage) obj;
		if (uuid == null) {
			if (other.uuid != null)
				return false;
		} else if (!uuid.equals(other.uuid))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}
		
}
