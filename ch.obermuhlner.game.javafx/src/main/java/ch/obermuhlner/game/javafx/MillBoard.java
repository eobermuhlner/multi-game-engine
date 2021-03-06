package ch.obermuhlner.game.javafx;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ch.obermuhlner.game.engine.DirectGameEngine;
import ch.obermuhlner.game.engine.GameEngine;
import ch.obermuhlner.game.engine.GameEngine.Side;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class MillBoard extends AbstractBoard {

	private static final String A1 = "a1";
	private static final String D1 = "d1";
	private static final String G1 = "g1";

	private static final String B2 = "b2";
	private static final String D2 = "d2";
	private static final String F2 = "f2";
	
	private static final String C3 = "c3";
	private static final String D3 = "d3";
	private static final String E3 = "e3";
	
	private static final String A4 = "a4";
	private static final String B4 = "b4";
	private static final String C4 = "c4";
	private static final String E4 = "e4";
	private static final String F4 = "f4";
	private static final String G4 = "g4";

	private static final String C5 = "c5";
	private static final String D5 = "d5";
	private static final String E5 = "e5";

	private static final String B6 = "b6";
	private static final String D6 = "d6";
	private static final String F6 = "f6";
	
	private static final String A7 = "a7";
	private static final String D7 = "d7";
	private static final String G7 = "g7";

	private static final int FIELD_SIZE = 50;
	private static final int FIELD_PLUS_MARGIN_SIZE = FIELD_SIZE + 0;

	private static final Color BACKGROUND_COLOR = Color.rgb(181, 136, 99);

	private final GameEngine gameEngine;

	private final Map<String, BlackWhiteGameField> fields = new HashMap<>();

	private final Map<String, Set<String>> possibleTargets = new HashMap<>();
	private final Set<String> possibleKillingTargets = new HashSet<>();
	private final Set<String> possibleKills = new HashSet<>();
	
	public MillBoard() {
		gameEngine = new DirectGameEngine();
		gameEngine.setGame("mill");
		gameEngine.setStartPosition();
		
		setupBoard(createBoard());
	}

	private Node createBoard() {
		StackPane stackPane = new StackPane();
		
		stackPane.getChildren().add(createBoardBackground());
		
		Group fieldGroup = new Group();
		stackPane.getChildren().add(fieldGroup);
		
		addField(fieldGroup, A1, 0, 0);
		addField(fieldGroup, D1, 3, 0);
		addField(fieldGroup, G1, 6, 0);

		addField(fieldGroup, B2, 1, 1);
		addField(fieldGroup, D2, 3, 1);
		addField(fieldGroup, F2, 5, 1);

		addField(fieldGroup, C3, 2, 2);
		addField(fieldGroup, D3, 3, 2);
		addField(fieldGroup, E3, 4, 2);

		addField(fieldGroup, A4, 0, 3);
		addField(fieldGroup, B4, 1, 3);
		addField(fieldGroup, C4, 2, 3);
		addField(fieldGroup, E4, 4, 3);
		addField(fieldGroup, F4, 5, 3);
		addField(fieldGroup, G4, 6, 3);

		addField(fieldGroup, C5, 2, 4);
		addField(fieldGroup, D5, 3, 4);
		addField(fieldGroup, E5, 4, 4);

		addField(fieldGroup, B6, 1, 5);
		addField(fieldGroup, D6, 3, 5);
		addField(fieldGroup, F6, 5, 5);

		addField(fieldGroup, A7, 0, 6);
		addField(fieldGroup, D7, 3, 6);
		addField(fieldGroup, G7, 6, 6);
		
		updateValidMoves();
		updateGameInfo();

		return stackPane;
	}
	
	private Node createBoardBackground() {
		Canvas canvas = new Canvas();
		int width = FIELD_PLUS_MARGIN_SIZE * 7;
		int height = FIELD_PLUS_MARGIN_SIZE * 7;

		canvas.setWidth(width);
		canvas.setHeight(height);
		
		GraphicsContext gc = canvas.getGraphicsContext2D();
		gc.setFill(BACKGROUND_COLOR);
		gc.fillRect(0, 0, width, height);

		gc.setStroke(Color.BLACK);

		// horizontal
		strokeMillLine(gc, 0, 0, 6, 0);
		strokeMillLine(gc, 1, 1, 5, 1);
		strokeMillLine(gc, 2, 2, 4, 2);

		strokeMillLine(gc, 0, 3, 2, 3);
		strokeMillLine(gc, 4, 3, 6, 3);

		strokeMillLine(gc, 2, 4, 4, 4);
		strokeMillLine(gc, 1, 5, 5, 5);
		strokeMillLine(gc, 0, 6, 6, 6);
		
		// vertical
		strokeMillLine(gc, 0, 0, 0, 6);
		strokeMillLine(gc, 1, 1, 1, 5);
		strokeMillLine(gc, 2, 2, 2, 4);

		strokeMillLine(gc, 3, 0, 3, 2);
		strokeMillLine(gc, 3, 4, 3, 6);

		strokeMillLine(gc, 4, 2, 4, 4);
		strokeMillLine(gc, 5, 1, 5, 5);
		strokeMillLine(gc, 6, 0, 6, 6);

		return canvas;
	}

	private void strokeMillLine(GraphicsContext gc, int x1, int y1, int x2, int y2) {
		double half = FIELD_PLUS_MARGIN_SIZE / 2.0;
		gc.strokeLine(x1 * FIELD_PLUS_MARGIN_SIZE + half, y1 * FIELD_PLUS_MARGIN_SIZE + half, x2 * FIELD_PLUS_MARGIN_SIZE + half, y2 * FIELD_PLUS_MARGIN_SIZE + half);
	}

	private void addField(Group group, String fieldCoordinate, int x, int y) {
		BlackWhiteGameField field = new BlackWhiteGameField(FIELD_SIZE, Color.TRANSPARENT, 0.25);
		group.getChildren().add(field);
		
		field.relocate(x * FIELD_PLUS_MARGIN_SIZE, (7 - y) * FIELD_PLUS_MARGIN_SIZE);
		fields.put(fieldCoordinate, field);
	}

	private void invalidateAllMoves() {
		for (BlackWhiteGameField field : fields.values()) {
			field.setDisable(true);
		}
	}

	private void updateValidMoves() {
		possibleKills.clear();
		possibleTargets.clear();
		possibleKillingTargets.clear();
		
		List<String> validMoves = gameEngine.getValidMoves();
		System.out.println("VALID " + validMoves);
		for (String move : validMoves) {
			prepareGuiMove(move);
		}
	}

	private void prepareGuiMove(String move) {
		String[] split = splitMove(move);
		String source = split[0];
		String target = split[1];
		String kill = split[2];
		
		if (source != null) {
			prepareGuiDragMove(source, target, kill);
		} else {
			prepareGuiSetMove(target, kill);
		}
	}

	private void prepareGuiDragMove(String source, String target, String kill) {
		BlackWhiteGameField sourceField = fields.get(source);
		sourceField.setGlow(Color.BLUE);
		sourceField.setDisable(false);
		// TODO prepare drag to target
		
		possibleTargets.computeIfAbsent(source, key -> new HashSet<>()).add(target);
		if (kill != null) {
			possibleKillingTargets.add(target);
			possibleKills.add(kill);
		}

		sourceField.setOnMouseClicked(event -> {
			System.out.println("DRAG MOVE SOURCE " + source);
			sourceField.setSide(Side.None);
			invalidateAllMoves();

			System.out.println("TARGETS " + possibleTargets);
			for (String possibleTarget : possibleTargets.get(source)) {
				prepareGuiDragMoveTarget(source, possibleTarget);
			}
		});
	}

	private void prepareGuiDragMoveTarget(String source, String target) {
		BlackWhiteGameField targetField = fields.get(target);
		targetField.setGlow(Color.GREEN);
		targetField.setDisable(false);

		if (possibleKillingTargets.contains(target)) {
			targetField.setOnMouseClicked(event -> {
				Side sideToMove = gameEngine.getSideToMove();
				targetField.setSide(sideToMove);
				
				prepareGuiAllKillMoves(source, target);
			});
		} else {
			targetField.setOnMouseClicked(event -> {
				Side sideToMove = gameEngine.getSideToMove();
				targetField.setSide(sideToMove);
				String move = toMove(source, target, null);
				lastMoveProperty.set(move);
				gameEngine.move(move);
				scoreProperty.set(gameEngine.getScore());
				
				invalidateAllMoves();
				opponentMove();
			});
		}
	}


	private void prepareGuiSetMove(String target, String kill) {
		BlackWhiteGameField targetField = fields.get(target);
		targetField.setGlow(Color.GREEN);
		targetField.setDisable(false);

		if (kill == null) {
			targetField.setOnMouseClicked(event -> {
				Side sideToMove = gameEngine.getSideToMove();
				targetField.setSide(sideToMove);
				lastMoveProperty.set(target);
				gameEngine.move(target);
				scoreProperty.set(gameEngine.getScore());
				
				invalidateAllMoves();
				opponentMove();
			});
		} else {
			possibleKills.add(kill);

			targetField.setOnMouseClicked(event -> {
				Side sideToMove = gameEngine.getSideToMove();
				targetField.setSide(sideToMove);
				
				prepareGuiAllKillMoves(null, target);
			});
		}
	}

	private void prepareGuiAllKillMoves(String source, String target) {
		invalidateAllMoves();
		
		for (String kill : possibleKills) {
			BlackWhiteGameField killField = fields.get(kill);
			killField.setGlow(Color.RED);
			killField.setDisable(false);
			
			killField.setOnMouseClicked(event -> {
				killField.setSide(Side.None);
				String move = toMove(source, target, kill);
				lastMoveProperty.set(move);
				gameEngine.move(move);
				scoreProperty.set(gameEngine.getScore());
				
				invalidateAllMoves();
				opponentMove();
			});
		}
	}

	private String toMove(String source, String target, String kill) {
		String move = "";
		
		if (source != null) {
			move += source;
		}
		
		move += target;
		
		if (kill != null) {
			move += "x";
			move += kill;
		}
		
		return move;
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

				executeMove(opponentMove, sideToMove);

				updateGameInfo();
				updateValidMoves();
			});
		}).start();
	}

	private void executeMove(String move, Side sideToMove) {
		String[] split = splitMove(move);
		executeMove(split[0], split[1], split[2], sideToMove);
	}

	private String[] splitMove(String move) {
		String first = move.substring(0, 2);
		
		String source;
		String target;
		String kill;
		
		if (move.length() <= 2) {
			source = null;
			target = first;
			kill = null;
		} else {
			if (move.charAt(2) == 'x') {
				source = null;
				target = first;
				kill = move.substring(3, 5);
			} else {
				source = first;
				target = move.substring(2, 4);
				kill = null;
				
				if (move.length() > 5) {
					// charAt(4) should be 'x'
					kill = move.substring(5, 7);
				}
			}
		}
		
		return new String[] { source, target, kill };
	}
	
	private void executeMove(String source, String target, String kill, Side sideToMove) {
		if (source != null) {
			fields.get(source).setSide(Side.None);
		}
		
		fields.get(target).setSide(sideToMove);
		
		if (kill != null) {
			fields.get(kill).setSide(Side.None);
		}
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
}
