package ch.obermuhlner.game.tictactoe;

import java.util.HashMap;
import java.util.Map;

import ch.obermuhlner.game.Engine;
import ch.obermuhlner.game.Game;
import ch.obermuhlner.game.Side;
import ch.obermuhlner.game.engine.random.RandomEngine;

public class TicTacToe implements Game<TicTacToe> {

	private static final char[] LETTERS = { 'a', 'b', 'c' }; 
	
	private final Side[] board = new Side[9];
	
	private Side sideToMove = Side.White;

	@Override
	public void setStartPosition() {
		for (int i = 0; i < board.length; i++) {
			board[i] = null;
		}
	}
	
	@Override
	public void setState(String state) {
		for (int i = 0; i < board.length; i++) {
			board[i] = null;
		}
		
		String[] split = state.split(" +");
		
		if (split.length > 0) {
			for (int i = 0; i < split[0].length(); i++) {
				char c = split[0].charAt(i);
				switch (c) {
				case 'X':
					board[i] = Side.White;
					break;
				case 'O':
					board[i] = Side.Black;
					break;
				}
			}
		}
		
		if (split.length > 1) {
			if (split[1].equals("O")) {
				sideToMove = Side.Black;
			}
			sideToMove = Side.White;
		}
	}

	@Override
	public String getState() {
		StringBuilder state = new StringBuilder();
		
		for (int i = 0; i < board.length; i++) {
			switch(board[i]) {
			case White:
				state.append("X");
				break;
			case Black:
				state.append("O");
				break;
			default:
				state.append("-");
			}
		}
		
		state.append(" ");
		state.append(sideToMove);
		
		return state.toString();
	}

	@Override
	public Side getSideToMove() {
		return sideToMove;
	}
	
	@Override
	public void move(String move) {
		int x = letterToInt(move.charAt(0));
		int y = Character.getNumericValue(move.charAt(1)) - 1;
		
		board[x + y * 3] = sideToMove;
		sideToMove = sideToMove.otherSide();
	}
	
	private static int letterToInt(char letter) {
		for (int i = 0; i < LETTERS.length; i++) {
			if (letter == LETTERS[i]) {
				return i;
			}
		}
		
		throw new IllegalArgumentException("Unknown position letter: " + letter);
	}

	public Side getPosition(int x, int y) {
		return board[x + y*3];
	}

	@Override
	public Map<String, Double> getAllMoves() {
		Map<String, Double> moves = new HashMap<>();
		
		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 3; x++) {
				if (getPosition(x, y) == null) {
					double value = 1.0;
					moves.put(toMove(x, y), value);
				}
			}
		}

		return moves;
	}

	@Override
	public boolean isValid(String move) {
		return true;
	}

	@Override
	public boolean isFinished() {
		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 3; x++) {
				if (getPosition(x, y) == null) {
					return false;
				}
			}
		}

		return true;
	}

	@Override
	public Side getWinner() {
		Side winner;
		
		for (int i = 0; i < 3; i++) {
			winner = getWinner(i, 0, i, 1, i, 2);
			if (winner != null) {
				return winner;
			}
			
			winner = getWinner(0, i, 1, i, 2, i);
			if (winner != null) {
				return winner;
			}
		}
		
		winner = getWinner(0, 0, 1, 1, 2, 2);
		if (winner != null) {
			return winner;
		}

		winner = getWinner(0, 2, 1, 1, 2, 0);
		if (winner != null) {
			return winner;
		}
		
		return null;
	}

	private Side getWinner(int x1, int y1, int x2, int y2, int x3, int y3) {
		Side side = getPosition(x1, y1);
		if (side == null) {
			return null;
		}
		
		if (getPosition(x2, y2) == side && getPosition(x3, y3) == side) {
			return side;
		}
		
		return null;
	}

	@Override
	public Game<TicTacToe> clone() {
		TicTacToe game = new TicTacToe();

		for (int i = 0; i < this.board.length; i++) {
			game.board[i] = board[i];
		}
		game.sideToMove = sideToMove;
		
		return game;
	}

	public String toMove(int x, int y) {
		return String.valueOf(LETTERS[x]) + String.valueOf(y+1);
	}

	public static void main(String[] args) {
		Engine<TicTacToe> engine = new RandomEngine<>(new TicTacToe());
		
		while (!engine.getGame().isFinished()) {
			String move = engine.bestMove();
			System.out.println("MOVE " + move);
			engine.getGame().move(move);
		}
		
		System.out.println("WINNER " + engine.getGame().getWinner());
	}
}
