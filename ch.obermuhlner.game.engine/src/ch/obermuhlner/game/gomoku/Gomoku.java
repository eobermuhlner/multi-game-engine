package ch.obermuhlner.game.gomoku;

import ch.obermuhlner.game.Engine;
import ch.obermuhlner.game.app.GameCommandLine;
import ch.obermuhlner.game.engine.random.RandomEngine;

public class Gomoku extends AbstractStonesInARow {
	
	public Gomoku() {
		this(19, 19, 5, false);
	}

	public Gomoku(int boardWidth, int boardHeight, int winCount, boolean exactWin) {
		super(boardWidth, boardHeight, winCount, exactWin);
	}

	@Override
	public Gomoku cloneGame() {
		return (Gomoku) super.cloneGame(new Gomoku(boardWidth, boardHeight, winCount, exactWin));
	}

	public static void main(String[] args) {
		//Engine<Gomoku> engine = new MonteCarloEngine<>(new Gomoku());
		Engine<Gomoku> engine = new RandomEngine<>(new Gomoku());
		GameCommandLine.playGame(engine);
	}
}
