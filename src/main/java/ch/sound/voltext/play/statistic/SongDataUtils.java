package ch.sound.voltext.play.statistic;

import java.time.LocalDateTime;

import ch.sound.voltext.play.model.PlayData;
import ch.sound.voltext.play.model.PlayLog;
import ch.sound.voltext.play.model.Song;
import ch.sound.voltext.play.model.Songlist;

public class SongDataUtils {

	private SongDataUtils() {
	}
	
	public static LocalDateTime getFirstDate(Songlist songData) {
		
		LocalDateTime firstDate = LocalDateTime.now();
		
		for (Song song : songData.getSongs()) {
			for (PlayData playData : song.getPlays()) {
				for (PlayLog play : playData.getPlaysLog()) {
					if(play.getDate().toLocalDate().isBefore(firstDate.toLocalDate())) {
						firstDate = play.getDate();
					}
				}
			}
		}
		
		return firstDate;
	}
	
	public static LocalDateTime getLastDate(Songlist songData) {
		
		LocalDateTime lastDate = LocalDateTime.of(1970, 1, 1,0,0,0);
		
		for (Song song : songData.getSongs()) {
			for (PlayData playData : song.getPlays()) {
				for (PlayLog play : playData.getPlaysLog()) {
					if(play.getDate().toLocalDate().isAfter(lastDate.toLocalDate())) {
						lastDate = play.getDate();
					}
				}
			}
		}
		
		return lastDate;
	}
	
}
