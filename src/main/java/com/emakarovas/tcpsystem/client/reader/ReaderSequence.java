package com.emakarovas.tcpsystem.client.reader;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Defines a sequence held by {@link TCPReader}.
 * @author Edgaras Makarovas
 *
 */
public interface ReaderSequence {

	/**
	 * Returns the corresponding {@link UUID}.
	 * @return
	 */
	UUID getUUID();
	
	/**
	 * Adds the <i>value</i> to this sequence.
	 * @param value
	 */
	void addToSequence(int value);
	
	/**
	 * Returns true if this sequence is complete; false otherwise.
	 * @return
	 */
	boolean isComplete();
	
	/**
	 * Sets this sequence's state to complete.
	 */
	void setAsComplete();
	
	/**
	 * Returns the underlying sequence.
	 * @return
	 */
	List<Integer> getSequence();
	
	/**
	 * Returns an {@link Optional} with the last value of this sequence. Returns an empty {@link Optional}
	 * if the sequence is empty.
	 * @return
	 */
	Optional<Integer> getLastValue();
	
}
