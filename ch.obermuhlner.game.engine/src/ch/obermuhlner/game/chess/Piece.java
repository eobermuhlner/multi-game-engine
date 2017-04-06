package ch.obermuhlner.game.chess;

import ch.obermuhlner.game.Side;

public enum Piece {
	
	Pawn('p', 1, 4, 2) {
		public double getValue(Side side, int x, int y) {
			double value = super.getValue(side, x, y);
			value *= PAWN_VALUE_Y[getPawnLine(side, y)];
			value *= PAWN_VALUE_X[x];
			return value;
		}		
	},
	Knight('n', 3, 8, 8) {
		public double getValue(Side side, int x, int y) {
			double value = super.getValue(side, x, y);
			value *= KNIGHT_VALUE_XY[x];
			value *= KNIGHT_VALUE_XY[y];
			return value;
		}		
	},
	Bishop('b', 3, 13, 4),
	Rook('r', 5, 14, 4),
	Queen('q', 9, 27, 8),
	King('k', 4, 8, 8);
	
	private char character;
	private double value;
	private int maxMoves;
	private int maxAttacks;

	Piece(char character, double value, int maxMoves, int maxAttacks) {
		this.character = character;
		this.value = value;
		this.maxMoves = maxMoves;
		this.maxAttacks = maxAttacks;
	}

	public char getCharacter() {
		return character;
	}
	
	public char getCharacter(Side side) {
		return side == Side.White ? getWhiteCharacter() : getBlackCharacter();
	}
	
	public char getBlackCharacter() {
		return character;
	}

	public char getWhiteCharacter() {
		return Character.toUpperCase(character);
	}

	public double getValue() {
		return value;
	}
	
	public double getValue(Side side, int x, int y) {
		return getValue();
	}

	public int getMaxMoves() {
		return maxMoves;
	}
	
	public int getMaxAttacks() {
		return maxAttacks;
	}

	private static int getPawnLine(Side side, int y) {
		switch(side) {
		case White:
			return y;
		case Black:
			return 7 - y;
		}
		throw new IllegalArgumentException("Unknown side: " + side);
	}
	
	private static final double PAWN_VALUE_X[] = { 1.0, 1.02, 1.05, 1.08, 1.08, 1.05, 1.02, 1.0 };
	private static final double PAWN_VALUE_Y[] = { 1.0, 1.1, 1.3, 1.6, 2.0, 2.5, 3.1, 3.8 };
	private static final double KNIGHT_VALUE_XY[] = { 1.0, 1.01, 1.03, 1.05, 1.05, 1.03, 1.01, 1.0 };

	public static Piece ofCharacter(char c) {
		for (Piece piece : Piece.values()) {
			if (piece.character == Character.toLowerCase(c)) {
				return piece;
			}
		}
		return null;
	}
}
