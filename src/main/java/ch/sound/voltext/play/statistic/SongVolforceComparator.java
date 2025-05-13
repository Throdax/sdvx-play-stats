package ch.sound.voltext.play.statistic;

import java.util.Comparator;

import ch.sound.voltext.play.model.FullPlayInformation;

public class SongVolforceComparator implements Comparator<FullPlayInformation> {

	@Override
	public int compare(FullPlayInformation o1, FullPlayInformation o2) {
		
		if(o1 == null) {
			return 1;
		}
		
		if(o2 == null) {
			return -1;
		}
		
		return o1.getVolforce().compareTo(o2.getVolforce());
	}

}
