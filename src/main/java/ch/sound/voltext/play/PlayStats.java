package ch.sound.voltext.play;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Map;

import ch.sound.voltext.play.model.Songlist;
import ch.sound.voltext.play.statistic.FileDAO;
import ch.sound.voltext.play.statistic.PlayStatsCalculator;
import ch.sound.voltext.play.statistic.SongDataUtils;
import ch.sound.voltext.play.statistic.VolforceCalculator;
import jakarta.xml.bind.JAXBException;

public class PlayStats {

	public static void main(String[] args) throws JAXBException {
		Path dataPath = Paths.get("D:\\Tools\\SoundVoltex\\sdvx_helper\\played_songs.xml");

		Songlist songData = FileDAO.loadSongData(dataPath);

		PlayStatsCalculator playsCalculator = PlayStatsCalculator.getInstance();
		playsCalculator.loadData(songData);

		System.out.println("Played " + playsCalculator.getTotalSongs() + " songs with "
				+ playsCalculator.getTotalPlays() + " total plays in "
				+ Duration
						.between(SongDataUtils.getFirstDate(songData), SongDataUtils.getLastDate(songData).plusDays(1))
						.toDays()
				+ " days");

		System.out.println("Volforce breakdown");
		printVolforceStatistics(songData);

		FileDAO.writeSongData(dataPath.getParent().resolve("played_songs_vf.xml"), songData);

	}


	

	

	private static void printVolforceStatistics(Songlist songData) {
		VolforceCalculator volforceCalculator = new VolforceCalculator(songData);

//		System.out.println("Volforce Songs: " + volforceCalculator.getTop50().size());
//		volforceCalculator.getSortedTop50ByName().forEach(vfTop -> System.out.println("\t" + vfTop));

		System.out.println("\t"+"Volforce: " + volforceCalculator.calculateCurrent().toString());

		Map<LocalDate, BigDecimal> vfPerDay = volforceCalculator.calculateByDate();

		vfPerDay.entrySet().stream()
				.forEach(e -> System.out.println(e.getKey().toString() + ": " + e.getValue().toString()));
	}

}
