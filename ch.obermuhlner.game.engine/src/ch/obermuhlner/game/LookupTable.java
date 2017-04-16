package ch.obermuhlner.game;

public interface LookupTable<G extends Game> {
	
	String bestMove(G game);
}
