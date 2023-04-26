package uk.ac.bris.cs.scotlandyard.ui.ai.Minimax;

import java.util.List;

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
            // get detective from remaining pieces
            Piece detective = state.getRemainingDetectivePieces().get(0);
            
            // get list of possible moves for detective
            List<Move> detectiveMoves = state.getDetectiveMoves(detective);

            // get best move for detective
            Move bestMove = State.getDetectiveBestMove(detective, detectiveMoves, state);

            // play best move
            State nextState = state.advanceDetective(bestMove);

            // get detective score of current state
            int detectiveScore = new ScoreDetective(bestMove.commencedBy(), bestMove, nextState).getScore();

            // recursive call to minimax
            int eval = minimax(nextState, move, round, depth - 1, alpha, beta);

            // compare detective score with beta
            beta = Math.min(beta, eval);

            return eval;
        }
    }

    public int getNumberOfRuns() {
        return this.nb_of_runs;
    }

}
