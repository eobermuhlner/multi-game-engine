package ch.obermuhlner.game.engine.random;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import ch.obermuhlner.game.Engine;
import ch.obermuhlner.game.Game;
import ch.obermuhlner.game.Side;
import ch.obermuhlner.game.StoppableCalculation;

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
			private Map<String, Double> validMoves = game.getValidMoves();
			
			PlayStatistic[] playStatistics = new PlayStatistic[validMoves.size()];
			{
				int i = 0;
				for(String move : validMoves.keySet()) {
					playStatistics[i++] = new PlayStatistic(move);
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
				for (PlayStatistic playStatistic : playStatistics) {
					int draw = playCount - playStatistic.win - playStatistic.loss;
					double value = (double)(playStatistic.win * 2 + draw) / playCount;
					validMoves.put(playStatistic.move, value);
				}				
				return pickBestMove(validMoves);
			}
		};
		
		new Thread(calculation).start();
		return calculation;
	}
	
	private String pickBestMove(Map<String, Double> moves) {
		String bestMove = null;
		double maxValue = Double.NEGATIVE_INFINITY;
		
		for (Entry<String, Double> entry : moves.entrySet()) {
			if (entry.getValue() > maxValue) {
				maxValue = entry.getValue();
				bestMove = entry.getKey();
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
			randomEngine.getGame().move(randomEngine.bestMove());
		}
		
		return randomEngine.getGame().getWinner();
	}

}
