package uk.ac.bris.cs.scotlandyard.ui.ai.MonteCarloImplementation;

import java.util.Random;

import io.atlassian.fugue.Pair;
import uk.ac.bris.cs.scotlandyard.model.Move;
import uk.ac.bris.cs.scotlandyard.ui.ai.State;
import java.util.concurrent.TimeUnit;


public class MonteCarlo {

    private MonteCarloNode rootNode;
    private State state;
    // private Game game;
    // private Boolean player;
    private int nb_simulations = 0;

    public MonteCarlo(State state, Boolean player) {
        this.state = state;
        // this.game = view.getGame();
        // this.player = player;
        // this.rootNode = new MonteCarloNode(null, null, state, state.getPossibleStates(), player);
        this.rootNode = new MonteCarloNode(null, null, state, state.isMrxTurn());
    }

    // public Move findNextMove(int iterations) {
    public Move findNextMove(Pair<Long, TimeUnit> simulationTime) {
      long endTime = System.currentTimeMillis()+simulationTime.right().toMillis(simulationTime.left());
      long currentTime = System.currentTimeMillis();
      // for (int i = 0; i < iterations; i++) {
      while (currentTime < endTime) {
        // System.out.println("Iteration: " + i);
        MonteCarloNode node = select(); // selects an unexpanded node
        // System.out.println("selection");

        node.expand(); // expands the node
        // System.out.println("expansion");
        
        // select random child of node after expansion
        if(!node.getChildren().isEmpty()) {
            node = node.getChildren()
                    .get(new Random().nextInt(node.getChildren().size()));
        }

        // simulate random game from node
        double result = node.simulate();
        // System.out.println("simulation");

        backup(node, result, node.getGameState().getRoundNumber());
        // System.out.println("back propagation");
        currentTime = System.currentTimeMillis();
        nb_simulations++;
      }

      // get move of the best child of root node
      System.out.println("Nb of simulations ran:" + nb_simulations);
      System.out.println("selected child value: " + rootNode.bestChild().getScore());
      return rootNode.bestChild().getMove();
      
      // return rootNode.selectChild().getMove();
    }

    private MonteCarloNode select() {
      MonteCarloNode selected = rootNode;

      while (true) {
        if (selected.isLeaf()) return selected;
        else selected = selected.selectChild();
      }
      // while (!selected.isLeaf() && !selected.isTerminalNode()) {
      //   selected = selected.selectChild(); // select child with highest UCB value
      // }

      // return selected;
    }

    private void backup(MonteCarloNode node, double reward, int rootNodeRound) {
      while (node.getParent() != null) {
        if(node.getParent().getGameState().isMrxTurn()) node.update(reward - rootNodeRound);
        else node.update(this.state.getTotalRounds() + 1 - reward);

        node = node.getParent();
      }
    }

    public void printFullTree(MonteCarloNode startNode) {
        MonteCarloNode node = startNode;


        System.out.println("Root: " + node.toString());
        System.out.println("Children: " + node.getChildren().toString());
        System.out.println();


        if (!node.getChildren().isEmpty()) {
            for (MonteCarloNode child : node.getChildren()) {
                printFullTree(child);
            }
        }
    }
}

