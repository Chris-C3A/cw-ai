package uk.ac.bris.cs.scotlandyard.ui.ai;


import io.atlassian.fugue.Pair;
import uk.ac.bris.cs.scotlandyard.model.Board;
import uk.ac.bris.cs.scotlandyard.model.Move;
import uk.ac.bris.cs.scotlandyard.model.Board.GameState;

import javax.annotation.Nonnull;
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

        // GameState state = (GameState) board;


        MiniMax miniMax = new MiniMax();

        // AI implementations
        // Scoring Function
        // MiniMax algorithm
        // montecarlo

        int maxScore = Integer.MIN_VALUE;
        Move bestMove = null;

        // get available moves for mrX in current round
        var moves = board.getAvailableMoves().asList();

        int mrXLocation = moves.get(0).source();
        State state = new State((GameState) board, mrXLocation);



        // miniMax.minimax(state, mrXLocation, 1, Integer.MIN_VALUE, Integer.MAX_VALUE, false);


        // ! minimax implementation
        // int score = miniMax.minimax(state, 3, Integer.MIN_VALUE, Integer.MAX_VALUE);
        // System.out.println("score: " + score);
        for (Move move: moves) {
            State nextState = state.advanceMrX(move);

            // Test
            // LogEntry mrXLocationTest = nextState.getBoard().getMrXTravelLog().get(nextState.getMrXTravelLog().size()-1);
            // System.out.println("location from log:" + mrXLocationTest.location().isEmpty());

            // depth of 2 minimax
            int score = miniMax.minimax(nextState, move, 6, Integer.MIN_VALUE, Integer.MAX_VALUE);

            if (score > maxScore) {
                maxScore = score;
                bestMove = move;
            }
            System.out.println("score: " + score + " move: " + move.toString());
        }
        //! idea: store all scores for each move
        //! filter moves based on round number (revealing round) filter out double moves unless its score is really high and other single moves are bad
        //! filter out secret moves unless its after a revealing round or its score is really high and other moves are bad


        System.out.println("nbr of runs: " + miniMax.getNumberOfRuns());

        System.out.println("best move:");
        System.out.println(bestMove.toString() + " score: " + maxScore);


        return bestMove;
        // return moves.get(0);
    }

}