package ch.sound.voltext.play.gui;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import ch.sound.voltext.play.model.FullPlayInformation;
import ch.sound.voltext.play.model.Song;
import ch.sound.voltext.play.model.Songlist;
import ch.sound.voltext.play.statistic.SongTitleComparator;

public class PlayListTreeModel extends DefaultTreeModel {


	private static final long serialVersionUID = -8914792811271866839L;
	private DefaultMutableTreeNode root;
	private Songlist songData;
	

	public PlayListTreeModel(DefaultMutableTreeNode root) {
		super(root);
		this.root = root;
	}
	
	public PlayListTreeModel(DefaultMutableTreeNode root, Songlist songData) {
		this(root);
		this.songData = songData;
	}

	public PlayListTreeModel buildTreeModel() {
		
		root.removeAllChildren();

		List<Song> sortedSongs = new ArrayList<>(songData.getSongs()); 
		sortedSongs.sort(new SongTitleComparator());
		
		sortedSongs.forEach(song -> {
			DefaultMutableTreeNode songNode = new DefaultMutableTreeNode(song.getTitle());

			song.getPlays().forEach(play -> {
				DefaultMutableTreeNode playNode = new DefaultMutableTreeNode(
						play.getDifficulty().getName() + " - " + play.getRating());
				songNode.add(playNode);

				play.getPlaysLog().forEach(log -> {

					DefaultMutableTreeNode lampNode = null;

					Iterator<TreeNode> it = playNode.children().asIterator();

					while (it.hasNext()) {
						DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) it.next();

						if (treeNode.getUserObject().equals(log.getLamp().getName())) {
							lampNode = treeNode;
							break;
						}
					}

					if (lampNode == null) {
						lampNode = new DefaultMutableTreeNode(log.getLamp().getName());
						playNode.add(lampNode);
					}

					BigDecimal volforce = new FullPlayInformation(song.getTitle(), play.getRating(),
							play.getDifficulty(), log.getLamp(), log.getScore()).getNormalizedVolforce();
					
					DefaultMutableTreeNode logNode = new DefaultMutableTreeNode(log.getFormatedScore() + " on "
							+ log.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + " with VF: ("
							+ volforce.toString() + ")");
					
					lampNode.add(logNode);
				});
			});
			root.add(songNode);
		});
		
		return this;
	
	}
	
}
