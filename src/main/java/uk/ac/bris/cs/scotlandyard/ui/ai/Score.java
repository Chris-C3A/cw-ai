package uk.ac.bris.cs.scotlandyard.ui.ai;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.graph.ImmutableValueGraph;

import uk.ac.bris.cs.scotlandyard.model.Board;
import uk.ac.bris.cs.scotlandyard.model.LogEntry;
import uk.ac.bris.cs.scotlandyard.model.Move;
import uk.ac.bris.cs.scotlandyard.model.Piece;
import uk.ac.bris.cs.scotlandyard.model.Board.GameState;
import uk.ac.bris.cs.scotlandyard.model.ScotlandYard.Ticket;
import uk.ac.bris.cs.scotlandyard.model.ScotlandYard.Transport;


public class Score {
    private int score;
    private int mrXlocation;
    private Move move;
    private GameState state;

    // Constructor
    public Score(Move move, GameState state) {
        this.move = move;
        this.state = state;

        this.score = 0;
        this.mrXlocation = this.getMrXLocationFromMove(move);

        this.scoreState();
    }

    // Getters
    public int getScore() {
        return this.score;
    }

    public int getMrXLocation() {
        return this.mrXlocation;
    }

    public Move getMove() {
        return this.move;
    }


    public void scoreState() {
        // TODO implement game winning / losing scores

        // Constants
        int C = 11;

        // detective locations
        // mrX location
        // distance mrx and detectives (dijkstra's algorithm)
        // number of moves available to mrX (more moves = better) (higher score) (nbr of desitnations from current location)
        // node station (taxi, bus, underground)
        // Integer mrXlocation = this.getMrXLocation(board).get();
        
        // int score = 0;


        // get detective locations
        List<Integer> detectiveLocations = this.getDetectiveLocations(this.state);

        // nodes from mrX location
        // Set<Integer> adjacentNodes = this.state.getSetup().graph.adjacentNodes(mrXlocation)
        // .stream()
        // .filter(node -> !this.occupiedLocation(detectiveLocations, node)).collect(ImmutableSet.toImmutableSet());
        Set<Integer> adjacentNodes = this.state.getSetup().graph.adjacentNodes(mrXlocation);

        // number of ajacent nodes
        int nbrOfNodes = adjacentNodes.size(); // part of score

        this.score += (nbrOfNodes * C); // 12

        // dijkstra's algorithm
        for (Integer detectiveLocation : detectiveLocations) {
            // shortest path from mrX to detective
            int shortestPath = this.ShortestPathFromSourceToDestination(this.state.getSetup().graph, detectiveLocation, mrXlocation);

            // System.out.println("MrX: " + mrXlocation);
            // System.out.println("Detective:" + detectiveLocation);
            // System.out.println("Shortest Path:" + shortestPath);

            this.score += shortestPath;

            // check if mrX is in a position where he can be caught
            if (adjacentNodes.contains(detectiveLocation)) {
                this.score -= 1000;
            }
        }

        System.out.println("mrXLocation" + mrXlocation);
        System.out.println("Score after dijkstra's algorithm: " + this.score);

        // tickets score
        this.score += moveTicketScore(move);
        System.out.println("Score after ticket score " + this.score);


        // winning state score
        this.state.getWinner().forEach(winner -> {
            if (winner == Piece.MrX.MRX) {
                this.score += 1000;
            } else {
                this.score -= 1000;
            }
        });

        // check if detective in adjacent nodes
            // mrX is in a position where he can be caught

 
    //    this.score = score;

        // a good move for mrx is to get away from the detectives and be in a position with many options to move
        // return score of the current board
        // using this method we select the best move for mrX to take
    }


    // get mrX location

    private Integer getMrXLocationFromMove(Move move) {
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

        private List<Integer> getDetectiveLocations(Board board) {
        List<Integer> detectiveLocations = new ArrayList<Integer>();
        // get player locations
        for (Piece player : board.getPlayers()) {
            if (player.isDetective()) {
                Optional<Integer> location = board.getDetectiveLocation((Piece.Detective) player);

                if (location.isPresent()) {
                    detectiveLocations.add(location.get());
                }
            }
        }

        return detectiveLocations;
    }

    private boolean occupiedLocation(List<Integer> detectiveLocations, int destination) {
        for (Integer location : detectiveLocations) {
            if (location == destination) {
                return true;
            }
        }
        return false;
    }

    // TODO fix
    private Integer moveTicketScore(Move move) {
        return move.accept(new Move.Visitor<Integer>() {
            @Override
            public Integer visit(Move.SingleMove move) {
                // return 20;
                if (move.ticket == Ticket.TAXI)
                    return 4;
                else if (move.ticket == Ticket.BUS)
                    return 3;
                else if (move.ticket == Ticket.UNDERGROUND)
                    return 2;
                else if (move.ticket == Ticket.SECRET) {
                    Boolean isRevealed = state.getSetup().moves.get(state.getMrXTravelLog().size());
                    if (isRevealed) {
                        // System.out.println(lastLog.location().get());
                        // if mrX location is revealed increase scroe fro secret move
                        return 1;
                    }

                    // System.out.println("MrX location is not revealed");
                    return 25;
                    // if (lastLog.location().get() == -1) {
                    //     return 5;
                    // } else {
                    //     return 25;
                    // }
                    // if (state.(state.getMrXTravelLog().size() - 1))
                    // return 5;
                }
                else
                    return 0;
            }

            @Override
            public Integer visit(Move.DoubleMove move) {
                // return 5;
                int score = 0;
                for (Ticket ticket : move.tickets()) {
                    if (ticket == Ticket.TAXI)
                        score += 4;
                    else if (ticket == Ticket.BUS)
                        score += 3;
                    else if (ticket == Ticket.UNDERGROUND)
                        score += 2;
                    else if (ticket == Ticket.SECRET)
                        score += 1;
                }

                Boolean isRevealed = state.getSetup().moves.get(state.getMrXTravelLog().size()+1);
                if (isRevealed) {
                    score += 20;
                }
                return score;
            }
        });
    }

// separate class
private Integer ShortestPathFromSourceToDestination(ImmutableValueGraph<Integer, ImmutableSet<Transport>> graph, int source, int destination) {
    int Infinity = Integer.MAX_VALUE;
    int size = graph.nodes().size();


    Integer dist[] = new Integer[size+1];
    Integer prev[] = new Integer[size+1];
    Boolean unvisited[] = new Boolean[size+1];


    List<Integer> shortestPath = new ArrayList<>();

    for (int i = 0; i <= size; i++) {
        dist[i] = Infinity;
        prev[i] = null; // undefined previous
        unvisited[i] = false;
    }

    // dist of source
    dist[source] = 0;

    for (int i = 0; i < size; i++) {
        Integer u = findMinDistance(dist, unvisited);

        // only interested in destination
        if (u == destination) {
            // if (prev[u] == null || u == source) {
            while (prev[u] != null || u != source) {
                shortestPath.add(u);
                u = prev[u];
            }

            shortestPath.add(source); // add source node to the shortest path
            Collections.reverse(shortestPath); // reverse the list to get the correct order
            // System.out.println(shortestPath);
            // distance to destination
            return dist[destination];
        }

        unvisited[u] = true;
        for (int v : graph.adjacentNodes(u)) {
            ImmutableSet<Transport> transports = graph.edgeValue(u, v).get();
            int edgeWeight = getTransportsWeight(transports);

            if (!unvisited[v] && !graph.edgeValue(u, v).isEmpty() && (dist[u] + edgeWeight < dist[v])) {
                dist[v] = dist[u] + edgeWeight;
                prev[v] = u;
            }
        }

    }

    return -1;
}

	private static int findMinDistance(Integer[] distance, Boolean[] visitedVertex) {
		int minDistance = Integer.MAX_VALUE;
		int minDistanceVertex = -1;

		for (int i = 0; i < distance.length; i++) {
			if (!visitedVertex[i] && distance[i] < minDistance) {
				minDistance = distance[i];
				minDistanceVertex = i;
			}
		}

		return minDistanceVertex;
	}

    private static int getTransportsWeight(ImmutableSet<Transport> transports) {
        int weight = 0;

        for (Transport transport : transports) {
            switch (transport) {
                case TAXI:
                    weight += 8;
                    break;
                case BUS:
                    weight += 4;
                    break;
                case UNDERGROUND:
                    weight += 1;
                    break;
            }
        }

        return weight;
    }

  
}
