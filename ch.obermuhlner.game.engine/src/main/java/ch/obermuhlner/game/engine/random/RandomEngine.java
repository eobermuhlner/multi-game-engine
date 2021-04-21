package ch.obermuhlner.game.engine.random;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import ch.obermuhlner.game.Engine;
import ch.obermuhlner.game.Game;
import ch.obermuhlner.game.StoppableCalculation;
import ch.obermuhlner.game.impl.TrivialCalculation;
import ch.obermuhlner.util.CheckArgument;
import ch.obermuhlner.util.GameUtil;
import ch.obermuhlner.util.Tuple2;

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
		List<Tuple2<String, Double>> allMoves = game.getAllMoves();

		CheckArgument.isTrue(!allMoves.isEmpty(), () -> "No possible moves found: " + game.getState());

		for (int i = 0; i < TRY_RANDOM_MOVE; i++) {
			String randomMove = GameUtil.pickRandom(random, allMoves);
			if (game.isValid(randomMove)) {
				return randomMove;
			}
		}
		
		List<Tuple2<String, Double>> allValidMoves = allMoves.stream()
				.filter(moveWithValue -> game.isValid(moveWithValue.getValue1()))
				.collect(Collectors.toList());

		CheckArgument.isTrue(!allValidMoves.isEmpty(), () -> "No valid moves found: " + game.getState());

		return GameUtil.pickRandom(random, allValidMoves);
	}

	@Override
	public StoppableCalculation<String> bestMove(long milliseconds) {
		return new TrivialCalculation<>(() -> bestMove());
	}
	
	@Override
	public G getGame() {
		return game;
	}
}
