package ch.sound.voltext.play.statistic;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import ch.sound.voltext.play.model.FullPlayInformation;
import ch.sound.voltext.play.model.Lamp;
import ch.sound.voltext.play.model.PlayData;
import ch.sound.voltext.play.model.PlayLog;
import ch.sound.voltext.play.model.Song;
import ch.sound.voltext.play.model.Songlist;

public class VolforceCalculator {

	private static final int VOLFORCE_TOP = 50;
	private Songlist songData;
	private List<FullPlayInformation> volforceTopPlays;

	public VolforceCalculator(Songlist songData) {
		this.songData = songData;
		volforceTopPlays = new ArrayList<>(VOLFORCE_TOP);
	}

	public synchronized BigDecimal calculateCurrent() {

		getTopPlays();

		return calculateFromTopVolforce(volforceTopPlays);

	}

	private BigDecimal calculateFromTopVolforce(List<FullPlayInformation> top50) {
		
		return BigDecimal.valueOf(top50.stream().mapToDouble(play -> play.getVolforce().doubleValue()).sum() / 1000)
				.setScale(3, RoundingMode.HALF_EVEN);
	}
	
	public synchronized Map<LocalDate, BigDecimal> calculateByDate() {

		Map<LocalDate, BigDecimal> vfPerDay = new TreeMap<>();
		
		LocalDate firstDate = SongDataUtils.getFirstDate(songData).toLocalDate();
		
		while (firstDate.compareTo(LocalDate.now()) <= 0) {
			List<FullPlayInformation> top50OnDate = getTopPlays(firstDate);
			
			BigDecimal vfOnDay = calculateFromTopVolforce(top50OnDate);
			
			if(vfOnDay.compareTo(BigDecimal.ZERO) == 1) {
				vfPerDay.put(firstDate, vfOnDay);
			}
			
			firstDate = firstDate.plusDays(1);
		}
		
		return vfPerDay;
		
								
	}

	private List<FullPlayInformation> getTopPlays(LocalDate filterDate) {
		
		List<FullPlayInformation> topPlaysOnDate = new ArrayList<>(VOLFORCE_TOP);
		
		for (Song song : songData.getSongs()) {
			for (PlayData playData : song.getPlays()) {
				int rating = playData.getRating();

				for (PlayLog play : playData.getPlaysLog()) {
					
					if(play.getDate().toLocalDate().compareTo(filterDate) <= 0) {

					FullPlayInformation fullPlay = new FullPlayInformation(song.getTitle(), rating,
							playData.getDifficulty(), play.getLamp(), play.getScore(), play.getDate());

					play.setVolforce(fullPlay.calculateVolforce());
					addToTopPlays(topPlaysOnDate, fullPlay);
					}
				}
			}
		}
		
		return topPlaysOnDate;
	}

	private List<FullPlayInformation> getTopVolforcePlays() {

		List<FullPlayInformation> topPlays = new ArrayList<>(VOLFORCE_TOP);

		for (Song song : songData.getSongs()) {
			for (PlayData playData : song.getPlays()) {
				int rating = playData.getRating();

				for (PlayLog play : playData.getPlaysLog()) {

					FullPlayInformation fullPlay = new FullPlayInformation(song.getTitle(), rating,
							playData.getDifficulty(), play.getLamp(), play.getScore(), play.getDate());

					play.setVolforce(fullPlay.calculateVolforce());
					addToTopPlays(topPlays, fullPlay);
				}
			}
		}

		return topPlays;
	}

	private boolean addToTopPlays(List<FullPlayInformation> topPlays, FullPlayInformation fullPlay) {
		
		if (topPlays.size() < VOLFORCE_TOP) {
			removeSameSong(topPlays, fullPlay);
			topPlays.add(fullPlay);
			return true;
		} else {
			FullPlayInformation lowVolforce = new FullPlayInformation();
			lowVolforce.setVolforce(new BigDecimal(10_000_000));

			for (FullPlayInformation calculatedVolforce : topPlays) {
				if (calculatedVolforce.getVolforce().compareTo(lowVolforce.getVolforce()) == -1) {
					lowVolforce = calculatedVolforce;
				}
			}

			if (fullPlay.getVolforce().compareTo(lowVolforce.getVolforce()) == 1) {
				topPlays.remove(lowVolforce);
				
				removeSameSong(topPlays, fullPlay);
				
				topPlays.add(fullPlay);
				return true;
			}
		}

		return false;
	}

	private void removeSameSong(List<FullPlayInformation> top50, FullPlayInformation fullPlay) {
		Iterator<FullPlayInformation> it = top50.iterator();
		while (it.hasNext()) {
			FullPlayInformation type = it.next();
			if(type.equalSong(fullPlay)) {
				it.remove();
			}
		}
	}

	public static BigDecimal calculateGradeCoefficient(int score) {
		return switch (PlayLog.Grade.fromScore(score)) {
		case D -> new BigDecimal(0.80);
		case C -> new BigDecimal(0.82);
		case B -> new BigDecimal(0.85);
		case A -> new BigDecimal(0.88);
		case A_PLUS -> new BigDecimal(0.91);
		case AA -> new BigDecimal(0.94);
		case AA_PLUS -> new BigDecimal(0.97);
		case AAA -> new BigDecimal(1.00);
		case AAA_PLUS -> new BigDecimal(1.02);
		case S -> new BigDecimal(1.05);
		};
	}

	public static BigDecimal calculateClearCoefficient(Lamp lamp) {

		return switch (lamp) {
		case FAILED -> new BigDecimal(0.50);
		case CLEAR -> new BigDecimal(1.00);
		case EX_CLEAR -> new BigDecimal(1.02);
		case UC -> new BigDecimal(1.05);
		case PUC -> new BigDecimal(1.10);
		};

	}

	public synchronized List<FullPlayInformation> getTopPlays() {
		if(volforceTopPlays.isEmpty()) {
			volforceTopPlays.addAll(getTopVolforcePlays());
		}
		
		return Collections.unmodifiableList(volforceTopPlays);
	}
	
	public synchronized List<FullPlayInformation> getSortedTopPlaysByName() {
		if(volforceTopPlays.isEmpty()) {
			volforceTopPlays.addAll(getTopVolforcePlays());
		}
		
		List<FullPlayInformation> sortedCopy = new ArrayList<>(volforceTopPlays);
		Collections.sort(sortedCopy, new SongTitleComparator());
		
		return Collections.unmodifiableList(sortedCopy);
	}
	
	public synchronized List<FullPlayInformation> getSortedTopPlaysByVolforce() {
		if(volforceTopPlays.isEmpty()) {
			volforceTopPlays.addAll(getTopVolforcePlays());
		}
		
		List<FullPlayInformation> sortedCopy = new ArrayList<>(volforceTopPlays);
		Collections.sort(sortedCopy, new SongVolforceComparator());
		
		return Collections.unmodifiableList(sortedCopy);
	}

}
