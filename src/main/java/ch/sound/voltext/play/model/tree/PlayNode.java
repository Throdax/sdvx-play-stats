package ch.sound.voltext.play.model.tree;

import java.time.format.DateTimeFormatter;

import ch.sound.voltext.play.model.PlayLog;

public class PlayNode {

    private final PlayLog log;
    private final String volforce;

    public PlayNode(PlayLog log, String volforce) {
        this.log = log;
        this.volforce = volforce;
    }

    public PlayLog getLog() {
        return log;
    }

    public String getVolforce() {
        return volforce;
    }

    @Override
    public String toString() {
        return log.getFormatedScore() + " on "
                + log.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + " with VF: ("
                + volforce + ")";
    }
}
