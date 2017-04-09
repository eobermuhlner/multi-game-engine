package ch.obermuhlner.game.engine.random;

import java.awt.JobAttributes.SidesType;
import java.util.Map;

import ch.obermuhlner.game.Engine;
import ch.obermuhlner.game.Game;
import ch.obermuhlner.game.StoppableCalculation;

public class MinMaxEngine<G extends Game> implements Engine<G> {

	private final G game;

	private final long defaultCalculationMilliseconds;
	
	private final int targetDepth = 3;

	public MinMaxEngine(G game) {
		this(game, 200);
	}

	public MinMaxEngine(G game, long defaultCalculationMilliseconds) {
		this.game = game;
		this.defaultCalculationMilliseconds = defaultCalculationMilliseconds;
	}

	@Override
	public G getGame() {
		return game;
	}

	@Override
	public String bestMove() {
		Map<String, Double> validMoves = game.getValidMoves();
		double value = minimax(game, targetDepth);
		return null;
	}

	private static <G extends Game> double minimax(G game, int depth) {
		if (game.isFinished()) {
			
		}
		
		return 0.0;
	}

	@Override
	public StoppableCalculation<String> bestMove(long milliseconds) {
		return new TrivialCalculation<>(() -> bestMove());
	}

}
