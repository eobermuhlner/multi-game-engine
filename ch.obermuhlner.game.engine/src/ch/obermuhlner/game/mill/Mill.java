package ch.obermuhlner.game.mill;

import java.util.HashMap;
import java.util.Map;

import ch.obermuhlner.game.Engine;
import ch.obermuhlner.game.Game;
import ch.obermuhlner.game.Side;
import ch.obermuhlner.game.app.GameCommandLine;
import ch.obermuhlner.game.engine.random.RandomEngine;

public class Mill implements Game {

	private static final char[] LETTERS = { 'a', 'b', 'c', 'd', 'e', 'f', 'g' };
	
	private static final int A1 = toIndex("a1");
	private static final int D1 = toIndex("d1");
	private static final int G1 = toIndex("g1");

	private static final int B2 = toIndex("b2");
	private static final int D2 = toIndex("d2");
	private static final int F2 = toIndex("f2");
	
	private static final int C3 = toIndex("c3");
	private static final int D3 = toIndex("d3");
	private static final int E3 = toIndex("e3");
	
	private static final int A4 = toIndex("a4");
	private static final int B4 = toIndex("b4");
	private static final int C4 = toIndex("c4");
	private static final int E4 = toIndex("e4");
	private static final int F4 = toIndex("f4");
	private static final int G4 = toIndex("g4");

	private static final int C5 = toIndex("c5");
	private static final int D5 = toIndex("d5");
	private static final int E5 = toIndex("e5");

	private static final int B6 = toIndex("b6");
	private static final int D6 = toIndex("d6");
	private static final int F6 = toIndex("f6");
	
	private static final int A7 = toIndex("a7");
	private static final int D7 = toIndex("d7");
	private static final int G7 = toIndex("g7");

	private static final int[][] VALID_MOVES = new int[7*7][];
	
	static {
		VALID_MOVES[A7] = new int[] { A4, D7 };
		VALID_MOVES[D7] = new int[] { A7, G7, D6 };
		VALID_MOVES[G7] = new int[] { D7, G4 };
		VALID_MOVES[B6] = new int[] { D6, B4 };
		VALID_MOVES[D6] = new int[] { D7, B6, F6, D5 };
		VALID_MOVES[F6] = new int[] { D6, F4 };
		VALID_MOVES[C5] = new int[] { D5, C4 };
		VALID_MOVES[D5] = new int[] { D6, C5, E4 };
		VALID_MOVES[E5] = new int[] { D5, E4 };
		VALID_MOVES[A4] = new int[] { A7, B4, A1 };
		VALID_MOVES[B4] = new int[] { B6, A4, C4, B2 };
		VALID_MOVES[C4] = new int[] { C5, B4, C3 };
		VALID_MOVES[E4] = new int[] { E5, F4, E3 };
		VALID_MOVES[F4] = new int[] { F6, E4, G4, F2 };
		VALID_MOVES[G4] = new int[] { G7, F4, G1 };
		VALID_MOVES[C3] = new int[] { C4, D3 };
		VALID_MOVES[D3] = new int[] { C3, E3, D2 };
		VALID_MOVES[E3] = new int[] { E4, D3 };
		VALID_MOVES[B2] = new int[] { B4, D2 };
		VALID_MOVES[D2] = new int[] { D3, B2, F2, D1 };
		VALID_MOVES[F2] = new int[] { F4, D2 };
		VALID_MOVES[A1] = new int[] { A4, D1 };
		VALID_MOVES[D1] = new int[] { D2, A1, G1 };
		VALID_MOVES[G1] = new int[] { G4, D1 };
	}
	
	private Side[] board = new Side[7 * 7]; 

	private Side sideToMove;
	
	private int moveCount;
	
	public Mill() {
		setStartPosition();
	}
	
	@Override
	public void setStartPosition() {
		for (int index = 0; index < board.length; index++) {
			board[index] = Side.None;
		}
		
		sideToMove = Side.White;
		moveCount = 0;
	}

	@Override
	public void setState(String state) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getState() {
		StringBuilder state = new StringBuilder();
		
		int validIndex = 0;
		for (int index = board.length - 1; index > 0 ; index--) {
			if (isValidPosition(index)) {
				state.append(toString(board[index], "-"));
				validIndex++;
				if (validIndex % 3 == 0 && validIndex < 24) {
					state.append("/");
				}
			}
		}
		
		state.append(" ");
		state.append(toString(sideToMove, "-"));

		state.append(" ");
		state.append(moveCount);

		return state.toString();
	}

	@Override
	public String getDiagram() {
		StringBuilder diagram = new StringBuilder();
		diagram.append(String.format("%s-----------%s-----------%s 7\n", toBoardString(A7), toBoardString(D7), toBoardString(G7)));
		diagram.append(String.format("|           |           |\n"));
		diagram.append(String.format("|   %s-------%s-------%s   | 6\n", toBoardString(B6), toBoardString(D6), toBoardString(F6)));
		diagram.append(String.format("|   |       |       |   |\n"));
		diagram.append(String.format("|   |   %s---%s---%s   |   | 5\n", toBoardString(C5), toBoardString(D5), toBoardString(E5)));
		diagram.append(String.format("|   |   |       |   |   |\n"));
		diagram.append(String.format("%s---%s---%s       %s---%s---%s 4\n", toBoardString(A4), toBoardString(B4), toBoardString(C4), toBoardString(E4), toBoardString(F4), toBoardString(G4)));
		diagram.append(String.format("|   |   |       |   |   |\n"));
		diagram.append(String.format("|   |   %s---%s---%s   |   | 3\n", toBoardString(C3), toBoardString(D3), toBoardString(E3)));
		diagram.append(String.format("|   |       |       |   |\n"));
		diagram.append(String.format("|   %s-------%s-------%s   | 2\n", toBoardString(B2), toBoardString(D2), toBoardString(F2)));
		diagram.append(String.format("|           |           |\n"));
		diagram.append(String.format("%s-----------%s-----------%s 1\n", toBoardString(A1), toBoardString(D1), toBoardString(G1)));
		diagram.append(String.format("a   b   c   d   e   f   g\n"));
		return diagram.toString();
	}

	private String toBoardString(int index) {
		return toString(board[index], "+");
	}

	private String toString(Side side, String defaultString) {
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

	@Override
	public Side getSideToMove() {
		return sideToMove;
	}

	@Override
	public double getScore() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void move(String move) {
		int sourceIndex = toIndex(move.charAt(0), move.charAt(1));

		if (move.length() > 2) {
			int targetIndex = toIndex(move.charAt(2), move.charAt(3));

			if (move.length() > 5) {
				// charAt(4) should be 'x'
				int killIndex = toIndex(move.charAt(5), move.charAt(6));
				
				move(sourceIndex, targetIndex, killIndex);
				return;
			}

			move(sourceIndex, targetIndex);
			return;
		}
		
		move(sourceIndex);
	}
	
	private void move(int index) {
		board[index] = sideToMove;
		
		sideToMove = sideToMove.otherSide();
		moveCount++;
	}

	private void move(int sourceIndex, int targetIndex) {
		board[targetIndex] = board[sourceIndex];
		board[sourceIndex] = Side.None;
		
		sideToMove = sideToMove.otherSide();
		moveCount++;
	}

	private void move(int sourceIndex, int targetIndex, int killIndex) {
		board[targetIndex] = board[sourceIndex];
		board[sourceIndex] = Side.None;
		board[killIndex] = Side.None;
		
		sideToMove = sideToMove.otherSide();
		moveCount++;
	}

	private static int letterToInt(char letter) {
		for (int i = 0; i < LETTERS.length; i++) {
			if (letter == LETTERS[i]) {
				return i;
			}
		}
		
		throw new IllegalArgumentException("Unknown position letter: " + letter);
	}

	@Override
	public Map<String, Double> getAllMoves() {
		Map<String, Double> allMoves = new HashMap<>();
		
		if (isSetMode()) {
			// still in set-stones mode
			for (int index = 0; index < board.length; index++) {
				if (board[index] == Side.None && isValidPosition(index)) {
					double value = 1.0;
					allMoves.put(toMove(index), value);
					// TODO recognize mills!!! -> kill
				}
			}
		} else {
			int countWhite = 0;
			int countBlack = 0;
			for (int index = 0; index < board.length; index++) {
				switch (board[index]) {
				case White:
					countWhite++;
					break;
				case Black:
					countBlack++;
					break;
				case None:
					// do nothing
				}
			}
			
			boolean jumpMode = sideToMove == Side.White ? countWhite == 3 : countBlack == 3;
			if (jumpMode) {
				for (int index = 0; index < board.length; index++) {
					if (board[index] == Side.None && isValidPosition(index)) {
						double value = 1.0;
						allMoves.put(toMove(index), value);
						// TODO recognize mills!!! -> kill
					}
				}
			} else {
				for (int index = 0; index < board.length; index++) {
					if (board[index] == sideToMove) {
						addAllSourceTargetMoves(allMoves, index);
					}
				}
			}
		}
		
		return allMoves;
	}

	private void addAllSourceTargetMoves(Map<String, Double> allMoves, int source) {
		for (int target : VALID_MOVES[source]) {
			if (board[target] == Side.None) {
				double value = 1.0;
				allMoves.put(toMove(source, target), value);
			}
		}
	}

	private boolean isSetMode() {
		return moveCount < 9 * 2;
	}
	
	private String toMove(int index) {
		int x = index % 7;
		int y = index / 7;
		return String.valueOf(LETTERS[x]) + (y+1); 
	}
	
	private String toMove(int sourceIndex, int targetIndex) {
		return toMove(sourceIndex) + toMove(targetIndex);
	}

	@Override
	public boolean isValid(String move) {
		return true;
	}

	@Override
	public boolean isFinished() {
		return getWinner() != Side.None;
	}

	@Override
	public Side getWinner() {
		if (isSetMode()) {
			return Side.None;
		}
		
		int countWhite = 0;
		int countBlack = 0;
		for (int index = 0; index < board.length; index++) {
			switch (board[index]) {
			case White:
				countWhite++;
				break;
			case Black:
				countBlack++;
				break;
			case None:
				// do nothing
			}
		}

		if (countWhite < 3) {
			return Side.White;
		}
		if (countBlack < 3) {
			return Side.Black;
		}
		
		// TODO loss if cannot move
		
		return Side.None;
	}

	@Override
	public Mill cloneGame() {
		Mill game = new Mill();

		for (int i = 0; i < this.board.length; i++) {
			game.board[i] = board[i];
		}
		game.sideToMove = sideToMove;
		
		return game;
	}

	private boolean isValidPosition(int index) {
		return VALID_MOVES[index] != null;
	}
	
	private static int toIndex(String xy) {
		return toIndex(xy.charAt(0), xy.charAt(1));
	}
	
	private static int toIndex(char xChar, char yChar) {
		int x = letterToInt(xChar);
		int y = Character.getNumericValue(yChar) - 1;
		
		return toIndex(x, y);
	}

	private static int toIndex(int x, int y) {
		return x + y * 7;
	}

	public static void main(String[] args) {
		Engine<Mill> engine = new RandomEngine<>(new Mill());
		GameCommandLine.playGame(engine);
	}
}
