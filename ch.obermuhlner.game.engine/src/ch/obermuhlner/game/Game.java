package ch.obermuhlner.game;

import java.util.Map;

public interface Game<G extends Game<G>> {

	void setStartPosition();
	
	void setState(String state);
	
	String getState();
	
	Side getSideToMove();
	
	void move(String move);
	
	Map<String, Double> getAllMoves();
	
	boolean isValid(String move);
	
	boolean isFinished();
	
	Side getWinner();

	Game<G> clone();
	
}
