package ch.obermuhlner.game.javafx;

import ch.obermuhlner.game.engine.GameEngine.Side;

public class ConnectFour extends AbstractRectBoard {
	
	public ConnectFour() {
		super(7, 6, "connectfour");
	}
	
	protected String toMove(int x, int y) {
		return String.valueOf(x + 1);
	}
	
	protected int moveToIndex(String move) {
		int x = Integer.parseInt(move) - 1;
		for (int y = boardHeight - 1; y >= 0; y--) {
			Side side = getPosition(x, y);
			if (side == Side.None) {
				return toIndex(x, y);
			}
		}
		return -1;
	}
}

