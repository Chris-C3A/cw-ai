package uk.ac.bris.cs.scotlandyard.ui.ai;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableSet;

import uk.ac.bris.cs.scotlandyard.model.GameSetup;
import uk.ac.bris.cs.scotlandyard.model.Move;
import uk.ac.bris.cs.scotlandyard.model.Piece;
import uk.ac.bris.cs.scotlandyard.model.Board.GameState;

public class State {
    private GameState board;


    public State(GameState board) {
        this.board = board;
    }

    public State advance(Move move) {
        return new State(this.board.advance(move));
    }

    public Set<Piece> getDetectivePieces() {
        return this.board.getPlayers().stream().filter(player -> player.isDetective()).collect(Collectors.toSet());
    }

    public State advanceMrX(Move move) {
        return new State(this.board.advance(move));
    }

    //! keep advancing until all detectives have moved
    public State advanceDetective(Move move) {
        return new State(this.board.advance(move));
    }
    // public State makeMove(Move move) {
    //     this.board = this.board.advance(move);
    //     return new State(this.board);
    // }

    public GameState getBoard() {
        return this.board;
    }

    public GameSetup getSetup() {
        return this.board.getSetup();
        // this.board.getSetup().moves.size();// total rounds
    }

    public boolean isTerminal() {
        return !this.board.getWinner().isEmpty();
    }

    // is mrX turn
    public boolean isMrxTurn() {
        return this.board.getAvailableMoves().stream().allMatch(move -> move.commencedBy().isMrX());
    }

    // is detectives turn
    public boolean isMinimizingPlayer() {
        return this.board.getAvailableMoves().stream().allMatch(move -> move.commencedBy().isDetective());
    }

    public ImmutableSet<Move> getAvailableMoves() {
        return this.board.getAvailableMoves();
    }    

    public List<State> getPossibleStates() {
        return this.board.getAvailableMoves().stream().map(move -> new State(this.board.advance(move))).collect(Collectors.toList());
    }

    // current round number
    public int getRoundNumber() {
        return this.board.getMrXTravelLog().size();
    }

    // total number of rounds
    public int getTotalRounds() {
        return this.board.getSetup().moves.size();
    }

    public enum winner {
        MrX,
        Detectives,
        None
    }

    public winner getWinner() {
        if (this.board.getWinner().contains(Piece.MrX.MRX)) {
            return winner.MrX;
        } else if (this.board.getWinner().containsAll(this.getDetectivePieces())) {
            return winner.Detectives;
        } else {
            return winner.None;
        }
    }
}
