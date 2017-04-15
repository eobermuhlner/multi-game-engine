package ch.obermuhlner.game.engine;

import java.util.List;
import java.util.stream.Collectors;

import ch.obermuhlner.game.Engine;
import ch.obermuhlner.game.Game;
import ch.obermuhlner.game.chess.Chess;
import ch.obermuhlner.game.engine.random.MinMaxEngine;
import ch.obermuhlner.game.gomoku.ConnectFour;
import ch.obermuhlner.game.gomoku.Gomoku;
import ch.obermuhlner.game.mill.Mill;
import ch.obermuhlner.game.tictactoe.TicTacToe;

public class DirectGameEngine implements GameEngine {

	private Engine<?> engine;
	
	@Override
	public void setGame(String gameName) {
		Game game = createGame(gameName);
		
		engine = new MinMaxEngine<>(game);
	}

	private Game createGame(String gameName) {
		switch(gameName) {
		case "tictactoe":
			return new TicTacToe();
		case "gomoku":
			return new Gomoku();
		case "connectfour":
			return new ConnectFour();
		case "mill":
			return new Mill();
		case "chess":
			return new Chess();
		default:
		}
		throw new IllegalArgumentException("Unknown game: " + gameName);
	}

	@Override
	public void setStartPosition() {
		engine.getGame().setStartPosition();
	}

	@Override
	public Side getSideToMove() {
		return convertSide(engine.getGame().getSideToMove());
	}
	
	@Override
	public List<String> getValidMoves() {
		return engine.getGame().getValidMoves().stream()
			.map(moveWithValue -> moveWithValue.getValue1())
			.collect(Collectors.toList());
	}

	@Override
	public void move(String move) {
		engine.getGame().move(move);
	}

	@Override
	public String bestMove() {
		return engine.bestMove();
	}

	@Override
	public boolean isFinished() {
		return engine.getGame().isFinished();
	}

	@Override
	public Side getWinner() {
		return convertSide(engine.getGame().getWinner());
	}

	private Side convertSide(ch.obermuhlner.game.Side side) {
		switch(side) {
		case White:
			return Side.White;
		case Black:
			return Side.Black;
		case None:
			return Side.None;
		}
		throw new IllegalArgumentException("Unknown side: " + side);
	}

}
