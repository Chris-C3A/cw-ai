package uk.ac.bris.cs.scotlandyard.ui.ai;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableSet;
import com.google.errorprone.annotations.Immutable;

import uk.ac.bris.cs.scotlandyard.model.Move;
import uk.ac.bris.cs.scotlandyard.model.Piece;
import uk.ac.bris.cs.scotlandyard.model.Board.GameState;

public class MiniMax {

    // MiniMax algorithm
    public int minimax(GameState state, Move move, int depth, int alpha, int beta, Boolean maximizingPlayer) {
        if (depth == 0 || !state.getWinner().isEmpty()) {
            return new Score(move, state, maximizingPlayer).getScore();
            // return new Score(move, state);
        }

        if (maximizingPlayer) {
            int maxEval = Integer.MIN_VALUE;
            // Score maxEval = null;

            for (Move nextMove : state.getAvailableMoves()) {
                GameState nextState = state.advance(nextMove);

                int eval = minimax(nextState, nextMove, depth - 1, alpha, beta, false);

                // if (maxEval == null || eval.getScore() > maxEval.getScore()) {
                //     maxEval = eval;
                // }
                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, eval);
                if (beta <= alpha) {
                    break;
                }
            }

            return maxEval;
        } else {
            // TODO take into consideration the different players
            int minEval = Integer.MAX_VALUE;
            // Score minEval = null;

            // loop through detectives and play their best move against mrX (score detectives)
            // get score for the detectives then get min evaluation of the board against mrX scoremrx?


            // get moves for a certain detctive?
            // check remaining players?
            // jfkdjfkdj

            // loop through detectives
            // GameState nextState = state;
            // Move bestMove = null;
            // for (Piece player : state.getPlayers()) {
            //     if (player.isDetective()) {
            //         List<Move> detectiveMoves = state.getAvailableMoves().stream().filter( (pmove) -> pmove.commencedBy() == player).collect(Collectors.toList());
            //         bestMove = detectiveMoves.get(new Random().nextInt(detectiveMoves.size()));
            //         // advance best move for detective
            //         // (random move for now)
            //         nextState = nextState.advance(bestMove);

            //     }
            // }

            // // switch to mrX
            // int eval = minimax(nextState, move, depth - 1, alpha, beta, true);

            // minEval = Math.min(minEval, eval);
            // beta = Math.min(beta, eval);
            // if (beta <= alpha) {
            //     break;
            // }

            // get best move for detectives and play
            for (Move nextMove : state.getAvailableMoves()) {
                GameState nextState = state.advance(nextMove);

                int eval = minimax(nextState, nextMove, depth - 1, alpha, beta, nextState.getAvailableMoves().stream().allMatch(pmove -> pmove.commencedBy() == Piece.MrX.MRX));

                // if (minEval == null || eval.getScore() < minEval.getScore()) {
                //     minEval = eval;
                // }

                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, eval);
                if (beta <= alpha) {
                    break;
                }
            }

            return minEval;
        }

        // return 0;
    }

    // private long evaluateState(Map<Colour, Integer> playersLocations, ScotlandYardGraph graph, Map<Colour, Map<Ticket, Integer>> playersTickets) {

	// 	List<Colour> players = getPlayersInOrder(view);

	// 	List<Integer> distancesFromMrXToDetectives = new ArrayList<Integer>();

	// 	int mrXLocation = playersLocations.get(colour);

	// 	for(Colour player : players) {
	// 		if(Utility.isPlayerMrX(player)) continue;
	// 		int detectiveLocation = playersLocations.get(player);
	// 		int currentDistance = distances.get(mrXLocation).get(detectiveLocation);
	// 		distancesFromMrXToDetectives.add(currentDistance);
	// 		//System.out.println("Distance from: " + colour + " to: " + player + ", is: " + currentDistance);
	// 	}
	// 	long heuristic = 0;
	// 	for(Integer distance : distancesFromMrXToDetectives) {
	// 		long distanceWeight = 100000;
	// 		if(distance != 0) distanceWeight = 210 / distance;
	// 		heuristic -= distanceWeight;
	// 	}
		
	// 	//now if there are more means of transport on Mr.X's position, increase the value of the score
	// 	//after all, the more options, the better escape plan we can conceive
	// 	List<Edge<Integer, Transport>> edges = graph.getEdgesFrom(graph.getNode(mrXLocation));
	// 	for(Edge<Integer, Transport> edge : edges) {
	// 		Transport kindOfTransport = edge.getData();
	// 		int value = 10;
	// 		switch(kindOfTransport) {
	// 		case Boat:
	// 			value = 100;
	// 			break;
	// 		case Underground:
	// 			value = 60;
	// 			break;
	// 		case Bus:
	// 			value = 25;
	// 			break;
	// 		}
	// 		heuristic += value;
	// 	}
	// 	return heuristic;
	// }
}
