package ch.obermuhlner.game.connectfour;

import java.util.ArrayList;
import java.util.List;

import ch.obermuhlner.game.Engine;
import ch.obermuhlner.game.Side;
import ch.obermuhlner.game.app.GameCommandLine;
import ch.obermuhlner.game.engine.random.MinMaxEngine;
import ch.obermuhlner.game.impl.AbstractStonesInARow;
import ch.obermuhlner.util.Tuple2;

public class ConnectFour extends AbstractStonesInARow {

	public ConnectFour() {
		this(7, 6);
	}

	public ConnectFour(int boardWidth, int boardHeight) {
		super(boardWidth, boardHeight, 4, false);
	}

	@Override
	public List<Tuple2<String, Double>> getAllMoves() {
		List<Tuple2<String, Double>> allMoves = new ArrayList<>();

		for (int x = 0; x < boardWidth; x++) {
			int y = findFreeY(x);
			if (y >= 0) {
				double value = 1.0;
				allMoves.add(Tuple2.of(toMove(x), value));
			}
		}
		
		return allMoves;
	}
	
	@Override
	public void move(String move) {
		int x = Integer.parseInt(move) - 1;
		int y = findFreeY(x);
		
		move(x, y);
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
	public ConnectFour cloneGame() {
		return (ConnectFour) super.cloneGame(new ConnectFour(boardWidth, boardHeight));
	}

	public static void main(String[] args) {
		Engine<ConnectFour> engine = new MinMaxEngine<>(new ConnectFour());
		//Engine<ConnectFour> engine = new MonteCarloEngine<>(new ConnectFour());
		//Engine<ConnectFour> engine = new RandomEngine<>(new ConnectFour());
		GameCommandLine.playGame(engine);
	}
}
