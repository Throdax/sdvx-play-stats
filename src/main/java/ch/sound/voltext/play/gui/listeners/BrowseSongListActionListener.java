package ch.sound.voltext.play.gui.listeners;

import java.io.File;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class BrowseSongListActionListener implements EventHandler<ActionEvent> {

    private final TextField locationTextField;
    private String lastFileLocation;

    public BrowseSongListActionListener(TextField locationTextField) {
        this.locationTextField = locationTextField;
    }

    @Override
    public void handle(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new ExtensionFilter("XML Files", "*.xml"));

        if (lastFileLocation != null && !lastFileLocation.isEmpty()) {
            fileChooser.setInitialDirectory(new File(lastFileLocation));
        } else {
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        }

        File selected = fileChooser.showOpenDialog(locationTextField.getScene().getWindow());

        if (selected != null) {
            lastFileLocation = selected.getParent();
            locationTextField.setText(selected.getAbsolutePath());
        }
    }
}
