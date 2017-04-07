package ch.obermuhlner.game.engine.random;

import java.util.HashMap;
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
		this(game, 1000);
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

	@Override
	public StoppableCalculation<String> bestMove(long milliseconds) {
		TimedCalculation<String> calculation = new TimedCalculation<String>(milliseconds) {
			private Side sideToMove = game.getSideToMove();
			private Map<String, Double> validMoves = game.getValidMoves();
			
			private Map<String, Integer> moveWin = new HashMap<>();
			private Map<String, Integer> moveLoss = new HashMap<>();
			
			private int playCount = 0;

			@Override
			protected boolean calculateChunk(long remainingMillis) {
				for (Entry<String, Double> entry : validMoves.entrySet()) {
					Side winner = randomPlay(entry.getKey());
					if (winner == sideToMove) {
						int count = moveWin.getOrDefault(entry.getKey(), 0);
						moveWin.put(entry.getKey(), count + 1);
						//moveWin.merge(entry.getKey(), 1, (oldValue, delta) -> oldValue + delta);
					}
					if (winner == sideToMove.otherSide()) {
						int count = moveLoss.getOrDefault(entry.getKey(), 0);
						moveLoss.put(entry.getKey(), count + 1);
						//moveLoss.merge(entry.getKey(), 1, (oldValue, delta) -> oldValue + delta);
					}
				}
				playCount++;
				return false;
			}

			@Override
			protected String calculateResult() {
				for (Entry<String, Double> entry : validMoves.entrySet()) {
					int win = moveWin.getOrDefault(entry.getKey(), 0);
					int loss = moveLoss.getOrDefault(entry.getKey(), 0);
					entry.setValue((double)(win - loss) / playCount);
				}
//				System.out.println("WINS   " + moveWin);
//				System.out.println("LOSSES " + moveLoss);
//				System.out.println("VALUES " + validMoves);
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
