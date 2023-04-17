package uk.ac.bris.cs.scotlandyard.ui.ai;

import com.google.common.collect.ImmutableSet;
import io.atlassian.fugue.Pair;
import uk.ac.bris.cs.scotlandyard.model.Board;
import uk.ac.bris.cs.scotlandyard.model.Move;
import uk.ac.bris.cs.scotlandyard.model.Piece;

import javax.annotation.Nonnull;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import uk.ac.bris.cs.scotlandyard.model.*;

public class MrXAI implements Ai {

    @Nonnull
    @Override public String name() { return "MrX Zabre AI"; }

    @Nonnull @Override public Move pickMove(
            @Nonnull Board board,
            Pair<Long, TimeUnit> timeoutPair) {
        // returns a random move, replace with your own implementation

        // AI implementations
        // Scoring Function
        // MiniMax algorithm
        // neural network?
        this.score(board);

        var moves = board.getAvailableMoves().asList();
        return moves.get(new Random().nextInt(moves.size()));
    }

    private void score(Board board) {
        var moves = board.getAvailableMoves().asList(); // mrX moves

//        ImmutableSet<Piece> players = board.getPlayers();
        ImmutableSet<Player> players = board.getPlayers().stream().map(piece -> );
        board.

        // get player locations
        for (Piece player : players) {
            if (player.isDetective()) {
                // weird way of casting to get detective location
                int detective_location = board.getDetectiveLocation((Piece.Detective) player).orElseThrow();
                // get distance of mrX to detective location (source: mrX, destination: detective_location)
                pathToDetective()
                System.out.println(detective_location);
            }
        }
        // a good move for mrx is to get away from the detectives and be in a position with many options to move
        // return score of the current board
        // using this method we select the best move for mrX to take
    }

}
