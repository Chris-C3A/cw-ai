package uk.ac.bris.cs.scotlandyard.ui.ai.Score;

import java.util.List;
import java.util.Set;


import uk.ac.bris.cs.scotlandyard.model.Move;
import uk.ac.bris.cs.scotlandyard.model.ScotlandYard.Ticket;
import uk.ac.bris.cs.scotlandyard.ui.ai.State;



public class ScoreMrX implements ScoreHeuristic {
    private int score;
    private int mrXlocation;
    private State state;
    private int round;
    private Move move;

    // Constructor
    public ScoreMrX(Move move, State state, int round) {
        this.move = move;
        this.state = state;
        this.round = round;

        this.score = 0;
        this.mrXlocation = this.state.getMrXLocation();

        // scores state
        this.scoreState();
    }

    // Getters
    public int getScore() {
        return this.score;
    }

    public Move getMove() {
        return this.move;
    }

    public int getRound() {
        return this.round;
    }

    public int getMrXLocation() {
        return this.mrXlocation;
    }

    public void scoreState() {
        // get detective locations
        List<Integer> detectiveLocations = this.state.getDetectiveLocations();

        // gets adjancent nodes to mrX location
        Set<Integer> adjacentNodes = this.state.getSetup().graph.adjacentNodes(mrXlocation);

        // add score for available locations from mrX location
        this.score += this.availableLocationsScore(adjacentNodes);


        // dijkstra's algorithm
        // get shortest path from mrX to detectives
        int minDetectiveDistance = Integer.MAX_VALUE;
        for (Integer detectiveLocation : detectiveLocations) {

            // shortest path from mrX to detective
            int shortestPath = Dijkstra.ShortestPathFromSourceToDestination(this.state.getSetup().graph, detectiveLocation, mrXlocation);

            // get minimum distance from mrX to detective
            minDetectiveDistance = Math.min(minDetectiveDistance, shortestPath);

            // this.score += shortestPath*10;

            // check if mrX is in a position where he can be caught
            if (adjacentNodes.contains(detectiveLocation)) {
                this.score -= 2000;
            }
        }

        // add min detective distance to score * 20
        this.score += minDetectiveDistance * 10;

        // tickets score
        this.score += moveTicketScore(move);


        // winning state score (terminal state scoring)
        if (this.state.getWinner() == State.winner.MrX) {
            this.score += 2000;
        } else if (this.state.getWinner() == State.winner.Detectives) {
            this.score -= 2000;
        }
    }

    private Integer availableLocationsScore(Set<Integer> adjacentNodes) {
        // int C = 12;
        int C = 12;

        // number of ajacent nodes
        int nbrOfNodes = adjacentNodes.size(); // part of score

        // return nbr of adjace cent nodes * C
        return nbrOfNodes * C;
    }



    /**
     * @param move
     * @return score for the move based on the ticket used and the type of move
     */
    private Integer moveTicketScore(Move move) {
        return move.accept(new MoveTicketVisitor());
    }


    /**
     * MoveTicketVisitor class implements the Visitor interface
     * to score the move based on the ticket used and the type of move
     */
    private class MoveTicketVisitor implements Move.Visitor<Integer> {
        @Override
        public Integer visit(Move.SingleMove move) {
            // multiplier constant for increase scoring for single moves
            // increases the likely hood of mrX choosing a single move
            int multiplier = 35;

            if (move.ticket == Ticket.TAXI)
                return 4*multiplier;
                // return TicketWeight.TAXI.getValue() * multiplier;
            else if (move.ticket == Ticket.BUS)
                return 4*multiplier;
                // return TicketWeight.BUS.getValue() * multiplier;
            else if (move.ticket == Ticket.UNDERGROUND)
                return 4*multiplier;
                // return TicketWeight.UNDERGROUND.getValue() * multiplier;
            else if (move.ticket == Ticket.SECRET) {
                
                // check if previous round was revealed
                if (round > 1 && state.getSetup().moves.get(round-2)) {
                    // increase score for secret ticket if previous round was revealed
                    // heavily weighted
                    return TicketWeight.SECRET_HEAVY.getValue() * multiplier;
                } else {
                    // lightly weighted
                    return TicketWeight.SECRET_LIGHT.getValue() * multiplier;
                }
            }
            else
                return 0;
        }

        @Override
        public Integer visit(Move.DoubleMove move) {
            int score = 0;
            for (Ticket ticket : move.tickets()) {
                if (ticket == Ticket.TAXI)
                    score += TicketWeight.TAXI.getValue();
                else if (ticket == Ticket.BUS)
                    score += TicketWeight.BUS.getValue();
                else if (ticket == Ticket.UNDERGROUND)
                    score += TicketWeight.UNDERGROUND.getValue();
                else if (ticket == Ticket.SECRET) {
                    // check if previous round was revealed
                    // Boolean previousRoundRevealed = state.getSetup().moves.get(round-2);
                    if (round > 1  && state.getSetup().moves.get(round-2)) {
                        // increase score for secret ticket if previous round was revealed
                        // heavily weighted
                        score += TicketWeight.SECRET_HEAVY.getValue();
                    } else {
                        // lightly weighted
                        score += TicketWeight.SECRET_LIGHT.getValue();
                    }
                }
            }
            return score;
        }
    }

  
}
