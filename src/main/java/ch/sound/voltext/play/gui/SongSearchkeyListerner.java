package ch.sound.voltext.play.gui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;

public class SongSearchkeyListerner implements KeyListener {

	private JTree playsTree;
	private PlayListTreeModel treeModel;

	public SongSearchkeyListerner(JTree playsTree, PlayListTreeModel treeModel) {
		this.playsTree = playsTree;
		this.treeModel = treeModel;
	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

	@Override
	public void keyPressed(KeyEvent e) {

	}

	@Override
	public void keyReleased(KeyEvent e) {
		

		JTextField songSearchText = (JTextField) e.getSource();
		
		if (songSearchText.getText().length() < 3) {
			playsTree.setModel(treeModel.buildTreeModel());
			
		} else if (songSearchText.getText().length() >= 3) {

			String criteria = songSearchText.getText();

			DefaultMutableTreeNode root = (DefaultMutableTreeNode) playsTree.getModel().getRoot();

			List<DefaultMutableTreeNode> nodesToRemove = new ArrayList<>();
			Iterator<TreeNode> it = root.children().asIterator();
			while (it.hasNext()) {
				DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) it.next();
				String nodeTitle = (String) treeNode.getUserObject();

				if (!nodeTitle.contains(criteria)) {
					nodesToRemove.add(treeNode);
				}
			}
			
			nodesToRemove.forEach(DefaultMutableTreeNode::removeFromParent);
			
		}
		playsTree.updateUI();

	}

}
