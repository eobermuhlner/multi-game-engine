package ch.obermuhlner.game.javafx;

import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class GomokuBoard extends AbstractRectBoard {
	
	private static final int FIELD_SIZE = 30;
	private static final double HALF_FIELD_SIZE = FIELD_SIZE / 2.0;
	private static final double DOT_SIZE = FIELD_SIZE / 8.0;

	private static final Color BACKGROUND_COLOR = Color.rgb(181, 136, 99);

	public GomokuBoard() {
		super("gomoku", 19, 19, (x, y) -> {
			return new BlackWhiteGameField(FIELD_SIZE, Color.TRANSPARENT);
		});
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
	
	@Override
	protected Node createBoard() {
		StackPane stackPane = new StackPane();

		stackPane.getChildren().add(createBoardBackground());
		stackPane.getChildren().add(super.createBoard());
		
		return stackPane;
	}

	private Node createBoardBackground() {
		Canvas canvas = new Canvas();
		int width = FIELD_SIZE * boardWidth;
		int height = FIELD_SIZE * boardHeight;

		canvas.setWidth(width);
		canvas.setHeight(height);
		
		GraphicsContext gc = canvas.getGraphicsContext2D();
		gc.setFill(BACKGROUND_COLOR);
		gc.fillRect(0, 0, width, height);

		gc.setStroke(Color.BLACK);
		for (int x = 0; x < boardWidth; x++) {
			gc.strokeLine(x*FIELD_SIZE + HALF_FIELD_SIZE, HALF_FIELD_SIZE, x*FIELD_SIZE + HALF_FIELD_SIZE, height - HALF_FIELD_SIZE);
		}
		for (int y = 0; y < boardWidth; y++) {
			gc.strokeLine(HALF_FIELD_SIZE, y*FIELD_SIZE + HALF_FIELD_SIZE, width - HALF_FIELD_SIZE, y*FIELD_SIZE + HALF_FIELD_SIZE);
		}
		gc.setFill(Color.BLACK);
		fillMarkerDot(gc, 3, 3);
		fillMarkerDot(gc, 3, 9);
		fillMarkerDot(gc, 3, 15);
		fillMarkerDot(gc, 9, 3);
		fillMarkerDot(gc, 9, 9);
		fillMarkerDot(gc, 9, 15);
		fillMarkerDot(gc, 15, 3);
		fillMarkerDot(gc, 15, 9);
		fillMarkerDot(gc, 15, 15);
		
		return canvas;
	}

	private void fillMarkerDot(GraphicsContext gc, int x, int y) {
		gc.fillOval(x*FIELD_SIZE + HALF_FIELD_SIZE - DOT_SIZE, y*FIELD_SIZE + HALF_FIELD_SIZE - DOT_SIZE, DOT_SIZE*2, DOT_SIZE*2);
	}
}

