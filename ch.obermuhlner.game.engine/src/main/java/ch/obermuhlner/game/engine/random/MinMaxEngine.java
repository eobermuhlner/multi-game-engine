package ch.obermuhlner.game.engine.random;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import ch.obermuhlner.game.Engine;
import ch.obermuhlner.game.Game;
import ch.obermuhlner.game.Side;
import ch.obermuhlner.game.StoppableCalculation;
import ch.obermuhlner.game.impl.TrivialCalculation;
import ch.obermuhlner.util.GameUtil;
import ch.obermuhlner.util.Tuple2;

public class MinMaxEngine<G extends Game> implements Engine<G> {

	private static final double MAX_VALUE = 1E100;

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

		Tuple2<String, Double> moveWithValue = minimax(game, null, 0, targetDepth, MIN_VALUE, MAX_VALUE, maximizePlayer, nodeCount);

		return moveWithValue.getValue1();
	}

	private <G extends Game> Tuple2<String, Double> minimax(G game, String lastMove, int depth, int targetDepth, double alpha, double beta, boolean maximizePlayer, AtomicInteger nodeCount) {
		if (game.isFinished() || depth == targetDepth) {
			double score = game.getScore();
			return Tuple2.of(lastMove, score);
		}

		List<Tuple2<String, Double>> validMoves = game.getValidMovesWithScore();
		GameUtil.sort(validMoves, maximizePlayer);
		
		double bestValue = maximizePlayer ? MIN_VALUE : MAX_VALUE;
		List<String> bestMoves = new ArrayList<String>();
		
		for (Tuple2<String, Double> moveWithSimpleValue : validMoves) {
			String move = moveWithSimpleValue.getValue1();

			Game local = game.cloneGame();
			local.move(move);
			nodeCount.incrementAndGet();
			
			Tuple2<String, Double> moveWithValue = minimax(local, move, depth + 1, targetDepth, alpha, beta, !maximizePlayer, nodeCount);
			double value = moveWithValue.getValue2();
			
			//System.out.println("MINMAX " + printLevel(depth) + " " + move + " " + value + " " + (maximizePlayer?"max":"min"));
			if (maximizePlayer) {
				if (value >= bestValue) {
					if (value > bestValue) {
						bestMoves.clear();
					}
					bestValue = value;
					bestMoves.add(move);
				}
				alpha = Math.max(alpha, bestValue);
			} else {
				if (value <= bestValue) {
					if (value < bestValue) {
						bestMoves.clear();
					}
					bestValue = value;
					bestMoves.add(move);
				}
				beta = Math.min(beta, bestValue);
			}
			if (beta <= alpha) {
				break;
			}
		}
		
		String bestMove = GameUtil.pickRandomElement(random, bestMoves);
		if (depth == 0) {
			System.out.printf("MINMAX depth=%2d nodes=%8d best=%-6s %15.1f %-15s : %s\n", depth, nodeCount.get(), bestMove, bestValue, game.getState(), validMoves);
		}
		
		return Tuple2.of(bestMove, bestValue);
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
