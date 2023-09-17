package pl.smarttesting.bik.score.domain;

import java.util.Objects;

public class Score {

	public static final Score ZERO = new Score(0);

	public int points;

	public Score(int points) {
		this.points = points;
	}

	public Score() {
	}

	public int getPoints() {
		return points;
	}

	public void setPoints(int points) {
		this.points = points;
	}

	public Score add(Score score) {
		return new Score(points + score.points);
	}

	@Override
	public String toString() {
		return "Score [points=" + points + "]";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Score score = (Score) o;
		return points == score.points;
	}

	@Override
	public int hashCode() {
		return Objects.hash(points);
	}
}
