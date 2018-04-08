package com.emakarovas.tcpsystem.client.writer;

import java.util.List;

import com.emakarovas.tcpsystem.client.TCPClientMessage;
import com.emakarovas.tcpsystem.util.StringUtil;

/**
 * A subtype of {@link TCPClientMessage} for {@link TCPWriter} input messages.
 * @author Edgaras Makarovas
 *
 */
public class TCPWriterInputMessage implements TCPClientMessage {

	public final int offset;
	public final int numberOfRequestedValues;
	
	public TCPWriterInputMessage(int offset, int numberOfRequestedValues) {
		this.offset = offset;
		this.numberOfRequestedValues = numberOfRequestedValues;
	}
	
	/**
	 * Creates a {@link TCPWriterInputMessage} by parsing the provided message in the format of "i,i", where i is an integer.
	 * @param msg
	 * @throws IllegalArgumentException If the message format is incorrect.
	 */
	public TCPWriterInputMessage(String msg) throws IllegalArgumentException {
		final List<Integer> values = StringUtil.convertCommaSeparatedStringToIntegerList(msg);
		this.offset = values.get(0);
		this.numberOfRequestedValues = values.get(1);
	}
	
	@Override
	public String asTCPMessage() {
		return offset + "," + numberOfRequestedValues;
	}

	@Override
	public String toString() {
		return "TCPWriterMessage [offset=" + offset + ", numberOfRequestedValues=" + numberOfRequestedValues + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + numberOfRequestedValues;
		result = prime * result + offset;
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
		TCPWriterInputMessage other = (TCPWriterInputMessage) obj;
		if (numberOfRequestedValues != other.numberOfRequestedValues)
			return false;
		if (offset != other.offset)
			return false;
		return true;
	}
	
}
