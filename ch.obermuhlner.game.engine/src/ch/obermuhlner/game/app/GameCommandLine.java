package ch.obermuhlner.game.app;

import java.util.ArrayList;
import java.util.List;

import ch.obermuhlner.game.Engine;
import ch.obermuhlner.game.Game;
import ch.obermuhlner.game.Side;
import ch.obermuhlner.game.engine.random.MonteCarloEngine;
import ch.obermuhlner.game.engine.random.RandomEngine;
import ch.obermuhlner.game.tictactoe.TicTacToe;
import ch.obermuhlner.util.Tuple2;

public class GameCommandLine {

	public static <G extends Game> void playGame(Engine<G> engine) {
		int step = 0;
		
		while (!engine.getGame().isFinished()) {
			System.out.println("STEP " + step++);
			System.out.println(engine.getGame().getDiagram());
			//System.out.println("VALIDMOVES " + engine.getGame().getValidMoves());
			String move = engine.bestMove();
			System.out.println("MOVE " + move);
			engine.getGame().move(move);
		}

		System.out.println(engine.getGame().getDiagram());
		System.out.println("WINNER " + engine.getGame().getWinner());
	}
	
	public static <G extends Game> void playTournament(int rounds, List<Tuple2<String, Engine<G>>> players) {
		int n = players.size();
		int[] totalWins = new int[n];
		int[] totalLoss = new int[n];
		int[] totalDraw = new int[n];
		int[] playerWins = new int[n*n];
		int[] playerLoss = new int[n*n];
		int[] playerDraw = new int[n*n];
		int firstMoveWin = 0;
		int firstMoveLoss = 0;
		int bothMovesDraw = 0;
		
		Side firstMove = players.get(0).getValue2().getGame().getSideToMove();

		for (int round = 0; round < rounds; round++) {
			System.out.println("ROUND " + round);
			for (int player1Index = 0; player1Index < n; player1Index++) {
				Tuple2<String, Engine<G>> player1 = players.get(player1Index);
				for (int player2Index = 0; player2Index < n; player2Index++) {
					Tuple2<String, Engine<G>> player2 = players.get(player2Index);

					if (player1Index != player2Index) {
						Side winner = playGame(player1.getValue2(), player2.getValue2());
						System.out.printf("%-10s %-10s : %s\n", player1.getValue1(), player2.getValue1(), winner);
						if (winner == firstMove) {
							totalWins[player1Index]++;
							totalLoss[player2Index]++;
							playerWins[player1Index+player2Index*n]++;
							playerLoss[player2Index+player1Index*n]++;
							firstMoveWin++;
						} else if (winner == firstMove.otherSide()) {
							totalWins[player2Index]++;
							totalLoss[player1Index]++;
							playerWins[player2Index+player1Index*n]++;
							playerLoss[player1Index+player2Index*n]++;
							firstMoveLoss++;
						} else {
							totalDraw[player1Index]++;
							totalDraw[player2Index]++;
							playerDraw[player1Index+player2Index*n]++;
							playerDraw[player2Index+player1Index*n]++;
							bothMovesDraw++;
						}
					}
				}				
			}
			System.out.println();
		}
		
		System.out.println("TOTAL");
		for (int playerIndex = 0; playerIndex < n; playerIndex++) {
			Tuple2<String, Engine<G>> player = players.get(playerIndex);
			System.out.printf("%-10s : %2d wins, %2d losses, %2d draws\n", player.getValue1(), totalWins[playerIndex], totalLoss[playerIndex], totalDraw[playerIndex]);
		}
		System.out.println();
		
		for (int player1Index = 0; player1Index < n; player1Index++) {
			Tuple2<String, Engine<G>> player1 = players.get(player1Index);
			for (int player2Index = 0; player2Index < n; player2Index++) {
				Tuple2<String, Engine<G>> player2 = players.get(player2Index);
				
				if (player1Index != player2Index) {
					System.out.printf("%-10s %-10s : %2d wins, %2d losses, %2d draws\n", player1.getValue1(), player2.getValue1(), playerWins[player1Index+player2Index*n], playerLoss[player1Index+player2Index*n], playerDraw[player1Index+player2Index*n]);
				}
			}
		}
		System.out.println();
		
		System.out.printf("%d first move wins, %d first move loses, %d draws\n", firstMoveWin, firstMoveLoss, bothMovesDraw);

	}
	
	private static <G extends Game> Side playGame(Engine<G> player1, Engine<G> player2) {
		G game = player1.getGame();
		game.setStartPosition();
		
		boolean finished = false;
		while (!finished) {
			String move1 = player1.bestMove();
			game.move(move1);
			
			finished = game.isFinished();
			if (!finished) {
				String move2 = player2.bestMove();
				game.move(move2);

				finished = game.isFinished();
			}
		}
		
		System.out.println(game.getDiagram());
		
		return game.getWinner();
	}
	
	public static <G extends Game> void playTournament(int rounds, long maxThinkMilliseconds, G game) {
		List<Tuple2<String, Engine<G>>> players = new ArrayList<>();
		
		players.add(Tuple2.of("Random", new RandomEngine<>(game)));
		
		long thinkMilliseconds = 1;
		while (thinkMilliseconds <= maxThinkMilliseconds) {
			players.add(Tuple2.of("Think" + thinkMilliseconds, new MonteCarloEngine<>(game, thinkMilliseconds)));
			thinkMilliseconds *= 2;
		}
		
		playTournament(rounds, players);
	}

	public static void main(String[] args) {
		playTournament(10, 20000, new TicTacToe());
	}
}
