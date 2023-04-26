package uk.ac.bris.cs.scotlandyard.ui.ai.Score;


import uk.ac.bris.cs.scotlandyard.model.Move;
import uk.ac.bris.cs.scotlandyard.model.Piece;
import uk.ac.bris.cs.scotlandyard.ui.ai.State;

public class ScoreDetective implements ScoreHeuristic {
    private int score;
    private int mrXlocation;
    private int detectiveLocation;
    private State state;
    private Move move;

    // Constructor
    public ScoreDetective(Piece detective, Move move, State state) {
        this.move = move;
        this.state = state;

        // get detective location
        this.detectiveLocation = this.state.getBoard().getDetectiveLocation((Piece.Detective) detective).get();

        this.score = 0;
        // get mrX location from current state
        this.mrXlocation = this.state.getMrXLocation();

        // score state
        this.scoreState();
    }

    // scores detective state
    public void scoreState() {
        // get shorest distance from detective to mrX
        int detectiveDistanceToMrX = Dijkstra.ShortestPathFromSourceToDestination(this.state.getSetup().graph, this.detectiveLocation, this.mrXlocation);

        if (detectiveDistanceToMrX == 0) {
            this.score = Integer.MAX_VALUE;
            return;
        }
        // higher score if closer to mrX
        // inverses distance so that the closer the detective is to mrX the higher the score
        this.score = (1/detectiveDistanceToMrX) * 100;
    }

    public int getScore() {
        return this.score;
    }

    public Move getMove() {
        return this.move;
    }
}
