package ch.obermuhlner.game.gomoku;

import ch.obermuhlner.game.Game;
import ch.obermuhlner.game.Side;

public abstract class AbstractStonesInARow implements Game {

	protected static final char[] LETTERS = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's' }; 

	protected final int boardWidth;
	protected final int boardHeight;
	
	protected final int winCount;
	protected final boolean exactWin;

	protected final Side[] board;
	
	protected Side sideToMove = Side.Black;
	
	private int lastMoveX;
	private int lastMoveY;

	public AbstractStonesInARow(int boardWidth, int boardHeight, int winCount, boolean exactWin) {
		this.boardWidth = boardWidth;
		this.boardHeight = boardHeight;
		this.winCount = winCount;
		this.exactWin = exactWin;
		this.board = new Side[boardWidth*boardHeight];
		
		setStartPosition();
	}
	
	@Override
	public void setStartPosition() {
		for (int i = 0; i < board.length; i++) {
			board[i] = Side.None;
		}

		sideToMove = Side.Black;
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
			sideToMove = toSide(split[1].charAt(0));
		}
	}

	@Override
	public String getState() {
		StringBuilder state = new StringBuilder();
		
		for (int y = 0; y < boardHeight; y++) {
			int emptyCount = 0;
			for (int x = 0; x < boardWidth; x++) {
				Side position = getPosition(x, y);
				if (position == Side.None) {
					emptyCount++;
				} else {
					if (emptyCount > 0) {
						state.append(emptyCount);
						emptyCount = 0;
					}
					state.append(toString(position, "-"));
				}
			}

			if (emptyCount > 0) {
				state.append(emptyCount);
			}
			
			if (y < boardHeight - 1) {
				state.append("/");
			}
		}
		
		state.append(" ");
		state.append(toString(sideToMove, ""));
		
		return state.toString();
	}

	private Side toSide(char c) {
		switch (c) {
		case 'w':
			return Side.White;
		case 'b':
			return Side.Black;
		}
		
		return Side.None;
	}

	private String toString(Side side, String defaultString) {
		switch(side) {
		case White:
			return "w";
		case Black:
			return "b";
		case None:
			return defaultString;
		}
		
		throw new IllegalArgumentException("Unknown side: " + side);
	}

	protected String toDiagramString(Side side, String defaultString) {
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

	protected static int letterToInt(char letter) {
		for (int i = 0; i < LETTERS.length; i++) {
			if (letter == LETTERS[i]) {
				return i;
			}
		}
		
		throw new IllegalArgumentException("Unknown position letter: " + letter);
	}

	protected Side getPosition(int x, int y) {
		return board[x + y*boardWidth];
	}
	
	protected void setPosition(int x, int y, Side side) {
		board[x + y * boardWidth] = side;
	}

	@Override
	public Side getSideToMove() {
		return sideToMove;
	}
	
	@Override
	public double getScore() {
		// TODO impl score
		return 0;
	}

	@Override
	public boolean isValid(String move) {
		return true;
	}
	
	protected void move(int x, int y) {
		lastMoveX = x;
		lastMoveY = y;
		
		setPosition(x, y, sideToMove);
		
		sideToMove = sideToMove.otherSide();
	}

	@Override
	public boolean isFinished() {
		Side winner = getWinner();
		if (winner != Side.None) {
			return true;
		}

		for (int i = 0; i < board.length; i++) {
			if (board[i] == Side.None) {
				return false;
			}
		}
		return true;
	}

	@Override
	public Side getWinner() {
		if (lastMoveX >= 0 && lastMoveY >= 0) {
			return getWinnerSearchLastMove();
		}

		return getWinnerSearchFull();
	}

	private Side getWinnerSearchFull() {
		Side winner;
		
		for (int x = 0; x < boardWidth; x++) {
			// vertical
			winner = getWinner(x, 0, 0, 1);
			if (winner != Side.None) {
				return winner;
			}
			
			// diagonal top side going down/right
			winner = getWinner(x, 0, 1, 1);
			if (winner != Side.None) {
				return winner;
			}

			// diagonal bottom side going up/right
			winner = getWinner(x, boardHeight-1, 1, -1);
			if (winner != Side.None) {
				return winner;
			}
		}
		for (int y = 0; y < boardHeight; y++) {
			// horizontal
			winner = getWinner(0, y, 1, 0);
			if (winner != Side.None) {
				return winner;
			}

			// diagonal left side going down/right
			winner = getWinner(0, y, 1, 1);
			if (winner != Side.None) {
				return winner;
			}

			// diagonal left side going up/right
			winner = getWinner(0, y, 1, -1);
			if (winner != Side.None) {
				return winner;
			}
		}
		
		return Side.None;
	}
	
	private Side getWinnerSearchLastMove() {
		Side winner;
		
		// horizontal
		winner = getWinner(0, lastMoveY, 1, 0);
		if (winner != Side.None) {
			return winner;
		}
		
		// vertical
		winner = getWinner(lastMoveX, 0, 0, 1);
		if (winner != Side.None) {
			return winner;
		}

		// diagonal going down/right
		int diagonalStartX = lastMoveX - Math.min(lastMoveX, lastMoveY);
		int diagonalStartY = lastMoveY - Math.min(lastMoveX, lastMoveY);
		winner = getWinner(diagonalStartX, diagonalStartY, 1, 1);
		if (winner != Side.None) {
			return winner;
		}
		
		// diagonal left side going up/right
		diagonalStartX = lastMoveX - Math.min(lastMoveX, boardHeight - lastMoveY - 1);
		diagonalStartY = lastMoveY + Math.min(lastMoveX, boardHeight - lastMoveY - 1);
		winner = getWinner(diagonalStartX, diagonalStartY, 1, -1);
		if (winner != Side.None) {
			return winner;
		}

		return Side.None;
	}
	
	private Side getWinner(int x, int y, int deltaX, int deltaY) {
		int whiteCount = 0;
		int blackCount = 0;
		
		int maxWhiteCount = 0;
		int maxBlackCount = 0;

		while (x >= 0 && x < boardWidth && y < boardHeight && y >= 0) {
			switch (getPosition(x, y)) {
			case Black:
				blackCount++;
				maxWhiteCount = Math.max(whiteCount, maxWhiteCount);
				whiteCount = 0;
				break;
			case White:
				whiteCount++;
				maxBlackCount = Math.max(blackCount, maxBlackCount);
				blackCount = 0;
				break;
			case None:
				maxWhiteCount = Math.max(whiteCount, maxWhiteCount);
				whiteCount = 0;
				maxBlackCount = Math.max(blackCount, maxBlackCount);
				blackCount = 0;
				break;
			}
		
			x += deltaX;
			y += deltaY;
		}
		
		if (exactWin) {
			if (maxWhiteCount == winCount) {
				return Side.White;
			}
			if (maxBlackCount == winCount) {
				return Side.Black;
			}
		} else {
			if (maxWhiteCount >= winCount) {
				return Side.White;
			}
			if (maxBlackCount >= winCount) {
				return Side.Black;
			}
		}
		return Side.None;
	}
	
	protected AbstractStonesInARow cloneGame(AbstractStonesInARow game) {
		for (int i = 0; i < this.board.length; i++) {
			game.board[i] = board[i];
		}
		game.sideToMove = sideToMove;

		return game;
	}

	@Override
	public String toString() {
		return getState();
	}
}
