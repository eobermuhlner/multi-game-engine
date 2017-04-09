package ch.obermuhlner.game.engine.random;

import ch.obermuhlner.game.Engine;
import ch.obermuhlner.game.Game;
import ch.obermuhlner.game.StoppableCalculation;

public class MinMaxEngine<G extends Game> implements Engine<G> {

	private final G game;

	private final long defaultCalculationMilliseconds;

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
		return bestMove(defaultCalculationMilliseconds).get();
	}

	@Override
	public StoppableCalculation<String> bestMove(long milliseconds) {
		TimedCalculation<String> calculation = new TimedCalculation<String>(milliseconds) {
			@Override
			protected boolean calculateChunk(long remainingMillis) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			protected String calculateResult() {
				// TODO Auto-generated method stub
				return null;
			}
		};
		
		new Thread(calculation).start();
		return calculation;
	}

}
