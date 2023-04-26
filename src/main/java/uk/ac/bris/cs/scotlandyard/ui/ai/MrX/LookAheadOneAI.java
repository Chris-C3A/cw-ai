package uk.ac.bris.cs.scotlandyard.ui.ai.MrX;

import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;


import io.atlassian.fugue.Pair;
import uk.ac.bris.cs.scotlandyard.model.*;
import uk.ac.bris.cs.scotlandyard.model.Board.GameState;
import uk.ac.bris.cs.scotlandyard.ui.ai.State;
import uk.ac.bris.cs.scotlandyard.ui.ai.Score.ScoreMrX;

public class LookAheadOneAI implements Ai {

	@Nonnull @Override public String name() { return "Look-Ahead-One AI"; }

  @Nonnull
  @Override 
  public Move pickMove(@Nonnull Board board, Pair<Long, TimeUnit> timeoutPair) {
    State state = new State((GameState) board, board.getAvailableMoves().asList().get(0).source());


		var moves = board.getAvailableMoves().asList();

    // maxScore object
    ScoreMrX maxScore = null;

    for (Move move : moves) {
        State nextState = state.advanceMrX(move);

        // get score of state
        ScoreMrX score = new ScoreMrX(move, nextState, nextState.getRoundNumber());

        // update maxScore
        if (maxScore == null || score.getScore() > maxScore.getScore()) {
            maxScore = score;
        }

        System.out.println("Score:" + score.getScore());
        System.out.println("Move:" + score.getMove().toString());
    }

    System.out.println("maxScore:" + maxScore.getScore());
    System.out.println("Best Move: " + maxScore.getMove().toString());

    return maxScore.getMove();
	}
}