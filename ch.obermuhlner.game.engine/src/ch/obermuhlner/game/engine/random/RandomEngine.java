package ch.obermuhlner.game.engine.random;

import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import ch.obermuhlner.game.Engine;
import ch.obermuhlner.game.Game;
import ch.obermuhlner.game.StoppableCalculation;
import ch.obermuhlner.util.CheckArgument;

public class RandomEngine<G extends Game> implements Engine<G> {

	private static final int TRY_RANDOM_MOVE = 5;

	private final G game;

	private final Random random;

	public RandomEngine(G game) {
		this(game, new Random());
	}

	public RandomEngine(G game, Random random) {
		this.game = game;
		this.random = random;
	}

	@Override
	public String bestMove() {
		Map<String, Double> allMoves = game.getAllMoves();

		CheckArgument.isTrue(!allMoves.isEmpty(), () -> "No possible moves found: " + game.getState());

		for (int i = 0; i < TRY_RANDOM_MOVE; i++) {
			String randomMove = pickRandomMove(allMoves);
			if (game.isValid(randomMove)) {
				return randomMove;
			}
		}
		
		Map<String, Double> allValidMoves = allMoves.entrySet().stream()
				.filter(entry -> game.isValid(entry.getKey()))
				.collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue()));

		CheckArgument.isTrue(!allValidMoves.isEmpty(), () -> "No valid moves found: " + game.getState());

		return pickRandomMove(allValidMoves);
	}

	@Override
	public StoppableCalculation<String> bestMove(long milliseconds) {
		StoppableCalculation<String> calculation = new StoppableCalculation<String>() {
			@Override
			public boolean isDone() {
				return true;
			}

			@Override
			public String get() {
				return bestMove();
			}

			@Override
			public void stop() {
				// ignore
			}
		};
		
		return calculation;
	}
	
	private String pickRandomMove(Map<String, Double> allMoves) {
		return RandomUtil.pickRandom(random, allMoves);
	}

	@Override
	public G getGame() {
		return game;
	}
}
