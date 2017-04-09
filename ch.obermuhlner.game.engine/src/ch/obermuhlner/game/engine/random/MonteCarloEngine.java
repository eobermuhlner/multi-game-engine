package ch.obermuhlner.game.engine.random;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import ch.obermuhlner.game.Engine;
import ch.obermuhlner.game.Game;
import ch.obermuhlner.game.Side;
import ch.obermuhlner.game.StoppableCalculation;
import ch.obermuhlner.util.CheckArgument;
import ch.obermuhlner.util.Tuple2;

public class MonteCarloEngine<G extends Game> implements Engine<G> {

	private final Random random = new Random();

	private G game;
	
	private final long defaultCalculationMilliseconds;

	public MonteCarloEngine(G game) {
		this(game, 200);
	}
	
	public MonteCarloEngine(G game, long defaultCalculationMilliseconds) {
		this.game = game;
		this.defaultCalculationMilliseconds = defaultCalculationMilliseconds;
	}
	
	@Override
	public G getGame() {
		return game;
	}

	@Override
	public String bestMove() {
		return bestMove(defaultCalculationMilliseconds).get();
	}

	private static class PlayStatistic {
		String move;
		int win;
		int loss;
		
		public PlayStatistic(String move) {
			this.move = move;
		}
	}
	
	@Override
	public StoppableCalculation<String> bestMove(long milliseconds) {
		TimedCalculation<String> calculation = new TimedCalculation<String>(milliseconds) {
			private Side sideToMove = game.getSideToMove();
			private List<Tuple2<String, Double>> validMoves = game.getValidMoves();
			
			PlayStatistic[] playStatistics = new PlayStatistic[validMoves.size()];
			{
				int i = 0;
				for(Tuple2<String, Double> moveWithValue : validMoves) {
					playStatistics[i++] = new PlayStatistic(moveWithValue.getValue1());
				}
			}
			
			private int playCount = 0;
			
			@Override
			protected boolean calculateChunk(long remainingMillis) {
				for (PlayStatistic playStatistic : playStatistics) {
					Side winner = randomPlay(playStatistic.move);
					if (winner == sideToMove) {
						playStatistic.win++;
					} else if (winner == sideToMove.otherSide()) {
						playStatistic.loss++;
					}
				}
				playCount++;
				return false;
			}

			@Override
			protected String calculateResult() {
				List<Tuple2<String, Double>> calculatedMoves = Arrays.stream(playStatistics)
					.map(playStatistic -> {
						int draw = playCount - playStatistic.win - playStatistic.loss;
						double value = (double)(playStatistic.win * 2 + draw) / playCount;
						return Tuple2.of(playStatistic.move, value);
					})
					.collect(Collectors.toList());
				return pickBestMove(calculatedMoves);
			}
		};
		
		new Thread(calculation).start();
		return calculation;
	}
	
	private String pickBestMove(List<Tuple2<String, Double>> moves) {
		String bestMove = null;
		double maxValue = Double.NEGATIVE_INFINITY;
		
		for (Tuple2<String, Double> entry : moves) {
			if (entry.getValue2() > maxValue) {
				maxValue = entry.getValue2();
				bestMove = entry.getValue1();
			}
		}
		
		return bestMove;
	}

	private Side randomPlay(String move) {
		@SuppressWarnings("unchecked")
		G clone = (G) game.cloneGame();
		
		clone.move(move);
		
		RandomEngine<G> randomEngine = new RandomEngine<>(clone, random);
		
		while(!randomEngine.getGame().isFinished()) {
			String bestMove = randomEngine.bestMove();
			CheckArgument.isNotNull(bestMove, () -> "Best random move must exist");
			randomEngine.getGame().move(bestMove);
		}
		
		return randomEngine.getGame().getWinner();
	}

}
