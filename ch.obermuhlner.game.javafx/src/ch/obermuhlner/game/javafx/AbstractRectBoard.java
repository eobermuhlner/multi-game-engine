package ch.obermuhlner.game.javafx;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ch.obermuhlner.game.engine.DirectGameEngine;
import ch.obermuhlner.game.engine.GameEngine;
import ch.obermuhlner.game.engine.GameEngine.Side;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

public abstract class AbstractRectBoard extends AbstractBoard {

	protected final GameEngine gameEngine;
	
	protected final int boardWidth;
	protected final int boardHeight;

	protected Button[] buttonFields;
	
	ExecutorService executor = Executors.newFixedThreadPool(1);
	
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
		buttonFields = new Button[boardWidth * boardHeight];
		
		GridPane gridPane = new GridPane();

		for (int y = 0; y < boardHeight; y++) {
			for (int x = 0; x < boardHeight; x++) {
				Button buttonField = new Button(" ");
				buttonField.setPrefWidth(26);
				buttonFields[toIndex(x, y)] = buttonField;
				gridPane.add(buttonField, x, y);
				
				String move = toMove(x, y);
				
				buttonField.setOnAction(event -> {
					Side sideToMove = gameEngine.getSideToMove();
					buttonField.setText(toString(sideToMove));
					lastMoveProperty.set(move);
					gameEngine.move(move);
					
					invalidateAllMoves();
					opponentMove();
				});
			}
		}
		
		updateValidMoves();
		updateGameInfo();

		return gridPane;
	}
	
	private void invalidateAllMoves() {
		for (int index = 0; index < buttonFields.length; index++) {
			if (buttonFields[index] != null) {
				buttonFields[index].setDisable(true);
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
		
		for (int index = 0; index < buttonFields.length; index++) {
			if (buttonFields[index] != null) {
				buttonFields[index].setDisable(!validMoveIndexes.contains(index));
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
		
		executor.submit(() -> {
			Side sideToMove = gameEngine.getSideToMove();
			String opponentMove = gameEngine.bestMove();
			Platform.runLater(() -> {
				lastMoveProperty.set(opponentMove);
				gameEngine.move(opponentMove);
				int moveIndex = moveToIndex(opponentMove);
				buttonFields[moveIndex].setText(toString(sideToMove));

				updateGameInfo();
				updateValidMoves();
			});
		});
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
