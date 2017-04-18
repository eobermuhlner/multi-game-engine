package ch.obermuhlner.game.javafx;

public class TicTacToe extends AbstractRectBoard {

	public TicTacToe() {
		super(3, 3, 100, "tictactoe");
	}
	
	protected String toMove(int x, int y) {
		return String.valueOf(toIndex(x, y) + 1);
	}
	
	protected int moveToIndex(String move) {
		return Integer.parseInt(move) - 1;
	}

}

