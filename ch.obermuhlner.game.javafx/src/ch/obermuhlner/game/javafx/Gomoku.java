package ch.obermuhlner.game.javafx;

public class Gomoku extends AbstractRectBoard {
	
	public Gomoku() {
		super("gomoku", 19, 19, 30);
	}
	
	protected String toMove(int x, int y) {
		char charX = (char) ('a' + x);
		char charY = (char) ('a' + y);
		return String.valueOf(charX) + String.valueOf(charY);
	}
	
	protected int moveToIndex(String move) {
		int x = move.charAt(0) - 'a';
		int y = move.charAt(1) - 'a';
		return x + y * boardWidth;
	}
}

