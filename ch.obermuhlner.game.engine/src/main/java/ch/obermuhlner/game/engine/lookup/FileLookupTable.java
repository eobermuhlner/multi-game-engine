package ch.obermuhlner.game.engine.lookup;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import ch.obermuhlner.game.Game;
import ch.obermuhlner.game.LookupTable;
import ch.obermuhlner.util.GameUtil;
import ch.obermuhlner.util.Tuple2;

public class FileLookupTable<G extends Game> implements LookupTable<G> {

	private final Map<String, List<Tuple2<String, Double>>> fenToRecommendedMoves = new ConcurrentHashMap<>();

	private final Random random = new Random();
	
	private Supplier<G> gameSupplier;

	private List<String> lastMoves = new ArrayList<>();
	
	public FileLookupTable(Supplier<G> gameSupplier) {
		this.gameSupplier = gameSupplier;
	}
	
	public void load(Reader reader) {
		try (BufferedReader bufferedReader = new BufferedReader(reader)) {
			String line = bufferedReader.readLine();
			while (line != null) {
				if (!line.isEmpty() && !line.startsWith("#")) {
					parseLine(line);
				}
				
				line = bufferedReader.readLine();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void parseLine(String line) {
		String[] moves = line.split("\\s+");
		
		G game = gameSupplier.get();
		game.setStartPosition();
		for (int i = 0; i < moves.length; i++) {
			String move = moves[i];

			if (move.equals("#")) {
				break;
			}
			
			if (move.equals(".")) {
				move = lastMoves.get(i);
				game.move(move);
			} else {
				lastMoves = new ArrayList<>(lastMoves.subList(0, i));
				lastMoves.add(move);
				double probability = 1;
				if (moves.length > i + 1) {
					probability = Double.parseDouble(moves[++i]);
				}
				
				String fen = game.getPositionState();
				
				List<Tuple2<String, Double>> recommendedMoves = fenToRecommendedMoves.computeIfAbsent(fen, (key) -> new ArrayList<>());
				recommendedMoves.add(Tuple2.of(move, probability));
				game.move(move);
				break;
			}
		}
	}

	@Override
	public String bestMove(G game) {
		String fen = game.getPositionState();
		
		List<Tuple2<String, Double>> recommendedMoves = fenToRecommendedMoves.get(fen);
		
		if (recommendedMoves == null) {
			return null;
		}
		System.out.println("info opening " + recommendedMoves);
		return GameUtil.pickRandom(random, recommendedMoves);
	}
}
