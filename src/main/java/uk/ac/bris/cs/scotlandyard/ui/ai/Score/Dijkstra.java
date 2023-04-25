package uk.ac.bris.cs.scotlandyard.ui.ai.Score;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableSet;
import com.google.common.graph.ImmutableValueGraph;

import uk.ac.bris.cs.scotlandyard.model.ScotlandYard.Transport;

public class Dijkstra {
  

  // get shortest path from source to destination using dijkstra's algorithm
  public static Integer ShortestPathFromSourceToDestination(ImmutableValueGraph<Integer, ImmutableSet<Transport>> graph, int source, int destination) {
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
              // Collections.reverse(shortestPath); // reverse the list to get the correct order
              // System.out.println(shortestPath);
              // distance to destination
              
              // return distance to destination
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

    // helper function for dijkstra's algorithm
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

    //! refactor values
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
