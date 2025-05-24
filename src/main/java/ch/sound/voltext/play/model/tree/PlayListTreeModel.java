package ch.sound.voltext.play.model.tree;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

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

					PlayNode logNode = new PlayNode(log, volforce.toString());

					lampNode.add(logNode);
				});
			});
			root.add(songNode);
		});

		return this;
	}

	public void removeNodes(Collection<DefaultMutableTreeNode> nodesToRemove) {
		// Remove Plays
		nodesToRemove.forEach(DefaultMutableTreeNode::removeFromParent);
		
		for(TreeNode titleNode : Collections.list(root.children())) { 
			
			// Remove Childless Titles
			removeChildlessNodes(titleNode);
		}

		// Remove Childless Lamps
//		nodesToRemove.stream().map(n -> (DefaultMutableTreeNode) n.getParent())
//				.filter(n -> n != null && n.getChildCount() == 0).forEach(DefaultMutableTreeNode::removeFromParent);
//
//		// Remove Childless Diff
//		nodesToRemove.stream().map(n -> (DefaultMutableTreeNode) n.getParent()).filter(Predicate.not(Objects::isNull))
//				.map(n -> (DefaultMutableTreeNode) n.getParent()).filter(n -> n != null && n.getChildCount() == 0)
//				.forEach(DefaultMutableTreeNode::removeFromParent);
//
//
//		nodesToRemove.stream().map(n -> (DefaultMutableTreeNode) n.getParent()).filter(Predicate.not(Objects::isNull))
//				.map(n -> (DefaultMutableTreeNode) n.getParent()).filter(Predicate.not(Objects::isNull))
//				.map(n -> (DefaultMutableTreeNode) n.getParent()).filter(n -> n.getChildCount() == 0)
//				.forEach(DefaultMutableTreeNode::removeFromParent);

	}

	private void removeChildlessNodes(TreeNode node) {
		if(node.getChildCount() == 0) {
			((DefaultMutableTreeNode) node).removeFromParent();
		}
		else {
			for(TreeNode subNode : Collections.list(node.children())) {
				removeChildlessNodes(subNode);
			}
			if(node.getChildCount() == 0) {
				((DefaultMutableTreeNode) node).removeFromParent();
			}
		}
	}

}
