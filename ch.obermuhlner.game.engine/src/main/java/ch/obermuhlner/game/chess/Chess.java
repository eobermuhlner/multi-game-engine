package ch.obermuhlner.game.chess;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import ch.obermuhlner.game.Engine;
import ch.obermuhlner.game.Game;
import ch.obermuhlner.game.Side;
import ch.obermuhlner.game.app.GameCommandLine;
import ch.obermuhlner.game.engine.random.RandomEngine;
import ch.obermuhlner.util.Tuple2;

public class Chess implements Game {

	private static final char[] LETTERS = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h' };

	private final List<Position> positions = new ArrayList<>();

	private Side sideToMove = Side.White;

	private List<Position> castlePositions = new ArrayList<>();

	private int halfMoveSinceCaptureOrPawnAdvanceNumber = 0;
	private int moveNumber = 0;

	private Analysis analysis;

	public Chess() {
		setStartPosition();
	}
	
	@Override
	public void setStartPosition() {
		clear();
		
		positions.add(new Position(Piece.Rook, Side.White, 0, 0));
		positions.add(new Position(Piece.Knight, Side.White, 1, 0));
		positions.add(new Position(Piece.Bishop, Side.White, 2, 0));
		positions.add(new Position(Piece.Queen, Side.White, 3, 0));
		positions.add(new Position(Piece.King, Side.White, 4, 0));
		positions.add(new Position(Piece.Bishop, Side.White, 5, 0));
		positions.add(new Position(Piece.Knight, Side.White, 6, 0));
		positions.add(new Position(Piece.Rook, Side.White, 7, 0));
		
		positions.add(new Position(Piece.Pawn, Side.White, 0, 1));
		positions.add(new Position(Piece.Pawn, Side.White, 1, 1));
		positions.add(new Position(Piece.Pawn, Side.White, 2, 1));
		positions.add(new Position(Piece.Pawn, Side.White, 3, 1));
		positions.add(new Position(Piece.Pawn, Side.White, 4, 1));
		positions.add(new Position(Piece.Pawn, Side.White, 5, 1));
		positions.add(new Position(Piece.Pawn, Side.White, 6, 1));
		positions.add(new Position(Piece.Pawn, Side.White, 7, 1));
		
		
		positions.add(new Position(Piece.Rook, Side.Black, 0, 7));
		positions.add(new Position(Piece.Knight, Side.Black, 1, 7));
		positions.add(new Position(Piece.Bishop, Side.Black, 2, 7));
		positions.add(new Position(Piece.Queen, Side.Black, 3, 7));
		positions.add(new Position(Piece.King, Side.Black, 4, 7));
		positions.add(new Position(Piece.Bishop, Side.Black, 5, 7));
		positions.add(new Position(Piece.Knight, Side.Black, 6, 7));
		positions.add(new Position(Piece.Rook, Side.Black, 7, 7));
		
		positions.add(new Position(Piece.Pawn, Side.Black, 0, 6));
		positions.add(new Position(Piece.Pawn, Side.Black, 1, 6));
		positions.add(new Position(Piece.Pawn, Side.Black, 2, 6));
		positions.add(new Position(Piece.Pawn, Side.Black, 3, 6));
		positions.add(new Position(Piece.Pawn, Side.Black, 4, 6));
		positions.add(new Position(Piece.Pawn, Side.Black, 5, 6));
		positions.add(new Position(Piece.Pawn, Side.Black, 6, 6));
		positions.add(new Position(Piece.Pawn, Side.Black, 7, 6));
	
		castlePositions.add(getPosition(7, 0));
		castlePositions.add(getPosition(0, 0));
		castlePositions.add(getPosition(7, 7));
		castlePositions.add(getPosition(0, 7));

		invalidateAnalysis();
	}

	public void clear() {
		positions.clear();
		castlePositions.clear();
		sideToMove = Side.White;
		
		halfMoveSinceCaptureOrPawnAdvanceNumber = 0;
		moveNumber = 1;
	}

	@Override
	public void setState(String state) {
		String[] splitFen = state.split(" +");
		
		clear();
		positions.addAll(toFenPositions(splitFen[0]));
		Side fenSide = Side.White;
		
		if (splitFen.length > 1) {
			fenSide = splitFen[1].equals("w") ? Side.White : Side.Black;
		}
		if (splitFen.length > 2) {
			for (char castleChar : splitFen[2].toCharArray()) {
				switch(castleChar) {
				case 'K':
					castlePositions.add(getPosition(7, 0));
					break;
				case 'Q':
					castlePositions.add(getPosition(0, 0));
					break;
				case 'k':
					castlePositions.add(getPosition(7, 7));
					break;
				case 'q':
					castlePositions.add(getPosition(0, 7));
					break;
				default:
					if (castleChar >= 'A' && castleChar <= 'H') {
						castlePositions.add(getPosition(letterToInt(Character.toLowerCase(castleChar)), 0));
					}
					if (castleChar >= 'a' && castleChar <= 'h') {
						castlePositions.add(getPosition(letterToInt(castleChar), 7));
					}
				}
			}
		}
		if (splitFen.length > 3) {
			// ignore en passant info
		}
		if (splitFen.length > 4) {
			halfMoveSinceCaptureOrPawnAdvanceNumber = Integer.parseInt(splitFen[4]);
		}
		
		if (splitFen.length > 5) {
			moveNumber = Integer.parseInt(splitFen[5]);
		}
		
		setSideToMove(fenSide);
		invalidateAnalysis();
	}

	private static List<Position> toFenPositions(String fen) {
		List<Position> fenPositions = new ArrayList<>();
		
		int index = 0;
		for (int i = 0; i < fen.length(); i++) {
			char c = fen.charAt(i);
			Position position = toPosition(c, index);
			if (position != null) {
				fenPositions.add(position);
				index++;
			} else if (c >= '1' && c <= '9') {
				int emptyCount = Character.getNumericValue(c);
				index += emptyCount;
			} else if (c == '/') {
				// ignore
			} else if (c == ' ') {
				return fenPositions;
			} else {
				throw new IllegalArgumentException("Unknown character '" + c + "' in FEN string: " + fen);
			}
		}
		return fenPositions;
	}

	@Override
	public String getState() {
		StringBuilder builder = new StringBuilder();

		builder.append(getPositionState());
		
		builder.append(" ");
		builder.append(halfMoveSinceCaptureOrPawnAdvanceNumber);
		builder.append(" ");
		builder.append(moveNumber);
		
		return builder.toString();
	}

	@Override
	public String getPositionState() {
		StringBuilder builder = new StringBuilder();

		builder.append(toFenPositionString());
		
		builder.append(" ");
		for(Position castle : castlePositions) {
			char c;
			switch(castle.getX()) {
			case 0:
				c = castle.getSide() == Side.White ? 'Q' : 'q';
				break;
			case 7:
				c = castle.getSide() == Side.White ? 'K' : 'k';
				break;
			default:
				c = LETTERS[castle.getX()];
			}
			builder.append(castle.getSide() == Side.White ? Character.toUpperCase(c) : c);
		}
		if (castlePositions.isEmpty()) {
			builder.append("-");
		}
		
		builder.append(" -"); // TODO en passant
		
		return builder.toString();
	}
	
	@Override
	public double getScore() {
		return getSideValue(Side.White) - getSideValue(Side.Black);
	}
	
	public double getSideValue(Side side) {
		double value = 0;
		
		boolean finished = isFinished();
		Side winner = getWinner();
		
		if (sideToMove == side) {
			if (finished && winner == sideToMove) {
				return 100;
			}
			if (finished && winner == Side.None) {
				return 0;
			}

			if (getAnalysis().isCheck(sideToMove)) {
				value += 20;
			} else {
				value += 0.5;
			}
		} else {
			if (finished) {
				return 0;
			}
		}

		value += positions.stream()
				.filter(position -> position.getSide() == side)
				.mapToDouble(position -> getAnalysis().getValue(position))
				.sum();
		
		
		return value;
	}
	
	@Override
	public String getDiagram() {
		StringBuilder diagram = new StringBuilder();
		final int boardSize = 8;

		for (int y = 0; y < boardSize; y++) {
			for (int x = 0; x < boardSize; x++) {
				diagram.append("+---");
			}
			diagram.append("+\n");
			
			for (int x = 0; x < boardSize; x++) {
				diagram.append("| ");
				Position position = getAnalysis().getPosition(x, boardSize - y - 1);
				if (position != null) {
					diagram.append(position.getCharacter());
				} else {
					diagram.append(" ");
				}
				diagram.append(" ");
			}
			diagram.append("| ");
			diagram.append(boardSize - y);
			diagram.append("\n");
		}
		
		for (int x = 0; x < boardSize; x++) {
			diagram.append("+---");
		}
		diagram.append("+\n");

		for (int x = 0; x < boardSize; x++) {
			diagram.append("  ");
			diagram.append(LETTERS[x]);
			diagram.append(" ");
		}
		diagram.append("\n");
		
		return diagram.toString();
	}

	@Override
	public Side getSideToMove() {
		return sideToMove;
	}

	@Override
	public void move(String move) {
		char[] chars = move.toCharArray();
		Piece convert = null;
		if (chars.length >= 5) {
			convert = Piece.ofCharacter(chars[4]);
		}
		move(
				letterToInt(chars[0]),
				Character.getNumericValue(chars[1]) - 1,
				letterToInt(chars[2]),
				Character.getNumericValue(chars[3]) - 1,
				convert);
	}
	
	public void move(int sourceX, int sourceY, int targetX, int targetY) {
		move(sourceX, sourceY, targetX, targetY, null);
	}

	public void move(int sourceX, int sourceY, int targetX, int targetY, Piece convert) {
		Position source = positions.stream()
				.filter(position -> position.getX() == sourceX && position.getY() == sourceY)
				.findAny()
				.get();
		Position target;
		
		if (source.getPiece() == Piece.King && Math.abs(targetX - sourceX) == 2) {
			boolean kingDirectionRight = targetX > sourceX;
			target = castlePositions.stream()
				.filter(rook -> rook.getSide() == source.getSide())
				.filter(rook -> kingDirectionRight == (rook.getX() > source.getX()))
				.findAny()
				.get();
			castlePositions = castlePositions.stream()
				.filter(rook -> rook.getSide() != source.getSide())
				.collect(Collectors.toList());

		} else {
			target = positions.stream()
					.filter(position -> position.getX() == targetX && position.getY() == targetY)
					.findAny()
					.orElse(null);
		}

		move(new Move(source, targetX, targetY, target, convert));
	}

	private void move(Move move) {
		// TODO validate input
		//CheckArgument.isTrue(move.getKill() == null || move.getKill().getPiece() != Piece.King, () -> "King cannot be killed: " + move);
		
		Position source = move.getSource();
		
		positions.remove(source);
		positions.remove(move.getKill());
		positions.remove(move.getCastle());

		if (move.getCastle() != null) {
			// castling (rochade)
			int kingDirectionX = move.getCastle().getX() > source.getX() ? +1 : -1;

			Position newKingPosition = new Position(Piece.King, source.getSide(), source.getX() + kingDirectionX*2, source.getY());
			positions.add(newKingPosition);
			
			Position newRookPosition = new Position(Piece.Rook, source.getSide(), source.getX() + kingDirectionX, source.getY());
			positions.add(newRookPosition);
		} else {
			// normal move (including conversion of pawn)
			Piece piece = move.getConvert() == null ? source.getPiece() : move.getConvert();
			Position newPosition = new Position(piece, source.getSide(), move.getTargetX(), move.getTargetY());
			positions.add(newPosition);
		}
		
		// TODO update castlePositions
		
		sideToMove = sideToMove.otherSide();
		if (sideToMove == Side.White) {
			moveNumber++;
		}
		
		if (move.getKill() != null || source.getPiece() == Piece.Pawn) {
			halfMoveSinceCaptureOrPawnAdvanceNumber = 0;
		} else {
			halfMoveSinceCaptureOrPawnAdvanceNumber++;
		}

		invalidateAnalysis();
	}

	@Override
	public List<Tuple2<String, Double>> getAllMoves() {
		return positions.stream()
				.filter(position -> position.getSide() == sideToMove)
				.flatMap(position -> getAnalysis().getMoves(position).stream())
				.map(move -> Tuple2.of(move.toUciString(), move.getValue()))
				.collect(Collectors.toList());
	}

	@Override
	public boolean isValid(String move) {
		Chess local = cloneGame();
		local.move(move);
		
		return !local.isCheck(sideToMove);
	}

	@Override
	public boolean isFinished() {
		return halfMoveSinceCaptureOrPawnAdvanceNumber > 50 || getValidMoves().isEmpty();
	}

	@Override
	public Side getWinner() {
		if (isCheck(sideToMove)) {
			return sideToMove.otherSide();
		}

		return Side.None;
	}

	public boolean isCheck(Side side) {
		return getAnalysis().isCheck(side);
	}
	
	@Override
	public Chess cloneGame() {
		Chess game = new Chess();

		game.positions.clear();
		game.positions.addAll(positions);
		game.sideToMove = sideToMove;
		game.moveNumber = moveNumber;
		game.halfMoveSinceCaptureOrPawnAdvanceNumber = halfMoveSinceCaptureOrPawnAdvanceNumber;
		
		return game;
	}
	
	public List<Position> getPositions() {
		return positions;
	}
	
	public Position getPosition(int x, int y) {
		return positions.stream()
			.filter(position -> position.getX() == x && position.getY() == y)
			.findAny().orElse(null);
	}

	public void addPosition(String position) {
		char character = position.charAt(0);
		
		for (Piece piece : Piece.values()) {
			if (piece.getWhiteCharacter() == character) {
				addPosition(piece, Side.White, position.substring(1));
				return;
			}
			if (piece.getBlackCharacter() == character) {
				addPosition(piece, Side.Black, position.substring(1));
				return;
			}
		}
		
		throw new IllegalArgumentException("Unknown position: " + position);
	}
	
	private void addPosition(Piece piece, Side side, String position) {
		int x = letterToInt(position.charAt(0));
		int y = Character.getNumericValue(position.charAt(1)) - 1;
		addPosition(piece, side, x, y);
	}

	private void addPosition(Piece piece, Side side, int x, int y) {
		positions.add(new Position(piece, side, x, y));
		invalidateAnalysis();
	}

	private void invalidateAnalysis() {
		analysis = null;		
	}
	
	private Analysis getAnalysis() {
		if (analysis == null) {
			analysis = new Analysis(this);
		}
		
		return analysis;
	}

	private static Position toPosition(char character, int index) {
		int x = index % 8;
		int y = 7 - index / 8;
	
		for (Piece piece : Piece.values()) {
			if (piece.getWhiteCharacter() == character) {
				return new Position(piece, Side.White, x, y);
			}
			if (piece.getBlackCharacter() == character) {
				return new Position(piece, Side.Black, x, y);
			}
		}
		return null;
	}

	private String toFenPositionString() {
		StringBuilder builder = new StringBuilder();

		char[] charBoard = new char[64];
		for (int i = 0; i < charBoard.length; i++) {
			charBoard[i] = ' ';
		}
		
		for(Position position : positions) {
			charBoard[position.getX() + (7-position.getY()) * 8] = position.getCharacter();
		}
		
		for (int y = 0; y < 8; y++) {
			int emptyCount = 0;
			for (int x = 0; x < 8; x++) {
				char figure = charBoard[x + y * 8];
				if (figure == ' ') {
					emptyCount++;
				} else {
					if (emptyCount > 0) {
						builder.append(emptyCount);
						emptyCount = 0;
					}
					builder.append(figure);
				}
			}

			if (emptyCount > 0) {
				builder.append(emptyCount);
			}
			
			if (y != 7) {
				builder.append("/");
			}
		}
		
		builder.append(" ");
		builder.append(sideToMove == Side.White ? "w" : "b");
		
		return builder.toString();
	}
	
	public void setSideToMove(Side sideToMove) {
		this.sideToMove = sideToMove;
		invalidateAnalysis();
	}

	static int getPawnDirection(Side side) {
		switch(side) {
		case White:
			return 1;
		case Black:
			return -1;
		case None:
			throw new IllegalArgumentException("Side " + side + " not supported");
		}
		throw new IllegalArgumentException("Unknown side: " + side);
	}

	static int getPawnStart(Side side) {
		switch(side) {
		case White:
			return 1;
		case Black:
			return 6;
		case None:
			throw new IllegalArgumentException("Side " + side + " not supported");
		}
		throw new IllegalArgumentException("Unknown side: " + side);
	}

	static int getLastRow(Side side) {
		switch(side) {
		case White:
			return 7;
		case Black:
			return 0;
		case None:
			throw new IllegalArgumentException("Side " + side + " not supported");
		}
		throw new IllegalArgumentException("Unknown side: " + side);
	}

	private static int letterToInt(char letter) {
		for (int i = 0; i < LETTERS.length; i++) {
			if (letter == LETTERS[i]) {
				return i;
			}
		}
		
		throw new IllegalArgumentException("Unknown chess position letter: " + letter);
	}

	public static String toPositionString(int x, int y) {
		return String.valueOf(LETTERS[x]) + (y + 1);
	}
	
	public static void main(String[] args) {
		Engine<Chess> engine = new RandomEngine<>(new Chess());
		
		GameCommandLine.playGame(engine);
	}
}
