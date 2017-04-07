package ch.obermuhlner.game.gomoku;

import java.util.HashMap;
import java.util.Map;

import ch.obermuhlner.game.Engine;
import ch.obermuhlner.game.Side;
import ch.obermuhlner.game.app.GameCommandLine;
import ch.obermuhlner.game.engine.random.MonteCarloEngine;

public class Gomoku extends AbstractStonesInARow {
	
	public Gomoku() {
		this(19, 19, 5, false);
	}

	public Gomoku(int boardWidth, int boardHeight, int winCount, boolean exactWin) {
		super(boardWidth, boardHeight, winCount, exactWin);
	}

	@Override
	public Map<String, Double> getAllMoves() {
		Map<String, Double> allMoves = new HashMap<>();
		
		for (int y = 0; y < boardHeight; y++) {
			for (int x = 0; x < boardWidth; x++) {
				if (getPosition(x, y) != Side.None) {
					addPositionIfFree(allMoves, x-1, y-1);
					addPositionIfFree(allMoves, x-1, y+0);
					addPositionIfFree(allMoves, x-1, y+1);

					addPositionIfFree(allMoves, x+0, y-1);
					addPositionIfFree(allMoves, x+0, y+1);

					addPositionIfFree(allMoves, x+1, y-1);
					addPositionIfFree(allMoves, x+1, y+0);
					addPositionIfFree(allMoves, x+1, y+1);
				}
			}
		}
		
		if (allMoves.isEmpty()) {
			double value = 1.0;
			allMoves.put(toMove(boardWidth/2+1, boardHeight/2+1), value);
		}
		
		return allMoves;
	}

	private void addPositionIfFree(Map<String, Double> allMoves, int x, int y) {
		if (x < 0 || x >= boardWidth || y < 0 || y >= boardHeight) {
			return;
		}
		
		if (getPosition(x, y) != Side.None) {
			return;
		}
		
		double value = 1.0;
		allMoves.put(toMove(x, y), value);
	}

	private String toMove(int x, int y) {
		return String.valueOf(LETTERS[x]) + String.valueOf(LETTERS[y]);
	}

	@Override
	public void move(String move) {
		int x = letterToInt(move.charAt(0));
		int y = letterToInt(move.charAt(1));

		setPosition(x, y, sideToMove);

		sideToMove = sideToMove.otherSide();
	}

	@Override
	public String getDiagram() {
		StringBuilder diagram = new StringBuilder();

		for (int y = 0; y < boardHeight; y++) {
			for (int x = 0; x < boardWidth; x++) {
				Side position = getPosition(x, y);
				diagram.append(toDiagramString(position, "."));
				diagram.append(" ");
			}
			diagram.append(" ");
			diagram.append(LETTERS[y]);
			diagram.append("\n");
		}
		diagram.append("\n");
		for (int x = 0; x < boardWidth; x++) {
			diagram.append(LETTERS[x]);
			diagram.append(" ");
		}
		diagram.append("\n");
		
		return diagram.toString();
	}

	@Override
	public Gomoku cloneGame() {
		return (Gomoku) super.cloneGame(new Gomoku(boardWidth, boardHeight, winCount, exactWin));
	}

	public static void main(String[] args) {
		Engine<Gomoku> engine = new MonteCarloEngine<>(new Gomoku());
		//Engine<Gomoku> engine = new RandomEngine<>(new Gomoku());
		GameCommandLine.playGame(engine);
	}
}
