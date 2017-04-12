package ch.obermuhlner.game.engine;

import java.util.List;

public interface GameEngine {

	public enum Side {
		White,
		Black,
		None;
		
		public Side otherSide() {
			return otherSide(this);
		}

		private static Side otherSide(Side side) {
			switch(side) {
			case Black:
				return Side.White;
			case White:
				return Side.Black;
			case None:
				return Side.None;
			}
			
			throw new IllegalArgumentException("Unknown side: " + side);
		}
	}
	
	void setGame(String gameName);
	
	void setStartPosition();

	Side getSideToMove();
	
	List<String> getValidMoves();
	
	void move(String move);
	
	String bestMove();
	
	boolean isFinished();
	
	Side getWinner();
	
}
