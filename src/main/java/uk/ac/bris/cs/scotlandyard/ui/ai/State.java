package uk.ac.bris.cs.scotlandyard.ui.ai;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableSet;

import uk.ac.bris.cs.scotlandyard.model.GameSetup;
import uk.ac.bris.cs.scotlandyard.model.Move;
import uk.ac.bris.cs.scotlandyard.model.Piece;
import uk.ac.bris.cs.scotlandyard.model.Board.GameState;
import uk.ac.bris.cs.scotlandyard.model.Board.TicketBoard;
import uk.ac.bris.cs.scotlandyard.ui.ai.Score.ScoreDetective;

public class State {
    private GameState board;
    private int mrXLocation;


    public State(GameState board, int mrXLocation) {
        this.board = board;
        this.mrXLocation = mrXLocation;
    }


    public Set<Piece> getDetectivePieces() {
        return this.board.getPlayers().stream().filter(player -> player.isDetective()).collect(Collectors.toSet());
    }

    // advances mrX move and returns a new state
    public State advanceMrX(Move move) {
        return new State(this.board.advance(move), move.accept(new Move.Visitor<Integer>() {
            @Override
            public Integer visit(Move.SingleMove move) {
                return move.destination;
            }

            @Override
            public Integer visit(Move.DoubleMove move) {
                return move.destination2;
            }
        }));
    }

    // advances detective move and returns a new state with previous mrX location
    public State advanceDetective(Move move) {
        return new State(this.board.advance(move), this.getMrXLocation());
    }

    // Getters
    public GameState getBoard() {
        return this.board;
    }

    public GameSetup getSetup() {
        return this.board.getSetup();
    }

    public int getMrXLocation() {
        return this.mrXLocation;
    }

    public ImmutableSet<Move> getAvailableMoves() {
        return this.board.getAvailableMoves();
    }    


    // returns a list of possible states
    public List<State> getPossibleStates() {
        if (this.isMrxTurn()) {
            return this.getAvailableMoves().stream().map(move -> this.advanceMrX(move)).collect(Collectors.toList());
        } else {
            return this.getAvailableMoves().stream().map(move -> this.advanceDetective(move)).collect(Collectors.toList());
        }
        // return this.board.getAvailableMoves().stream().map(move -> new State(this.board.advance(move))).collect(Collectors.toList());
    }

    // current round number
    public int getRoundNumber() {
        return this.board.getMrXTravelLog().size();
    }

    // total number of rounds
    public int getTotalRounds() {
        return this.board.getSetup().moves.size();
    }

    // Boolean
    public boolean isTerminal() {
        return !this.board.getWinner().isEmpty();
    }

    // get player tickets
    public Optional<TicketBoard> getPlayerTickets(Piece player) {
        return this.board.getPlayerTickets(player);
        // return this.board.getSetup().playerInfo.get(player).get().get(ticket);
    }

    // is mrX turn
    // returns if current state is mrX turn
    public boolean isMrxTurn() {
        return this.board.getAvailableMoves().stream().allMatch(move -> move.commencedBy().isMrX());
    }


    // Winner Enum
    public enum winner {
        MrX,
        Detectives,
        None
    }

    // returns winner of state (MrX, Detectives, None) if winner exists
    public winner getWinner() {
        if (this.board.getWinner().contains(Piece.MrX.MRX)) {
            return winner.MrX;
        } else if (this.board.getWinner().containsAll(this.getDetectivePieces())) {
            return winner.Detectives;
        } else {
            return winner.None;
        }
    }

    // ! write in functional style
    public List<Integer> getDetectiveLocations() {
        return this.getDetectivePieces()
        .stream()
        .map(detective -> this.getBoard().getDetectiveLocation((Piece.Detective) detective))
        .filter(location -> location.isPresent())
        .map(location -> location.get())
        .collect(Collectors.toList());
        // List<Integer> detectiveLocations = new ArrayList<Integer>();
        // // get player locations
        // for (Piece detective : this.getDetectivePieces()) {
        //     Optional<Integer> location = this.getBoard().getDetectiveLocation((Piece.Detective) detective);

        //     if (location.isPresent()) {
        //         detectiveLocations.add(location.get());
        //     }
        // }

        // return detectiveLocations;
    }


    // Static methods
    /**
     * @param detective
     * @param detectiveMoves
     * @param state
     * @return Move best move for detective
     */
    public static Move getDetectiveBestMove(Piece detective, List<Move> detectiveMoves, State state) {
        // get best move for detective
        Move bestMove = null;
        int bestScore = Integer.MIN_VALUE;

        for (Move move : detectiveMoves) {
            State newState = state.advanceDetective(move);
            int score = new ScoreDetective(detective, move, newState).getScore();
            // int score = 0;

            if (score > bestScore) {
                bestScore = score;
                bestMove = move;
            }
        }

        // System.out.println("MrX's Locaiton:" + state.getMrXLocation());
        // System.out.println("Best Detective move: " + bestMove.toString());

        return bestMove;
    }
}
