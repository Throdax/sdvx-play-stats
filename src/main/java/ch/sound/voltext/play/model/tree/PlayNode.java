package ch.sound.voltext.play.model.tree;

import java.time.format.DateTimeFormatter;

import javax.swing.tree.DefaultMutableTreeNode;

import ch.sound.voltext.play.model.PlayLog;

public class PlayNode extends DefaultMutableTreeNode {


	private static final long serialVersionUID = 8720288835606190644L;
	private PlayLog log;
	private String volforce;
	
	
	public PlayNode(PlayLog log, String volforce) {
		this.log = log;
		this.volforce = volforce;
	}


	@Override
	public String toString() {
		return log.getFormatedScore() + " on "
				+ log.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + " with VF: ("
				+ volforce.toString() + ")";
	}
	
	public PlayLog getLog() {
		return log;
	}


	public String getVolforce() {
		return volforce;
	}

}
