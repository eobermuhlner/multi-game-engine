package ch.obermuhlner.game.tictactoe;

import ch.obermuhlner.game.Board;
import ch.obermuhlner.game.Side;

public class TicTacToeBoard implements Board<TicTacToe> {

	private final Side[] data = new Side[9];
	
	private Side sideToMove = Side.White;

	@Override
	public void setStartPosition() {
		for (int i = 0; i < data.length; i++) {
			data[i] = null;
		}
	}
	
	@Override
	public void setState(String state) {
		for (int i = 0; i < data.length; i++) {
			data[i] = null;
		}
		
		String[] split = state.split(" +");
		
		if (split.length > 0) {
			for (int i = 0; i < split[0].length(); i++) {
				char c = split[0].charAt(i);
				switch (c) {
				case 'X':
					data[i] = Side.White;
					break;
				case 'O':
					data[i] = Side.Black;
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
		
		for (int i = 0; i < data.length; i++) {
			switch(data[i]) {
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
		int x = toCoord(move.charAt(0));
		int y = toCoord(move.charAt(1));
		
		data[x + y * 3] = sideToMove;
		sideToMove = sideToMove.otherSide();
	}
	
	public Side getPosition(int x, int y) {
		return data[x + y*3];
	}

	private int toCoord(char c) {
		return Character.getNumericValue(c);
	}

	@Override
	public Board<TicTacToe> clone() {
		TicTacToeBoard board = new TicTacToeBoard();

		for (int i = 0; i < this.data.length; i++) {
			board.data[i] = data[i];
		}
		board.sideToMove = sideToMove;
		
		return board;
	}

	public String toMove(int x, int y) {
		return String.valueOf(x) + String.valueOf(y);
	}

}
