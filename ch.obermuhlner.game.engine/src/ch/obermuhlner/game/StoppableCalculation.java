package ch.obermuhlner.game;

/**
 * Calculates a value with a potentially long running calculation.
 * The calculation may be stopped at any time, yielding a best effort result.
 *
 * @param <T> the type of the calculated result
 */
public interface StoppableCalculation<T> {
	/**
	 * Returns whether the result has been calculated.
	 * 
	 * @return <code>true</code> if the calculation is done, <code>false</code> otherwise
	 * @see #get()
	 */
	boolean isDone();
	
	/**
	 * Returns the calculated result.
	 * 
	 * @return the calculated result, may be <code>null</code>
	 */
	T get();
	
	/**
	 * Stops the calculation, yielding a best effort result.
	 * 
	 * @see #get()
	 */
	void stop();
}
