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
            // get remaining detective pieces
            List<Piece> remainingDetectivePieces = state.getRemainingDetectivePieces();

            // initialize bestMoves list
            List<Move> bestMoves = new ArrayList<>();

            // go through all remaining detective pieces
            for (Piece detective : remainingDetectivePieces) {
                // get detective moves
                List<Move> detectiveMoves = state.getAvailableMoves().stream()
                    .filter(m -> m.commencedBy().equals(detective))
                    .collect(Collectors.toList());

                // get best move for detective
                Move bestMove = State.getDetectiveBestMove(detective, detectiveMoves, state);

                // add best move to bestMoves list
                bestMoves.add(bestMove);

            }

            // get first best move
            Move moveToPlay = bestMoves.get(0);

            // play move
            State nextState = state.advanceDetective(moveToPlay);

            // get score of detective
            int detectiveScore = new ScoreDetective(moveToPlay.commencedBy(), moveToPlay, nextState).getScore();

            // update beta
            beta = Math.min(beta, detectiveScore);

            // recursive call to minimax
            int eval = minimax(nextState, move, round, depth - 1, alpha, beta);

            return eval;
        }
    }

    public int getNumberOfRuns() {
        return this.nb_of_runs;
    }

}
