package ch.obermuhlner.game.gomoku;

import static org.junit.Assert.*;

import org.junit.Test;

import ch.obermuhlner.game.Side;
import ch.obermuhlner.game.gomoku.ConnectFour;

public class ConnectFourTest {

	private static final double EPSILON = 0.001;

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
		
		connectFour.setState("7/7/7/7/6w/6b b");
		assertEquals("7/7/7/7/6w/6b b", connectFour.getState());
	}
	
	@Test
	public void testWinHorizontalLeft() {
		ConnectFour connectFour = new ConnectFour();
		connectFour.move("1");
		connectFour.move("1");
		connectFour.move("2");
		connectFour.move("2");
		connectFour.move("3");
		connectFour.move("3");
		connectFour.move("4");
		assertEquals(true, connectFour.isFinished());
		assertEquals(Side.Black, connectFour.getWinner());
	}
	
	@Test
	public void testWinHorizontalRight() {
		ConnectFour connectFour = new ConnectFour();
		connectFour.move("4");
		connectFour.move("4");
		connectFour.move("5");
		connectFour.move("5");
		connectFour.move("6");
		connectFour.move("6");
		connectFour.move("7");
		assertEquals(true, connectFour.isFinished());
		assertEquals(Side.Black, connectFour.getWinner());
	}

	@Test
	public void testWinVerticalBottom() {
		ConnectFour connectFour = new ConnectFour();
		connectFour.move("1");
		connectFour.move("2");
		connectFour.move("1");
		connectFour.move("2");
		connectFour.move("1");
		connectFour.move("2");
		connectFour.move("1");
		assertEquals(true,connectFour.isFinished());
		assertEquals(Side.Black, connectFour.getWinner());
	}

	@Test
	public void testWinVerticalTop() {
		ConnectFour connectFour = new ConnectFour();
		connectFour.move("2");
		connectFour.move("1");
		connectFour.move("2");
		connectFour.move("1");
		
		connectFour.move("1");
		connectFour.move("2");
		connectFour.move("1");
		connectFour.move("2");
		connectFour.move("1");
		connectFour.move("2");
		connectFour.move("1");
		assertEquals(true,connectFour.isFinished());
		assertEquals(Side.Black, connectFour.getWinner());
	}

	@Test
	public void testWinDiagonalUpRight() {
		ConnectFour connectFour = new ConnectFour();
		connectFour.move("1");
		connectFour.move("2");
		connectFour.move("2");
		connectFour.move("3");
		connectFour.move("3");
		connectFour.move("4");
		connectFour.move("3");
		connectFour.move("4");
		connectFour.move("4");
		connectFour.move("7");
		connectFour.move("4");
		assertEquals(true, connectFour.isFinished());
		assertEquals(Side.Black, connectFour.getWinner());
	}


	@Test
	public void testWinDiagonalDownRight() {
		ConnectFour connectFour = new ConnectFour();
		connectFour.move("1");
		connectFour.move("2");
		connectFour.move("3");
		connectFour.move("4");
		connectFour.move("1");
		connectFour.move("3");
		connectFour.move("2");
		connectFour.move("2");
		connectFour.move("1");
		connectFour.move("1");
		assertEquals(true, connectFour.isFinished());
		assertEquals(Side.White, connectFour.getWinner());
	}

	@Test
	public void testScore1() {
		ConnectFour connectFour = new ConnectFour();
		connectFour.move("1");
		assertEquals(-3, connectFour.getScore(), EPSILON);
	}
	
	@Test
	public void testScore2() {
		ConnectFour connectFour = new ConnectFour();
		connectFour.move("1");
		connectFour.move("7");
		assertEquals(0, connectFour.getScore(), EPSILON);
	}
	
	@Test
	public void testScore3() {
		ConnectFour connectFour = new ConnectFour();
		connectFour.move("1");
		connectFour.move("7");
		connectFour.move("1");
		assertEquals(-102, connectFour.getScore(), EPSILON);
	}
	
	@Test
	public void testScore4() {
		ConnectFour connectFour = new ConnectFour();
		connectFour.setState("7/7/7/7/b6/b6 b");
		assertEquals(-105, connectFour.getScore(), EPSILON);
	}

	@Test
	public void testScore5() {
		ConnectFour connectFour = new ConnectFour();
		connectFour.setState("7/7/7/7/7/bb5 b");
		System.out.println(connectFour.getDiagram());
		assertEquals(-105, connectFour.getScore(), EPSILON);
	}

	@Test
	public void testScore6() {
		ConnectFour connectFour = new ConnectFour();
		connectFour.setState("7/7/7/7/7/1bb4 b");
		assertEquals(-1006, connectFour.getScore(), EPSILON);
	}

	@Test
	public void testScore7() {
		ConnectFour connectFour = new ConnectFour();
		connectFour.setState("7/7/7/7/1b5/1b5 b");
		assertEquals(-142, connectFour.getScore(), EPSILON);
	}

	@Test
	public void testScore8() {
		ConnectFour connectFour = new ConnectFour();
		connectFour.setState("7/7/7/7/6b/6b b");
		System.out.println(connectFour.getState());
		System.out.println(connectFour.getDiagram());
		assertEquals(-105, connectFour.getScore(), EPSILON);
	}


	@Test
	public void testScore9() {
		ConnectFour connectFour = new ConnectFour();
		connectFour.setState("7/7/7/6b/6b/6w b");
		System.out.println(connectFour.getState());
		System.out.println(connectFour.getDiagram());
		assertEquals(-104, connectFour.getScore(), EPSILON);
	}
}
