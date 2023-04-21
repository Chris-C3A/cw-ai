package uk.ac.bris.cs.scotlandyard.ui.ai.MonteCarloTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.google.common.collect.ImmutableSet;

import uk.ac.bris.cs.scotlandyard.model.Move;
import uk.ac.bris.cs.scotlandyard.model.Move.DoubleMove;
import uk.ac.bris.cs.scotlandyard.model.Move.SingleMove;
import uk.ac.bris.cs.scotlandyard.model.Piece;
import uk.ac.bris.cs.scotlandyard.model.Board.GameState;
import uk.ac.bris.cs.scotlandyard.model.ScotlandYard.Ticket;
import uk.ac.bris.cs.scotlandyard.ui.ai.Score;

public class MonteCarloNode {

    private MonteCarloNode parent;
    private List<MonteCarloNode> children;

    private int visits; // plays
    private double totalReward; // wins
    private double ucbValue;
    // private int player; //! keep track if mrX or detective
    private Boolean maximizingPlayer;

    private GameState gameState;
    private ImmutableSet<Move> availableMoves;
    private Move move;

    // private int n_plays;
    // private int n_wins;

    // Constructor
    public MonteCarloNode(MonteCarloNode parent, Move move, GameState gameState, ImmutableSet<Move> availableMoves, Boolean maximizingPlayer) {
        this.move = move;
        this.gameState = gameState;

        // this.player = player;
        this.maximizingPlayer = maximizingPlayer;

        // this.n_plays = 0;
        // this.n_wins = 0;
        this.visits = 0;
        this.totalReward = 0.0;
        this.ucbValue = 0.0;

        // Tree
        this.parent = parent;
        this.children = new ArrayList<>();

        this.availableMoves = availableMoves;
    }

    public Boolean isTerminalNode() {
        return !this.gameState.getWinner().isEmpty();
    }

    public Boolean isFullyExpanded() {
        return this.children.size() == this.availableMoves.size();
    }

    // Select a child node based on UCB1 value
    public MonteCarloNode selectChild() {
        MonteCarloNode selected = null;

        double bestUcbValue = Double.MIN_VALUE;

        for (MonteCarloNode child : children) {

            double ucbValue = child.ucbValue();

            if (ucbValue > bestUcbValue) {
                selected = child;
                bestUcbValue = ucbValue;
            }
        }
        return selected;
    }
    
    // Expand the node by adding child nodes for all legal moves
    public void expand() {
        for (Move move : this.availableMoves) {
            GameState nextState = this.gameState.advance(move);

            // check if mrX or detective's turn

            // put in seperate method
            Boolean mrXturn = nextState.getAvailableMoves().stream().allMatch(pmove -> pmove.commencedBy() == Piece.MrX.MRX);

            //! check if we need to get avaliable moves for a certain detective
            MonteCarloNode childNode = new MonteCarloNode(this, move, nextState, nextState.getAvailableMoves(), mrXturn);
            this.children.add(childNode);
        }

        // List<GameState> legalMoves = gameState.getLegalMoves(player);
        // for (GameState move : legalMoves) {
        //     MonteCarloNode childNode = new MonteCarloNode(this, move, 3 - player);
        //     children.add(childNode);
        // }
    }
    
    // Update node statistics with simulation result
    public void update(double reward) {
        this.visits++;

        Boolean isScarceTicket = false;        

        if (this.move != null) {
            isScarceTicket = this.move.accept(new Move.Visitor<Boolean>() {

                @Override
                public Boolean visit(SingleMove move) {
                    if (move.ticket.equals(Ticket.SECRET) || move.ticket.equals(Ticket.DOUBLE)) {
                        return true;
                    }

                    return false;
                }

                @Override
                public Boolean visit(DoubleMove move) {
                    return true;
                }

            });
        }

        if (isScarceTicket) {
            // reward += Score.SCARE_TICKET;
            this.totalReward += reward * 0.4; // increase nbr of wins
        } else {
            this.totalReward += reward; // increase nbr of wins
        }
        // this.totalReward += reward; // increase nbr of wins

        this.ucbValue = 0.0;
    }
    
    // Get the UCB1 value for this node
    public double ucbValue() {
        if (visits == 0) {
            return Double.MAX_VALUE;
        }

        if (parent == null) {
            return Double.NaN;
        }

        double exploitation = totalReward / visits;

        double exploration = Math.sqrt(Math.log(parent.visits) / visits);

        // double C = Math.sqrt(2); // exploration parameter
        double C = Math.sqrt(2);

        ucbValue = exploitation + C * exploration;

        return ucbValue;
    }
    
    // Get the best child node based on visit count
    public MonteCarloNode bestChild() {
        MonteCarloNode best = null;

        int bestVisits = Integer.MIN_VALUE;

        for (MonteCarloNode child : children) {
            int childVisits = child.getVisits();

            if (childVisits > bestVisits) {
                best = child;
                bestVisits = childVisits;
            }
        }

        return best;
    }
    
    // Perform a random simulation from this node
    public double simulate() {
        Random rand = new Random();

        // int currentPlayer = player; // ! mrX or detectives

        GameState currentState = gameState;
        Move randomMove = null;

        // while (!currentState.isTerminal()) {
        while (currentState.getWinner().isEmpty()) {
            // List<GameState> legalMoves = currentState.getLegalMoves(currentPlayer);
            List<Move> legalMoves = currentState.getAvailableMoves().asList();

            if (legalMoves.size() == 0) {
                break;
            }

            int randomIndex = rand.nextInt(legalMoves.size());

            randomMove = legalMoves.get(randomIndex);

            currentState = currentState.advance(randomMove);

            // currentPlayer = 3 - currentPlayer;
        }

        // System.out.println(currentState.getWinner());

        // ! give score based on winner +1 for mrX win, -1 for Detectives win
        if (currentState.getWinner().contains(Piece.MrX.MRX)) {
            return 1;
            // if (maximizingPlayer) {
            //     return 1;
            // } else {
            //     return -1;
            // }
            // return 1;
        } else {
            // if (maximizingPlayer) {
            //     return -1;
            // } else {
            //     return 1;
            // }
            return -1;
            // return 1;
        }
        // acc calculate score
        // return new Score(randomMove, currentState).getScore();

        // return currentState.getReward(player);
    }

    // Getters and setters
    public Move getMove() {
        return move;
    }

    public ImmutableSet<Move> getAvailableMoves() {
        return availableMoves;
    }

    public MonteCarloNode getParent() {
        return parent;
    }
    
    public void setParent(MonteCarloNode parent) {
        this.parent = parent;
    }
    
    public List<MonteCarloNode> getChildren() {
        return this.children;
    }

    public void setChildren(List<MonteCarloNode> children) {
        this.children = children;
    }

    public int getVisits() {
        return visits;
    }

    public void setVisits(int visits) {
        this.visits = visits;
    }

    public double getTotalReward() {
        return totalReward;
    }

    public void setTotalReward(double totalReward) {
        this.totalReward = totalReward;
    }

    public double getUcbValue() {
        return ucbValue;
    }

    public void setUcbValue(double ucbValue) {
        this.ucbValue = ucbValue;
    }

    public Boolean getPlayer() {
        return maximizingPlayer;
    }

    // public void setPlayer(int player) {
    //     this.player = player;
    // }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public String toString() {
        if (this.move == null) {
            return "root";
        }
        return this.move.toString() + ", wins: " + this.getTotalReward() + "visits: " + this.getVisits();
    }
}
