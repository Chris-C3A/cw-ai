package uk.ac.bris.cs.scotlandyard.ui.ai;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.graph.ImmutableValueGraph;

import uk.ac.bris.cs.scotlandyard.model.Move;
import uk.ac.bris.cs.scotlandyard.model.Piece;
import uk.ac.bris.cs.scotlandyard.model.Board.GameState;
import uk.ac.bris.cs.scotlandyard.model.ScotlandYard.Ticket;
import uk.ac.bris.cs.scotlandyard.model.ScotlandYard.Transport;



public class Score {
    private int score;
    private int mrXlocation;
    private State state;
    private Move move;

    // Constructor
    public Score(Move move, State state, Boolean maximizingPlayer) {
        this.move = move;
        this.state = state;

        this.score = 0;
        this.mrXlocation = this.state.getMrXLocation();

        this.scoreState();
    }

    // Getters
    public int getScore() {
        return this.score;
    }

    public int getMrXLocation() {
        return this.mrXlocation;
    }

    // public Move getMove() {
    //     return this.move;
    // }

    public void scoreDetectives(Move move, GameState state) {
        // get mrX location from travel log
        int mrXLocation = state.getMrXTravelLog().get(state.getMrXTravelLog().size() - 1).location().get();
        System.out.println(mrXLocation);
    }

    public void scoreState() {
        // Constants
        // int C = 12;
        int C = 9;

        // get detective locations
        // List<Integer> detectiveLocations = Game.getDetectiveLocations(this.state);
        List<Integer> detectiveLocations = this.state.getDetectiveLocations();

        Set<Integer> adjacentNodes = this.state.getSetup().graph.adjacentNodes(mrXlocation);

        // number of ajacent nodes
        int nbrOfNodes = adjacentNodes.size(); // part of score

        // add score for nbr of nodes mrX can move to
        this.score += (nbrOfNodes * C); // 12


        int minDetectiveDistance = Integer.MAX_VALUE;
        // dijkstra's algorithm
        for (Integer detectiveLocation : detectiveLocations) {
            // shortest path from mrX to detective
            int shortestPath = this.ShortestPathFromSourceToDestination(this.state.getSetup().graph, detectiveLocation, mrXlocation);

            // get minimum distance from mrX to detective
            minDetectiveDistance = Math.min(minDetectiveDistance, shortestPath);

            // System.out.println("MrX: " + mrXlocation);
            // System.out.println("Detective:" + detectiveLocation);
            // System.out.println("Shortest Path:" + shortestPath);

            // this.score += shortestPath;

            // check if mrX is in a position where he can be caught
            if (adjacentNodes.contains(detectiveLocation)) {
                this.score -= 1000;
            }
        }

        this.score += minDetectiveDistance;

        // System.out.println("mrXLocation" + mrXlocation);
        // System.out.println("Score after dijkstra's algorithm: " + this.score);

        // tickets score
        this.score += moveTicketScore(move);
        // System.out.println("Score after ticket score " + this.score);


        // winning state score
        if (this.state.getWinner() == State.winner.MrX) {
            this.score += 200;
        } else if (this.state.getWinner() == State.winner.Detectives) {
            this.score -= 200;
        }

        // System.out.println("Score: " + this.score);
    }



    // move filtering (try to se if it can be used separately)
    // TODO fix
    private Integer moveTicketScore(Move move) {
        //! take scarcity into consideration
        return move.accept(new Move.Visitor<Integer>() {
            @Override
            public Integer visit(Move.SingleMove move) {
                int multiplier = 10;
                // return 20;
                if (move.ticket == Ticket.TAXI)
                    return 4*multiplier;
                else if (move.ticket == Ticket.BUS)
                    return 3*multiplier;
                else if (move.ticket == Ticket.UNDERGROUND)
                    return 2*multiplier;
                else if (move.ticket == Ticket.SECRET) {
                    // System.out.println(state.getRoundNumber());
                    // System.out.println(state.getSetup().moves);

                    int round = state.getRoundNumber();
                    
                    if (round > 1 && state.getSetup().moves.get(round-1-1)) {
                        return 8*multiplier;
                    } else {
                        return 1*multiplier;
                    }
                    // Boolean isRevealed = state.getSetup().moves.get(state.getMrXTravelLog().size());
                    // if (isRevealed) {
                    //     // System.out.println(lastLog.location().get());
                    //     // if mrX location is revealed increase scroe fro secret move
                    //     return 1;
                    // }

                    // // System.out.println("MrX location is not revealed");
                    // return 25;
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
                    else if (ticket == Ticket.SECRET) {
                        int round = state.getRoundNumber();
                        // previous round was a reveawl lround
                        if (round > 1  && state.getSetup().moves.get(round-1-1)) {
                            score += 8;
                        } else {
                            score += 1;
                        }

                    }
                        // score += 1;
                }
                return score;

                // Boolean isRevealed = state.getSetup().moves.get(state.getMrXTravelLog().size()+1);
                // if (isRevealed) {
                //     score += 20;
                // }
                // return score;
            }
        });
    }

// separate class (dijsktra's algorithm)
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
        Integer weight = Integer.MAX_VALUE;

        for (Transport transport : transports) {
            Integer transportWeight = 0;
            switch (transport.requiredTicket()) {
                case TAXI:
                    transportWeight = 1;
                    break;
                case BUS:
                    transportWeight = 2;
                    break;
                case UNDERGROUND:
                    transportWeight = 4;
                    break;
                case SECRET:
                    transportWeight = 4;
                    break;
                case DOUBLE:
                    transportWeight = 8;
                    break;
            }
            weight = Math.min(weight, transportWeight);
        }

        return weight;
    }



  
}
