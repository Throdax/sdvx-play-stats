package ch.sound.voltext.play.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JToggleButton;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import ch.sound.voltext.play.model.Difficulty;
import ch.sound.voltext.play.model.FullPlayInformation;
import ch.sound.voltext.play.model.Lamp;
import ch.sound.voltext.play.model.tree.PlayListTreeModel;
import ch.sound.voltext.play.model.tree.PlayNode;
import ch.sound.voltext.play.statistic.VolforceCalculator;

public class VolforceFilterAction implements ActionListener {

	private JTree playsTree;
	private PlayListTreeModel treeModel;
	private VolforceCalculator volforceCalculator;

	public VolforceFilterAction(JTree playsTree, PlayListTreeModel treeModel, VolforceCalculator volforceCalculator) {
		this.playsTree = playsTree;
		this.treeModel = treeModel;
		this.volforceCalculator = volforceCalculator;
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		JToggleButton vfButton = (JToggleButton) e.getSource();

		if (!vfButton.isSelected()) {
			playsTree.setModel(treeModel.buildTreeModel());
		} else {

			List<FullPlayInformation> topPlays = volforceCalculator.getTopPlays();

			DefaultMutableTreeNode root = (DefaultMutableTreeNode) playsTree.getModel().getRoot();

			List<DefaultMutableTreeNode> nodesToRemove = new ArrayList<>();
			Iterator<TreeNode> it = root.children().asIterator();
			while (it.hasNext()) {
				DefaultMutableTreeNode titleNode = (DefaultMutableTreeNode) it.next();

				List<FullPlayInformation> topSubFilterr = topPlays.stream()
						.filter(tp -> tp.getTitle().equalsIgnoreCase((String) titleNode.getUserObject()))
						.collect(Collectors.toList());

				nodesToRemove.addAll(iterateDificulty(topSubFilterr, titleNode));

			}

			treeModel.removeNodes(nodesToRemove);
		}
		
		playsTree.updateUI();
	}

	private List<DefaultMutableTreeNode> iterateDificulty(List<FullPlayInformation> topPlays,
			DefaultMutableTreeNode titleNode) {

		List<DefaultMutableTreeNode> nodesToRemove = new ArrayList<>();

		for (int i = 0; i < titleNode.getChildCount(); i++) {
			DefaultMutableTreeNode difficuultyLevelNode = (DefaultMutableTreeNode) titleNode.getChildAt(i);

			String dificulty = ((String) difficuultyLevelNode.getUserObject()).split("-")[0].toString();
			String rating = ((String) difficuultyLevelNode.getUserObject()).split("-")[0].toString();

			List<FullPlayInformation> topSubFilterr = topPlays.stream()
					.filter(tp -> tp.getDifficulty() == Difficulty.fromName(dificulty)
							&& tp.getRating() == Integer.valueOf(rating))
					.collect(Collectors.toList());

			nodesToRemove.addAll(iterateLamp(topSubFilterr, titleNode, difficuultyLevelNode));
		}

		return nodesToRemove;
	}

	private List<DefaultMutableTreeNode> iterateLamp(List<FullPlayInformation> topPlays,
			DefaultMutableTreeNode titleNode, TreeNode difficuultyLevelNode) {

		List<DefaultMutableTreeNode> nodesToRemove = new ArrayList<>();

		for (int j = 0; j < difficuultyLevelNode.getChildCount(); j++) {
			DefaultMutableTreeNode lampNode = (DefaultMutableTreeNode) difficuultyLevelNode.getChildAt(j);
			
			List<FullPlayInformation> topSubFilterr = topPlays.stream()
					.filter(tp -> tp.getLamp() == (Lamp)lampNode.getUserObject())
					.collect(Collectors.toList());

			nodesToRemove.addAll(iteratePlay(topSubFilterr, titleNode, lampNode));
		}

		return nodesToRemove;
	}

	private List<DefaultMutableTreeNode> iteratePlay(List<FullPlayInformation> topPlays,
			DefaultMutableTreeNode titleNode, TreeNode lampNode) {

		List<DefaultMutableTreeNode> nodesToRemove = new ArrayList<>();

		for (int k = 0; k < lampNode.getChildCount(); k++) {
			PlayNode playNode = (PlayNode) lampNode.getChildAt(k);
			
			for(FullPlayInformation topPlay : topPlays) {			
				if (playNode.getLog().getScore() != topPlay.getScore()) {
					nodesToRemove.add(playNode);
				}
			}
		}

		return nodesToRemove;

	}

}
