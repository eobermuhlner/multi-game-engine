package ch.obermuhlner.game.tictactoe;

import java.util.HashMap;
import java.util.Map;

import ch.obermuhlner.game.Engine;
import ch.obermuhlner.game.Game;
import ch.obermuhlner.game.Side;
import ch.obermuhlner.game.engine.random.MonteCarloEngine;

public class TicTacToe implements Game {

	private static final char[] LETTERS = { 'a', 'b', 'c' }; 
	
	private final Side[] board = new Side[9];
	
	private Side sideToMove = Side.White;

	@Override
	public void setStartPosition() {
		for (int i = 0; i < board.length; i++) {
			board[i] = null;
		}
		
		sideToMove = Side.White;
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
			Side side = toSide(split[1].charAt(0));
			if (side != null) {
				sideToMove = side;
			}
		}
	}

	@Override
	public String getState() {
		StringBuilder state = new StringBuilder();
		
		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 3; x++) {
				Side position = getPosition(x, y);
				state.append(toString(position, "-"));
			}
			if (y < 2) {
				state.append("/");
			}
		}
		
		state.append(" ");
		state.append(sideToMove);
		
		return state.toString();
	}
	
	@Override
	public String getDiagram() {
		StringBuilder diagram = new StringBuilder();
		
		for (int y = 0; y < 3; y++) {
			diagram.append("+---+---+---+\n");
			for (int x = 0; x < 3; x++) {
				Side position = getPosition(x, y);
				diagram.append("| ");
				diagram.append(toString(position, " "));
				diagram.append(" ");
			}
			diagram.append("|\n");
		}
		diagram.append("+---+---+---+\n");
		
		return diagram.toString();
	}

	private Side toSide(char c) {
		switch (c) {
		case 'X':
			return Side.White;
		case 'O':
			return Side.Black;
		}
		
		return null;
	}
	
	private String toString(Side side, String defaultString) {
		if (side == null) {
			return defaultString;
		}
		
		switch(side) {
		case White:
			return "X";
		case Black:
			return "O";
		}
		
		throw new IllegalArgumentException("Unknown side: " + side);
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
		Side winner = getWinner();
		if (winner != null) {
			return true;
		}
		
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
	public TicTacToe clone() {
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

	@Override
	public String toString() {
		return getState();
	}

	public static void main(String[] args) {
		//Engine<TicTacToe> engine = new RandomEngine<>(new TicTacToe());
		Engine<TicTacToe> engine = new MonteCarloEngine<>(new TicTacToe());

		while (!engine.getGame().isFinished()) {
			String move = engine.bestMove();
			System.out.println("MOVE " + move);
			engine.getGame().move(move);
		}

		System.out.println(engine.getGame().getDiagram());
		System.out.println("WINNER " + engine.getGame().getWinner());
	}
}
