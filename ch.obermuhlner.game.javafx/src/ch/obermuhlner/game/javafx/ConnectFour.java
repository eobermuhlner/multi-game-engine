package ch.obermuhlner.game.javafx;


import ch.obermuhlner.game.engine.GameEngine.Side;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

public class ConnectFour extends AbstractRectBoard {
	
	public ConnectFour() {
		super("connectfour", 7, 6, (x, y) -> {
			int size = 50;
			Color backgroundColor = ((x+y) % 2 == 0) ? Color.DARKGREY : Color.GREY;
			Shape background = new Rectangle(size, size, backgroundColor);
			Circle whiteStone = new Circle(size * 0.4, Color.BLUE);
			Circle blackStone = new Circle(size * 0.4, Color.RED);
			return new BlackWhiteGameField(50, background, whiteStone, blackStone);
		});
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

