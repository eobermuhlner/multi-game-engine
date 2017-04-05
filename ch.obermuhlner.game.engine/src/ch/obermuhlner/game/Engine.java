package ch.obermuhlner.game;

public interface Engine<G extends Game<G>> {

	G getGame();
	
	String bestMove();
	
}
