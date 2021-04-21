package ch.obermuhlner.game;

/**
 * The side of a game: white, black or none.
 */
public enum Side {
	None,
	White,
	Black;
	
	/**
	 * Returns the other side of this side.
	 * 
	 * @return {@link #White} returns {@link #Black},<br>
	 *         {@link #Black} returns {@link #White},<br>
	 *         {@link #None} returns {@link #None},<br>
	 */
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