package ch.obermuhlner.game.javafx;

public class TicTacToe extends AbstractRectBoard {

	public TicTacToe() {
		super("tictactoe", 3, 3, 100);
	}
	
	protected String toMove(int x, int y) {
		return String.valueOf(toIndex(x, y) + 1);
	}
	
	protected int moveToIndex(String move) {
		return Integer.parseInt(move) - 1;
	}

}

