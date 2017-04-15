package ch.obermuhlner.game.chess;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

import ch.obermuhlner.game.Game;
import ch.obermuhlner.game.Side;
import ch.obermuhlner.util.Tuple2;

public class ChessTest {

	@Test
	public void testGetState() {
		Chess chess = new Chess();
		chess.setStartPosition();
		assertEquals("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1", chess.getState());
	}

	@Test
	public void testSetState() {
		Chess chess = new Chess();

		// positions only
		chess.setState("6k1/rr1q2p1/2bnnpbp/2ppppp1/8/8/PPPPPPPP/RNBQKBNR");
		assertEquals("6k1/rr1q2p1/2bnnpbp/2ppppp1/8/8/PPPPPPPP/RNBQKBNR w - - 0 1", chess.getState());
		
		// side to move
		chess.setState("6k1/rr1q2p1/2bnnpbp/2ppppp1/8/8/PPPPPPPP/RNBQKBNR w");
		assertEquals("6k1/rr1q2p1/2bnnpbp/2ppppp1/8/8/PPPPPPPP/RNBQKBNR w - - 0 1", chess.getState());

		chess.setState("6k1/rr1q2p1/2bnnpbp/2ppppp1/8/8/PPPPPPPP/RNBQKBNR b");
		assertEquals("6k1/rr1q2p1/2bnnpbp/2ppppp1/8/8/PPPPPPPP/RNBQKBNR b - - 0 1", chess.getState());
		
		// castling
		chess.setState("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq");
		assertEquals("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1", chess.getState());

		chess.setState("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w kK");
		assertEquals("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w kK - 0 1", chess.getState());

		// castling extension for Chess960
		chess.setState("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w AHah");
		assertEquals("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w QKqk - 0 1", chess.getState());

		chess.setState("nrbqkrnb/pppppppp/8/8/8/8/PPPPPPPP/NRBQKRNB w bBfF");
		assertEquals("nrbqkrnb/pppppppp/8/8/8/8/PPPPPPPP/NRBQKRNB w bBfF - 0 1", chess.getState());
	}
	
	@Test
	public void testWhitePawnMoves1() {
		assertMoves(whiteToMove("Pe2"), "e2e3", "e2e4"); // double move
		assertMoves(whiteToMove("Pe3"), "e3e4"); // single move
		assertMoves(whiteToMove("Pe3", "pd4", "pf4"), "e3e4", "e3d4", "e3f4"); // kill left and right
		assertMoves(whiteToMove("Pa3", "pb4"), "a3a4", "a3b4"); // left side of chess, kill right
		assertMoves(whiteToMove("Ph3", "pg4"), "h3h4", "h3g4"); // right side of chess, kill left
		assertMoves(whiteToMove("Pe3", "pe4")); // blocked pawn
	}

	@Test
	public void testWhitePawnMoves2() {
		assertMoves(whiteToMove("Pe7"), "e7e8n", "e7e8b", "e7e8r", "e7e8q"); // four conversions: NBRQ
		assertMoves(whiteToMove("Pe7", "pd8"), "e7e8n", "e7e8b", "e7e8r", "e7e8q", "e7d8n", "e7d8b", "e7d8r", "e7d8q"); // four conversions: NBRQ
	}
	
	@Test
	public void testBlackPawnMoves1() {
		assertMoves(blackToMove("pe7"), "e7e6", "e7e5"); // double move
		assertMoves(blackToMove("pe6"), "e6e5"); // single move
		assertMoves(blackToMove("pe6", "Pd5", "Pf5"), "e6e5", "e6d5", "e6f5"); // kill left and right
		assertMoves(blackToMove("pa6", "Pb5"), "a6a5", "a6b5"); // left side of chess, kill right
		assertMoves(blackToMove("ph6", "Pg5"), "h6h5", "h6g5"); // right side of chess, kill left
		assertMoves(blackToMove("pe6", "Pe5")); // blocked pawn
	}

	@Test
	public void testBlackPawnMoves2() {
		assertMoves(blackToMove("pe2"), "e2e1n", "e2e1b", "e2e1r", "e2e1q"); // four conversions: NBRQ
		assertMoves(blackToMove("pe2", "Pd1"), "e2e1n", "e2e1b", "e2e1r", "e2e1q", "e2d1n", "e2d1b", "e2d1r", "e2d1q"); // four conversions: NBRQ
	}

	@Test
	public void testKnightMoves() {
		assertMoves(whiteToMove("Ne4"), "e4d6", "e4f6", "e4g5", "e4g3", "e4f2", "e4d2", "e4c3", "e4c5");
		assertMoves(whiteToMove("Ne4", "Pd6"), "e4f6", "e4g5", "e4g3", "e4f2", "e4d2", "e4c3", "e4c5", // pawn blocks one move
				"d6d7"); // pawn can also move
		assertMoves(whiteToMove("Ne4", "pd6"), "e4d6", "e4f6", "e4g5", "e4g3", "e4f2", "e4d2", "e4c3", "e4c5"); // enemy pawn just taken
	}

	@Test
	public void testBishopMoves() {
		assertMoves(whiteToMove("Be4"),
				"e4d3", "e4c2", "e4b1",
				"e4f3", "e4g2", "e4h1",
				"e4d5", "e4c6", "e4b7", "e4a8",
				"e4f5", "e4g6", "e4h7");
		assertMoves(whiteToMove("Be4", "Pc6"),
				"e4d3", "e4c2", "e4b1",
				"e4f3", "e4g2", "e4h1",
				"e4d5", // this ray blocked by own pawn
				"e4f5", "e4g6", "e4h7",
				"c6c7"); // pawn can also move
		assertMoves(whiteToMove("Be4", "pc6"),
				"e4d3", "e4c2", "e4b1",
				"e4f3", "e4g2", "e4h1",
				"e4d5", "e4c6", // this ray blocked by enemy pawn
				"e4f5", "e4g6", "e4h7");
	}

	@Test
	public void testRookMoves() {
		assertMoves(whiteToMove("Re4"),
				"e4f4", "e4g4", "e4h4", 
				"e4e5", "e4e6", "e4e7", "e4e8", 
				"e4e3", "e4e2", "e4e1", 
				"e4d4", "e4c4", "e4b4", "e4a4");
		assertMoves(whiteToMove("Re4", "Pc4"),
				"e4f4", "e4g4", "e4h4", 
				"e4e5", "e4e6", "e4e7", "e4e8", 
				"e4e3", "e4e2", "e4e1",
				"e4d4", // this ray blocked by own pawn
				"c4c5"); // pawn can also move
		assertMoves(whiteToMove("Re4", "pd4"),
				"e4f4", "e4g4", "e4h4", 
				"e4e5", "e4e6", "e4e7", "e4e8", 
				"e4e3", "e4e2", "e4e1",
				"e4d4"); // this ray blocked by enemy pawn
	}
	
	@Test
	public void testQueenMoves() {
		assertMoves(whiteToMove("Qe4"),
				"e4d3", "e4c2", "e4b1",
				"e4f3", "e4g2", "e4h1",
				"e4d5", "e4c6", "e4b7", "e4a8",
				"e4f5", "e4g6", "e4h7",
				"e4f4", "e4g4", "e4h4", 
				"e4e5", "e4e6", "e4e7", "e4e8", 
				"e4e3", "e4e2", "e4e1", 
				"e4d4", "e4c4", "e4b4", "e4a4");
		assertMoves(whiteToMove("Qe4", "Pc6"),
				"e4d3", "e4c2", "e4b1",
				"e4f3", "e4g2", "e4h1",
				"e4d5", // this ray blocked by own pawn
				"e4f5", "e4g6", "e4h7",
				"c6c7", // pawn can also move
				"e4f4", "e4g4", "e4h4", 
				"e4e5", "e4e6", "e4e7", "e4e8", 
				"e4e3", "e4e2", "e4e1", 
				"e4d4", "e4c4", "e4b4", "e4a4");
		assertMoves(whiteToMove("Qe4", "Pc4"),
				"e4d3", "e4c2", "e4b1",
				"e4f3", "e4g2", "e4h1",
				"e4d5", "e4c6", "e4b7", "e4a8",
				"e4f5", "e4g6", "e4h7",
				"e4f4", "e4g4", "e4h4", 
				"e4e5", "e4e6", "e4e7", "e4e8", 
				"e4e3", "e4e2", "e4e1",
				"e4d4", // this ray blocked by own pawn
				"c4c5"); // pawn can also move
		assertMoves(whiteToMove("Qe4", "pc6"),
				"e4d3", "e4c2", "e4b1",
				"e4f3", "e4g2", "e4h1",
				"e4d5", "e4c6", // this ray blocked by enemy pawn
				"e4f5", "e4g6", "e4h7",
				"e4f4", "e4g4", "e4h4", 
				"e4e5", "e4e6", "e4e7", "e4e8", 
				"e4e3", "e4e2", "e4e1", 
				"e4d4", "e4c4", "e4b4", "e4a4");
		assertMoves(whiteToMove("Qe4", "pd4"),
				"e4d3", "e4c2", "e4b1",
				"e4f3", "e4g2", "e4h1",
				"e4d5", "e4c6", "e4b7", "e4a8",
				"e4f5", "e4g6", "e4h7",
				"e4f4", "e4g4", "e4h4", 
				"e4e5", "e4e6", "e4e7", "e4e8", 
				"e4e3", "e4e2", "e4e1",
				"e4d4"); // this ray blocked by enemy pawn
	}
	
	@Test
	public void testKingMoves() {
		assertMoves(whiteToMove("Ke4"),
				"e4d5", "e4e5", "e4f5", "e4d4", "e4f4", "e4d3", "e4e3", "e4f3");
	}
	
	@Test
	public void testKingMovesInCheck() {
		assertMoves(whiteToMove("Ke4", "re8"),
				"e4d5", "e4f5", "e4d4", "e4f4", "e4d3", "e4f3");
	}
	
	@Test
	public void testKingNotInCheck() {
		Chess chess = newChess(Side.White, "Ka1", "rb8");
		assertEquals(false, chess.isCheck(Side.White));
		assertEquals(false, chess.isFinished());
		assertMoves(getValidMoves(chess), "a1a2");
	}	
	
	@Test
	public void testKingInCheck() {
		Chess chess = newChess(Side.White, "Ka1", "ra8");
		assertEquals(true, chess.isCheck(Side.White));
		assertEquals(false, chess.isFinished());
		assertMoves(getValidMoves(chess), "a1b1", "a1b2");
	}	
	
	@Test
	public void testKingInCheckPawn() {
		Chess chess = newChess(Side.White, "Ka1", "pb2");
		assertEquals(true, chess.isCheck(Side.White));
		assertEquals(false, chess.isFinished());
		assertMoves(getValidMoves(chess), "a1a2", "a1b1", "a1b2");
	}	

	@Test
	public void testKingCannotMoveIntoPawnThreat() {
		Chess chess = newChess(Side.White, "Ka1", "pa2");
		assertEquals(false, chess.isCheck(Side.White));
		assertEquals(false, chess.isFinished());
		assertMoves(getValidMoves(chess), "a1a2", "a1b2");
	}	

	@Test
	public void testKingInCheckKillAttacker() {
		Chess chess = newChess(Side.White, "Ka1", "ra8", "Rh8");
		assertEquals(true, chess.isCheck(Side.White));
		assertEquals(false, chess.isFinished());
		assertMoves(getValidMoves(chess), "a1b1", "a1b2", "h8a8");
	}	
	
	@Test
	public void testPieceWillLeaveKingInCheck() {
		Chess chess = newChess(Side.White, "Ka1", "Ba4", "ra8");
		assertEquals(false, chess.isCheck(Side.White));
		assertEquals(false, chess.isFinished());
		assertMoves(getValidMoves(chess), "a1a2", "a1b1", "a1b2");
	}	
	
	@Test
	public void testKingMate() {
		Chess chess = newChess(Side.White, "Ka1", "ra8", "rb8");
		assertEquals(true, chess.isCheck(Side.White));
		assertEquals(true, chess.isFinished());
		assertMoves(getValidMoves(chess)); // no moves
	}
	
	@Test
	public void testKingPatt() {
		Chess chess = newChess(Side.White, "Ka1", "rh2", "rb8");
		assertEquals(false, chess.isCheck(Side.White));
		assertEquals(true, chess.isFinished());
		assertMoves(getValidMoves(chess)); // no moves
	}	

	@Test
	public void testKingsThreateningEachOther() {
		Chess chess = newChess(Side.White, "Ka1", "kc1");
		assertEquals(false, chess.isCheck(Side.White));
		assertEquals(false, chess.isFinished());
		assertMoves(getValidMoves(chess), "a1a2");
	}	

	private List<String> whiteToMove(String... positions) {
		Chess chess = newChess(Side.White, positions);
		return getValidMoves(chess);
	}

	private List<String> blackToMove(String... positions) {
		Chess chess = newChess(Side.Black, positions);
		return getValidMoves(chess);
	}

	private Chess newChess(Side sideToMove, String... positions) {
		Chess chess = new Chess();
		chess.clear();
		
		for (String position : positions) {
			chess.addPosition(position);
		}
		
		chess.setSideToMove(sideToMove);
		
		return chess;
	}
	
	private List<String> getValidMoves(Game game) {
		return onlyMoves(game.getValidMoves());
	}
	
	private List<String> onlyMoves(List<Tuple2<String, Double>> movesWithValues) {
		return movesWithValues.stream()
			.map(moveWithValue -> moveWithValue.getValue1())
			.collect(Collectors.toList());
	}
	
	private void assertMoves(List<String> actualMoves, String... expectedMoves) {
		List<String> remainingActualMoves = new ArrayList<>(actualMoves);
		
		for(String expectedMove : expectedMoves) {
			boolean found = remainingActualMoves.remove(expectedMove);
			assertTrue("expected " + expectedMove + " not found in " + actualMoves, found);
		}
		assertTrue("unexpected moves: " + remainingActualMoves, remainingActualMoves.isEmpty());
	}
}
