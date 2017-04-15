package ch.obermuhlner.game.chess.gomoku;

import static org.junit.Assert.*;

import org.junit.Test;

import ch.obermuhlner.game.gomoku.ConnectFour;

public class ConnectFourTest {

	@Test
	public void testGetState() {
		ConnectFour connectFour = new ConnectFour();
		connectFour.setStartPosition();
		assertEquals("7/7/7/7/7/7 b", connectFour.getState());
		
		connectFour.move("1");
		assertEquals("7/7/7/7/7/b6 w", connectFour.getState());
	}
	
	@Test
	public void testSetState() {
		ConnectFour connectFour = new ConnectFour();
		connectFour.setState("7/7/7/7/7/b6 w");
		assertEquals("7/7/7/7/7/b6 w", connectFour.getState());
	}
}
