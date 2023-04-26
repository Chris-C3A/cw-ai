package uk.ac.bris.cs.scotlandyard.ui.ai.Score;

import uk.ac.bris.cs.scotlandyard.model.Move;

public interface ScoreHeuristic {
	public int getScore();
	public Move getMove();

	public void scoreState(); // score state function
}
