package ch.sound.voltext.play.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JToggleButton;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import ch.sound.voltext.play.model.Lamp;
import ch.sound.voltext.play.model.tree.PlayListTreeModel;

public class LampFilterAction implements ActionListener {

	private JTree playsTree;
	private PlayListTreeModel treeModel;
	private Lamp lamp;

	public LampFilterAction(JTree playsTree, PlayListTreeModel treeModel, Lamp lamp) {
		this.playsTree = playsTree;
		this.treeModel = treeModel;
		this.lamp = lamp;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		JToggleButton lampButton = (JToggleButton)e.getSource();
		
		if(!lampButton.isSelected()) {
			playsTree.setModel(treeModel.buildTreeModel());
		}
		else {
			DefaultMutableTreeNode root = (DefaultMutableTreeNode) playsTree.getModel().getRoot();

			List<DefaultMutableTreeNode> nodesToRemove = new ArrayList<>();
			Iterator<TreeNode> it = root.children().asIterator();
			while (it.hasNext()) {
				DefaultMutableTreeNode titleNode =  (DefaultMutableTreeNode) it.next();
				
				nodesToRemove.addAll(iterateDificulty(titleNode));
			}
			
			treeModel.removeNodes(nodesToRemove);
		}

	}
	
	
	private List<DefaultMutableTreeNode> iterateDificulty(DefaultMutableTreeNode titleNode) {
		
		List<DefaultMutableTreeNode> nodesToRemove = new ArrayList<>();
		
		for(int i=0;i < titleNode.getChildCount();i++) {
			TreeNode difficuultyLevelNode = titleNode.getChildAt(i);
			
			nodesToRemove.addAll(iterateLamp(titleNode, difficuultyLevelNode));
		}
		
		return nodesToRemove;
	}
	
	private List<DefaultMutableTreeNode> iterateLamp(DefaultMutableTreeNode titleNode, TreeNode difficuultyLevelNode) {
		
		List<DefaultMutableTreeNode> nodesToRemove = new ArrayList<>();
		
		for(int j=0;j < difficuultyLevelNode.getChildCount();j++) {
			DefaultMutableTreeNode lampNode = (DefaultMutableTreeNode) difficuultyLevelNode.getChildAt(j);
			
			if(!lamp.getName().equalsIgnoreCase((String) lampNode.getUserObject())) {
				nodesToRemove.add(titleNode);
			}
		}
		
		return nodesToRemove;
	}

}
