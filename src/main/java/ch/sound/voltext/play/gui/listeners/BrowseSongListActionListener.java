package ch.sound.voltext.play.gui.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

public class BrowseSongListActionListener implements ActionListener {

	private JTextField locationTextField;
	private String lastFileLocation;

	public BrowseSongListActionListener(JTextField locationTextField) {
		this.locationTextField = locationTextField;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		JFileChooser fileChooser = new JFileChooser(new File("/"));
		fileChooser.setApproveButtonText("Select");
		
		if (lastFileLocation != null && !lastFileLocation.isEmpty()) {
			fileChooser.setCurrentDirectory(new File(lastFileLocation));
		}

		fileChooser.setFileFilter(new FileFilter() {
			
			@Override
			public String getDescription() {
				return "*.xml";
			}
			
			@Override
			public boolean accept(File f) {
				return f.getName().endsWith(".xml") || f.isDirectory();
			}
		});
		
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

		int ret = fileChooser.showOpenDialog(locationTextField.getParent());

		if (ret == JFileChooser.APPROVE_OPTION) {
			File f = fileChooser.getSelectedFile();
			lastFileLocation = f.getParent();
			locationTextField.setText(f.getAbsolutePath());
		}
		
	}

}
