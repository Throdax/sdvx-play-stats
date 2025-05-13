package ch.sound.voltext.play.model;

import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "songList")
@XmlAccessorType(XmlAccessType.FIELD)
public class Songlist {

	@XmlElement(name = "song")
	private List<Song> songs;
	
	
	public List<Song> getSongs() {
		return songs;
	}
}
