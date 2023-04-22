package uk.ac.bris.cs.scotlandyard.ui.ai.Score;


import uk.ac.bris.cs.scotlandyard.model.Move;
import uk.ac.bris.cs.scotlandyard.model.Piece;
import uk.ac.bris.cs.scotlandyard.ui.ai.State;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.ImmutableSet;
import com.google.common.graph.ImmutableValueGraph;

import uk.ac.bris.cs.scotlandyard.model.ScotlandYard.Transport;

public class ScoreDetective {
    private int score;
    private int mrXlocation;
    private int detectiveLocation;
    private State state;
    private Move move;

    // Constructor
    public ScoreDetective(Piece detective, Move move, State state) {
        this.move = move;
        this.state = state;

        this.detectiveLocation = this.state.getBoard().getDetectiveLocation((Piece.Detective) detective).get();

        this.score = 0;
        this.mrXlocation = this.state.getMrXLocation();

        this.scoreState();
    }

    public void scoreState() {
        int detectiveDistanceToMrX = this.ShortestPathFromSourceToDestination(this.state.getSetup().graph, this.detectiveLocation, this.mrXlocation);

        if (detectiveDistanceToMrX == 0) {
            this.score = Integer.MAX_VALUE;
            return;
        }
        // higher score if closer to mrX
        this.score += (1/detectiveDistanceToMrX) * 100;

        // int minDetectiveDistance = Integer.MAX_VALUE;
        // // dijkstra's algorithm
        // for (Integer detectiveLocation : detectiveLocations) {
        //     // shortest path from mrX to detective
        //     int shortestPath = this.ShortestPathFromSourceToDestination(this.state.getSetup().graph, detectiveLocation, mrXlocation);

        //     // get minimum distance from mrX to detective
        //     minDetectiveDistance = Math.min(minDetectiveDistance, shortestPath);

        //     // System.out.println("MrX: " + mrXlocation);
        //     // System.out.println("Detective:" + detectiveLocation);
        //     // System.out.println("Shortest Path:" + shortestPath);

        //     // this.score += shortestPath;

        //     // check if mrX is in a position where he can be caught
        //     if (adjacentNodes.contains(detectiveLocation)) {
        //         this.score -= 1000;
        //     }
        // }

        // this.score += minDetectiveDistance;

    }

    public int getScore() {
        return this.score;
    }

    public Move getMove() {
        return this.move;
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
