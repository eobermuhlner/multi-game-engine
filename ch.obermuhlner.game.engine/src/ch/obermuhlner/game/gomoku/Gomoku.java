package ch.obermuhlner.game.gomoku;

import java.util.HashMap;
import java.util.Map;

import ch.obermuhlner.game.Engine;
import ch.obermuhlner.game.Game;
import ch.obermuhlner.game.Side;
import ch.obermuhlner.game.app.GameCommandLine;
import ch.obermuhlner.game.engine.random.RandomEngine;

public class Gomoku implements Game {

	private static final char[] LETTERS = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's' }; 

	private final int boardSize;
	
	private final Side[] board;
	
	private final boolean exactWin;

	private Side sideToMove = Side.Black;

	public Gomoku() {
		this(19, false);
	}
	
	public Gomoku(int boardSize, boolean exactWin) {
		this.boardSize = boardSize;
		this.exactWin = exactWin;
		this.board = new Side[boardSize*boardSize];
		
		setStartPosition();
	}
	
	@Override
	public void setStartPosition() {
		for (int i = 0; i < board.length; i++) {
			board[i] = Side.None;
		}

		sideToMove = Side.Black;
	}
	
	@Override
	public void setState(String state) {
		setStartPosition();
		
		String[] split = state.split(" +");
		
		if (split.length > 0) {
			for (int i = 0; i < split[0].length(); i++) {
				char c = split[0].charAt(i);
				board[i] = toSide(c);
			}
		}
		
		if (split.length > 1) {
			sideToMove = toSide(split[1].charAt(0));
		}
	}

	@Override
	public String getState() {
		StringBuilder state = new StringBuilder();
		
		for (int y = 0; y < boardSize; y++) {
			int emptyCount = 0;
			for (int x = 0; x < boardSize; x++) {
				Side position = getPosition(x, y);
				if (position == Side.None) {
					emptyCount++;
				} else {
					if (emptyCount > 0) {
						state.append(emptyCount);
						emptyCount = 0;
					}
					state.append(toString(position, "-"));
				}
			}

			if (emptyCount > 0) {
				state.append(emptyCount);
			}
			
			if (y < boardSize - 1) {
				state.append("/");
			}
		}
		
		state.append(" ");
		state.append(toString(sideToMove, ""));
		
		return state.toString();
	}

	private Side toSide(char c) {
		switch (c) {
		case 'w':
			return Side.White;
		case 'b':
			return Side.Black;
		}
		
		return Side.None;
	}

	private String toString(Side side, String defaultString) {
		switch(side) {
		case White:
			return "w";
		case Black:
			return "b";
		case None:
			return defaultString;
		}
		
		throw new IllegalArgumentException("Unknown side: " + side);
	}

	private String toDiagramString(Side side, String defaultString) {
		switch(side) {
		case White:
			return "X";
		case Black:
			return "O";
		case None:
			return defaultString;
		}
		
		throw new IllegalArgumentException("Unknown side: " + side);
	}

	private static int letterToInt(char letter) {
		for (int i = 0; i < LETTERS.length; i++) {
			if (letter == LETTERS[i]) {
				return i;
			}
		}
		
		throw new IllegalArgumentException("Unknown position letter: " + letter);
	}

	private Side getPosition(int x, int y) {
		return board[x + y*boardSize];
	}

	@Override
	public String getDiagram() {
		StringBuilder diagram = new StringBuilder();

		for (int y = 0; y < boardSize; y++) {
			for (int x = 0; x < boardSize; x++) {
				Side position = getPosition(x, y);
				diagram.append(toDiagramString(position, "."));
				diagram.append(" ");
			}
			diagram.append(" ");
			diagram.append(LETTERS[y]);
			diagram.append("\n");
		}
		diagram.append("\n");
		for (int x = 0; x < boardSize; x++) {
			diagram.append(LETTERS[x]);
			diagram.append(" ");
		}
		diagram.append("\n");
		
		return diagram.toString();
	}

	@Override
	public Side getSideToMove() {
		return sideToMove;
	}

	@Override
	public void move(String move) {
		int x = letterToInt(move.charAt(0));
		int y = letterToInt(move.charAt(1));
		
		board[x + y * boardSize] = sideToMove;
		sideToMove = sideToMove.otherSide();
	}

	@Override
	public Map<String, Double> getAllMoves() {
		Map<String, Double> allMoves = new HashMap<>();
		
		for (int y = 0; y < boardSize; y++) {
			for (int x = 0; x < boardSize; x++) {
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
			allMoves.put(toMove(boardSize/2+1, boardSize/2+1), value);
		}
		
		return allMoves;
	}

	private void addPositionIfFree(Map<String, Double> allMoves, int x, int y) {
		if (x < 0 || x >= boardSize || y < 0 || y >= boardSize) {
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
	public boolean isValid(String move) {
		return true;
	}

	@Override
	public boolean isFinished() {
		Side winner = getWinner();
		return winner != Side.None;
	}

	@Override
	public Side getWinner() {
		Side winner;
		
		for (int i = 0; i < boardSize; i++) {
			// horizontal
			winner = getWinner(i, 0, 1, 0);
			if (winner != Side.None) {
				return winner;
			}
			
			// vertical
			winner = getWinner(0, i, 0, 1);
			if (winner != Side.None) {
				return winner;
			}

			// diagonal left side going down/right
			winner = getWinner(0, i, 1, 1);
			if (winner != Side.None) {
				return winner;
			}

			// diagonal top side going down/right
			winner = getWinner(i, 0, 1, 1);
			if (winner != Side.None) {
				return winner;
			}

			// diagonal left side going up/right
			winner = getWinner(0, i, 1, -1);
			if (winner != Side.None) {
				return winner;
			}

			// diagonal bottom side going up/right
			winner = getWinner(i, boardSize-1, 1, -11);
			if (winner != Side.None) {
				return winner;
			}
		}
		
		return Side.None;
	}
	
	private Side getWinner(int x, int y, int deltaX, int deltaY) {
		int whiteCount = 0;
		int blackCount = 0;
		
		int maxWhiteCount = 0;
		int maxBlackCount = 0;

		while (x >= 0 && x < boardSize && y < boardSize && y >= 0) {
			switch (getPosition(x, y)) {
			case Black:
				blackCount++;
				maxWhiteCount = Math.max(whiteCount, maxWhiteCount);
				whiteCount = 0;
				break;
			case White:
				whiteCount++;
				maxBlackCount = Math.max(blackCount, maxBlackCount);
				blackCount = 0;
				break;
			case None:
				maxWhiteCount = Math.max(whiteCount, maxWhiteCount);
				whiteCount = 0;
				maxBlackCount = Math.max(blackCount, maxBlackCount);
				blackCount = 0;
				break;
			}
		
			x += deltaX;
			y += deltaY;
		}
		
		if (exactWin) {
			if (maxWhiteCount == 5) {
				return Side.White;
			}
			if (maxBlackCount == 5) {
				return Side.Black;
			}
		} else {
			if (maxWhiteCount >= 5) {
				return Side.White;
			}
			if (maxBlackCount >= 5) {
				return Side.Black;
			}
		}
		return Side.None;
	}
	
	@Override
	public Gomoku clone() {
		Gomoku game = new Gomoku(boardSize, exactWin);
		
		for (int i = 0; i < this.board.length; i++) {
			game.board[i] = board[i];
		}
		game.sideToMove = sideToMove;

		return game;
	}

	@Override
	public String toString() {
		return getState();
	}

	public static void main(String[] args) {
		//Engine<Gomoku> engine = new MonteCarloEngine<>(new Gomoku());
		Engine<Gomoku> engine = new RandomEngine<>(new Gomoku());
		GameCommandLine.playGame(engine);
	}
}
