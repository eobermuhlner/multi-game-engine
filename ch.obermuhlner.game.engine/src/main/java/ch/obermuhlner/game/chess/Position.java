package ch.obermuhlner.game.chess;

import ch.obermuhlner.game.Side;
import ch.obermuhlner.util.CheckArgument;

public class Position {
	private final Piece piece;
	private final int x;
	private final int y;
	private final Side side;

	public Position(Piece piece, Side side, int x, int y) {
		CheckArgument.isTrue(x >= 0 && x <= 7, "x " + x);
		CheckArgument.isTrue(y >= 0 && y <= 7, "y " + y);

		this.piece = piece;
		this.x = x;
		this.y = y;
		this.side = side;
	}
	
	public Piece getPiece() {
		return piece;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public Side getSide() {
		return side;
	}
	
	public char getCharacter() {
		return piece.getCharacter(side);
	}
	
	public String getPositionString() {
		return Chess.toPositionString(x, y);
	}

	@Override
	public String toString() {
		return String.valueOf(getCharacter()) + getPositionString();
	}
}