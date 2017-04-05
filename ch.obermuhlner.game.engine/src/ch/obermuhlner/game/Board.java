package ch.obermuhlner.game;

public interface Board<G extends Game<G>> {
	
	void setStartPosition();
	
	void setState(String state);
	
	String getState();
	
	Side getSideToMove();
	
	void move(String move);
	
	Board<G> clone();

}
