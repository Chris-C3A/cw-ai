package uk.ac.bris.cs.scotlandyard.ui.ai;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;


import com.google.common.collect.ImmutableSet;
import io.atlassian.fugue.Pair;
import uk.ac.bris.cs.scotlandyard.model.*;

public class MyAi implements Ai {

	@Nonnull @Override public String name() { return "Manyak AI"; }

	@Nonnull @Override public Move pickMove(
			@Nonnull Board board,
			Pair<Long, TimeUnit> timeoutPair) {
		// returns a random move, replace with your own implementation

		// AI implementations
		// Scoring Function
		// MiniMax algorithm
		// neural network?
		this.score(board);

		var moves = board.getAvailableMoves().asList();
		return moves.get(new Random().nextInt(moves.size()));
	}

	private void score(Board board) {
		var moves = board.getAvailableMoves().asList(); // mrX moves

		ImmutableSet<Piece> players = board.getPlayers();

		// get player locations
		for (Piece player : players) {
			if (player.isDetective()) {
				// weird way of casting to get detective location
				int detective_location = board.getDetectiveLocation((Piece.Detective) player).orElseThrow();
				System.out.println(detective_location);
			}
		}
		// a good move for mrx is to get away from the detectives and be in a position with many options to move
		// return score of the current board
		// using this method we select the best move for mrX to take
	}

//	private Board shortestPathFromMrxToDetectives(Board board, Move move) {
//		GameSetup setup = board.getSetup();
//		var moves = board.getAvailableMoves().asList(); // mrX moves
//
//		int size = setup.graph.nodes().size();
//		int source = move.source();
//
//
//		int Infinity = Integer.MAX_VALUE;
//		Integer dist[] = new Integer[size];
//		Integer prev[] = new Integer[size];
//		Boolean unvisited[] = new Boolean[size];
//
//		// dijkstras algorithm to get a list queue of shortest paths from detectives location to each of the possible move
//		// then calculate score based on the distance from each of the detectives
//
//		for (int i = 0; i < size; i++) {
//			dist[i] = Infinity;
//			prev[i] = null; // undefined previous
//			unvisited[i] = false;
////			SPT[i] = false;
//		}
//
//		// dist of source
//		dist[source] = 0;
////		shortestPath.add(source);
//
//		for (int i = 0; i < size; i++) {
//			Integer u = findMinDistance(dist, unvisited);
//
//			// only interested in destination
//			if (u == destination) {
//				if (prev[u] == null || u == source) {
//					while (prev[u] != null) {
//						shortestPath.add(u);
//						u = prev[u];
//					}
//				}
//				return shortestPath;
//			}
//
//			unvisited[u] = true;
//			for (int v = 0; v < size; v++) {
//				if (!unvisited[v] && !setup.graph.edgeValue(u, v).isEmpty() && (dist[u] + setup.graph.edgeValue(u, v).get() < dist[v])) {
//					dist[v] = dist[u] + setup.graph.edgeValue(u, v).get();
//					prev[v] = u;
//				}
//			}
//		}
//
//		return null;
//	}
}
