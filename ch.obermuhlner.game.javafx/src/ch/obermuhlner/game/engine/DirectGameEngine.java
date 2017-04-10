package ch.obermuhlner.game.engine;

import java.util.List;
import java.util.stream.Collectors;

import ch.obermuhlner.game.Engine;
import ch.obermuhlner.game.Game;
import ch.obermuhlner.game.chess.Chess;
import ch.obermuhlner.game.engine.random.MonteCarloEngine;
import ch.obermuhlner.game.gomoku.ConnectFour;
import ch.obermuhlner.game.gomoku.Gomoku;
import ch.obermuhlner.game.mill.Mill;
import ch.obermuhlner.game.tictactoe.TicTacToe;

public class DirectGameEngine implements GameEngine {

	private Engine<?> engine;
	
	@Override
	public void setGame(String gameName) {
		Game game;
		switch(gameName) {
		case "tictactoe":
			game = new TicTacToe();
			break;
		case "gomoku":
			game = new Gomoku();
			break;
		case "connectfour":
			game = new ConnectFour();
			break;
		case "mill":
			game = new Mill();
			break;
		case "chess":
			game = new Chess();
			break;
		default:
			throw new IllegalArgumentException("Unknown game: " + gameName);
		}
		
		engine = new MonteCarloEngine<>(game);
	}

	@Override
	public void setStartPosition() {
		engine.getGame().setStartPosition();
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
		ch.obermuhlner.game.Side winner = engine.getGame().getWinner();
		switch(winner) {
		case White:
			return Side.White;
		case Black:
			return Side.Black;
		case None:
			return Side.None;
		}
		throw new IllegalArgumentException("Unknown side: " + winner);
	}

}
