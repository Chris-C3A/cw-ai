package uk.ac.bris.cs.scotlandyard.ui.ai;

import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;


import io.atlassian.fugue.Pair;
import uk.ac.bris.cs.scotlandyard.model.*;
import uk.ac.bris.cs.scotlandyard.model.Board.GameState;

public class LookAheadOneAI implements Ai {

	@Nonnull @Override public String name() { return "Look-Ahead-One AI"; }

  @Nonnull
  @Override 
  public Move pickMove(@Nonnull Board board, Pair<Long, TimeUnit> timeoutPair) {
    GameState state = (GameState) board;

		var moves = board.getAvailableMoves().asList();

    Score maxScore = null;

    for (Move move : moves) {
        GameState nextState = state.advance(move);

        // get score of state
        Score score = new Score(move, nextState, true);

        // update maxScore
        if (maxScore == null || score.getScore() > maxScore.getScore()) {
            maxScore = score;
        }

        System.out.println("Score:" + score.getScore());
        System.out.println("Move:" + score.getMove());
    }

    System.out.println("maxScore:" + maxScore.getScore());
    // System.out.println("maxScore:" + maxScore);
    // return moves.get(new Random().nextInt(moves.size()));
    System.out.println("Best Move: " + maxScore.getMove().toString());
    // System.out.println("Best Move: " + bestMove.toString());
    return maxScore.getMove();
	}
}