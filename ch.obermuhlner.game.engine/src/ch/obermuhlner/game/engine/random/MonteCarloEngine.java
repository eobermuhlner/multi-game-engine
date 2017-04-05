package ch.obermuhlner.game.engine.random;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import ch.obermuhlner.game.Engine;
import ch.obermuhlner.game.Game;
import ch.obermuhlner.game.Side;

public class MonteCarloEngine<G extends Game<G>> implements Engine<G> {

	private final Random random = new Random();

	private G game;

	public MonteCarloEngine(G game) {
		this.game = game;
	}
	
	@Override
	public G getGame() {
		return game;
	}

	@Override
	public String bestMove() {
		Side sideToMove = game.getSideToMove();
		Map<String, Double> validMoves = game.getValidMoves();
		
		Map<String, Integer> moveWin = new HashMap<>();
		Map<String, Integer> moveLoss = new HashMap<>();

		int playCount = 10;
		for (int i = 0; i < playCount; i++) {
			for (Entry<String, Double> entry : validMoves.entrySet()) {
				Side winner = randomPlay(entry.getKey());
				if (winner == sideToMove) {
					moveWin.compute(entry.getKey(), (key, value) -> value == null ? 1 : value + 1);
				}
				if (winner == sideToMove.otherSide()) {
					moveLoss.compute(entry.getKey(), (key, value) -> value == null ? 1 : value + 1);
				}
			}
		}

		for (Entry<String, Double> entry : validMoves.entrySet()) {
			entry.setValue(0.0);
		}

		String bestMove = RandomUtil.pickRandom(random, validMoves);
		return bestMove;
	}

	private Side randomPlay(String move) {
		RandomEngine randomEngine = new RandomEngine(game.clone());
		
		while(!randomEngine.getGame().isFinished()) {
			randomEngine.getGame().move(randomEngine.bestMove());
		}
		
		return randomEngine.getGame().getWinner();
	}

}
