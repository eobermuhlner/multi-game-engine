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

		int playCount = 10000;
		for (Entry<String, Double> entry : validMoves.entrySet()) {
			for (int i = 0; i < playCount; i++) {
				Side winner = randomPlay(entry.getKey());
				if (winner == sideToMove) {
					int count = moveWin.getOrDefault(entry.getKey(), 0);
					moveWin.put(entry.getKey(), count + 1);
					//moveWin.merge(entry.getKey(), 1, (key, value) -> value + 1);
				}
				if (winner == sideToMove.otherSide()) {
					int count = moveLoss.getOrDefault(entry.getKey(), 0);
					moveLoss.put(entry.getKey(), count + 1);
					//moveLoss.merge(entry.getKey(), 1, (key, value) -> value + 1);
				}
			}
		}

		for (Entry<String, Double> entry : validMoves.entrySet()) {
			int win = moveWin.getOrDefault(entry.getKey(), 0);
			int loss = moveLoss.getOrDefault(entry.getKey(), 0);
			entry.setValue((double)(win - loss) / playCount);
		}
		
		//System.out.println(validMoves);
		String bestMove = RandomUtil.pickRandom(random, validMoves);
		return bestMove;
	}

	private Side randomPlay(String move) {
		RandomEngine randomEngine = new RandomEngine(game.clone(), random);
		
		while(!randomEngine.getGame().isFinished()) {
			randomEngine.getGame().move(randomEngine.bestMove());
		}
		
		return randomEngine.getGame().getWinner();
	}

}
