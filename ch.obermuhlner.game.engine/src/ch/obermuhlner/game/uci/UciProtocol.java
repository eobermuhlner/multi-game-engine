package ch.obermuhlner.game.uci;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Arrays;

import ch.obermuhlner.game.Engine;
import ch.obermuhlner.game.Side;
import ch.obermuhlner.game.engine.random.MonteCarloEngine;
import ch.obermuhlner.game.tictactoe.TicTacToe;

public class UciProtocol {

	private final BufferedReader in;
	private final PrintWriter out;
	private Engine<?> engine;

	private volatile boolean stop;

	public UciProtocol() {
		this(createTicTacToeEngine());
	}
	
	public UciProtocol(Engine<?> engine) {
		this(System.in, System.out, engine);
	}
	
	public UciProtocol(InputStream inputStream, OutputStream outputStream, Engine<?> engine) {
		in = new BufferedReader(new InputStreamReader(inputStream));
		out = new PrintWriter(outputStream, true);
		
		this.engine = engine;
	}
	
	public void run() {
		try {
			String line = in.readLine();
			while (line != null) {
				if (line.isEmpty()) {
					continue;
				}
				
				String[] args = line.split(" +");
				if (args.length > 0) {
					try {
						execute(args);
					} catch(Exception ex) {
						ex.printStackTrace();
					}
				}
				
				line = in.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void execute(String[] args) {
		switch(args[0]) {
		case "quit":
			System.exit(0);
			break;
		case "stop":
			System.out.println("Stopping");
			stop = true;
			break;
		case "uci":
			executeUci(args);
			break;
		case "game":
			executeGame(args);
			break;
		case "ucinewgame":
			executeUcinewgame(args);
			break;
		case "isready":
			executeIsready(args);
			break;
		case "position":
			executePosition(args);
			break;
		case "go":
			executeGo(args);
			break;
		case "move":
			executeMove(args);
			break;
		case "d":
		case "diagram":
			executeDiagram(args);
			break;
		default:
			println("Unknown command: " + Arrays.toString(args));
		}
	}

	private void executeUci(String[] args) {
		println("id name guppy 0.1");
		println("id author Eric Obermuhlner");
		println("uciok");
	}

	private void executeGame(String[] args) {
		switch(args[1]) {
		case "tictactoe":
			engine = createTicTacToeEngine();
		}
	}

	private static MonteCarloEngine<TicTacToe> createTicTacToeEngine() {
		return new MonteCarloEngine<>(new TicTacToe());
	}

	private void executeUcinewgame(String[] args) {
		// does nothing
	}
	
	private void executeIsready(String[] args) {
		println("readyok");
	}

	private void executeDiagram(String[] args) {
		println(engine.getGame().getDiagram());
		println("FEN " + engine.getGame().getState());
	}

	private void executeGo(String[] args) {
		long thinkingMilliseconds = calculateThinkingTime(args);
		
		if (thinkingMilliseconds == 0) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// ignore
			}
		}
		
		stop = false;
		println("bestmove " + engine.bestMove());
		/*
		CalculationState<String> calculateBestMove = chessEngine.bestMove(thinkingMilliseconds);
		new Thread(() -> {
			while (!calculateBestMove.isFinished() && !stop) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// ignore
				}
			}
			String bestMove = calculateBestMove.getResult();
			println("bestmove " + bestMove);
		}).start();
		*/
	}

	private void executeMove(String[] args) {
		engine.getGame().move(args[1]);
	}
	
	private long calculateThinkingTime(String[] args) {
		long whiteTime = -1;
		long blackTime = -1;
		long moveTime = -1;
		int movesToGo = -1;
		
		for (int i = 1; i < args.length; i++) {
			switch (args[i]) {
			case "wtime":
				whiteTime = Long.parseLong(args[++i]);
				break;
			case "btime":
				blackTime = Long.parseLong(args[++i]);
				break;
			case "movestogo":
				movesToGo = Integer.parseInt(args[++i]);
				break;
			case "movetime":
				moveTime = Long.parseLong(args[++i]);
				moveTime = Math.max(0, moveTime - 1500); // reserve time for inaccuracies
				break;
			case "depth":
				moveTime = Integer.parseInt(args[++i]) * 100;
				break;
			case "infinity":
				moveTime = Integer.MAX_VALUE;
				break;
			}
		}

		if (moveTime >= 0) {
			return moveTime;
		}
		
		if (whiteTime >= 0 && blackTime >= 0) {
			boolean whiteToMove = engine.getGame().getSideToMove() == Side.White;
			moveTime = whiteToMove ? whiteTime : blackTime;
			if (movesToGo < 0) {
				movesToGo = 40;
			}
			moveTime = (int) ((moveTime / 2.0) / (movesToGo / 4.0));
		}
		
		if (moveTime < 0) {
			moveTime = 5000;
		}
		
		return moveTime;
	}

	private void executePosition(String[] args) {
		int argIndex = 1;
		while (argIndex < args.length) {
			switch(args[argIndex]) {
			case "startpos":
				engine.getGame().setStartPosition();
				break;
			case "fen":
				String fen = "";
				for (int i = 0; i < 6; i++) {
					fen += args[++argIndex] + " ";
				}
				engine.getGame().setState(fen);
				break;
			case "moves":
				argIndex++;
				while (argIndex < args.length) {
					engine.getGame().move(args[argIndex++]);
				}
				break;
			default:
				println("Unknown position option: " + args[argIndex]);
			}
			
			argIndex++;
		}
	}

	private void println(String message) {
		out.println(message);
	}

	public static void main(String[] args) {
		UciProtocol uciProtocol = new UciProtocol();
		uciProtocol.run();
	}

}
