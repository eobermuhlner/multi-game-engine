package ch.obermuhlner.game.engine.lookup;

import java.util.ArrayList;
import java.util.List;

import ch.obermuhlner.game.Engine;
import ch.obermuhlner.game.Game;
import ch.obermuhlner.game.LookupTable;
import ch.obermuhlner.game.StoppableCalculation;
import ch.obermuhlner.game.impl.TrivialCalculation;

public class LookupEngine<G extends Game> implements Engine<G> {

	private Engine<G> engine;
	
	private final List<LookupTable<G>> lookupTables = new ArrayList<>();

	public LookupEngine(Engine<G> engine) {
		this.engine = engine;
	}
	
	public void addLookupTable(LookupTable<G> lookupTable) {
		lookupTables.add(lookupTable);
	}
	
	@Override
	public G getGame() {
		return engine.getGame();
	}

	@Override
	public String bestMove() {
		String bestMove = lookupBestMove();
		if (bestMove != null) {
			return bestMove;
		}
		return engine.bestMove();
	}

	@Override
	public StoppableCalculation<String> bestMove(long milliseconds) {
		String bestMove = lookupBestMove();
		if (bestMove != null) {
			return new TrivialCalculation<String>(() -> bestMove);
		}

		return engine.bestMove(milliseconds);
	}

	private String lookupBestMove() {
		G game = engine.getGame();
		for (LookupTable<G> lookupTable : lookupTables) {
			String bestMove = lookupTable.bestMove(game);
			if (bestMove != null) {
				return bestMove;
			}
		}
		
		return null;
	}

}
