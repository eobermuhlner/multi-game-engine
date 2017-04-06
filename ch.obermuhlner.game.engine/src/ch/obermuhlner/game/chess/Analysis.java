package ch.obermuhlner.game.chess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.obermuhlner.game.Side;

public class Analysis {
	private final Position[] positionBoard = new Position[64];
	
	private final Map<Position, List<Move>> positionMovesMap = new HashMap<>();
	private final Map<Position, List<Position>> positionAttacksMap = new HashMap<>();
	private final Map<Position, List<Position>> positionDefendsMap = new HashMap<>();

	private final Map<Position, List<Position>> positionAttackersMap = new HashMap<>();
	private final Map<Position, List<Position>> positionDefendersMap = new HashMap<>();
	
	private boolean whiteKingInCheck = false;
	private boolean blackKingInCheck = false;
	
	public Analysis(Chess board) {
		for (Position position : board.getPositions()) {
			positionBoard[position.getX() + position.getY() * 8] = position;
		}
		
		for (Position position : board.getPositions()) {
			analysePosition(position);
		}
		
		analyseKings(board.getPositions());
	}

	private void analyseKings(List<Position> positions) {
		positions.stream()
			.filter(position -> position.getPiece() == Piece.King)
			.filter(king -> !getAttackers(king).isEmpty())
			.forEach(king -> {
				if (king.getSide() == Side.White) {
					whiteKingInCheck = true;
				}
				if (king.getSide() == Side.Black) {
					blackKingInCheck = true;
				}
			});
	}

	public double getValue(Position position) {
		double value = position.getPiece().getValue(position.getSide(), position.getX(), position.getY());
		
		switch(position.getPiece()) {
		case Knight:
		case Bishop:
		case Rook:
		case Queen:
			value *= 1.0 + getMobilityFactor(position) * 0.1;
			break;
		default:
		}
		
		value *= 1.0 + getAttacksFactor(position) * 0.2;
		value *= 1.0 + getDefendsFactor(position) * 0.15;
		
		value *= 1.0 + getDefendedFactor(position) * 0.1;
		value *= 1.0 - getAttackedFactor(position) * 0.1;
		
		return value;
	}

	public double getValue(Move move) {
		double value = move.getValue();
		
		return value;
	}
	
	public boolean isCheck(Side side) {
		switch(side) {
		case Black:
			return blackKingInCheck;
		case White:
			return whiteKingInCheck;
		case None:
			throw new IllegalArgumentException("Side " + side + " not supported");
		}
		
		throw new IllegalArgumentException("Unknown side: " + side);
	}

	private double getMobilityFactor(Position position) {
		return (double) getMoves(position).size() / position.getPiece().getMaxMoves();
	}

	private double getAttacksFactor(Position position) {
		return (double) getAttacks(position).size() / position.getPiece().getMaxAttacks();
	}

	private double getDefendsFactor(Position position) {
		return (double) getDefends(position).size() / position.getPiece().getMaxAttacks();
	}

	private double getAttackedFactor(Position position) {
		return (double) getAttackers(position).size() / 16;
	}

	private double getDefendedFactor(Position position) {
		return (double) getDefenders(position).size() / 16;
	}

	private void analysePosition(Position position) {
		List<Move> moves = new ArrayList<>();
		List<Position> attacks = new ArrayList<>();
		List<Position> defends = new ArrayList<>();
		
		addAllMoves(position, moves, attacks, defends);
		
		positionMovesMap.put(position, moves);
		positionAttacksMap.put(position, attacks);
		positionDefendsMap.put(position, defends);

		for(Position attacked : attacks) {
			positionAttackersMap.computeIfAbsent(attacked, key -> new ArrayList<>()).add((position));
		}
		for(Position defended : defends) {
			positionDefendersMap.computeIfAbsent(defended, key -> new ArrayList<>()).add((position));
		}
	}

	public Position getPosition(int x, int y) {
		return positionBoard[x + y * 8];
	}
	
	public List<Move> getMoves(Position position) {
		return positionMovesMap.getOrDefault(position, Collections.emptyList());
	}

	public List<Position> getAttacks(Position attacker) {
		return positionAttacksMap.getOrDefault(attacker, Collections.emptyList());
	}

	public List<Position> getDefends(Position defender) {
		return positionDefendsMap.getOrDefault(defender, Collections.emptyList());
	}

	public List<Position> getAttackers(Position victim) {
		return positionAttackersMap.getOrDefault(victim, Collections.emptyList());
	}

	public List<Position> getDefenders(Position victim) {
		return positionDefendersMap.getOrDefault(victim, Collections.emptyList());
	}

	private void addAllMoves(Position position, List<Move> moves, List<Position> attacks, List<Position> defends) {
		switch(position.getPiece()) {
		case Pawn:
			addPawnMoves(position, moves, attacks, defends);
			break;
		case Knight:
			addKnightMoves(position, moves, attacks, defends);
			break;
		case Bishop:
			addBishopMoves(position, moves, attacks, defends);
			break;
		case Rook:
			addRookMoves(position, moves, attacks, defends);
			break;
		case Queen:
			addQueenMoves(position, moves, attacks, defends);
			break;
		case King:
			addKingMoves(position, moves, attacks, defends);
			break;
		}
	}

	private void addPawnMoves(Position position, List<Move> moves, List<Position> attacks, List<Position> defends) {
		int x = position.getX();
		int y = position.getY();

		int direction = Chess.getPawnDirection(position.getSide());
		
		if (addMovePawnIfFree(position, x, y + direction, moves, attacks, defends) && y == Chess.getPawnStart(position.getSide())) {
			addMovePawnIfFree(position, x, y + direction + direction, moves, attacks, defends);
		}
		addMovePawnMustKill(position, x+1, y + direction, moves, attacks, defends);
		addMovePawnMustKill(position, x-1, y + direction, moves, attacks, defends);
		
		// TODO add en-passant
		
	}
	
	private void addKnightMoves(Position position, List<Move> moves, List<Position> attacks, List<Position> defends) {
		int x = position.getX();
		int y = position.getY();

		addMove(position, x-2, y+1, moves, attacks, defends);
		addMove(position, x-1, y+2, moves, attacks, defends);
		addMove(position, x+1, y+2, moves, attacks, defends);
		addMove(position, x+2, y+1, moves, attacks, defends);
		addMove(position, x+2, y-1, moves, attacks, defends);
		addMove(position, x+1, y-2, moves, attacks, defends);
		addMove(position, x-1, y-2, moves, attacks, defends);
		addMove(position, x-2, y-1, moves, attacks, defends);
	}

	private void addBishopMoves(Position position, List<Move> moves, List<Position> attacks, List<Position> defends) {
		addRayMoves(position, -1, -1, moves, attacks, defends);
		addRayMoves(position, +1, -1, moves, attacks, defends);
		addRayMoves(position, -1, +1, moves, attacks, defends);
		addRayMoves(position, +1, +1, moves, attacks, defends);
	}

	private void addRookMoves(Position position, List<Move> moves, List<Position> attacks, List<Position> defends) {
		addRayMoves(position, -1, 0, moves, attacks, defends);
		addRayMoves(position, +1, 0, moves, attacks, defends);
		addRayMoves(position, 0, -1, moves, attacks, defends);
		addRayMoves(position, 0, +1, moves, attacks, defends);
	}

	private void addQueenMoves(Position position, List<Move> moves, List<Position> attacks, List<Position> defends) {
		addRayMoves(position, -1, 0, moves, attacks, defends);
		addRayMoves(position, +1, 0, moves, attacks, defends);
		addRayMoves(position, 0, -1, moves, attacks, defends);
		addRayMoves(position, 0, +1, moves, attacks, defends);
		
		addRayMoves(position, -1, -1, moves, attacks, defends);
		addRayMoves(position, +1, -1, moves, attacks, defends);
		addRayMoves(position, -1, +1, moves, attacks, defends);
		addRayMoves(position, +1, +1, moves, attacks, defends);
	}

	private void addRayMoves(Position position, int directionX, int directionY, List<Move> moves, List<Position> attacks, List<Position> defends) {
		int x = position.getX();
		int y = position.getY();
		
		boolean ok = false;
		do {
			x += directionX;
			y += directionY;
			Move move = addMove(position, x, y, moves, attacks, defends);
			ok = move != null && move.getKill() == null;
		} while(ok);
	}
	
	private void addKingMoves(Position position, List<Move> moves, List<Position> attacks, List<Position> defends) {
		int x = position.getX();
		int y = position.getY();

		addMove(position, x-1, y-1, moves, attacks, defends);
		addMove(position, x-1, y+0, moves, attacks, defends);
		addMove(position, x-1, y+1, moves, attacks, defends);
		addMove(position, x+0, y-1, moves, attacks, defends);
		addMove(position, x+0, y+1, moves, attacks, defends);
		addMove(position, x+1, y-1, moves, attacks, defends);
		addMove(position, x+1, y+0, moves, attacks, defends);
		addMove(position, x+1, y+1, moves, attacks, defends);
		
		// TODO castling
	}
	
	private boolean addMovePawnIfFree(Position position, int targetX, int targetY, List<Move> moves, List<Position> attacks, List<Position> defends) {
		if (targetX < 0 || targetX > 7 || targetY < 0 || targetY > 7) {
			return false;
		}
		
		Position target = getPosition(targetX, targetY);
		if (target == null) {
			if (targetY == Chess.getLastRow(position.getSide())) {
				for (Piece convert : Arrays.asList(Piece.Knight, Piece.Bishop, Piece.Rook, Piece.Queen)) {
					moves.add(new Move(position, targetX, targetY, target, convert));
				}
			} else {
				Move move = new Move(position, targetX, targetY, target);
				moves.add(move);
			}
			return true;
		}
		return false;
	}

	private boolean addMovePawnMustKill(Position position, int targetX, int targetY, List<Move> moves, List<Position> attacks, List<Position> defends) {
		if (targetX < 0 || targetX > 7 || targetY < 0 || targetY > 7) {
			return false;
		}
		
		Position target = getPosition(targetX, targetY);
		if (target != null) {
			if (target.getSide() != position.getSide()) {
				if (targetY == Chess.getLastRow(position.getSide())) {
					for (Piece convert : Arrays.asList(Piece.Knight, Piece.Bishop, Piece.Rook, Piece.Queen)) {
						moves.add(new Move(position, targetX, targetY, target, convert));
						attacks.add(target);
					}
				} else {
					moves.add(new Move(position, targetX, targetY, target));
					attacks.add(target);
				}
			} else {
				defends.add(target);
			}
			return true;
		}
		return false;
	}

	private Move addMove(Position position, int targetX, int targetY, List<Move> moves, List<Position> attacks, List<Position> defends) {
		if (targetX < 0 || targetX > 7 || targetY < 0 || targetY > 7) {
			return null;
		}
		
		Move move = null;
		Position target = getPosition(targetX, targetY);
		if (target == null) {
			move = new Move(position, targetX, targetY, target);
			moves.add(move);
		} else {
			if (target.getSide() != position.getSide()) {
				move = new Move(position, targetX, targetY, target);
				moves.add(move);
				attacks.add(target);
			} else {
				defends.add(target);
			}
		}

		return move;
	}
}
