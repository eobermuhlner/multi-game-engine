package ch.obermuhlner.game.impl;

import java.util.function.Supplier;

import ch.obermuhlner.game.StoppableCalculation;

public class TrivialCalculation<T> implements StoppableCalculation<T> {

	private Supplier<T> supplier;

	public TrivialCalculation(Supplier<T> supplier) {
		this.supplier = supplier;
	}
	
	@Override
	public boolean isDone() {
		return true;
	}

	@Override
	public T get() {
		return supplier.get();
	}

	@Override
	public void stop() {
		// does nothing
	}

}
