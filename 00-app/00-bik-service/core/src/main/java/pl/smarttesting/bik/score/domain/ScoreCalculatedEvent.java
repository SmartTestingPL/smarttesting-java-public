package pl.smarttesting.bik.score.domain;

public class ScoreCalculatedEvent {
	
	private Pesel pesel;
	
	private Score score;
	
	public ScoreCalculatedEvent(Pesel pesel, Score score) {
		this.pesel = pesel;
		this.score = score;
	}
	
	public ScoreCalculatedEvent() {
		
	}

	public Pesel getPesel() {
		return pesel;
	}

	public void setPesel(Pesel pesel) {
		this.pesel = pesel;
	}

	public Score getScore() {
		return score;
	}

	public void setScore(Score score) {
		this.score = score;
	}

	@Override
	public String toString() {
		return "ScoreCalculatedEvent [pesel=" + pesel + ", score=" + score + "]";
	}
}
