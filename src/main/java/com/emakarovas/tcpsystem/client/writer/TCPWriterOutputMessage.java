package com.emakarovas.tcpsystem.client.writer;

import java.util.ArrayList;
import java.util.List;

import com.emakarovas.tcpsystem.client.TCPClientMessage;

/**
 * A subtype of {@link TCPClientMessage} for {@link TCPWriter} output messages.
 * @author Edgaras Makarovas
 *
 */
public class TCPWriterOutputMessage implements TCPClientMessage {

	public final List<Integer> values;

	public TCPWriterOutputMessage(List<Integer> values) {
		this.values = values;
	}
	
	public TCPWriterOutputMessage(TCPWriterInputMessage inputMsg) {
		this.values = new ArrayList<Integer>();
		final int numberOfValuesRequested = inputMsg.numberOfRequestedValues;
		for(int i=0; i<numberOfValuesRequested; i++) {
			values.add(inputMsg.offset + i);
		}
	}

	@Override
	public String asTCPMessage() {
		final StringBuffer sb = new StringBuffer();
		for(Integer value : values) {
			sb.append(value + ",");
		}
		// delete the last comma if it exists
		if(sb.length()>0) {
			sb.deleteCharAt(sb.length()-1);
		}
		return sb.toString();
	}

	@Override
	public String toString() {
		return "TCPWriterOutputMessage [values=" + values + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((values == null) ? 0 : values.hashCode());
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
		TCPWriterOutputMessage other = (TCPWriterOutputMessage) obj;
		if (values == null) {
			if (other.values != null)
				return false;
		} else if (!values.equals(other.values))
			return false;
		return true;
	}
	
}
