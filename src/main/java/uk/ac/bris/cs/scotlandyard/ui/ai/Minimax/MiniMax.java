package uk.ac.bris.cs.scotlandyard.ui.ai.Minimax;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import uk.ac.bris.cs.scotlandyard.model.Move;
import uk.ac.bris.cs.scotlandyard.model.Piece;
import uk.ac.bris.cs.scotlandyard.ui.ai.State;
import uk.ac.bris.cs.scotlandyard.ui.ai.Score.ScoreMrX;
import uk.ac.bris.cs.scotlandyard.ui.ai.Score.ScoreDetective;

public class MiniMax {
    private int nb_of_runs = 0;

    // MiniMax algorithm
    public int minimax(State state, Move move, int round, int depth, int alpha, int beta) {
        this.nb_of_runs++;

        // true if it is MrX turn otherwise false (detective turn)
        Boolean isMrXTurn = state.isMrxTurn();

        if (depth == 0 || state.isTerminal()) {
            // return score of current state if depth is 0 or state is terminal
            return new ScoreMrX(move, state, round).getScore();
        }

        if (isMrXTurn) {
            int maxEval = Integer.MIN_VALUE;

            for (Move nextMove : state.getAvailableMoves()) {
                // play move
                State nextState = state.advanceMrX(nextMove);

                // recursive call to minimax
                int eval = minimax(nextState, move, round, depth - 1, alpha, beta);

                // compare maxEval
                maxEval = Math.max(maxEval, eval);

                // compare alpha
                alpha = Math.max(alpha, eval);

                // alpha-beta pruning
                if (maxEval <= alpha) {
                    break;
                }
            }

            return maxEval;
        } else {
            //             int minEval = Integer.MAX_VALUE;
            // List<Piece> remainingDetectivePieces = state.getDetectivePieces().stream()
            //     .filter(detective -> state.getAvailableMoves().stream().anyMatch(m -> m.commencedBy().equals(detective)))
            //     .collect(Collectors.toList());;

            // Piece detective = remainingDetectivePieces.get(0);

            // List<Move> detectiveMoves = state.getAvailableMoves().stream()
            //     .filter(m -> m.commencedBy().equals(detective))
            //     .collect(Collectors.toList());


            // for (Move nextMove : detectiveMoves) {
            //     State nextState = state.advanceDetective(nextMove);

            //     int eval = minimax(nextState, move, round, depth - 1, alpha, beta);

            //     minEval = Math.min(minEval, eval);
            //     beta = Math.min(beta, eval);
            //     if (minEval <= alpha) {
            //         break;
            //     }
            // }
            // get detective from remaining pieces
            // Pi)ece detective = state.getRemainingDetectivePieces();
            List<Piece> remainingDetectivePieces = state.getRemainingDetectivePieces();

            List<Move> bestMoves = new ArrayList<>();
            for (Piece detective : remainingDetectivePieces) {
                // get detective moves
                List<Move> detectiveMoves = state.getAvailableMoves().stream()
                    .filter(m -> m.commencedBy().equals(detective))
                    .collect(Collectors.toList());

                // Move bestMove = detectiveMoves.get(0); //! get best move for detective (to implement later with dijkstra's algorithm)
                //! test if selected move is the best move
                Move bestMove = State.getDetectiveBestMove(detective, detectiveMoves, state); //! get best move for detective (to implement later with dijkstra's algorithm

                bestMoves.add(bestMove);

            }

            // !play one best move at a time
            Move moveToPlay = bestMoves.get(0);

            State nextState = state.advanceDetective(moveToPlay);

            int detectiveScore = new ScoreDetective(moveToPlay.commencedBy(), moveToPlay, nextState).getScore();
            // if (detectiveScore < beta) beta = detectiveScore;
            beta = Math.min(beta, detectiveScore);


            int eval = minimax(nextState, move, round, depth - 1, alpha, beta);
            // // get list of possible moves for detective
            // List<Move> detectiveMoves = state.getDetectiveMoves(detective);

            // // get best move for detective
            // Move bestMove = State.getDetectiveBestMove(detective, detectiveMoves, state);

            // // play best move
            // State nextState = state.advanceDetective(bestMove);

            // // get detective score of current state
            // int detectiveScore = new ScoreDetective(bestMove.commencedBy(), bestMove, nextState).getScore();

            // // recursive call to minimax
            // int eval = minimax(nextState, move, round, depth - 1, alpha, beta);

            // // compare detective score with beta
            // beta = Math.min(beta, detectiveScore);

            return eval;
        }
    }

    public int getNumberOfRuns() {
        return this.nb_of_runs;
    }

}
