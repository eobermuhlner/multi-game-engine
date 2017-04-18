package ch.obermuhlner.game.javafx;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ch.obermuhlner.game.engine.DirectGameEngine;
import ch.obermuhlner.game.engine.GameEngine;
import ch.obermuhlner.game.engine.GameEngine.Side;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

public abstract class AbstractRectBoard extends AbstractBoard {

	private static final int SIZE = 30;

	private static final Color LIGHT_BACKGROUND_COLOR = Color.rgb(181, 136, 99);
	private static final Color DARK_BACKGROUND_COLOR = Color.rgb(240, 217, 181);

	protected final GameEngine gameEngine;
	
	protected final int boardWidth;
	protected final int boardHeight;

	protected BlackWhiteGameField fields[];
	
	public AbstractRectBoard(int boardWidth, int boardHeight, String gameName) {
		this.gameEngine = createGameEngine(gameName);
		this.boardWidth = boardWidth;
		this.boardHeight = boardHeight;

		setupBoard(createBoard());
	}

	private GameEngine createGameEngine(String gameName) {
		GameEngine gameEngine = new DirectGameEngine();
		gameEngine.setGame(gameName);
		gameEngine.setStartPosition();
		return gameEngine;
	}

	protected Node createBoard() {
		fields = new BlackWhiteGameField[boardWidth * boardHeight];
		
		GridPane gridPane = new GridPane();

		for (int y = 0; y < boardHeight; y++) {
			for (int x = 0; x < boardWidth; x++) {
				Color beige = ((x+y) % 2 == 0) ? LIGHT_BACKGROUND_COLOR : DARK_BACKGROUND_COLOR;
				BlackWhiteGameField field = new BlackWhiteGameField(SIZE, beige);
				fields[toIndex(x, y)] = field;
				gridPane.add(field, x, y);
				
				String move = toMove(x, y);
				
				field.setOnMouseClicked(event -> {
					Side sideToMove = gameEngine.getSideToMove();
					field.setSide(sideToMove);
					lastMoveProperty.set(move);
					gameEngine.move(move);
					scoreProperty.set(gameEngine.getScore());
					
					invalidateAllMoves();
					opponentMove();
				});
			}
		}
		
		updateValidMoves();
		updateGameInfo();

		return gridPane;
	}
	
	protected Side getPosition(int x, int y) {
		return fields[toIndex(x, y)].getSide();
	}
	
	private void invalidateAllMoves() {
		for (int index = 0; index < fields.length; index++) {
			if (fields[index] != null) {
				fields[index].setDisable(true);
			}
		}
	}

	private void updateValidMoves() {
		List<String> validMoves = gameEngine.getValidMoves();
		Set<Integer> validMoveIndexes = new HashSet<>();
		for (String move : validMoves) {
			int index = moveToIndex(move);
			validMoveIndexes.add(index);
		}
		
		for (int index = 0; index < fields.length; index++) {
			if (fields[index] != null) {
				fields[index].setDisable(!validMoveIndexes.contains(index));
			}
		}
	}

	protected abstract String toMove(int x, int y);

	protected abstract int moveToIndex(String move);

	protected int toIndex(int x, int y) {
		return x + y * boardWidth;
	}

	protected void opponentMove() {
		updateGameInfo();
		if (gameEngine.isFinished()) {
			return;
		}
		
		new Thread(() -> {
			Side sideToMove = gameEngine.getSideToMove();
			String opponentMove = gameEngine.bestMove();
			Platform.runLater(() -> {
				lastMoveProperty.set(opponentMove);
				gameEngine.move(opponentMove);
				scoreProperty.set(gameEngine.getScore());

				int moveIndex = moveToIndex(opponentMove);
				fields[moveIndex].setSide(sideToMove);

				updateGameInfo();
				updateValidMoves();
			});
		}).start();
	}

	private void updateGameInfo() {
		boolean finished = gameEngine.isFinished();
		if (finished) {
			nextPlayerProperty.set("");
			winnerProperty.set(String.valueOf(gameEngine.getWinner()));
		} else {
			nextPlayerProperty.set(String.valueOf(gameEngine.getSideToMove()));
		}
	}

	protected static String toString(Side side) {
		switch(side) {
		case White:
			return "X";
		case Black:
			return "O";
		case None:
			return " ";
		}
		
		throw new IllegalArgumentException("Unknown side: " + side);
	}

}
