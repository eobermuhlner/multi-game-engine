package ch.obermuhlner.game.tictactoe;

import java.util.HashMap;
import java.util.Map;

import ch.obermuhlner.game.Board;
import ch.obermuhlner.game.Engine;
import ch.obermuhlner.game.Game;
import ch.obermuhlner.game.Side;
import ch.obermuhlner.game.engine.random.RandomEngine;

public class TicTacToe implements Game<TicTacToe> {

	@Override
	public Board<TicTacToe> createBoard() {
		return new TicTacToeBoard();
	}
	
	@Override
	public Map<String, Double> getAllMoves(Board<TicTacToe> board) {
		Map<String, Double> moves = new HashMap<>();
		
		if (board instanceof TicTacToeBoard) {
			TicTacToeBoard ticTacToeBoard = (TicTacToeBoard) board;

			for (int y = 0; y < 3; y++) {
				for (int x = 0; x < 3; x++) {
					if (ticTacToeBoard.getPosition(x, y) == null) {
						double value = 1.0;
						moves.put(ticTacToeBoard.toMove(x, y), value);
					}
				}
			}
		}
		
		return moves;
	}

	@Override
	public boolean isValid(Board<TicTacToe> board, String move) {
		return true;
	}

	@Override
	public boolean isFinished(Board<TicTacToe> board) {
		if (board instanceof TicTacToeBoard) {
			TicTacToeBoard ticTacToeBoard = (TicTacToeBoard) board;
		
			for (int y = 0; y < 3; y++) {
				for (int x = 0; x < 3; x++) {
					if (ticTacToeBoard.getPosition(x, y) == null) {
						return false;
					}
				}
			}
		}

		return true;
	}

	@Override
	public boolean isStaleMate(Board<TicTacToe> board) {
		return isFinished(board) && getWinner(board) == null;
	}

	@Override
	public Side getWinner(Board<TicTacToe> board) {
		if (board instanceof TicTacToeBoard) {
			TicTacToeBoard ticTacToeBoard = (TicTacToeBoard) board;

			Side winner;
			
			for (int i = 0; i < 3; i++) {
				winner = getWinner(ticTacToeBoard, i, 0, i, 1, i, 2);
				if (winner != null) {
					return winner;
				}
				
				winner = getWinner(ticTacToeBoard, 0, i, 1, i, 2, i);
				if (winner != null) {
					return winner;
				}
			}
			
			winner = getWinner(ticTacToeBoard, 0, 0, 1, 1, 2, 2);
			if (winner != null) {
				return winner;
			}

			winner = getWinner(ticTacToeBoard, 0, 2, 1, 1, 2, 0);
			if (winner != null) {
				return winner;
			}
		}
		
		return null;
	}

	private Side getWinner(TicTacToeBoard ticTacToeBoard, int x1, int y1, int x2, int y2, int x3, int y3) {
		Side side = ticTacToeBoard.getPosition(x1, y1);
		if (side == null) {
			return null;
		}
		
		if (ticTacToeBoard.getPosition(x2, y2) == side && ticTacToeBoard.getPosition(x3, y3) == side) {
			return side;
		}
		
		return null;
	}
	
	public static void main(String[] args) {
		Engine<TicTacToe> engine = new RandomEngine<>(new TicTacToe());
		
		while (!engine.isFinished()) {
			String move = engine.bestMove();
			System.out.println("MOVE " + move);
			engine.move(move);
		}
		
		System.out.println("WINNER " + engine.getWinner());
	}
}
