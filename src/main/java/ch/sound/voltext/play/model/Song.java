package ch.sound.voltext.play.model;

import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.FIELD)
public class Song {

	@XmlAttribute
	private String title;
	
	private List<PlayData> plays;
	
	public List<PlayData> getPlays() {
		return plays;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}
	
}
