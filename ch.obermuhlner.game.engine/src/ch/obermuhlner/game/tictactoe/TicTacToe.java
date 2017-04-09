package ch.obermuhlner.game.tictactoe;

import java.util.ArrayList;
import java.util.List;

import ch.obermuhlner.game.Engine;
import ch.obermuhlner.game.Game;
import ch.obermuhlner.game.Side;
import ch.obermuhlner.game.app.GameCommandLine;
import ch.obermuhlner.game.engine.random.MinMaxEngine;
import ch.obermuhlner.util.Tuple2;

public class TicTacToe implements Game {

	private static final int[][] THREE_IN_A_ROW = {
			{ 0, 1, 2 },
			{ 3, 4, 5 },
			{ 6, 7, 8 },
			{ 0, 3, 6 },
			{ 1, 4, 7 },
			{ 2, 5, 8 },
			{ 0, 4, 8 },
			{ 2, 4, 6 }
	};
	
	private static final int[][] HEURISTIC_SCORE = {
			{    0, -10, -100, -1000 },
			{   10,   0,    0,     0 },
			{  100,   0,    0,     0 },
			{ 1000,   0,    0,     0 }
	};
	
	private final Side[] board = new Side[9];
	
	private Side sideToMove = Side.White;

	public TicTacToe() {
		setStartPosition();
	}
	
	@Override
	public void setStartPosition() {
		for (int i = 0; i < board.length; i++) {
			board[i] = Side.None;
		}
		
		sideToMove = Side.White;
	}
	
	@Override
	public void setState(String state) {
		setStartPosition();
		
		String[] split = state.split(" +");
		
		if (split.length > 0) {
			int boardIndex = 0;
			for (int i = 0; i < split[0].length(); i++) {
				char c = split[0].charAt(i);
				if (c != '/') {
					board[boardIndex++] = toSide(c);
				}
			}
		}
		
		if (split.length > 1) {
			sideToMove = toSide(split[1].charAt(0));
		}
	}

	@Override
	public String getState() {
		StringBuilder state = new StringBuilder();

		for (int index = 0; index < board.length; index++) {
			Side position = board[index];
			state.append(toString(position, "-"));
			if (index == 2 || index == 5) {
				state.append("/");
			}
		}
		
		state.append(" ");
		state.append(toString(sideToMove, "-"));
		
		return state.toString();
	}
	
	@Override
	public double getScore() {
		// https://kartikkukreja.wordpress.com/2013/03/30/heuristic-function-for-tic-tac-toe/
		double score = 0;

		for (int i = 0; i < THREE_IN_A_ROW.length; i++) {
			int whiteCount = 0;
			int blackCount = 0;
			for (int j = 0; j < 3; j++) {
				Side side = board[THREE_IN_A_ROW[i][j]];
				switch(side) {
				case White:
					whiteCount++;
					break;
				case Black:
					blackCount++;
					break;
				case None:
					// nothing
					break;
				}
			}
			score += HEURISTIC_SCORE[whiteCount][blackCount];
		}
		
		return score;
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
		
		return Side.None;
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
	public void move(String move) {
		int index = Integer.parseInt(move) - 1;
		
		board[index] = sideToMove;
		sideToMove = sideToMove.otherSide();
	}
	
	public Side getPosition(int x, int y) {
		return board[x + y*3];
	}

	@Override
	public List<Tuple2<String, Double>> getAllMoves() {
		List<Tuple2<String, Double>> allMoves = new ArrayList<>();
		
		for (int index = 0; index < board.length; index++) {
			Side position = board[index];
			if (position == Side.None) {
				double value = 1.0;
				allMoves.add(Tuple2.of(toMove(index), value));
			}
		}

		return allMoves;
	}

	@Override
	public boolean isValid(String move) {
		return true;
	}

	@Override
	public boolean isFinished() {
		Side winner = getWinner();
		if (winner != Side.None) {
			return true;
		}

		for (int index = 0; index < board.length; index++) {
			if (board[index] == Side.None) {
				return false;
			}
		}

		return true;
	}

	@Override
	public Side getWinner() {
		Side winner;
		
		for (int i = 0; i < 3; i++) {
			winner = getWinner(i, 0, i, 1, i, 2);
			if (winner != Side.None) {
				return winner;
			}
			
			winner = getWinner(0, i, 1, i, 2, i);
			if (winner != Side.None) {
				return winner;
			}
		}
		
		winner = getWinner(0, 0, 1, 1, 2, 2);
		if (winner != Side.None) {
			return winner;
		}

		winner = getWinner(0, 2, 1, 1, 2, 0);
		if (winner != Side.None) {
			return winner;
		}
		
		return Side.None;
	}

	private Side getWinner(int x1, int y1, int x2, int y2, int x3, int y3) {
		Side side = getPosition(x1, y1);
		if (side == Side.None) {
			return Side.None;
		}
		
		if (getPosition(x2, y2) == side && getPosition(x3, y3) == side) {
			return side;
		}
		
		return Side.None;
	}

	@Override
	public TicTacToe cloneGame() {
		TicTacToe game = new TicTacToe();

		for (int i = 0; i < this.board.length; i++) {
			game.board[i] = board[i];
		}
		game.sideToMove = sideToMove;
		
		return game;
	}

	public String toMove(int index) {
		return String.valueOf(index + 1);
	}

	@Override
	public String toString() {
		return getState();
	}

	public static void main(String[] args) {
		//Engine<TicTacToe> engine = new MonteCarloEngine<>(new TicTacToe());
		Engine<TicTacToe> engine = new MinMaxEngine<>(new TicTacToe());
		GameCommandLine.playGame(engine);
	}
}
