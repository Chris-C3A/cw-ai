package uk.ac.bris.cs.scotlandyard.ui.ai;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableSet;
import com.google.errorprone.annotations.Immutable;

import uk.ac.bris.cs.scotlandyard.model.Move;
import uk.ac.bris.cs.scotlandyard.model.Piece;
import uk.ac.bris.cs.scotlandyard.model.Board.GameState;

public class MiniMax {
    private int nb_of_runs = 0;

    // MiniMax algorithm
    public int minimax(State state, Move move, int depth, int alpha, int beta) {
        this.nb_of_runs++;

        Boolean isMrXTurn = state.isMrxTurn();        

        if (depth == 0 || state.isTerminal()) {
            // return new Score(move, state, maximizingPlayer).getScore();
            // Score score = new Score(state, isMrXTurn);
            // score.setMove(move);
            // return score.getScore();
            return new Score(move, state, isMrXTurn).getScore();
        }

        if (state.isMrxTurn()) {
            int maxEval = Integer.MIN_VALUE;

            for (Move nextMove : state.getAvailableMoves()) {
                State nextState = state.advanceMrX(nextMove);

                int eval = minimax(nextState, move, depth - 1, alpha, beta);

                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, eval);
                if (maxEval <= alpha) {
                    break;
                }
            }

            return maxEval;
        } else {
            // TODO take into consideration the different players
            int minEval = Integer.MAX_VALUE;

            // get best move for detectives and play
            // ! get distint moves for each detective such that not all possibilites are tested
            // ! find a way to play the best move for each detective
            // ! play the move taht gets closer to mrX (dijkstra's algorithm)

            // System.out.println(state.getMrXLocation());
            // System.out.println(state.getAvailableMoves());
            //! get best move for each detective

            List<Move> bestMoves = new ArrayList<>();

            // get remaining detectives
            List<Piece> remainingDetectivePieces = state.getDetectivePieces().stream()
                .filter(detective -> state.getAvailableMoves().stream().anyMatch(m -> m.commencedBy().equals(detective)))
                .collect(Collectors.toList());;

            for (Piece detective : remainingDetectivePieces) {
                // get detective moves
                List<Move> detectiveMoves = state.getAvailableMoves().stream()
                    .filter(m -> m.commencedBy().equals(detective))
                    .collect(Collectors.toList());

                Move bestMove = detectiveMoves.get(0); //! get best move for detective (to implement later with dijkstra's algorithm)

                bestMoves.add(bestMove);

                // get best move for detective
                // Move bestMove = detectiveMoves.get(0);
                // int bestScore = Integer.MIN_VALUE;
                // for (Move move : detectiveMoves) {
                //     State nextState = state.advanceDetective(move);
                //     int score = new Score(nextState, isMrXTurn).getScore();
                //     if (score > bestScore) {
                //         bestScore = score;
                //         bestMove = move;
                //     }
                // }

            }

            for (Move nextMove : bestMoves) {
                State nextState = state.advanceDetective(nextMove);

                int eval = minimax(nextState, move, depth - 1, alpha, beta);

                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, eval);
                // System.out.println("beta: " + beta + "alpha: " + alpha);
                if (minEval <= alpha) {
                    break;
                }
            }

            return minEval;
        }
    }

    public int getNumberOfRuns() {
        return this.nb_of_runs;
    }

}
