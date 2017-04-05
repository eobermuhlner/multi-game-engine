package ch.obermuhlner.game.engine.random;

import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import ch.obermuhlner.game.Engine;
import ch.obermuhlner.game.Game;

public class RandomEngine<G extends Game<G>> implements Engine<G> {

	private static final int TRY_RANDOM_MOVE = 5;

	private final G game;

	private final Random random;

	public RandomEngine(G game) {
		this.game = game;
		random = new Random();
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

	private String pickRandomMove(Map<String, Double> allMoves) {
		return RandomUtil.pickRandom(random, allMoves);
	}

	@Override
	public G getGame() {
		return game;
	}
}
