package ch.obermuhlner.game;

import java.util.Map;

public interface Game<G extends Game<G>> {

	Board<G> createBoard();
	
	Map<String, Double> getAllMoves(Board<G> board);
	
	boolean isValid(Board<G> board, String move);
	
	boolean isFinished(Board<G> board);
	
	boolean isStaleMate(Board<G> board);
	
	Side getWinner(Board<G> board);
}
