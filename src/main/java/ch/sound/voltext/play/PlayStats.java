package ch.sound.voltext.play;

import java.nio.file.Path;
import java.nio.file.Paths;

import ch.sound.voltext.play.model.Difficulty;
import ch.sound.voltext.play.model.Lamp;
import ch.sound.voltext.play.model.Songlist;
import ch.sound.voltext.play.statistic.FileDAO;
import ch.sound.voltext.play.statistic.PlayStatsCalculator;
import ch.sound.voltext.play.statistic.VolforceCalculator;
import jakarta.xml.bind.JAXBException;

public class PlayStats {

	public static void main(String[] args) throws JAXBException {
			Path dataPath = Paths.get("D:\\Tools\\SoundVoltex\\sdvx_helper\\played_songs.xml");

			Songlist songData = FileDAO.loadSongData(dataPath);

			PlayStatsCalculator playsCalculator = PlayStatsCalculator.getInstance();
			playsCalculator.loadData(songData);

			System.out.println("Loaded " + playsCalculator.getTotalSongs() + " songs with "
					+ playsCalculator.getTotalPlays() + " total plays");
			
			System.out.println("-------------------");
			printDifficultyStatistics(playsCalculator);
			
			System.out.println("-------------------");
			printRatingStatistics(playsCalculator);

			System.out.println("-------------------");
			printLampStatistics(playsCalculator);
			
			System.out.println("-------------------");
			printGradeStatistics(playsCalculator);
			
			System.out.println("-------------------");
			printVolforceStatistics(songData);

			FileDAO.writeSongData(dataPath.getParent().resolve("played_songs_vf.xml"), songData);

	}

	private static void printRatingStatistics(PlayStatsCalculator playsCalculator) {
		playsCalculator.getTotalByRating().entrySet().stream()
		.sorted((g1, g2) -> Integer.compare(g1.getKey(), g2.getKey()))
		.forEach(e -> System.out.println("Level: "+e.getKey() + ": " + e.getValue()));
		
	}

	private static void printDifficultyStatistics(PlayStatsCalculator playsCalculator) {
		System.out.println(
				Difficulty.NOVICE.getName() + ": " + playsCalculator.getTotalByDificulty(Difficulty.NOVICE));
		System.out.println(
				Difficulty.ADVANCED.getName() + ": " + playsCalculator.getTotalByDificulty(Difficulty.ADVANCED));
		System.out.println(
				Difficulty.EXHAUST.getName() + ": " + playsCalculator.getTotalByDificulty(Difficulty.EXHAUST));
		System.out.println(
				Difficulty.APPEND.getName() + ": " + playsCalculator.getTotalByDificulty(Difficulty.APPEND));
	}

	private static void printGradeStatistics(PlayStatsCalculator playsCalculator) {
		
		playsCalculator.getTotalByGrade().entrySet().stream()
				.sorted((g1, g2) -> Integer.compare(g1.getKey().ordinal(), g2.getKey().ordinal()))
				.forEach(e -> System.out.println(e.getKey().getName() + ": " + e.getValue()));
	}

	private static void printLampStatistics(PlayStatsCalculator playsCalculator) {
		System.out.println(Lamp.FAILED.getName() + ": " + playsCalculator.getTotalByLamp(Lamp.FAILED));
		System.out.println(Lamp.CLEAR.getName() + ": " + playsCalculator.getTotalByLamp(Lamp.CLEAR));
		System.out.println(Lamp.EX_CLEAR.getName() + ": " + playsCalculator.getTotalByLamp(Lamp.EX_CLEAR));
		System.out.println(Lamp.UC.getName() + ": " + playsCalculator.getTotalByLamp(Lamp.UC));
		System.out.println(Lamp.PUC.getName() + ": " + playsCalculator.getTotalByLamp(Lamp.PUC));
	}

	private static void printVolforceStatistics(Songlist songData) {
		VolforceCalculator volforceCalculator = new VolforceCalculator(songData);

//		System.out.println("Volforce Songs: " + volforceCalculator.getTop50().size());
//		volforceCalculator.getSortedTop50ByName().forEach(vfTop -> System.out.println("\t" + vfTop));
		
		System.out.println("Volforce: " + volforceCalculator.calculateCurrent().toString());
		
//		Map<LocalDate, BigDecimal> vfPerDay = volforceCalculator.calculateByDate();
		
//		vfPerDay.entrySet().stream()
//				.forEach(e -> System.out.println(e.getKey().toString() + ": " + e.getValue().toString()));
	}

}
