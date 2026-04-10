package ch.sound.voltext.play;

import ch.sound.voltext.play.gui.SoundVoltexStatsFrame;
import javafx.application.Application;
import javafx.stage.Stage;

public class SoundVoltexStatsApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        new SoundVoltexStatsFrame(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
