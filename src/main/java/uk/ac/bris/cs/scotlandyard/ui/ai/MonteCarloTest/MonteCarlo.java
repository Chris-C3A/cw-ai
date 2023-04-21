package uk.ac.bris.cs.scotlandyard.ui.ai.MonteCarloTest;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;


import uk.ac.bris.cs.scotlandyard.model.Move;
import uk.ac.bris.cs.scotlandyard.model.Piece;
import uk.ac.bris.cs.scotlandyard.model.Board.GameState;

// public class MonteCarlo {

//     // /**  From given state, repeatedly run MCTS to build statistics. */
//     // public static void runSearch(GameState state, int timeout) {
//     //     // get all available moves
//     //     // for each move
//     //     //     play move
//     //     //     get score
//     //     //     add score to list
//     //     //     undo move
//     //     // return best move
//     // }


//     //  /** Get the best move from available statistics. */
//     //  public static Move bestPlay(GameState state) {
//     //      // get all available moves
//     //      // for each move
//     //      //     get score
//     //      //     add score to list
//     //      // return best move
//     //      return null;
//     //  }
    
//     private int maxIterations;
//     private MonteCarloNode rootNode;
//     private Random random;
//     private int player;
    
//     public MonteCarlo(int maxIterations, int player) {
//         this.maxIterations = maxIterations;
//         this.player = player;
//         this.random = new Random();
//     }
    
//     public void setRootNode(MonteCarloNode rootNode) {
//         this.rootNode = rootNode;
//     }
    
//     public MonteCarloNode getRootNode() {
//         return rootNode;
//     }
    
//     public int getMaxIterations() {
//         return maxIterations;
//     }
    
//     public void setMaxIterations(int maxIterations) {
//         this.maxIterations = maxIterations;
//     }
    
//     public int getPlayer() {
//         return player;
//     }
    
//     public void setPlayer(int player) {
//         this.player = player;
//     }
    
//     public MonteCarloNode selectPromisingNode(MonteCarloNode rootNode) {
//         MonteCarloNode node = rootNode;
//         while (!node.isTerminalNode()) {
//             if (!node.isFullyExpanded()) {
//                 return node.expand();
//             } else {
//                 node = node.selectChild();
//             }
//         }
//         return node;
//         // return child with highest UCB1 value
//         // return node.selectChild();
//         // while (node.getChildren().size() != 0) {
//         //     node = Ucb1.findBestNodeWithUcbValue(node);
//         // }
//         // return node;
//     }
    
//     public void expandNode(MonteCarloNode node) {
//         // List<GameState> possibleStates = node.getGameState().getAllPossibleStates(node.getPlayer());
//         ImmutableSet<Move> possibleMoves = node.getGameState().getAvailableMoves(); // gets available moves for either (mrX or detectives) //! see if needed to get moves for serperate detectives

//         // for (GameState state : possibleStates) {
//         //     MonteCarloNode newNode = new MonteCarloNode(node, node.getPlayer() * -1, state);
//         //     node.getChildren().add(newNode);
//         // }

//         for (Move move : possibleMoves) {
//             GameState nextState = node.getGameState().advance(move);
//             //! recheck this
//             MonteCarloNode newNode = new MonteCarloNode(node, move, node.getGameState(), nextState.getAvailableMoves());
//             node.getChildren().add(newNode);
//         }

//     }
    
//     public void simulateRandomPlayout(MonteCarloNode node) {
//         int currentPlayer = node.getPlayer();
//         GameState currentState = node.getGameState().clone();
//         while (!currentState.isGameOver()) {
//             List<GameState> possibleStates = currentState.getAllPossibleStates(currentPlayer);
//             if (possibleStates.size() == 0) {
//                 break;
//             }
//             int randomIndex = random.nextInt(possibleStates.size());
//             currentState = possibleStates.get(randomIndex);
//             currentPlayer *= -1;
//         }
//         double reward = currentState.getReward(player);
//         while (node != null) {
//             node.setVisits(node.getVisits() + 1);
//             node.setTotalReward(node.getTotalReward() + reward);
//             node = node.getParent();
//         }
//     }
    
//     public MonteCarloNode runSimulation() {
//         MonteCarloNode promisingNode = selectPromisingNode(rootNode);
//         expandNode(promisingNode);
//         MonteCarloNode nodeToExplore = promisingNode;
//         if (promisingNode.getChildren().size() > 0) {
//             nodeToExplore = promisingNode.getRandomChildNode();
//         }
//         simulateRandomPlayout(nodeToExplore);
//         return rootNode;
//     }
    
//     public GameState findNextMove() {
//         for (int i = 0; i < maxIterations; i++) {
//             MonteCarloNode rootNode = getRootNode();
//             runSimulation();
//         }
//         MonteCarloNode bestNode = Ucb1.findBestNodeWithMaxScore(rootNode);
//         setRootNode(bestNode);
//         return bestNode.getGameState();
//     }
  
// }

public class MonteCarlo {

    private MonteCarloNode rootNode;
    private GameState state;
    // private Game game;
    private Boolean player;

    public MonteCarlo(GameState state, Boolean player) {
        this.state = state;
        // this.game = view.getGame();
        this.player = player;
        this.rootNode = new MonteCarloNode(null, null, state, state.getAvailableMoves(), player);
    }

    public Move findNextMove(int iterations) {
        for (int i = 0; i < iterations; i++) {
            // MonteCarloNode node = treePolicy();


            MonteCarloNode leaf = select(rootNode); // selection

            MonteCarloNode expandedNode = expand(leaf); // expansion
            // MonteCarloNode expandedNode = treePolicy();

            // expandedNode.setTotalReward(0); // reset reward

            // double reward = 0;
            // for (int j = 0; j < 1; j++) {
                // reward win = 1, loss = -1
            double reward = expandedNode.simulate(); // simulation
            backup(expandedNode, reward); // backpropagation

            // }
            // simulation expanded node
            // double reward = expandedNode.simulate(); // simulation

            // backup(expandedNode, reward); // backpropagation
        }
        // System.out.println(rootNode.getChildren().toString());
        this.printFullTree(rootNode);
        System.out.println("best child visits: " + rootNode.bestChild().getVisits() + " best child reward: " + rootNode.bestChild().getTotalReward());
        return rootNode.bestChild().getMove();
    }

    // private MonteCarloNode treePolicy() {
    //     MonteCarloNode node = rootNode;
        
    //     // if (node == null) {
    //     //     this.printFullTree(rootNode);
    //     // }

    //     while (!node.isTerminalNode()) {
    //         if (!node.isFullyExpanded()) {
    //             return expand(node);
    //         } else {
    //             node = node.selectChild();
    //             if (node == null) {
    //                 System.out.println("NULL NODE");
    //                 this.printFullTree(rootNode);
    //             }
    //         }
    //     }
    //     return node;
    // }

    private MonteCarloNode select(MonteCarloNode node) {
        MonteCarloNode leaf = node;

        while (!leaf.isTerminalNode() && leaf.isFullyExpanded()) {
            leaf = leaf.selectChild();
            if (leaf == null) {
                return node;
            }
        }


        // while (!leaf.getChildren().isEmpty()) {
        //     leaf = leaf.selectChild();
        // }

        return leaf;

    }

    private MonteCarloNode expand(MonteCarloNode node) {
        //! keep track of already tried moves

        // get untried moves
        List<Move> untriedMoves = new ArrayList<>(node.getAvailableMoves()
        .stream()
        .filter(move -> !node.getChildren()
            .stream()
            .anyMatch(child -> child.getMove().equals(move))).collect(Collectors.toList()));
        // System.out.println(untriedMoves.size());
        // List<Move> untriedMoves = new ArrayList<>(state.getAvailableMoves());

        Random random = new Random();

        if (untriedMoves.size() == 0) {
            // return node;
            System.out.println("children size: " + node.getChildren().size() + " available moves: " + node.getAvailableMoves().size());
        }

        Move move = untriedMoves.get(random.nextInt(untriedMoves.size()));


        GameState nextState = node.getGameState().advance(move);
        // GameState nextState = node.getGameState();

        // Boolean mrXturn = nextState.getAvailableMoves().stream().allMatch(pmove -> pmove.commencedBy().isMrX());


        // detective turn
        // if (!node.getPlayer()) {
        //     while (!mrXturn) {
        //         move = untriedMoves.get(random.nextInt(untriedMoves.size()));
        //         // GameState nextState = state.advance(move);
        //         nextState = node.getGameState().advance(move);

        //         mrXturn = nextState.getAvailableMoves().stream().allMatch(pmove -> pmove.commencedBy().isMrX());

        //         untriedMoves = new ArrayList<>(nextState.getAvailableMoves().stream().filter(pmove -> !node.getChildren().stream().anyMatch(child -> child.getMove().equals(pmove))).collect(Collectors.toList()));
        //     }
        // }

        // play that move
        // GameState nextState = state.advance(move);
        // GameState nextState = node.getGameState().advance(move);

        Boolean mrXturn = nextState.getAvailableMoves().stream().allMatch(pmove -> pmove.commencedBy() == Piece.MrX.MRX);

        MonteCarloNode child = new MonteCarloNode(node, move, nextState, nextState.getAvailableMoves(), mrXturn);

        node.getChildren().add(child);

        return child;
    }

    //! dont modify root node score
    private void backup(MonteCarloNode node, double reward) {
        while (node.getParent() != null) {
            Boolean player = node.getParent().getPlayer(); // original player
            if (player) {
                node.update(reward);
            } else {
                node.update(-1 * reward);
            }
            // node.update(reward);
            // if (node.getPlayer() == player) {
            //     node.setTotalReward(node.getTotalReward() + reward);
            // } else {
            //     node.setTotalReward(node.getTotalReward() - reward);
            // }
            // if (node.getPlayer() == player) {
            //     if (reward == 1) {
            //         node.update(1);
            //     } else {
            //         node.update(0);
            //     }
            // } else {
            //     if (reward == 1) {
            //         node.update(0);
            //     } else {
            //         node.update(1);
            //     }
            //     // node.update(reward);
            // }
            // node.update(reward);
            node = node.getParent();
        }
    }

    public void printFullTree(MonteCarloNode startNode) {
        MonteCarloNode node = startNode;

        // if (node.getChildren().isEmpty()) {
        //     System.out.println(node.toString());
        // }


        System.out.println("Root: " + node.toString());
        System.out.println("Children: " + node.getChildren().toString());
        System.out.println();

        // printFullTree(rootNode);

        if (!node.getChildren().isEmpty()) {
            for (MonteCarloNode child : node.getChildren()) {
                printFullTree(child);
            }
        }
        // MonteCarloNode node = rootNode;

        // System.out.println(node.toString());

        // if (node.getChildren().isEmpty()) {
        //     System.out.println(node.toString());
        // }        

        // for (MonteCarloNode child : node.getChildren()) {
        //     printFullTree(child);
        //     if (child.getChildren().isEmpty()) {
        //         System.out.println(child.toString());
        //     }
        // }

        // while (node != null) {
        //     System.out.println(node.toString());
        //     node = node.selectChild();
        // }
    }
}
