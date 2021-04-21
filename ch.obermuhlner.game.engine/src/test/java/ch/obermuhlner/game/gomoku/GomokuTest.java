package ch.obermuhlner.game.gomoku;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import ch.obermuhlner.game.gomoku.Gomoku;

public class GomokuTest {

	@Test
	public void testGetState() {
		Gomoku gomoku = new Gomoku();
		gomoku.setStartPosition();
		assertEquals("19/19/19/19/19/19/19/19/19/19/19/19/19/19/19/19/19/19/19 b", gomoku.getState());
		
		gomoku.move("aa");
		assertEquals("19/19/19/19/19/19/19/19/19/19/19/19/19/19/19/19/19/19/b18 w", gomoku.getState());
	}
	
	@Test
	public void testSetState() {
		Gomoku gomoku = new Gomoku();
		gomoku.setState("19/19/19/19/19/19/19/19/19/19/19/19/19/19/19/19/19/19/b18 w");
		assertEquals("19/19/19/19/19/19/19/19/19/19/19/19/19/19/19/19/19/19/b18 w", gomoku.getState());

		gomoku.setState("19/19/19/19/19/19/19/19/19/19/19/19/19/19/19/19/19/19/18b w");
		assertEquals("19/19/19/19/19/19/19/19/19/19/19/19/19/19/19/19/19/19/18b w", gomoku.getState());
	}
}
