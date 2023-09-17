package pl.smarttesting.bik.score.analysis;

import pl.smarttesting.bik.score.domain.ScoreCalculatedEvent;

interface ScoreUpdater {

	void scoreCalculated(ScoreCalculatedEvent scoreCalculatedEvent);
}
