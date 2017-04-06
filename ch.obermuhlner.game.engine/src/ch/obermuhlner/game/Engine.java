package ch.obermuhlner.game;

public interface Engine<G extends Game> {

	G getGame();
	
	String bestMove();
	
}
