package uk.ac.bris.cs.scotlandyard.ui.ai;

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
import uk.ac.bris.cs.scotlandyard.ui.ai.Score.ScoreMrX;

public class State {
    private GameState board;
    private int mrXLocation;


    // Constructor
    /**
     * @param board
     * @param mrXLocation
     */
    public State(GameState board, int mrXLocation) {
        this.board = board;
        this.mrXLocation = mrXLocation;
    }

    /**
     * @return Set of detective pieces
     */
    public Set<Piece> getDetectivePieces() {
        return this.board.getPlayers()
            .stream()
            .filter(player -> player.isDetective())
            .collect(Collectors.toSet());
    }

    /**
     * advances mrX move and returns a new state
     * @param move
     * @return new State
     */
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

    /**
     * advances detective move and returns a new state with previous mrX location
     * @param move
     * @return new State
     */
    public State advanceDetective(Move move) {
        return new State(this.board.advance(move), this.getMrXLocation());
    }

    /* Getters */
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


    public int getRoundNumber() {
        // current round number
        return this.board.getMrXTravelLog().size();
    }

    public int getTotalRounds() {
        // total number of rounds
        return this.board.getSetup().moves.size();
    }

    /**
     * @return true if state is terminal (game over)
     */
    public boolean isTerminal() {
        return !this.board.getWinner().isEmpty();
    }

    // get player tickets
    public Optional<TicketBoard> getPlayerTickets(Piece player) {
        return this.board.getPlayerTickets(player);
    }

    /**
     * @return true if mrX turn
     */
    public boolean isMrxTurn() {
        return this.board.getAvailableMoves()
        .stream()
        .allMatch(move -> move.commencedBy().isMrX());
    }


    // Winner Enum
    public enum winner {
        MrX,
        Detectives,
        None
    }

    /**
     * @return winner of state (MrX, Detectives, None)
     */
    public winner getWinner() {
        if (this.board.getWinner().contains(Piece.MrX.MRX)) {
            return winner.MrX;
        } else if (this.board.getWinner().containsAll(this.getDetectivePieces())) {
            return winner.Detectives;
        } else {
            return winner.None;
        }
    }

    /**
     * @return List of detective locations
     */
    public List<Integer> getDetectiveLocations() {
        return this.getDetectivePieces()
        .stream()
        .map(detective -> this.getBoard().getDetectiveLocation((Piece.Detective) detective))
        .filter(location -> location.isPresent())
        .map(location -> location.get())
        .collect(Collectors.toList());
    }
    
    /**
     * @return List of remaining detective pieces
     */
    public List<Piece> getRemainingDetectivePieces() {
        return this.getDetectivePieces().stream()
            .filter(detective -> this.getAvailableMoves().stream().anyMatch(m -> m.commencedBy().equals(detective)))
            .collect(Collectors.toList());
    }

    /**
     * @param detective
     * @return List of moves for detective piece
     */
    public List<Move> getDetectiveMoves(Piece detective) {
        return this.getAvailableMoves().stream()
            .filter(m -> m.commencedBy().equals(detective))
            .collect(Collectors.toList());
    }


    /* Static methods */

    /**
     * @param detective
     * @param detectiveMoves
     * @param state
     * @return Move best move for detective
     */
    public static Move getDetectiveBestMove(Piece detective, List<Move> detectiveMoves, State state) {
        Move bestMove = null;
        int bestScore = Integer.MIN_VALUE;

        for (Move move : detectiveMoves) {
            State newState = state.advanceDetective(move);
            int score = new ScoreDetective(detective, move, newState).getScore();

            if (score > bestScore) {
                bestScore = score;
                bestMove = move;
            }
        }

        return bestMove;
    }

    /**
     * @param state
     * @return Move best move for mrX
     */
    public static Move getMrXBestMove(State state) {
        Move bestMove = null;
        int bestScore = Integer.MIN_VALUE;

        for (Move move : state.getAvailableMoves()) {
            State newState = state.advanceMrX(move);
            int score = new ScoreMrX(move, newState, newState.getRoundNumber()).getScore();

            if (score > bestScore) {
                bestScore = score;
                bestMove = move;
            }
        }

        return bestMove;
    }
}
