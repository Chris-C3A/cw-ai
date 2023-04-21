package uk.ac.bris.cs.scotlandyard.ui.ai.MonteCarloImplementation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;


import uk.ac.bris.cs.scotlandyard.model.Move;
import uk.ac.bris.cs.scotlandyard.ui.ai.State;

public class MonteCarloNode {
    private MonteCarloNode parent;
    private List<MonteCarloNode> children;

    private State gameState;
    private Move move;
    private List<State> possibleStates;

    private int n_plays;
    private int score;

    private Boolean ismrX;

    // Constructor
    public MonteCarloNode(MonteCarloNode parent, Move move, State gameState, Boolean ismrX) {
        this.move = move;
        this.gameState = gameState;

        this.n_plays = 0;
        this.score = 0;

        // Tree
        this.parent = parent;
        this.children = new ArrayList<>();

        this.ismrX = ismrX;

        // this.availableMoves = availableMoves; //! probably keep available moves since we keep track of the move for each node
        // this.possibleStates = possibleStates;
    }

    public Boolean isTerminalNode() {
        return this.gameState.isTerminal();
    }

    public Boolean isFullyExpanded() {
        return this.children.size() == this.possibleStates.size();
    }

    public Boolean isLeaf() {
        return this.children.isEmpty();
    }

    public Boolean isMrxTurn() {
        return this.ismrX;
    }

    // Select a child node based on UCB value
    public MonteCarloNode selectChild() {

        MonteCarloNode selected = children.get(0);
        double bestUCBValue = children.get(0).ucbValue();


        for (MonteCarloNode child : children) {

            double ucbValue = child.ucbValue();

            if (ucbValue > bestUCBValue) {
                selected = child;
                bestUCBValue = ucbValue;
            }
        }

        // if (selected == null) {
        //     return this;
        // }

        return selected;
    }
    
    // Expand the node by adding child nodes for all legal moves
    public void expand() {
        if(!isLeaf()) throw new UnsupportedOperationException("Can not populate tree node!");

        if(!this.isTerminalNode()) {
            for (Move m : this.gameState.getAvailableMoves()) {
                State newState;
                if (this.gameState.isMrxTurn()) {
                    newState = this.gameState.advanceMrX(m);
                } else {
                    newState = this.gameState.advanceDetective(m);
                }
                // State newState = this.gameState.advance(m);
                this.children.add(new MonteCarloNode(this, m, newState, newState.isMrxTurn()));
            }
        }
        
    }
    
    // Update node statistics with simulation result
    public void update(double reward) {
        this.n_plays++;
        this.score += reward;

    }
    
    // Get the UCB1 value for this node
    public double ucbValue() {
        if (this.n_plays == 0) {
            return Double.MAX_VALUE;
        }

        if (this.parent == null) {
            return 0;
        }

        double exploitation = this.score / this.n_plays;
        double exploration = Math.sqrt(Math.log(this.parent.getPlays()) / this.n_plays);

        // double domainKnowledge = 0.0; // Domain knowledge constant //! to implement

        double C = 3.0; // Exploration constant

        return exploitation + C * exploration;
    }
    
    // Get the best child node based on number of plays
    public MonteCarloNode bestChild() {
        MonteCarloNode best = null;

        // double maxPlays = Double.MIN_VALUE;
        double maxScore = Double.MIN_VALUE;

        for (MonteCarloNode child : children) {
            double childScore = child.getAverageScore();

            if (childScore > maxScore) {
                best = child;
                maxScore = childScore;
            }
        }

        return best;
    }

    // get moves for rollout policy
    // public Move rolloutPolicy(State state) {
    //     Random rand = new Random();
    //     List<Move> legalMoves = state.getAvailableMoves().stream().collect(Collectors.toList());
    //     int randomIndex = rand.nextInt(legalMoves.size());
    //     Move randomMove = legalMoves.get(randomIndex);
    //     return randomMove;
    // }
    
    // Perform a random simulation from this node
    public double simulate() {
        Random rand = new Random();

        State state = this.gameState;

        // rollout policy
        // play until end of game
        while (state.getBoard().getWinner().isEmpty()) {
            List<Move> legalMoves = state.getAvailableMoves().stream().collect(Collectors.toList());

            int randomIndex = rand.nextInt(legalMoves.size());

            Move randomMove = legalMoves.get(randomIndex);

            //! rewrite in state class to avoid redundancy
            if (state.isMrxTurn()) {
                state = state.advanceMrX(randomMove);
            } else {
                state = state.advanceDetective(randomMove);
            }
            // state = state.advance(randomMove);
        }


        if (state.getWinner() == State.winner.MrX) {
            return state.getTotalRounds();
        } else {
            return state.getRoundNumber();
        }
        // if(rollingMiniBoard.getWinner() == MiniBoard.winner.MRX) return getRoundSize()+1;
        // else return rollingMiniBoard.getRound();

        // return 0;
    }

    // Getters and setters
    public Move getMove() {
        return move;
    }

    public double getPlays() {
        return this.n_plays;
    }

    public double getScore() {
        return this.score;
    }

    public double getAverageScore() {
        if (this.n_plays == 0) {
            return Double.MIN_VALUE;
        }
        return this.score / this.n_plays;
    }

    // public ImmutableSet<Move> getAvailableMoves() {
    //     return availableMoves;
    // }

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


    public State getGameState() {
        return this.gameState;
    }

    public void setGameState(State gameState) {
        this.gameState = gameState;
    }

    public String toString() {
        if (this.move == null) {
            return "root";
        }
        // return this.move.toString() + ", wins: " + this.getTotalReward() + "visits: " + this.getVisits();
        return this.move.toString();
    }

}
