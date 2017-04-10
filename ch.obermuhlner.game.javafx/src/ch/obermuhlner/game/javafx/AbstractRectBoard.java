package ch.obermuhlner.game.javafx;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ch.obermuhlner.game.engine.DirectGameEngine;
import ch.obermuhlner.game.engine.GameEngine;
import ch.obermuhlner.game.engine.GameEngine.Side;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

public abstract class AbstractRectBoard {

	protected final GameEngine gameEngine;
	
	protected final int boardWidth;
	protected final int boardHeight;

	protected final GridPane gridPane;

	protected final Button[] buttonFields;
	
	protected Side sideToMove = Side.White;

	public AbstractRectBoard(int boardWidth, int boardHeight, String gameName) {
		this.gameEngine = createGameEngine(gameName);
		this.boardWidth = boardWidth;
		this.boardHeight = boardHeight;
		
		gridPane = new GridPane();
		buttonFields = new Button[boardWidth * boardHeight];
		
		setupBoard();
	}

	private GameEngine createGameEngine(String gameName) {
		GameEngine gameEngine = new DirectGameEngine();
		gameEngine.setGame(gameName);
		gameEngine.setStartPosition();
		return gameEngine;
	}

	protected void setupBoard() {
		for (int y = 0; y < boardHeight; y++) {
			for (int x = 0; x < boardHeight; x++) {
				Button buttonField = new Button(" ");
				buttonField.setPrefWidth(26);
				buttonFields[toIndex(x, y)] = buttonField;
				gridPane.add(buttonField, x, y);
				
				String move = toMove(x, y);
				
				buttonField.setOnAction(event -> {
					buttonField.setText(toString(sideToMove));
					gameEngine.move(move);
					sideToMove = sideToMove.otherSide();
					
					opponentMove();
					updateValidMoves();
				});
			}
		}
		
		updateValidMoves();
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
		if (gameEngine.isFinished()) {
			return;
		}
		
		String opponentMove = gameEngine.bestMove();
		gameEngine.move(opponentMove);
		int moveIndex = moveToIndex(opponentMove);
		buttonFields[moveIndex].setText(toString(sideToMove));
		
		sideToMove = sideToMove.otherSide();
	}

	public Node getBoard() {
		return gridPane;
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
