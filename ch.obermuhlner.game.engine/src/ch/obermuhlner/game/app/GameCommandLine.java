package ch.obermuhlner.game.app;

import ch.obermuhlner.game.Engine;
import ch.obermuhlner.game.Game;
import ch.obermuhlner.game.engine.random.RandomEngine;
import ch.obermuhlner.game.gomoku.Gomoku;

public class GameCommandLine {

	public static <G extends Game> void playGame(Engine<G> engine) {
		while (!engine.getGame().isFinished()) {
			System.out.println(engine.getGame().getDiagram());
			String move = engine.bestMove();
			System.out.println("MOVE " + move);
			engine.getGame().move(move);
		}

		System.out.println(engine.getGame().getDiagram());
		System.out.println("WINNER " + engine.getGame().getWinner());
	}
	
	public static void main(String[] args) {
		playGame(new RandomEngine<>(new Gomoku()));
//		playGame(new MonteCarloEngine<>(new Gomoku()));
//		
//		playGame(new RandomEngine<>(new TicTacToe()));
//		playGame(new MonteCarloEngine<>(new TicTacToe()));
	}
}
