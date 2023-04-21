package uk.ac.bris.cs.scotlandyard.ui.ai;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.google.common.collect.ImmutableSet;

import uk.ac.bris.cs.scotlandyard.model.Move;
import uk.ac.bris.cs.scotlandyard.model.Piece;
import uk.ac.bris.cs.scotlandyard.model.Board.GameState;

public class Game {
    // private GameState state;
    // private Board board;

    public static List<Integer> getDetectiveLocations(GameState state) {
        List<Integer> detectiveLocations = new ArrayList<Integer>();
        // get player locations
        for (Piece player : state.getPlayers()) {
            if (player.isDetective()) {
                Optional<Integer> location = state.getDetectiveLocation((Piece.Detective) player);

                if (location.isPresent()) {
                    detectiveLocations.add(location.get());
                }
            }
        }

        return detectiveLocations;
    }

    public static Integer getMrXLocationFromMove(Move move) {
        return move.accept(new Move.Visitor<Integer>() {
            @Override
            public Integer visit(Move.SingleMove move) {
                return move.destination;
            }

            @Override
            public Integer visit(Move.DoubleMove move) {
                return move.destination2;
            }
        });
    }

    public static Integer getMrXLocationFromLog(GameState state) {
        // ! location is hidden
        return state.getMrXTravelLog().get(state.getMrXTravelLog().size() - 1).location().get();
    }


    public static boolean occupiedLocation(List<Integer> detectiveLocations, int destination) {
        for (Integer location : detectiveLocations) {
            if (location == destination) {
                return true;
            }
        }
        return false;
    }

    // get nextState
    public static GameState playMove(GameState state, Move move) {
        return state.advance(move);
    }

    public static ImmutableSet<Move> getLegalMoves(GameState state) {
        return state.getAvailableMoves();
    }

    // winner of game
    // public static Optional<Piece> getWinner(GameState state) {
    //     return state.getWinner();
    // }

    // public static void getPlayerMoves();
}
