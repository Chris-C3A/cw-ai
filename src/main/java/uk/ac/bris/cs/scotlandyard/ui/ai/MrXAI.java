package uk.ac.bris.cs.scotlandyard.ui.ai;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import com.google.common.collect.ImmutableSet;
import com.google.common.graph.ImmutableValueGraph;

import io.atlassian.fugue.Pair;
import uk.ac.bris.cs.scotlandyard.model.Board;
import uk.ac.bris.cs.scotlandyard.model.Move;
import uk.ac.bris.cs.scotlandyard.model.Piece;
import uk.ac.bris.cs.scotlandyard.model.Board.GameState;
import uk.ac.bris.cs.scotlandyard.model.Piece.MrX;
import uk.ac.bris.cs.scotlandyard.model.ScotlandYard.Ticket;
import uk.ac.bris.cs.scotlandyard.model.ScotlandYard.Transport;

import javax.annotation.Nonnull;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import uk.ac.bris.cs.scotlandyard.model.*;

// import uk.ac.bris.cs.scotlandyard.ui.ai.Score;

public class MrXAI implements Ai {

    @Nonnull
    @Override 
    public String name() { return "MrX Zabre AI"; }

    @Nonnull
    @Override 
    public Move pickMove(@Nonnull Board board, Pair<Long, TimeUnit> timeoutPair) {

        GameState state = (GameState) board;

        // AI implementations
        // Scoring Function
        // MiniMax algorithm
        // montecarlo

        Score maxScore = null;
        // Move bestMove = null;

        // get available moves for mrX in current round
        var moves = board.getAvailableMoves().asList();

        for (Move move : moves) {
            GameState nextState = state.advance(move);

            // get score of state
            Score score = new Score(move, nextState);
            
            // int score = Score(getMrXLocationFromMove(move), nextState, move).scoreState();
            // int score = this.score(getMrXLocationFromMove(move), nextState, move);

            // update maxScore
            if (maxScore == null || score.getScore() > maxScore.getScore()) {
                maxScore = score;
                // bestMove = move;
            }

            System.out.println("Score:" + score.getScore());
            System.out.println("Move:" + score.getMove());
        }

        System.out.println("maxScore:" + maxScore.getScore());
        // return moves.get(new Random().nextInt(moves.size()));
        System.out.println("Best Move: " + maxScore.getMove().toString());
        // return bestMove;
        return maxScore.getMove();
    }

    // private int score(int mrXlocation, Board board, Move move) {
    //     // TODO implement game winning / losing scores

    //     // Constants
    //     int C = 12;

    //     // detective locations
    //     // mrX location
    //     // distance mrx and detectives (dijkstra's algorithm)
    //     // number of moves available to mrX (more moves = better) (higher score) (nbr of desitnations from current location)
    //     // node station (taxi, bus, underground)
    //     // Integer mrXlocation = this.getMrXLocation(board).get();
        
    //     int score = 0;


    //     // get detective locations
    //     List<Integer> detectiveLocations = this.getDetectiveLocations(board);

    //     // nodes from mrX location
    //     Set<Integer> adjacentNodes = board.getSetup().graph.adjacentNodes(mrXlocation)
    //     .stream()
    //     .filter(node -> !occupiedLocation(detectiveLocations, node)).collect(ImmutableSet.toImmutableSet());

    //     // number of ajacent nodes
    //     int nbrOfNodes = adjacentNodes.size(); // part of score

    //     score += (nbrOfNodes * C);// 12

    //     // dijkstra's algorithm
    //     for (Integer detectiveLocation : detectiveLocations) {
    //         // shortest path from mrX to detective
    //         int shortestPath = this.ShortestPathFromSourceToDestination(board.getSetup().graph, mrXlocation, detectiveLocation);

    //         System.out.println("MrX: " + mrXlocation);
    //         System.out.println("Detective:" + detectiveLocation);
    //         System.out.println("Shortest Path:" + shortestPath);

    //         score += shortestPath;
    //     }

    //     // tickets score
    //     score += moveTicketScore(move);

 
    //    return score;

    //     // a good move for mrx is to get away from the detectives and be in a position with many options to move
    //     // return score of the current board
    //     // using this method we select the best move for mrX to take
    // }


//     private List<Integer> getDetectiveLocations(Board board) {
//         List<Integer> detectiveLocations = new ArrayList<Integer>();
//         // get player locations
//         for (Piece player : board.getPlayers()) {
//             if (player.isDetective()) {
//                 Optional<Integer> location = board.getDetectiveLocation((Piece.Detective) player);

//                 if (location.isPresent()) {
//                     detectiveLocations.add(location.get());
//                 }
//             }
//         }

//         return detectiveLocations;
//     }

//     private static boolean occupiedLocation(List<Integer> detectiveLocations, int destination) {
//         for (Integer location : detectiveLocations) {
//             if (location == destination) {
//                 return true;
//             }
//         }
//         return false;
//     }

//     private Integer moveTicketScore(Move move) {
//         return move.accept(new Move.Visitor<Integer>() {
//             @Override
//             public Integer visit(Move.SingleMove move) {
//                 if (move.ticket == Ticket.TAXI)
//                     return 30;
//                 else if (move.ticket == Ticket.BUS)
//                     return 20;
//                 else if (move.ticket == Ticket.UNDERGROUND)
//                     return 10;
//                 else if (move.ticket == Ticket.SECRET)
//                     return 4;
//                 else
//                     return 0;
//             }

//             @Override
//             public Integer visit(Move.DoubleMove move) {
//                 int score = 0;
//                 for (Ticket ticket : move.tickets()) {
//                     if (ticket == Ticket.TAXI)
//                         score += 4;
//                     else if (ticket == Ticket.BUS)
//                         score += 3;
//                     else if (ticket == Ticket.UNDERGROUND)
//                         score += 2;
//                     else if (ticket == Ticket.SECRET)
//                         score += 1;
//                 }
//                 return score;
//             }
//         });
//     }

// private Integer ShortestPathFromSourceToDestination(ImmutableValueGraph<Integer, ImmutableSet<Transport>> graph, int source, int destination) {
//     int Infinity = Integer.MAX_VALUE;
//     int size = graph.nodes().size();


//     Integer dist[] = new Integer[size+1];
//     Integer prev[] = new Integer[size+1];
//     Boolean unvisited[] = new Boolean[size+1];


//     List<Integer> shortestPath = new ArrayList<>();

//     for (int i = 0; i <= size; i++) {
//         dist[i] = Infinity;
//         prev[i] = null; // undefined previous
//         unvisited[i] = false;
//     }

//     // dist of source
//     dist[source] = 0;

//     for (int i = 0; i < size; i++) {
//         Integer u = findMinDistance(dist, unvisited);

//         // only interested in destination
//         if (u == destination) {
//             // if (prev[u] == null || u == source) {
//             while (prev[u] != null || u != source) {
//                 shortestPath.add(u);
//                 u = prev[u];
//             }

//             shortestPath.add(source); // add source node to the shortest path
//             Collections.reverse(shortestPath); // reverse the list to get the correct order
//             System.out.println(shortestPath);
//             // distance to destination
//             return dist[destination];
//         }

//         unvisited[u] = true;
//         for (int v : graph.adjacentNodes(u)) {
//             ImmutableSet<Transport> transports = graph.edgeValue(u, v).get();
//             int edgeWeight = getTransportsWeight(transports);

//             if (!unvisited[v] && !graph.edgeValue(u, v).isEmpty() && (dist[u] + edgeWeight < dist[v])) {
//                 dist[v] = dist[u] + edgeWeight;
//                 prev[v] = u;
//             }
//         }

//     }

//     return -1;
// }

// 	private static int findMinDistance(Integer[] distance, Boolean[] visitedVertex) {
// 		int minDistance = Integer.MAX_VALUE;
// 		int minDistanceVertex = -1;

// 		for (int i = 0; i < distance.length; i++) {
// 			if (!visitedVertex[i] && distance[i] < minDistance) {
// 				minDistance = distance[i];
// 				minDistanceVertex = i;
// 			}
// 		}

// 		return minDistanceVertex;
// 	}

//     private static int getTransportsWeight(ImmutableSet<Transport> transports) {
//         int weight = 0;

//         for (Transport transport : transports) {
//             switch (transport) {
//                 case TAXI:
//                     weight += 8;
//                     break;
//                 case BUS:
//                     weight += 4;
//                     break;
//                 case UNDERGROUND:
//                     weight += 1;
//                     break;
//             }
//         }

//         return weight;
//     }


}
        // for (Ticket ticket : tickets) {
        //     if (ticket == Ticket.DOUBLE) {

        //     }
        //     if (ticket == Ticket.TAXI) {
        //         score += 20;
        //     } else if (ticket == Ticket.BUS) {
        //         score += 16;
        //     } else if (ticket == Ticket.UNDERGROUND) {
        //         score += 8;
        //     } else if (ticket == Ticket.DOUBLE) {
        //         score += 2;
        //     } else if (ticket == Ticket.SECRET) {
        //         score += 1;
        //     }
        // }