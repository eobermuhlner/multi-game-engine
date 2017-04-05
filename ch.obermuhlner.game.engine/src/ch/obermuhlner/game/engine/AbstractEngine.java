package ch.obermuhlner.game.engine;

import java.util.Map;

import ch.obermuhlner.game.Board;
import ch.obermuhlner.game.Engine;
import ch.obermuhlner.game.Game;
import ch.obermuhlner.game.Side;

public abstract class AbstractEngine<G extends Game<G>> implements Engine<G> {

	protected final G game;
	
	protected Board<G> board;

	public AbstractEngine(G game) {
		this.game = game;
		board = game.createBoard();
	}
	
	@Override
	public Board<G> getBoard() {
		return board;
	}

	@Override
	public void move(String move) {
		board.move(move);
	}

	@Override
	public Map<String, Double> getAllMoves() {
		return game.getAllMoves(board);
	}

	@Override
	public boolean isValid(String move) {
		return game.isValid(board, move);
	}

	@Override
	public boolean isFinished() {
		return game.isFinished(board);
	}

	@Override
	public boolean isStaleMate() {
		return game.isStaleMate(board);
	}

	@Override
	public Side getWinner() {
		return game.getWinner(board);
	}

}
