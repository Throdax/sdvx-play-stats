package ch.sound.voltext.play.model;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlType;

@XmlType
@XmlEnum(String.class)
public enum Difficulty {
	@XmlEnumValue("nov")
	NOVICE("NOV"),
	
	@XmlEnumValue("adv")
	ADVANCED("ADV"),
	
	@XmlEnumValue("exh")
	EXHAUST("EXH"),
	
	@XmlEnumValue("append")
	APPEND("APPEND");
	
	private String name;
	
	private Difficulty(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
}
