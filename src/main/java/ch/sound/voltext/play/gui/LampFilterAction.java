package ch.sound.voltext.play.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTree;

import ch.sound.voltext.play.model.Lamp;

public class LampFilterAction implements ActionListener {

	private JTree playsTree;
	private PlayListTreeModel treeModel;
	private Lamp exClear;

	public LampFilterAction(JTree playsTree, PlayListTreeModel treeModel, Lamp exClear) {
		this.playsTree = playsTree;
		this.treeModel = treeModel;
		this.exClear = exClear;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

	}

}
