package ch.obermuhlner.game;

/**
 * Calculates the best move for a given game situation.
 *
 * @param <G> the game
 */
public interface Engine<G extends Game> {

	/**
	 * Returns the current game.
	 * 
	 * @return the game
	 */
	G getGame();

	/**
	 * Returns the best move after calculating for a default time or to a default depth.
	 * 
	 * @return the best move, or <code>null</code> if none
	 */
	String bestMove();

	/**
	 * Returns a {@link StoppableCalculation} that calculates the best move.
	 * 
	 * @param milliseconds the given time to calculate the best move 
	 * @return the {@link StoppableCalculation} that will return the best move, or <code>null</code> if none
	 */
	StoppableCalculation<String> bestMove(long milliseconds);

}
