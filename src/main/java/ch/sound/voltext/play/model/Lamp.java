package ch.sound.voltext.play.model;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlType;

@XmlType
@XmlEnum(String.class)
public enum Lamp {
	@XmlEnumValue("failed")
	FAILED("FAILED"),
	
	@XmlEnumValue("clear")
	CLEAR("CLEAR"),
	
	@XmlEnumValue("hard")
	EX_CLEAR("EX Clear"),
	
	@XmlEnumValue("uc")
	UC("UC"),
	
	@XmlEnumValue("puc")
	PUC("PUC");
	
	private String name;
	
	private Lamp(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
}
