package ch.obermuhlner.game;

import java.util.List;
import java.util.stream.Collectors;

import ch.obermuhlner.util.Tuple2;

/**
 * Represents the rules of a game by determining all valid moves for a given game state. 
 */
public interface Game {

	/**
	 * Sets the start position of the game.
	 * 
	 * This is includes the complete game state (positions, move count, special moves).
	 */
	void setStartPosition();
	
	/**
	 * Sets the full state of the game from the specified string representation.
	 * 
	 *  @see #getState()
	 */
	void setState(String state);
	
	/**
	 * Returns the full state of the game as string representation.
	 * 
	 * @return the game state.
	 * @see #setState()
	 */
	String getState();

	/**
	 * Returns the position state of the game as string representation.
	 * 
	 * The position state contains only information that influences the game play, but not additional information such as move count.
	 * It can be used as key in transposition tables.
	 * 
	 * @return the position state
	 */
	String getPositionState();
	
	/**
	 * Returns a string diagram (typically multiple lines) of the current game state.
	 * 
	 * @return the diagram
	 */
	String getDiagram();
	
	/**
	 * Returns the {@link Side} that has the next move.
	 * 
	 * @return the {@link Side} to move next
	 */
	Side getSideToMove();
	
	/**
	 * Returns the score of the current game state.
	 * 
	 * @return the score, positive values indicate that {@link Side#White} has the advantage, negative values signify {@link Side#Black} advantage.
	 */
	double getScore();

	/**
	 * Executes the specified move.
	 * 
	 * @param move the move to execute
	 */
	void move(String move);

	/**
	 * Returns all possible moves.
	 * 
	 * Each move comes with an estimated value (higher is better).
	 * Note: This are semi-legal moves, to guarantee that the move is legal you must validate it with {@link #isValid(String)}.
	 * 
	 * This allows games with complex validation (such as chess) to produce the list of semi-valid moves cheaply and only validate a move if necessary.
	 * 
	 * @return the list of possible moves with an estimated value of each move
	 * @see #isValid(String)
	 * @see #getValidMoves()
	 */
	List<Tuple2<String, Double>> getAllMoves();

	/**
	 * Returns whether the specified move is valid.
	 * 
	 * @param move the move to validate
	 * @return <code>true</code> if the move is valid, <code>false</code> otherwise
	 */
	boolean isValid(String move);

	/**
	 * Returns a list of all valid moves.
	 * 
	 * @return the list of all valid moves with an estimated value of each move
	 */
	default List<Tuple2<String, Double>> getValidMoves() {
		return getAllMoves().stream()
				.filter(moveAndValue -> isValid(moveAndValue.getValue1()))
				.collect(Collectors.toList());
	}

	/**
	 * Returns a list of all valid moves with the game score after executing the move.
	 * 
	 * @return the list of all valid moves with the game score of each move
	 * @see #getValidMoves()
	 * @see #getScore()
	 */
	default List<Tuple2<String, Double>> getValidMovesWithScore() {
		return getAllMoves().stream()
				.filter(moveAndValue -> isValid(moveAndValue.getValue1()))
				.map(moveAndValue -> moveAndValue.getValue1())
				.map(move -> Tuple2.of(move, getScore(move)))
				.collect(Collectors.toList());
	}

	/**
	 * Returns the game score of the specified move.
	 * 
	 * Does not modify the game state.
	 * 
	 * @param move the move to calculate the game score
	 * @return the game score after the move
	 * @see #getScore()
	 */
	default double getScore(String move) {
		Game local = cloneGame();
		local.move(move);
		return local.getScore();
	}

	/**
	 * Returns whether the game is finished.
	 * 
	 * @return <code>true</code> if the game is finished, <code>false</code> otherwise
	 */
	boolean isFinished();
	
	/**
	 * Returns the winner if the game is finished.
	 * 
	 * The result is unspecified if {@link #isFinished()} returns <code>false</code>.
	 * 
	 * @return the winning {@link Side}, or {@link Side#None} if the game is a draw
	 * @see #isFinished()
	 */
	Side getWinner();

	/**
	 * Clones the current game.
	 * 
	 * @return the created clone of the current game
	 */
	Game cloneGame();
	
}
