package ch.sound.voltext.play.gui.listeners;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import ch.sound.voltext.play.model.PlayLog.Grade;
import ch.sound.voltext.play.model.tree.PlayListTreeModel;
import ch.sound.voltext.play.model.tree.PlayNode;

public class GradeFilterActionListener implements ItemListener {

	private JTree playsTree;
	private PlayListTreeModel treeModel;

	public GradeFilterActionListener(JTree playsTree, PlayListTreeModel treeModel) {
		this.playsTree = playsTree;
		this.treeModel = treeModel;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void itemStateChanged(ItemEvent e) {
		JComboBox<Grade> songSearchText = (JComboBox<Grade>) e.getSource();

		Grade selectedGrade = songSearchText.getItemAt(songSearchText.getSelectedIndex());

		if (selectedGrade == Grade.NONE) {
			playsTree.setModel(treeModel.buildTreeModel());

		} else {

			DefaultMutableTreeNode root = (DefaultMutableTreeNode) playsTree.getModel().getRoot();

			List<DefaultMutableTreeNode> nodesToRemove = new ArrayList<>();
			Iterator<TreeNode> it = root.children().asIterator();
			while (it.hasNext()) {
				DefaultMutableTreeNode titleNode = (DefaultMutableTreeNode) it.next();

				nodesToRemove.addAll(iterateDificulty(selectedGrade, titleNode));
			}

			nodesToRemove.forEach(DefaultMutableTreeNode::removeFromParent);

		}
		playsTree.updateUI();

	}

	private List<DefaultMutableTreeNode> iterateDificulty(Grade selectedGrade, DefaultMutableTreeNode titleNode) {

		List<DefaultMutableTreeNode> nodesToRemove = new ArrayList<>();

		for (int i = 0; i < titleNode.getChildCount(); i++) {
			TreeNode difficuultyLevelNode = titleNode.getChildAt(i);

			nodesToRemove.addAll(iterateLamp(selectedGrade, titleNode, difficuultyLevelNode));
		}

		return nodesToRemove;
	}

	private List<DefaultMutableTreeNode> iterateLamp(Grade selectedGrade, DefaultMutableTreeNode titleNode,
			TreeNode difficuultyLevelNode) {

		List<DefaultMutableTreeNode> nodesToRemove = new ArrayList<>();

		for (int j = 0; j < difficuultyLevelNode.getChildCount(); j++) {
			TreeNode lampNode = difficuultyLevelNode.getChildAt(j);

			nodesToRemove.addAll(iteratePlay(selectedGrade, titleNode, lampNode));
		}

		return nodesToRemove;
	}

	private List<DefaultMutableTreeNode> iteratePlay(Grade selectedGrade, DefaultMutableTreeNode titleNode,
			TreeNode lampNode) {

		List<DefaultMutableTreeNode> nodesToRemove = new ArrayList<>();

		for (int k = 0; k < lampNode.getChildCount(); k++) {
			PlayNode playNode = (PlayNode) lampNode.getChildAt(k);
			if (selectedGrade != Grade.fromScore(playNode.getLog().getScore())) {
				nodesToRemove.add(playNode);
			}
		}

		return nodesToRemove;

	}

}
