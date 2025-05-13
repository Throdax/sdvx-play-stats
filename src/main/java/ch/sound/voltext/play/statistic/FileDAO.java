package ch.sound.voltext.play.statistic;

import java.nio.file.Path;

import ch.sound.voltext.play.model.Songlist;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

public class FileDAO {

	public static Songlist loadSongData(Path playDataXmlPath) throws JAXBException {

		JAXBContext ctx = JAXBContext.newInstance(Songlist.class);
		Unmarshaller unmarshaller = ctx.createUnmarshaller();
		Object unmarshal = unmarshaller.unmarshal(playDataXmlPath.toFile());
		return Songlist.class.cast(unmarshal);

	}

	public static boolean writeSongData(Path playDataXmlPath, Songlist songList) throws JAXBException {
		JAXBContext ctx = JAXBContext.newInstance(Songlist.class);
		Marshaller marshaller = ctx.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		marshaller.marshal(songList, playDataXmlPath.toFile());
		return true;

	}

}
