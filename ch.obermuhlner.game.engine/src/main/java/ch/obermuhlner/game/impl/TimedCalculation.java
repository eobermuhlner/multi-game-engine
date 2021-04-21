package ch.obermuhlner.game.impl;

import java.util.concurrent.CountDownLatch;

import ch.obermuhlner.game.StoppableCalculation;

public abstract class TimedCalculation<T> implements StoppableCalculation<T>, Runnable {

	private static final long RESERVE_MILLIS = 50;

	private long remainingMillis;
	
	private volatile boolean stop = false;
	private volatile boolean done = false;
	private volatile T result;
	private final CountDownLatch countDownLatch = new CountDownLatch(1);

	public TimedCalculation(long milliseconds) {
		this.remainingMillis = milliseconds;
	}
	
	@Override
	public boolean isDone() {
		return done;
	}

	@Override
	public T get() {
		done = true;
		
		try {
			countDownLatch.await();
		} catch (InterruptedException e) {
			// ignore
		}

		return result;
	}

	@Override
	public void stop() {
		stop = true;
	}
	
	public void run() {
		boolean finishedEarly = false;
		do {
			long startMillis = System.currentTimeMillis();

			finishedEarly = calculateChunk(remainingMillis);
			
			long endMillis = System.currentTimeMillis();
			long deltaMillis = endMillis - startMillis;
			remainingMillis -= deltaMillis;
		} while (!finishedEarly && !stop && remainingMillis > RESERVE_MILLIS);
		
		result = calculateResult();
		countDownLatch.countDown();
	}

	abstract protected boolean calculateChunk(long remainingMillis);

	abstract protected T calculateResult();
}