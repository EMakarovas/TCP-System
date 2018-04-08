package com.emakarovas.tcpsystem.client.reader;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A thread-safe implementation of {@link ReaderSequence}.
 * @author Edgaras Makarovas
 *
 */
public class ConcurrentReaderSequence implements ReaderSequence {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ConcurrentReaderSequence.class);
	
	protected final UUID uuid;
	protected final List<Integer> sequence;
	protected final AtomicBoolean complete;

	public ConcurrentReaderSequence(UUID uuid) {
		this.uuid = uuid;
		this.sequence = new ArrayList<Integer>();
		this.complete = new AtomicBoolean(false);
	}

	@Override
	public UUID getUUID() {
		return uuid;
	}

	@Override
	public void addToSequence(int value) {
		if(!complete.get()) {
			sequence.add(value);
		} else {
			final String msg = String.format("Could not add a new value to the sequence [%s] due to it being complete!", uuid);
			LOGGER.error(msg);
			throw new IllegalStateException(msg);
		}
	}

	@Override
	public boolean isComplete() {
		return complete.get();
	}

	@Override
	public void setAsComplete() {
		complete.set(true);
	}

	@Override
	public List<Integer> getSequence() {
		return new ArrayList<Integer>(sequence);
	}

	@Override
	public Optional<Integer> getLastValue() {
		if(sequence.isEmpty()) {
			return Optional.empty();
		}
		return Optional.of(sequence.get(sequence.size()-1));
	}

}
