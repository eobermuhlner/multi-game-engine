package ch.obermuhlner.game.engine.random;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import ch.obermuhlner.game.Engine;
import ch.obermuhlner.game.Game;
import ch.obermuhlner.game.StoppableCalculation;

public class RandomEngine<G extends Game> implements Engine<G> {

	private static final int TRY_RANDOM_MOVE = 5;

	private final G game;

	private final Random random;

	private final ExecutorService executor = Executors.newFixedThreadPool(1);

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

		for (int i = 0; i < TRY_RANDOM_MOVE; i++) {
			String move = pickRandomMove(allMoves);
			if (game.isValid(move)) {
				return move;
			}
		}
		
		Map<String, Double> allValidMoves = allMoves.entrySet().stream()
				.filter(entry -> game.isValid(entry.getKey()))
				.collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue()));

		return pickRandomMove(allValidMoves);
	}

	@Override
	public StoppableCalculation<String> bestMove(long milliseconds) {
		TimedCalculation<String> calculation = new TimedCalculation<String>(milliseconds) {
			private String result;

			@Override
			protected boolean calculateChunk(long remainingMillis) {
				result = bestMove();
				return true;
			}

			@Override
			protected String calculateResult() {
				return result;
			}
		};
		
		executor.submit(calculation);
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
