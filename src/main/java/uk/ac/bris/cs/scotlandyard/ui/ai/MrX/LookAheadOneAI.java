package uk.ac.bris.cs.scotlandyard.ui.ai.MrX;

import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;


import io.atlassian.fugue.Pair;
import uk.ac.bris.cs.scotlandyard.model.*;
import uk.ac.bris.cs.scotlandyard.model.Board.GameState;
import uk.ac.bris.cs.scotlandyard.ui.ai.State;
import uk.ac.bris.cs.scotlandyard.ui.ai.Score.Score;

public class LookAheadOneAI implements Ai {

	@Nonnull @Override public String name() { return "Look-Ahead-One AI"; }

  @Nonnull
  @Override 
  public Move pickMove(@Nonnull Board board, Pair<Long, TimeUnit> timeoutPair) {
    // GameState state = (GameState) board;
    State state = new State((GameState) board, board.getAvailableMoves().asList().get(0).source());


		var moves = board.getAvailableMoves().asList();

    Score maxScore = null;
    Move bestMove = null;

    for (Move move : moves) {
        // GameState nextState = state.advance(move);
        State nextState = state.advanceMrX(move);

        // get score of state
        Score score = new Score(move, nextState, true, nextState.getRoundNumber());
        // Score score = new Score(nextState, true);

        // update maxScore
        if (maxScore == null || score.getScore() > maxScore.getScore()) {
            maxScore = score;
            bestMove = move;
        }

        System.out.println("Score:" + score.getScore());
        System.out.println("Move:" + bestMove);
    }

    System.out.println("maxScore:" + maxScore.getScore());
    // System.out.println("maxScore:" + maxScore);
    // return moves.get(new Random().nextInt(moves.size()));
    System.out.println("Best Move: " + bestMove.toString());
    // System.out.println("Best Move: " + bestMove.toString());
    // return maxScore.getMove();
    return bestMove;
	}
}