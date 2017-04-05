package ch.obermuhlner.game.engine.random;

import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import ch.obermuhlner.game.Engine;
import ch.obermuhlner.game.Game;
import ch.obermuhlner.game.engine.AbstractEngine;

public class RandomEngine<G extends Game<G>> extends AbstractEngine<G> implements Engine<G> {

	private static final int TRY_RANDOM_MOVE = 5;

	private final Random random;

	public RandomEngine(G game) {
		super(game);
		
		random = new Random();
	}

	@Override
	public String bestMove() {
		Map<String, Double> allMoves = game.getAllMoves(board);

		for (int i = 0; i < TRY_RANDOM_MOVE; i++) {
			String move = pickRandomMove(allMoves);
			if (game.isValid(board, move)) {
				return move;
			}
		}
		
		Map<String, Double> allValidMoves = allMoves.entrySet().stream()
				.filter(entry -> game.isValid(board, entry.getKey()))
				.collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue()));

		return pickRandomMove(allValidMoves);
	}

	private String pickRandomMove(Map<String, Double> allMoves) {
		return RandomUtil.pickRandom(random, allMoves);
	}
}
