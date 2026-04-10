package ch.sound.voltext.play.statistic;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import ch.sound.voltext.play.model.Difficulty;
import ch.sound.voltext.play.model.Lamp;
import ch.sound.voltext.play.model.PlayData;
import ch.sound.voltext.play.model.PlayLog;
import ch.sound.voltext.play.model.PlayLog.Grade;
import ch.sound.voltext.play.model.Song;
import ch.sound.voltext.play.model.Songlist;

public class PlayStatsCalculator {

	private static final PlayStatsCalculator INSTANCE = new PlayStatsCalculator();

	private Songlist songData;

	private PlayStatsCalculator() {
	}

	public static PlayStatsCalculator getInstance() {
		return INSTANCE;
	}

	public void loadData(Songlist songData) {
		this.songData = songData;
	}

	public int getTotalPlays() {
		if (songData == null) {
			throw new IllegalArgumentException("Song data not loaded.");
		}
		return songData.getSongs().stream()
				.mapToInt(t -> t.getPlays().stream().mapToInt(p -> p.getPlaysLog().size()).sum()).sum();
	}

	public int getTotalSongs() {
		if (songData == null) {
			throw new IllegalArgumentException("Song data not loaded.");
		}
		return songData.getSongs().size();
	}

	public int getTotalByDificulty(Difficulty difficulty) {
		if (songData == null) {
			throw new IllegalArgumentException("Song data not loaded.");
		}
		return songData
				.getSongs().stream().mapToInt(t -> t.getPlays().stream()
						.filter(play -> play.getDifficulty() == difficulty).mapToInt(p -> p.getPlaysLog().size()).sum())
				.sum();
	}

	public long getTotalByLamp(Lamp lamp) {
		if (songData == null) {
			throw new IllegalArgumentException("Song data not loaded.");
		}
		return songData.getSongs().stream()
				.mapToLong(
						t -> t.getPlays().stream()
								.mapToLong(play -> play.getPlaysLog().stream()
										.filter(playLog -> playLog.getLamp() == lamp).collect(Collectors.counting()))
								.sum())
				.sum();
	}

	public long getTotalByGrade(PlayLog.Grade grade) {
		if (songData == null) {
			throw new IllegalArgumentException("Song data not loaded.");
		}
		return songData.getSongs().stream()
				.mapToLong(t -> t.getPlays().stream()
						.mapToLong(play -> play.getPlaysLog().stream()
								.filter(playLog -> PlayLog.Grade.fromScore(playLog.getScore()) == grade)
								.collect(Collectors.counting()))
						.sum())
				.sum();
	}

	public Map<Grade, Integer> getTotalByGrade() {
		if (songData == null) {
			throw new IllegalArgumentException("Song data not loaded.");
		}

		Map<Grade, Integer> gradeMap = new HashMap<>();

		for (Song song : songData.getSongs()) {
			for (PlayData data : song.getPlays()) {
				for (PlayLog log : data.getPlaysLog()) {
					gradeMap.compute(Grade.fromScore(log.getScore()), (t, u) -> gradeMap.getOrDefault(t, 0) + 1);
				}
			}
		}

		return gradeMap;
	}

	public Map<Integer, Integer> getTotalByRating() {
		if (songData == null) {
			throw new IllegalArgumentException("Song data not loaded.");
		}

		Map<Integer, Integer> gradeMap = new HashMap<>();

		for (Song song : songData.getSongs()) {
			for (PlayData data : song.getPlays()) {

				data.getPlaysLog()
						.forEach((l) -> gradeMap.compute(data.getRating(), (t, u) -> gradeMap.getOrDefault(t, 0) + 1));
			}
		}

		return gradeMap;
	}

	public Map<LocalDate, List<PlayLog>> getPlaysPerDay() {
		if (songData == null) {
			throw new IllegalArgumentException("Song data not loaded.");
		}

		Map<LocalDate, List<PlayLog>> byDateMap = new TreeMap<>((o1, o2) -> o1.compareTo(o2));

		LocalDateTime firstDate = SongDataUtils.getFirstDate(songData);
		LocalDate stopDate = LocalDate.now().plusDays(1);

		for (LocalDate currentDate = firstDate.toLocalDate(); currentDate
				.compareTo(stopDate) < 1; currentDate = currentDate.plusDays(1)) {

			List<PlayLog> dateList = new ArrayList<>();

			for (Song song : songData.getSongs()) {
				for (PlayData data : song.getPlays()) {
					for (PlayLog log : data.getPlaysLog()) {
						if (log.getDate().toLocalDate().equals(currentDate)) {
							dateList.add(log);
						}
					}
				}
			}

			byDateMap.put(currentDate, dateList);
		}

		return byDateMap;
	}
}
