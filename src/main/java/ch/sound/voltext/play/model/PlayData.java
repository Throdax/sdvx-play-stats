package ch.sound.voltext.play.model;

import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class PlayData {
	
	@XmlAttribute
	private Difficulty difficulty;
	
	@XmlAttribute
	private int rating;
	
	@XmlElement(name = "play")
	private List<PlayLog> playsLog;
	
	public List<PlayLog> getPlaysLog() {
		return playsLog;
	}

	/**
	 * @return the difficulty
	 */
	public Difficulty getDifficulty() {
		return difficulty;
	}

	/**
	 * @return the rating
	 */
	public int getRating() {
		return rating;
	}
	

}
