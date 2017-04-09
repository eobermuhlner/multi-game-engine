package ch.obermuhlner.game.mill;

import java.util.HashMap;
import java.util.Map;

import ch.obermuhlner.game.Engine;
import ch.obermuhlner.game.Game;
import ch.obermuhlner.game.Side;
import ch.obermuhlner.game.app.GameCommandLine;
import ch.obermuhlner.game.engine.random.MonteCarloEngine;
import ch.obermuhlner.util.CheckArgument;

public class Mill implements Game {

	private static final char[] LETTERS = { 'a', 'b', 'c', 'd', 'e', 'f', 'g' };
	
	private static final int A1 = 0;
	private static final int D1 = 1;
	private static final int G1 = 2;

	private static final int B2 = 3;
	private static final int D2 = 4;
	private static final int F2 = 5;
	
	private static final int C3 = 6;
	private static final int D3 = 7;
	private static final int E3 = 8;
	
	private static final int A4 = 9;
	private static final int B4 = 10;
	private static final int C4 = 11;
	private static final int E4 = 12;
	private static final int F4 = 13;
	private static final int G4 = 14;

	private static final int C5 = 15;
	private static final int D5 = 16;
	private static final int E5 = 17;

	private static final int B6 = 18;
	private static final int D6 = 19;
	private static final int F6 = 20;
	
	private static final int A7 = 21;
	private static final int D7 = 22;
	private static final int G7 = 23;

	private static final int CELL_COUNT = 24;

	private static final int[] GRID_TO_MOVES = new int[7*7];

	private static final int[][] VALID_MOVES = new int[CELL_COUNT][];

	private static final int[][][] MILLS = new int[CELL_COUNT][][];

	static {
		for (int i = 0; i < GRID_TO_MOVES.length; i++) {
			GRID_TO_MOVES[i] = -1;
		}
		GRID_TO_MOVES[toIndex("a7")] = A7;
		GRID_TO_MOVES[toIndex("d7")] = D7;
		GRID_TO_MOVES[toIndex("g7")] = G7;
		GRID_TO_MOVES[toIndex("b6")] = B6;
		GRID_TO_MOVES[toIndex("d6")] = D6;
		GRID_TO_MOVES[toIndex("f6")] = F6;
		GRID_TO_MOVES[toIndex("c5")] = C5;
		GRID_TO_MOVES[toIndex("d5")] = D5;
		GRID_TO_MOVES[toIndex("e5")] = E5;
		GRID_TO_MOVES[toIndex("a4")] = A4;
		GRID_TO_MOVES[toIndex("b4")] = B4;
		GRID_TO_MOVES[toIndex("c4")] = C4;
		GRID_TO_MOVES[toIndex("e4")] = E4;
		GRID_TO_MOVES[toIndex("f4")] = F4;
		GRID_TO_MOVES[toIndex("g4")] = G4;
		GRID_TO_MOVES[toIndex("c3")] = C3;
		GRID_TO_MOVES[toIndex("d3")] = D3;
		GRID_TO_MOVES[toIndex("e3")] = E3;
		GRID_TO_MOVES[toIndex("b2")] = B2;
		GRID_TO_MOVES[toIndex("d2")] = D2;
		GRID_TO_MOVES[toIndex("f2")] = F2;
		GRID_TO_MOVES[toIndex("a1")] = A1;
		GRID_TO_MOVES[toIndex("d1")] = D1;
		GRID_TO_MOVES[toIndex("g1")] = G1;

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
		
		MILLS[A7] = new int[][] { { D7, G7 }, { A4, A1} };
		MILLS[D7] = new int[][] { { A7, G7 }, { D6, D5 } };
		MILLS[G7] = new int[][] { { D7, A7 }, { G4, G1 } };
		MILLS[B6] = new int[][] { { D6, F6 }, { B4, B2 } };
		MILLS[D6] = new int[][] { { D7, D5 }, { B6, F6 } };
		MILLS[F6] = new int[][] { { D6, B6 }, { F4, F2 } };
		MILLS[C5] = new int[][] { { D5, E5 }, { C4, C3 } };
		MILLS[D5] = new int[][] { { D6, D7 }, { C5, E5 } };
		MILLS[E5] = new int[][] { { D5, C5 }, { E4, E3 } };
		MILLS[A4] = new int[][] { { A7, A1 }, { B4, C4 } };
		MILLS[B4] = new int[][] { { A4, C4 }, { B6, B2 } };
		MILLS[C4] = new int[][] { { B4, A4 }, { C5, C3 } };
		MILLS[E4] = new int[][] { { E5, E3 }, { F4, G4 } };
		MILLS[F4] = new int[][] { { F6, F2 }, { E4, G4 } };
		MILLS[G4] = new int[][] { { F4, E4 }, { G7, G1 } };
		MILLS[C3] = new int[][] { { C4, C5 }, { D3, E3 } };
		MILLS[D3] = new int[][] { { C3, E3 }, { D2, D1 } };
		MILLS[E3] = new int[][] { { D3, C3 }, { E4, E5 } };
		MILLS[B2] = new int[][] { { B4, B6 }, { D2, F2 } };
		MILLS[D2] = new int[][] { { B2, F2 }, { D3, D1 } };
		MILLS[F2] = new int[][] { { D2, B2 }, { F4, F6 } };
		MILLS[A1] = new int[][] { { A4, A7 }, { D1, G1 } };
		MILLS[D1] = new int[][] { { A1, G1 }, { D2, D3 } };
		MILLS[G1] = new int[][] { { D1, A1 }, { G4, G7 } };
	}

	private static final double MOVE_VALUE = 1.0;
	private static final double KILL_VALUE = 10.0;

	private Side[] board = new Side[CELL_COUNT]; 
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
		for (int index = 0; index < CELL_COUNT; index++) {
			state.append(toString(board[index], "-"));
			validIndex++;
			if (validIndex % 3 == 0 && validIndex < CELL_COUNT) {
				state.append("/");
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
		int firstIndex = toIndex(move.charAt(0), move.charAt(1));

		int sourceIndex;
		int targetIndex;
		int killIndex;
		
		if (move.length() <= 2) {
			sourceIndex = -1;
			targetIndex = firstIndex;
			killIndex = -1;
		} else {
			if (move.charAt(2) == 'x') {
				sourceIndex = -1;
				targetIndex = firstIndex;
				killIndex = toIndex(move.charAt(3), move.charAt(4));
			} else {
				sourceIndex = firstIndex;
				targetIndex = toIndex(move.charAt(2), move.charAt(3));
				killIndex = -1;
				
				if (move.length() > 5) {
					// charAt(4) should be 'x'
					killIndex = toIndex(move.charAt(5), move.charAt(6));
				}
			}
		}
		
		move(sourceIndex, targetIndex, killIndex);
	}
	
	private void move(int sourceIndex, int targetIndex, int killIndex) {
		if (sourceIndex >= 0) {
			CheckArgument.isTrue(board[sourceIndex] == sideToMove, () -> "Cannot be move source " + board[sourceIndex] + " : " + toMove(sourceIndex, targetIndex, killIndex));
			board[sourceIndex] = Side.None;
		}
		
		CheckArgument.isTrue(board[targetIndex] == Side.None, () -> "Cannot be move target " + board[targetIndex] + " : " + toMove(sourceIndex, targetIndex, killIndex));
		board[targetIndex] = sideToMove;
		
		if (killIndex >= 0) {
			CheckArgument.isTrue(board[killIndex] == sideToMove.otherSide(), () -> "Cannot kill " + board[killIndex] + " : " + toMove(sourceIndex, targetIndex, killIndex));
			CheckArgument.isTrue(isInMill(targetIndex, sideToMove), () -> "Cannot kill if not in mill : " + toMove(sourceIndex, targetIndex, killIndex) + " : " + getState());
			board[killIndex] = Side.None;
		}
		
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
			for (int target = 0; target < board.length; target++) {
				if (board[target] == Side.None) {
					if (isInMill(target, sideToMove)) {
						double value = KILL_VALUE;
						Side otherSide = sideToMove.otherSide();
						for (int killIndex = 0; killIndex < board.length; killIndex++) {
							if (board[killIndex] == sideToMove.otherSide() && !isInMill(killIndex, otherSide)) {
								allMoves.put(toKillingMove(target, killIndex), value);
							}
						}							
					} else {
						double value = MOVE_VALUE;
						allMoves.put(toMove(target), value);
					}
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
				for (int source = 0; source < board.length; source++) {
					if (board[source] == sideToMove) {
						addAllJumpMoves(allMoves, source);
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

	private void addAllJumpMoves(Map<String, Double> allMoves, int source) {
		for (int target = 0; target < board.length; target++) {
			if (board[target] == Side.None) {
				if (willBeInMill(source, target, sideToMove)) {
					double value = KILL_VALUE;
					Side otherSide = sideToMove.otherSide();
					for (int killIndex = 0; killIndex < board.length; killIndex++) {
						if (board[killIndex] == sideToMove.otherSide() && !isInMill(killIndex, otherSide)) {
							allMoves.put(toKillingMove(source, target, killIndex), value);
						}
					}							
				} else {
					double value = MOVE_VALUE;
					allMoves.put(toMove(source, target), value);
				}
			}
		}
	}

	private void addAllSourceTargetMoves(Map<String, Double> allMoves, int source) {
		for (int target : VALID_MOVES[source]) {
			if (board[target] == Side.None) {
				if (willBeInMill(source, target, sideToMove)) {
					double value = KILL_VALUE;
					Side otherSide = sideToMove.otherSide();
					for (int killIndex = 0; killIndex < board.length; killIndex++) {
						if (board[killIndex] == otherSide && !isInMill(killIndex, otherSide)) {
							allMoves.put(toKillingMove(source, target, killIndex), value);
						}
					}							
				} else {
					double value = MOVE_VALUE;
					allMoves.put(toMove(source, target), value);
				}
			}
		}
	}

	private boolean willBeInMill(int source, int target, Side side) {
		// make sure it is correct with stone moving from X-X to the left (becoming XX- but might return true!)
		Mill local = cloneGame();
		local.move(source, target, -1);
		return local.isInMill(target, side);
	}
	
	private boolean isInMill(int index, Side side) {
		for (int[] millCheck : MILLS[index]) {
			if (isInMill(millCheck, side)) {
				return true;
			}
		}
		return false;
	}

	private boolean isInMill(int[] millCheck, Side side) {
		for (int millIndex : millCheck) {
			if (board[millIndex] != side) {
				return false;
			}
		}
		return true;
	}

	private boolean isSetMode() {
		return moveCount < 9 * 2;
	}

	private String toMove(int sourceIndex, int targetIndex, int killIndex) {
		return toKillingMove(sourceIndex, targetIndex, killIndex);
	}
	
	private String toMove(int index) {
		if (index < 0) {
			return "-";
		}
		int x = index % 7;
		int y = index / 7;
		return String.valueOf(LETTERS[x]) + (y+1); 
	}

	private String toKillingMove(int targetIndex, int killIndex) {
		return toMove(targetIndex) + "x" + toMove(killIndex);
	}
	
	private String toMove(int sourceIndex, int targetIndex) {
		return toMove(sourceIndex) + toMove(targetIndex);
	}

	private String toKillingMove(int sourceIndex, int targetIndex, int killIndex) {
		return toMove(sourceIndex, targetIndex) + "x" + toMove(killIndex);
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
		
		if (getAllMoves().isEmpty()) {
			return sideToMove.otherSide();
		}
		
		return Side.None;
	}

	@Override
	public Mill cloneGame() {
		Mill game = new Mill();

		for (int i = 0; i < this.board.length; i++) {
			game.board[i] = board[i];
		}
		game.sideToMove = sideToMove;
		game.moveCount = moveCount;
		
		return game;
	}

	@Override
	public String toString() {
		return getState();
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
		Engine<Mill> engine = new MonteCarloEngine<>(new Mill());
		//Engine<Mill> engine = new RandomEngine<>(new Mill());
		GameCommandLine.playGame(engine);
	}
}
