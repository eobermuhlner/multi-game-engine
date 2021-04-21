package ch.obermuhlner.game.chess;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ch.obermuhlner.game.LookupTable;

// https://github.com/niklasf/lila-tablebase#http-api
// http://tablebase.lichess.ovh/standard?fen=4k3/6KP/8/8/8/8/7p/8_w_-_-_0_1
public class SyzygyRestLookupTable implements LookupTable<Chess> {

	private static final Pattern BEST_MOVE_PATTERN = Pattern.compile("\"uci\"\\s*:\\s*\"([a-h1-8nbrq]+)\"");
	
	@Override
	public String bestMove(Chess chess) {
		String fen = chess.getState();

		return bestMove(chess.getState());
	}

	public String bestMove(String fen) {
		String json = getHttp(escapeUrl("http://tablebase.lichess.ovh/standard?fen=" + fen));
		if (json == null) {
			return null;
		}

		//System.out.println("info syzygy json " + json.replace('\n', ' '));
		String bestMove = getJsonBestMove(json);
		System.out.println("info syzygy best " + bestMove);
		return bestMove;
	}

	private String getHttp(String url) {
		try {
			StringBuilder result = new StringBuilder();
			HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
			conn.setRequestMethod("GET");
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
				String line;
				while ((line = reader.readLine()) != null) {
					result.append(line);
				}
			}
			return result.toString();
		} catch (ProtocolException e) {
			// ignore
		} catch (MalformedURLException e) {
			// ignore
		} catch (IOException e) {
			// ignore
		}
		return null;
	}

	private static String escapeUrl(String string) {
		return string.replaceAll(" ", "%20");
	}

	/*
{"checkmate":false,"stalemate":false,"variant_win":false,"variant_loss":false,"insufficient_material":false,"wdl":2,"dtz":1,"dtm":17,"moves":[{"uci":"h7h8q","san":"h8=Q+","zeroing":true,"checkmate":false,"stalemate":false,"variant_win":false,"variant_loss":false,"insufficient_material":false,"wdl":-2,"dtz":-2,"dtm":-16},{"uci":"h7h8r","san":"h8=R+","zeroing":true,"checkmate":false,"stalemate":false,"variant_win":false,"variant_loss":false,"insufficient_material":false,"wdl":-2,"dtz":-2,"dtm":-26},{"uci":"g7g8","san":"Kg8","zeroing":false,"checkmate":false,"stalemate":false,"variant_win":false,"variant_loss":false,"insufficient_material":false,"wdl":0,"dtz":0,"dtm":0},{"uci":"g7f6","san":"Kf6","zeroing":false,"checkmate":false,"stalemate":false,"variant_win":false,"variant_loss":false,"insufficient_material":false,"wdl":2,"dtz":1,"dtm":19},{"uci":"g7g6","san":"Kg6","zeroing":false,"checkmate":false,"stalemate":false,"variant_win":false,"variant_loss":false,"insufficient_material":false,"wdl":2,"dtz":1,"dtm":19},{"uci":"g7h6","san":"Kh6","zeroing":false,"checkmate":false,"stalemate":false,"variant_win":false,"variant_loss":false,"insufficient_material":false,"wdl":2,"dtz":1,"dtm":19},{"uci":"g7h8","san":"Kh8","zeroing":false,"checkmate":false,"stalemate":false,"variant_win":false,"variant_loss":false,"insufficient_material":false,"wdl":2,"dtz":1,"dtm":19},{"uci":"h7h8n","san":"h8=N","zeroing":true,"checkmate":false,"stalemate":false,"variant_win":false,"variant_loss":false,"insufficient_material":false,"wdl":2,"dtz":1,"dtm":19},{"uci":"h7h8b","san":"h8=B","zeroing":true,"checkmate":false,"stalemate":false,"variant_win":false,"variant_loss":false,"insufficient_material":false,"wdl":2,"dtz":1,"dtm":15}]}
	 */
	private String getJsonBestMove(String json) {
		try (BufferedReader reader = new BufferedReader(new StringReader(json))) {
			String line = reader.readLine();
			while (line != null) {
				Matcher matcher = BEST_MOVE_PATTERN.matcher(line);
				if (matcher.find()) {
					return matcher.group(1);
				}
				
				line = reader.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
