package ch.obermuhlner.game.engine.random;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import ch.obermuhlner.game.Engine;
import ch.obermuhlner.game.Game;
import ch.obermuhlner.game.Side;
import ch.obermuhlner.game.StoppableCalculation;
import ch.obermuhlner.util.GameUtil;
import ch.obermuhlner.util.Tuple2;

public class MinMaxEngine<G extends Game> implements Engine<G> {

	private static final double WIN_VALUE = 100000;

	private static final double LOSS_VALUE = -WIN_VALUE;

	private final Random random = new Random();

	private final G game;

	private final long defaultCalculationMilliseconds;
	
	private final int targetDepth = 2;

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
		List<Tuple2<String, Double>> validMoves = game.getValidMoves();
		
		List<Tuple2<String, Double>> calculatedMoves = validMoves.stream()
			.map(moveWithValue -> minimax(game, moveWithValue.getValue1()))
			.collect(Collectors.toList());

		System.out.println(calculatedMoves);
		return GameUtil.pickRandom(random, calculatedMoves);
	}

	private Tuple2<String, Double> minimax(G game, String move) {
		Game local = game.cloneGame();
		local.move(move);
		
		return Tuple2.of(move, minimax(local, targetDepth, LOSS_VALUE, WIN_VALUE, false));
	}
	
	private static <G extends Game> double minimax(G game, int depth, double alpha, double beta, boolean maximizePlayer) {
		Side sideToMove = game.getSideToMove();

		if (game.isFinished()) {
			Side winner = game.getWinner();
			if (winner == sideToMove) {
				return WIN_VALUE;
			} else if (winner == sideToMove.otherSide()) {
				return LOSS_VALUE;
			} else {
				return 0.0;
			}
		}
		
		if (depth == 0) {
			return game.getScore();
		}

		double bestValue = maximizePlayer ? LOSS_VALUE : WIN_VALUE;

		List<Tuple2<String, Double>> validMoves = game.getValidMoves();
		for (Tuple2<String, Double> moveWithValue : validMoves) {
			String move = moveWithValue.getValue1();

			Game local = game.cloneGame();
			local.move(move);
			double value = minimax(local, depth - 1, alpha, beta, !maximizePlayer);
			if (maximizePlayer) {
				bestValue = Math.max(bestValue, value);
				alpha = Math.max(alpha, value);
			} else {
				bestValue = Math.min(bestValue, value);
				beta = Math.min(beta, value);
			}
			if (beta <= alpha) {
				break;
			}
		}
		
		return bestValue;
	}

	@Override
	public StoppableCalculation<String> bestMove(long milliseconds) {
		return new TrivialCalculation<>(() -> bestMove());
	}

}
