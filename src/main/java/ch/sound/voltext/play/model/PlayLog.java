package ch.sound.voltext.play.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlTransient;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlAccessorType(XmlAccessType.FIELD)
public class PlayLog {

	@XmlAttribute
	private int score;

	@XmlAttribute
	private Lamp lamp;

	@XmlAttribute
	@XmlJavaTypeAdapter(type = LocalDateTime.class, value = LocalDateTimeXMLAdapter.class)
	private LocalDateTime date;

	@XmlTransient
	private BigDecimal volforce;

	@XmlAttribute(name = "volforce")
	private BigDecimal volforceNormalized;

	/**
	 * @return the score
	 */
	public int getScore() {
		return score;
	}

	public String getFormatedScore() {
		String scoreAsString = Integer.toString(score);
		if (scoreAsString.length() > 4) {
			String relevantPart = scoreAsString.substring(0, scoreAsString.length() - 4);
			String forgetablePart = scoreAsString.substring(scoreAsString.length() - 4);
			return relevantPart + "(" + forgetablePart + ")";
		}

		return scoreAsString;
	}

	/**
	 * @return the lamp
	 */
	public Lamp getLamp() {
		return lamp;
	}

	/**
	 * @return the date
	 */
	public LocalDateTime getDate() {
		return date;
	}

	public enum Grade {
		// @formatter:off
		D("D", 0, 6_499_499), 
		C("C", 6_500_000, 7_499_999), 
		B("B", 7_500_000, 8_699_999), 
		A("A", 8_700_000, 8_999_999),
		A_PLUS("A+", 9_000_000, 9_299_999), 
		AA("AA", 9_300_000, 9_499_999), 
		AA_PLUS("AA+", 9_500_000, 9_699_999),
		AAA("AAA", 9_700_000, 9_799_999), 
		AAA_PLUS("AAA+", 9_800_000, 9_899_999), 
		S("S", 9_900_000, 10_000_000);
		// @formatter:on

		private String name;
		private int from;
		private int to;

		private Grade(String name, int from, int to) {
			this.name = name;
			this.from = from;
			this.to = to;
		}

		public static Grade fromScore(int score) {
			for (Grade grade : Grade.values()) {
				if (score >= grade.from && score <= grade.to) {
					return grade;
				}
			}

			throw new IllegalArgumentException("Score not between 0 and 10.000.000!");
		}

		public String getName() {
			return name;
		}

		public int getFrom() {
			return from;
		}

		public int getTo() {
			return to;
		}
		@Override
		public String toString() {
			return getName();
		}

	}

	public void setVolforce(BigDecimal volforce) {
		this.volforce = volforce;
		this.volforceNormalized = volforce.multiply(BigDecimal.valueOf(0.001)).setScale(3, RoundingMode.HALF_DOWN);
	}

}
