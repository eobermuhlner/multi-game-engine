package ch.obermuhlner.game;

/**
 * Provides a lookup table of best moves for a given game state.
 *
 * @param <G> the game
 */
public interface LookupTable<G extends Game> {
	
	/**
	 * The best move for the specified game state.
	 * 
	 * @param game the game state
	 * @return the best move, or <code>null</code> if none
	 */
	String bestMove(G game);
}
