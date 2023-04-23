package uk.ac.bris.cs.scotlandyard.ui.ai.MrX;


import io.atlassian.fugue.Pair;
import uk.ac.bris.cs.scotlandyard.model.Board;
import uk.ac.bris.cs.scotlandyard.model.Move;
import uk.ac.bris.cs.scotlandyard.model.Board.GameState;
import uk.ac.bris.cs.scotlandyard.ui.ai.State;
import uk.ac.bris.cs.scotlandyard.ui.ai.Minimax.MiniMax;

import javax.annotation.Nonnull;
import java.util.concurrent.TimeUnit;

import uk.ac.bris.cs.scotlandyard.model.*;

// import uk.ac.bris.cs.scotlandyard.ui.ai.Score;

public class MiniMaxAI implements Ai {

    @Nonnull
    @Override 
    public String name() { return "MiniMax MrX AI"; }

    @Nonnull
    @Override 
    public Move pickMove(@Nonnull Board board, Pair<Long, TimeUnit> timeoutPair) {


        // Minimax class instance
        MiniMax miniMax = new MiniMax();


        // initilaize maxScore and bestMove
        int maxScore = Integer.MIN_VALUE;
        Move bestMove = null;

        // get available moves for mrX in current round
        var moves = board.getAvailableMoves().asList();

        // get mrX's location from available moves source location
        int mrXLocation = moves.get(0).source();

        // create new state Object with the current board state and mrX's location
        State state = new State((GameState) board, mrXLocation);


        // * minimax implementation
        // go through all available moves
        for (Move move: moves) {
            // get nextState
            State nextState = state.advanceMrX(move);


            // int score = miniMax.minimax(nextState, move, 4, Integer.MIN_VALUE, Integer.MAX_VALUE);

            // depth of 6 minimax
            // get score of move using minimax with deph of 6
            int score = miniMax.minimax(nextState, move, nextState.getRoundNumber(), 6, Integer.MIN_VALUE, Integer.MAX_VALUE);

            // compare score and keep track of the best move
            if (score > maxScore) {
                maxScore = score;
                bestMove = move;
            }
        }

        //! idea: store all scores for each move
        //! filter moves based on round number (revealing round) filter out double moves unless its score is really high and other single moves are bad
        //! filter out secret moves unless its after a revealing round or its score is really high and other moves are bad


        System.out.println("nbr of runs: " + miniMax.getNumberOfRuns());

        System.out.println("best move:");
        System.out.println(bestMove.toString() + " score: " + maxScore);


        return bestMove;
    }

}