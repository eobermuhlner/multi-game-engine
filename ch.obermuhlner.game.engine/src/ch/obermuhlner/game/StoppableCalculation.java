package ch.obermuhlner.game;

public interface StoppableCalculation<T> {
	boolean isDone();
	
	T get();
	
	void stop();
}
