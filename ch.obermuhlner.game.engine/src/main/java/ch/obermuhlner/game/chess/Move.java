package ch.obermuhlner.game.chess;

import ch.obermuhlner.util.CheckArgument;

public class Move {
	private static final double KILL_VALUE_FACTOR = 5;
	private static final double CONVERT_VALUE_FACTOR = 5;

	private final Position source;
	private final int targetX;
	private final int targetY;
	private final Position killOrCastle;
	private final Piece convert;
	private final double value;
	
	public Move(Position source, int targetX, int targetY, Position killOrCastle) {
		this(source, targetX, targetY, killOrCastle, null);
	}

	public Move(Position source, int targetX, int targetY, Position killOrCastle, Piece convert) {
		CheckArgument.isTrue(targetX >= 0 && targetX <= 7, "targetX " + targetX);
		CheckArgument.isTrue(targetY >= 0 && targetY <= 7, "targetY " + targetY);
		
		this.source = source;
		this.targetX = targetX;
		this.targetY = targetY;
		this.killOrCastle = killOrCastle;
		this.convert = convert;
		
		value = calculateMoveValue();
	}
	
	public Position getSource() {
		return source;
	}
	
	public int getTargetX() {
		return targetX;
	}
	
	public int getTargetY() {
		return targetY;
	}

	public Position getKill() {
		if (killOrCastle != null && killOrCastle.getSide() != source.getSide()) {
			return killOrCastle;
		}
		return null;
	}
	
	public Position getCastle() {
		if (killOrCastle != null && killOrCastle.getSide() == source.getSide()) {
			return killOrCastle;
		}
		return null;
	}
	
	public Piece getConvert() {
		return convert;
	}

	public String getTargetPositionString() {
		return Chess.toPositionString(targetX, targetY);
	}
	
	public double getValue() {
		return value; 
	}
	
	private double calculateMoveValue() {
		double result = 1.0;
		
		result -= source.getPiece().getValue(source.getSide(), source.getX(), source.getY());
		result += source.getPiece().getValue(source.getSide(), targetX, targetY);

		Position kill = getKill();
		if (kill != null) {
			result += kill.getPiece().getValue(kill.getSide(), kill.getX(), kill.getY()) * KILL_VALUE_FACTOR;
		}
		Position castle = getKill();
		if (castle != null) {
			result += castle.getPiece().getValue(castle.getSide(), castle.getX(), castle.getY());
		}
		if (convert != null) {
			result += convert.getValue(source.getSide(), source.getX(), source.getY()) * CONVERT_VALUE_FACTOR;
		}
		return result;
	}

	@Override
	public String toString() {
		return toNotationString();
	}
	
	public String toUciString() {
		StringBuilder result = new StringBuilder();

		result.append(getSource().getPositionString());
		result.append(getTargetPositionString());
		if (convert != null) {
			result.append(convert.getCharacter());
		}
		
		return result.toString();
	}
	
	public String toNotationString() {
		StringBuilder result = new StringBuilder();
		result.append(source);
		if (getKill() != null) {
			result.append("x");
			result.append(getKill().getCharacter());
		}
		if (getCastle() != null) {
			result.append("-");
			result.append(getCastle().getCharacter());
		}
		result.append(Chess.toPositionString(targetX, targetY));
		if (convert != null) {
			result.append("=");
			result.append(convert.getCharacter(source.getSide()));
		}
		result.append("(");
		result.append(String.format("%4.3f", getValue()));
		result.append(")");
		
		return result.toString();
	}
}