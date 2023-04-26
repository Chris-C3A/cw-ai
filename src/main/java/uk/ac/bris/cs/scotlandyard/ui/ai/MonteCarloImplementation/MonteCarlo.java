package uk.ac.bris.cs.scotlandyard.ui.ai.MonteCarloImplementation;

import java.util.Random;

import io.atlassian.fugue.Pair;
import uk.ac.bris.cs.scotlandyard.model.Move;
import uk.ac.bris.cs.scotlandyard.ui.ai.State;
import java.util.concurrent.TimeUnit;


public class MonteCarlo {

    private MonteCarloNode rootNode;
    private int nb_simulations = 0;

    public MonteCarlo(State state, Boolean player) {
        this.rootNode = new MonteCarloNode(null, null, state, state.isMrxTurn());
    }

    // public Move findNextMove(int iterations) {
    public Move findNextMove(Pair<Long, TimeUnit> simulationTime) {
      long endTime = System.currentTimeMillis()+simulationTime.right().toMillis(simulationTime.left())-1000;
      long currentTime = System.currentTimeMillis();
      while (currentTime < endTime) {
        MonteCarloNode node = select(); // selects an unexpanded node

        node.expand(); // expands the node
        
        // select random child of node after expansion
        if(!node.getChildren().isEmpty()) {
            node = node.getChildren()
                    .get(new Random().nextInt(node.getChildren().size()));
        }

        // simulate random game from node
        double result = node.simulate();

        // back propoagation
        backup(node, result, node.getGameState().getRoundNumber());

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
    }

    private void backup(MonteCarloNode node, double reward, int rootNodeRound) {
      while (node.getParent() != null) {
        // if(node.getParent().getGameState().isMrxTurn()) node.update(reward - rootNodeRound);
        // else node.update(this.state.getTotalRounds() + 1 - reward);
        if(node.getParent().getGameState().isMrxTurn()) node.update(reward);
        else node.update(-1*reward);

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

