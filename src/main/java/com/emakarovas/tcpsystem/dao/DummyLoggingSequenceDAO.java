package com.emakarovas.tcpsystem.dao;

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A dummy implementation of {@link SequenceDAO} which simply logs the actions without actually doing anything.
 * @author Edgaras Makarovas
 *
 */
public class DummyLoggingSequenceDAO implements SequenceDAO {

	private static final Logger LOGGER = LoggerFactory.getLogger(DummyLoggingSequenceDAO.class);
	
	@Override
	public List<List<Integer>> findAll() {
		LOGGER.info("Retrieving all sequences from {}.", DummyLoggingSequenceDAO.class.getSimpleName());
		return Collections.emptyList();
	}

	@Override
	public void save(List<Integer> sequence) {
		LOGGER.info("Saving the sequence {} in {}.", sequence, DummyLoggingSequenceDAO.class.getSimpleName());
	}

}
