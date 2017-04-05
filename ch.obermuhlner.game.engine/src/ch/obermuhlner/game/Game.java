package ch.obermuhlner.game;

import java.util.Map;
import java.util.stream.Collectors;

public interface Game<G extends Game<G>> {

	void setStartPosition();
	
	void setState(String state);
	
	String getState();
	
	Side getSideToMove();
	
	void move(String move);
	
	Map<String, Double> getAllMoves();
	
	boolean isValid(String move);
	
	default Map<String, Double> getValidMoves() {
		return getAllMoves().entrySet().stream()
				.filter(entry -> isValid(entry.getKey()))
				.collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue()));
	}
	
	boolean isFinished();
	
	Side getWinner();

	Game<G> clone();
	
}
