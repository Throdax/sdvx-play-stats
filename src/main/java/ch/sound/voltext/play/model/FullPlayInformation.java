package ch.sound.voltext.play.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.Objects;

import ch.sound.voltext.play.statistic.VolforceCalculator;

public class FullPlayInformation {

	private String title;
	private int rating;
	private Difficulty difficulty;
	private Lamp lamp;
	private int score;
	private LocalDateTime playDate;
	private BigDecimal volforce;
	private BigDecimal normalizedVolforce;

	private NumberFormat numberFormat = NumberFormat.getNumberInstance();

	public FullPlayInformation() {
	}
	
	public FullPlayInformation(String title, int rating, Difficulty difficulty, Lamp lamp, int score) {
		this.title = title;
		this.rating = rating;
		this.difficulty = difficulty;
		this.lamp = lamp;
		this.score = score;
	}

	public FullPlayInformation(String title, int rating, Difficulty difficulty, Lamp lamp, int score,
			LocalDateTime playDate) {
		this(title,rating,difficulty,lamp,score);
		this.playDate = playDate;
	}

	@Override
	public String toString() {
		return title + ": " + difficulty.getName() + "-" + rating + ": " + lamp.getName() + ": "
				+ numberFormat.format(score) + " -> Volforce:  "+volforce.toString();
	}

	public BigDecimal calculateVolforce() {
		
		BigDecimal clearCoefficient = VolforceCalculator.calculateClearCoefficient(lamp).setScale(2,
				RoundingMode.HALF_DOWN);
		BigDecimal gradeCoefficient = VolforceCalculator.calculateGradeCoefficient(score).setScale(2,
				RoundingMode.HALF_DOWN);

		volforce = new BigDecimal(
				rating * (score / 10_000_000f) * gradeCoefficient.floatValue() * clearCoefficient.floatValue() * 20)
				.setScale(0, RoundingMode.DOWN);

		normalizedVolforce = volforce.multiply(BigDecimal.valueOf(0.001)).setScale(3, RoundingMode.DOWN);
		return volforce;
	}

	public BigDecimal getNormalizedVolforce() {
		if(normalizedVolforce == null) {
			calculateVolforce();
		}
		
		return normalizedVolforce;
	}

	public BigDecimal getVolforce() {
		return volforce;
	}

	public void setVolforce(BigDecimal volforce) {
		this.volforce = volforce;
	}

	@Override
	public int hashCode() {
		return Objects.hash(difficulty, lamp, playDate, rating, score, title);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		FullPlayInformation other = (FullPlayInformation) obj;
		return difficulty == other.difficulty && lamp == other.lamp && Objects.equals(playDate, other.playDate)
				&& rating == other.rating && score == other.score && Objects.equals(title, other.title);
	}
	
	public boolean equalSong(FullPlayInformation other) {
		if (this == other) {
			return true;
		}
		if (other == null) {
			return false;
		}
		if (getClass() != other.getClass()) {
			return false;
		}
		return difficulty == other.difficulty && rating == other.rating && title.equalsIgnoreCase(other.title);
	}

	public String getTitle() {
		return title;
	}

	public int getRating() {
		return rating;
	}

	public Difficulty getDifficulty() {
		return difficulty;
	}

	public Lamp getLamp() {
		return lamp;
	}

	public int getScore() {
		return score;
	}

	public LocalDateTime getPlayDate() {
		return playDate;
	}

	public NumberFormat getNumberFormat() {
		return numberFormat;
	}

	

}
