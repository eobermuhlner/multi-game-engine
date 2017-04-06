package ch.obermuhlner.game.gomoku;

import java.util.HashMap;
import java.util.Map;

import ch.obermuhlner.game.Engine;
import ch.obermuhlner.game.Side;
import ch.obermuhlner.game.app.GameCommandLine;
import ch.obermuhlner.game.engine.random.RandomEngine;

public class ConnectFour extends AbstractStonesInARow {

	public ConnectFour() {
		this(7, 6);
	}

	public ConnectFour(int boardWidth, int boardHeight) {
		super(boardWidth, boardHeight, 4, true);
	}

	@Override
	public Map<String, Double> getAllMoves() {
		Map<String, Double> allMoves = new HashMap<>();

		for (int x = 0; x < boardWidth; x++) {
			int y = findFreeY(x);
			if (y >= 0) {
				double value = 1.0;
				allMoves.put(toMove(x), value);
			}
		}
		
		return allMoves;
	}
	
	@Override
	public void move(String move) {
		int x = Integer.parseInt(move) - 1;
		int y = findFreeY(x);
		
		setPosition(x, y, sideToMove);
		
		sideToMove = sideToMove.otherSide();
	}
	
	private String toMove(int x) {
		return String.valueOf(x + 1);
	}

	private int findFreeY(int x) {
		for (int y = 0; y < boardHeight; y++) {
			if (getPosition(x, y) == Side.None) {
				return y;
			}
		}
		
		return -1;
	}

	@Override
	public String getDiagram() {
		StringBuilder diagram = new StringBuilder();

		for (int y = 0; y < boardHeight; y++) {
			for (int x = 0; x < boardWidth; x++) {
				Side position = getPosition(x, boardHeight - y - 1);
				diagram.append(toDiagramString(position, "."));
				diagram.append(" ");
			}
			diagram.append("\n");
		}
		diagram.append("\n");
		for (int x = 0; x < boardWidth; x++) {
			diagram.append(x + 1);
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
		//Engine<FourWins> engine = new MonteCarloEngine<>(new FourWins());
		Engine<ConnectFour> engine = new RandomEngine<>(new ConnectFour());
		GameCommandLine.playGame(engine);
	}
}
