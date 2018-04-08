package com.emakarovas.tcpsystem.dao;

import java.util.List;

/**
 * Defines a DAO of sequences.
 * @author Edgaras Makarovas
 *
 */
public interface SequenceDAO {
	
	/**
	 * Returns all the stored sequences in the form of {@link List Lists} of Integers.
	 * @return
	 */
	List<List<Integer>> findAll();
	
	/**
	 * Saves the provided <i>sequence</i>.
	 * @param sequence
	 */
	void save(List<Integer> sequence);

}
