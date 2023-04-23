package uk.ac.bris.cs.scotlandyard.ui.ai.MrX;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;


import io.atlassian.fugue.Pair;
import uk.ac.bris.cs.scotlandyard.model.*;

public class RandomAI implements Ai {

	@Nonnull @Override public String name() { return "Random AI"; }

	@Nonnull @Override public Move pickMove(
			@Nonnull Board board,
			Pair<Long, TimeUnit> timeoutPair) {

		var moves = board.getAvailableMoves().asList();

		// returns a random move
		return moves.get(new Random().nextInt(moves.size()));
	}

}
