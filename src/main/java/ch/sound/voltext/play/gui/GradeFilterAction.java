package ch.sound.voltext.play.gui;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JTree;

public class GradeFilterAction implements ItemListener {

	private JTree playsTree;
	private PlayListTreeModel treeModel;

	public GradeFilterAction(JTree playsTree, PlayListTreeModel treeModel) {
		this.playsTree = playsTree;
		this.treeModel = treeModel;
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		// TODO Auto-generated method stub

	}

}
