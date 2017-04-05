package ch.obermuhlner.game;

import java.util.Map;

public interface Engine<G extends Game<G>> {

	Board<G> getBoard();
	
	String bestMove();
	
	void move(String move);
	
	Map<String, Double> getAllMoves();
	
	boolean isValid(String move);
	
	boolean isFinished();
	
	boolean isStaleMate();
	
	Side getWinner();

}
