package uk.ac.bris.cs.scotlandyard.ui.ai.MrX;

import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;


import io.atlassian.fugue.Pair;
import uk.ac.bris.cs.scotlandyard.model.*;
import uk.ac.bris.cs.scotlandyard.model.Board.GameState;
// import uk.ac.bris.cs.scotlandyard.ui.ai.MonteCarloTest.MonteCarlo;
import uk.ac.bris.cs.scotlandyard.ui.ai.MonteCarloImplementation.MonteCarlo;
import uk.ac.bris.cs.scotlandyard.ui.ai.State;

public class MonteCarloMrXAI implements Ai {

	@Nonnull @Override public String name() { return "MonteCarlo AI"; }

  @Nonnull
  @Override
  public Move pickMove(@Nonnull Board board, Pair<Long, TimeUnit> timeoutPair) {
    State state = new State((GameState) board, board.getAvailableMoves().asList().get(0).source());

    // MonteCarlo class instance
    MonteCarlo monteCarlo = new MonteCarlo(state, true);
 
    Move bestMove = monteCarlo.findNextMove(timeoutPair);

    System.out.println("Best Move: " + bestMove.toString());
    return bestMove;
	}

}