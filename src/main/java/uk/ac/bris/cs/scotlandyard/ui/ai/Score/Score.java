package uk.ac.bris.cs.scotlandyard.ui.ai.Score;

import java.util.List;
import java.util.Set;


import uk.ac.bris.cs.scotlandyard.model.Move;
import uk.ac.bris.cs.scotlandyard.model.Board.GameState;
import uk.ac.bris.cs.scotlandyard.model.ScotlandYard.Ticket;
import uk.ac.bris.cs.scotlandyard.ui.ai.State;



public class Score {
    private int score;
    private int mrXlocation;
    private State state;
    private int round;
    private Move move;

    // Constructor
    public Score(Move move, State state, Boolean maximizingPlayer, int round) {
        this.move = move;
        this.state = state;
        this.round = round;

        this.score = 0;
        this.mrXlocation = this.state.getMrXLocation();

        this.scoreState();
    }

    // Getters
    public int getScore() {
        return this.score;
    }

    public int getRound() {
        return this.round;
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
        int C = 6;

        // get detective locations
        List<Integer> detectiveLocations = this.state.getDetectiveLocations();

        // gets adjancent nodes to mrX location
        Set<Integer> adjacentNodes = this.state.getSetup().graph.adjacentNodes(mrXlocation);

        // number of ajacent nodes
        int nbrOfNodes = adjacentNodes.size(); // part of score

        // add score for nbr of nodes mrX can move to
        // scores locations where mrX has more options to move to
        this.score += (nbrOfNodes * C); // 12


        int minDetectiveDistance = Integer.MAX_VALUE;
        // dijkstra's algorithm
        // get shortest path from mrX to detectives
        for (Integer detectiveLocation : detectiveLocations) {

            // shortest path from mrX to detective
            int shortestPath = Dijkstra.ShortestPathFromSourceToDestination(this.state.getSetup().graph, detectiveLocation, mrXlocation);

            // get minimum distance from mrX to detective
            minDetectiveDistance = Math.min(minDetectiveDistance, shortestPath);

            // this.score += shortestPath;

            // check if mrX is in a position where he can be caught
            if (adjacentNodes.contains(detectiveLocation)) {
                this.score -= 1000;
            }
        }

        // add min detective distance to score
        this.score += minDetectiveDistance;

        // tickets score
        this.score += moveTicketScore(move);


        // winning state score (terminal state scoring)
        if (this.state.getWinner() == State.winner.MrX) {
            this.score += 2000;
        } else if (this.state.getWinner() == State.winner.Detectives) {
            this.score -= 2000;
        }
    }



    //! move filtering (try to se if it can be used separately)
    private Integer moveTicketScore(Move move) {
        //! take scarcity into consideration
        return move.accept(new Move.Visitor<Integer>() {
            @Override
            public Integer visit(Move.SingleMove move) {
                // multiplier constant for increase scoring for single moves
                // increases the likely hood of mrX choosing a single move
                int multiplier = 10;
                if (move.ticket == Ticket.TAXI)
                    return 4*multiplier;
                else if (move.ticket == Ticket.BUS)
                    return 3*multiplier;
                else if (move.ticket == Ticket.UNDERGROUND)
                    return 2*multiplier;
                else if (move.ticket == Ticket.SECRET) {
                    
                    // check if previous round was revealed
                    if (round > 1 && state.getSetup().moves.get(round-1-1)) {
                        // increase score for secret ticket if previous round was revealed
                        return 8*multiplier;
                    } else {
                        return 1*multiplier;
                    }
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
                        // check if previous round was revealed
                        // Boolean previousRoundRevealed = state.getSetup().moves.get(round-2);
                        if (round > 1  && state.getSetup().moves.get(round-1-1)) {
                            // increase score for secret ticket if previous round was revealed
                            score += 8;
                        } else {
                            score += 1;
                        }

                    }
                }
                return score;
            }
        });
    }



  
}
