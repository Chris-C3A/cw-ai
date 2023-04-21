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
import uk.ac.bris.cs.scotlandyard.ui.ai.MonteCarloTest.MonteCarlo;

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

        MiniMax miniMax = new MiniMax();

        // AI implementations
        // Scoring Function
        // MiniMax algorithm
        // montecarlo
        // System.out.println(state.get);

        int maxScore = Integer.MIN_VALUE;
        // Score maxScore = null;
        Move bestMove = null;

        // get available moves for mrX in current round
        var moves = board.getAvailableMoves().asList();

        int mrXLocation = moves.get(0).source();

        System.out.println(mrXLocation);


        // miniMax.minimax(state, mrXLocation, 1, Integer.MIN_VALUE, Integer.MAX_VALUE, false);


        // ! minimax implementation
        for (Move move: moves) {
            GameState nextState = state.advance(move);

            // Test
            LogEntry mrXLocationTest = nextState.getMrXTravelLog().get(nextState.getMrXTravelLog().size()-1);
            // System.out.println("location from log:" + mrXLocationTest.location().isEmpty());

            // depth of 2 minimax
            int score = miniMax.minimax(nextState, move, 1, Integer.MIN_VALUE, Integer.MAX_VALUE, false);

            if (score > maxScore) {
                maxScore = score;
                bestMove = move;
            }
        }


        return bestMove;
    }

}