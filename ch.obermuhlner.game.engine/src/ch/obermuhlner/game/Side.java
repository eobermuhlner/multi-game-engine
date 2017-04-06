package ch.obermuhlner.game;

public enum Side {
	None,
	White,
	Black;
	
	public Side otherSide() {
		return otherSide(this);
	}

	private static Side otherSide(Side side) {
		switch(side) {
		case Black:
			return Side.White;
		case White:
			return Side.White;
		case None:
			return Side.None;
		}
		
		throw new IllegalArgumentException("Unknown side: " + side);
	}
}