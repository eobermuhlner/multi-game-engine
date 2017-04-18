package ch.obermuhlner.game;

import java.util.List;
import java.util.stream.Collectors;

import ch.obermuhlner.util.Tuple2;

public interface Game {

	void setStartPosition();
	
	void setState(String state);
	
	String getState();
	
	String getPositionState();
	
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

	default List<Tuple2<String, Double>> getValidMovesWithScore() {
		return getAllMoves().stream()
				.filter(moveAndValue -> isValid(moveAndValue.getValue1()))
				.map(moveAndValue -> moveAndValue.getValue1())
				.map(move -> Tuple2.of(move, getScore(move)))
				.collect(Collectors.toList());
	}

	default double getScore(String move) {
		Game local = cloneGame();
		local.move(move);
		return local.getScore();
	}
	
	boolean isFinished();
	
	Side getWinner();

	Game cloneGame();
	
}
