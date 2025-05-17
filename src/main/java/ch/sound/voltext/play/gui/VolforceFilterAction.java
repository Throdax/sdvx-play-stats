package ch.sound.voltext.play.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTree;

public class VolforceFilterAction implements ActionListener {

	private JTree playsTree;
	private PlayListTreeModel treeModel;

	public VolforceFilterAction(JTree playsTree, PlayListTreeModel treeModel) {
		this.playsTree = playsTree;
		this.treeModel = treeModel;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

	}

}
