package ch.obermuhlner.game;

import java.util.List;
import java.util.stream.Collectors;

import ch.obermuhlner.util.Tuple2;

public interface Game {

	void setStartPosition();
	
	void setState(String state);
	
	String getState();
	
	String getDiagram();
	
	Side getSideToMove();
	
	double getScore();
	
	void move(String move);
	
	List<Tuple2<String, Double>> getAllMoves();
	
	boolean isValid(String move);
	
	default List<Tuple2<String, Double>> getValidMoves() {
		return getAllMoves().stream()
				.filter(moveAndValue -> isValid(moveAndValue.getValue1()))
				.collect(Collectors.toList());
	}
	
	boolean isFinished();
	
	Side getWinner();

	Game cloneGame();
	
}
