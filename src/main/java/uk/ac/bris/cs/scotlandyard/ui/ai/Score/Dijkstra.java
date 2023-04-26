package uk.ac.bris.cs.scotlandyard.ui.ai.Score;

import com.google.common.collect.ImmutableSet;
import com.google.common.graph.ImmutableValueGraph;

import uk.ac.bris.cs.scotlandyard.model.ScotlandYard.Transport;

public class Dijkstra {
  

    /**
     * @param graph
     * @param source
     * @param destination
     * @return the shortest path from source to destination
     */
    public static Integer ShortestPathFromSourceToDestination(ImmutableValueGraph<Integer, ImmutableSet<Transport>> graph, int source, int destination) {
        int Infinity = Integer.MAX_VALUE;

        // size of graph
        int size = graph.nodes().size();


        // initialise distance, previous and unvisited arrays
        Integer dist[] = new Integer[size+1];
        Integer prev[] = new Integer[size+1];
        Boolean unvisited[] = new Boolean[size+1];

        for (int i = 0; i <= size; i++) {
            // initialise distance to infinity for all nodes
            dist[i] = Infinity;
            // initialise previous to null for all nodes
            prev[i] = null;
            // initialise unvisited to false for all nodes
            unvisited[i] = false;
        }

        // distance of source
        dist[source] = 0;

        for (int i = 0; i < size; i++) {
            // find the vertex with the minimum distance
            Integer u = findMinDistance(dist, unvisited);

            // only interested in destination
            if (u == destination) {
                while (prev[u] != null || u != source) {
                    u = prev[u];
                }

                // return distance to destination
                return dist[destination];
            }

            // mark as visited
            unvisited[u] = true;

            for (int v : graph.adjacentNodes(u)) {
                // get the transports between u and v
                ImmutableSet<Transport> transports = graph.edgeValue(u, v).get();
                
                // get the minimum weight of the transports
                int edgeWeight = getTransportsWeight(transports);

                // if the vertex is unvisited and there is an edge between u and v
                if (!unvisited[v] && !graph.edgeValue(u, v).isEmpty() && (dist[u] + edgeWeight < dist[v])) {
                    // update distance
                    dist[v] = dist[u] + edgeWeight;
                    // update previous
                    prev[v] = u;
                }
            }
        }

      return -1;
    }

    /**
     * helper function for dijkstra's algorithm
     * @param distance
     * @param visitedVertex
     * @return the vertex with the minimum distance
     */
    private static int findMinDistance(Integer[] distance, Boolean[] visitedVertex) {
        int minDistance = Integer.MAX_VALUE;
        int minDistanceVertex = -1;

        for (int i = 0; i < distance.length; i++) {
            // if the vertex is unvisited and the distance is less than the current minimum distance
            if (!visitedVertex[i] && distance[i] < minDistance) {
                // update minimum distance
                minDistance = distance[i];
                // update minimum distance vertex
                minDistanceVertex = i;
            }
        }

        return minDistanceVertex;
    }

    /**
     * @param transports
     * @return the minimum weight of the transports
     */
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
