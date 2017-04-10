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

	private static final double MAX_VALUE = 100000;

	private static final double MIN_VALUE = -MAX_VALUE;

	private final Random random = new Random();

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
		List<Tuple2<String, Double>> validMoves = game.getValidMoves();
		
		List<Tuple2<String, Double>> calculatedMoves = validMoves.stream()
			.map(moveWithValue -> minimax(game, moveWithValue.getValue1()))
			.collect(Collectors.toList());

		GameUtil.sort(calculatedMoves);
		System.out.println(game.getState());
		System.out.println(calculatedMoves);
		//return GameUtil.pickRandom(random, calculatedMoves);
		return GameUtil.findMax(calculatedMoves);
	}

	private Tuple2<String, Double> minimax(G game, String move) {
		Game local = game.cloneGame();
		local.move(move);
		
		double value = minimax(local, 1, targetDepth, MIN_VALUE, MAX_VALUE, false);
		System.out.println("MINMAX " + printLevel(0) + " " + move + " " + value + " " + "max");
		return Tuple2.of(move, value);
	}
	
	private static <G extends Game> double minimax(G game, int depth, int targetDepth, double alpha, double beta, boolean maximizePlayer) {
		if (game.isFinished() || depth == targetDepth) {
			double score = game.getScore();
			if (game.getSideToMove() == Side.Black) {
				score = -score;
			}
			return score;
		}

		double bestValue = maximizePlayer ? MIN_VALUE : MAX_VALUE;

		List<Tuple2<String, Double>> validMoves = game.getValidMoves();
		GameUtil.sort(validMoves);
		
		for (Tuple2<String, Double> moveWithValue : validMoves) {
			String move = moveWithValue.getValue1();

			Game local = game.cloneGame();
			local.move(move);
			double value = minimax(local, depth + 1, targetDepth, alpha, beta, !maximizePlayer);
			//System.out.println("MINMAX " + printLevel(depth) + " " + move + " " + value + " " + (maximizePlayer?"max":"min"));
			if (maximizePlayer) {
				bestValue = Math.max(bestValue, value);
				alpha = Math.max(alpha, bestValue);
			} else {
				bestValue = Math.min(bestValue, value);
				beta = Math.min(beta, bestValue);
			}
			if (beta <= alpha) {
				break;
			}
		}
		
		return bestValue;
	}

	private static String printLevel(int depth) {
		String s = "";
		for (int i = 0; i < depth; i++) {
			s += "| ";
		}
		return s;
	}

	@Override
	public StoppableCalculation<String> bestMove(long milliseconds) {
		return new TrivialCalculation<>(() -> bestMove());
	}

}
