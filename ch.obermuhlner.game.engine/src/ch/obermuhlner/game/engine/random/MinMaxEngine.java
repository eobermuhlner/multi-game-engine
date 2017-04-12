package ch.obermuhlner.game.engine.random;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
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

	private final int targetDepth;

	public MinMaxEngine(G game) {
		this(game, 3);
	}

	public MinMaxEngine(G game, int targetDepth) {
		this.game = game;
		this.targetDepth = targetDepth;
	}

	@Override
	public G getGame() {
		return game;
	}

	@Override
	public String bestMove() {
		AtomicInteger nodeCount = new AtomicInteger();
		boolean maximizePlayer = game.getSideToMove() == Side.White;

		List<Tuple2<String, Double>> validMoves = game.getValidMoves();
		
		List<Tuple2<String, Double>> calculatedMoves = validMoves.stream()
			.map(moveWithValue -> minimax(game, moveWithValue.getValue1(), maximizePlayer, nodeCount))
			.collect(Collectors.toList());

		//GameUtil.sort(calculatedMoves, maximizePlayer);
		System.out.println("MINMAXVALUES " + nodeCount + " nodes : " + calculatedMoves);

		if (maximizePlayer) {
			return GameUtil.findMax(random, calculatedMoves);
		} else {
			return GameUtil.findMin(random, calculatedMoves);
		}
	}

	private Tuple2<String, Double> minimax(G game, String move, boolean maximizePlayer, AtomicInteger nodeCount) {
		Game local = game.cloneGame();
		local.move(move);
		
		nodeCount.incrementAndGet();
		double value = minimax(local, 1, targetDepth, MIN_VALUE, MAX_VALUE, !maximizePlayer, nodeCount);
		//System.out.println("MINMAX " + printLevel(0) + " " + move + " " + value + " " + (maximizePlayer?"max":"min"));
		return Tuple2.of(move, value);
	}
	
	private static <G extends Game> double minimax(G game, int depth, int targetDepth, double alpha, double beta, boolean maximizePlayer, AtomicInteger nodeCount) {
		if (game.isFinished() || depth == targetDepth) {
			double score = game.getScore();
			return score;
		}

		double bestValue = maximizePlayer ? MIN_VALUE : MAX_VALUE;

		List<Tuple2<String, Double>> validMoves = game.getValidMoves();
		GameUtil.sort(validMoves, maximizePlayer);
		
		for (Tuple2<String, Double> moveWithValue : validMoves) {
			String move = moveWithValue.getValue1();

			Game local = game.cloneGame();
			local.move(move);
			nodeCount.incrementAndGet();
			double value = minimax(local, depth + 1, targetDepth, alpha, beta, !maximizePlayer, nodeCount);
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
