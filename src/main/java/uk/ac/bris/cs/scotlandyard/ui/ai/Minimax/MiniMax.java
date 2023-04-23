package uk.ac.bris.cs.scotlandyard.ui.ai.Minimax;

import java.util.List;
import java.util.stream.Collectors;

import uk.ac.bris.cs.scotlandyard.model.Move;
import uk.ac.bris.cs.scotlandyard.model.Piece;
import uk.ac.bris.cs.scotlandyard.ui.ai.State;
import uk.ac.bris.cs.scotlandyard.ui.ai.Score.Score;

public class MiniMax {
    private int nb_of_runs = 0;

    // MiniMax algorithm
    public int minimax(State state, Move move, int round, int depth, int alpha, int beta) {
        this.nb_of_runs++;

        Boolean isMrXTurn = state.isMrxTurn();


        if (depth == 0 || state.isTerminal()) {
            // return new Score(move, state, maximizingPlayer).getScore();
            // Score score = new Score(state, isMrXTurn);
            // score.setMove(move);
            // return score.getScore();
            return new Score(move, state, isMrXTurn, round).getScore();
        }

        if (state.isMrxTurn()) {
            int maxEval = Integer.MIN_VALUE;

            for (Move nextMove : state.getAvailableMoves()) {
                State nextState = state.advanceMrX(nextMove);

                int eval = minimax(nextState, move, round, depth - 1, alpha, beta);

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
            List<Piece> remainingDetectivePieces = state.getDetectivePieces().stream()
                .filter(detective -> state.getAvailableMoves().stream().anyMatch(m -> m.commencedBy().equals(detective)))
                .collect(Collectors.toList());;

            Piece detective = remainingDetectivePieces.get(0);

            List<Move> detectiveMoves = state.getAvailableMoves().stream()
                .filter(m -> m.commencedBy().equals(detective))
                .collect(Collectors.toList());


            for (Move nextMove : detectiveMoves) {
                State nextState = state.advanceDetective(nextMove);

                int eval = minimax(nextState, move, round, depth - 1, alpha, beta);

                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, eval);
                if (minEval <= alpha) {
                    break;
                }
            }

            // get best move for detectives and play
            // ! get distint moves for each detective such that not all possibilites are tested
            // ! find a way to play the best move for each detective
            // ! play the move taht gets closer to mrX (dijkstra's algorithm)


            // List<Move> bestMoves = new ArrayList<>();

            // // get remaining detectives
            // List<Piece> remainingDetectivePieces = state.getDetectivePieces().stream()
            //     .filter(detective -> state.getAvailableMoves().stream().anyMatch(m -> m.commencedBy().equals(detective)))
            //     .collect(Collectors.toList());;

            // for (Piece detective : remainingDetectivePieces) {
            //     // get detective moves
            //     List<Move> detectiveMoves = state.getAvailableMoves().stream()
            //         .filter(m -> m.commencedBy().equals(detective))
            //         .collect(Collectors.toList());

            //     // Move bestMove = detectiveMoves.get(0); //! get best move for detective (to implement later with dijkstra's algorithm)
            //     Move bestMove = State.getDetectiveBestMove(detective, detectiveMoves, state); //! get best move for detective (to implement later with dijkstra's algorithm

            //     bestMoves.add(bestMove);

            // }

            // !play one best move at a time

            // State nextState = state.advanceDetective(bestMoves.get(0));

            // ! not working way of playing all best moves
            // //
            // // play all best detective moves
            // State nextState = state;
            // for (Move nextMove : bestMoves) {
            //     // System.out.println(nextState.isTerminal());
            //     if (nextState.isTerminal()) {
            //         break;
            //     }
            //     nextState = nextState.advanceDetective(nextMove);

            //     // int eval = minimax(nextState, move, round, depth - 1, alpha, beta);

            //     // minEval = Math.min(minEval, eval);
            //     // beta = Math.min(beta, eval);
            //     // // System.out.println("beta: " + beta + "alpha: " + alpha);
            //     // if (minEval <= alpha) {
            //     //     break;
            //     // }
            // }
            // // System.out.println(nextState.isMrxTurn());
            // //


            // int eval = minimax(nextState, move, round, depth - 1, alpha, beta);
            // minEval = Math.min(minEval, eval);
            // beta = Math.min(beta, eval);


            return minEval;
        }
    }

    public int getNumberOfRuns() {
        return this.nb_of_runs;
    }

}
