package ch.sound.voltext.play.model.tree;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.control.TreeItem;

import ch.sound.voltext.play.model.FullPlayInformation;
import ch.sound.voltext.play.model.Song;
import ch.sound.voltext.play.model.Songlist;
import ch.sound.voltext.play.statistic.SongTitleComparator;

public class PlayListTreeModel {

    private final Songlist songData;

    public PlayListTreeModel(Songlist songData) {
        this.songData = songData;
    }

    public TreeItem<Object> buildTreeItem() {
        TreeItem<Object> root = new TreeItem<>("Songs");
        root.setExpanded(true);

        List<Song> sortedSongs = new ArrayList<>(songData.getSongs());
        sortedSongs.sort(new SongTitleComparator());

        for (Song song : sortedSongs) {
            TreeItem<Object> songNode = new TreeItem<>(song.getTitle());

            for (var play : song.getPlays()) {
                TreeItem<Object> playNode = new TreeItem<>(
                        play.getDifficulty().getName() + " - " + play.getRating());
                songNode.getChildren().add(playNode);

                for (var log : play.getPlaysLog()) {
                    TreeItem<Object> lampNode = null;

                    for (TreeItem<Object> child : playNode.getChildren()) {
                        if (child.getValue().equals(log.getLamp().getName())) {
                            lampNode = child;
                            break;
                        }
                    }

                    if (lampNode == null) {
                        lampNode = new TreeItem<>(log.getLamp().getName());
                        playNode.getChildren().add(lampNode);
                    }

                    BigDecimal volforce = new FullPlayInformation(song.getTitle(), play.getRating(),
                            play.getDifficulty(), log.getLamp(), log.getScore()).getNormalizedVolforce();

                    lampNode.getChildren().add(new TreeItem<>(new PlayNode(log, volforce.toString())));
                }
            }
            root.getChildren().add(songNode);
        }

        return root;
    }

    /**
     * Recursively removes structural (non-PlayNode) tree items that have no children.
     */
    public static void removeChildlessNodes(TreeItem<Object> node) {
        List<TreeItem<Object>> children = new ArrayList<>(node.getChildren());
        for (TreeItem<Object> child : children) {
            if (!(child.getValue() instanceof PlayNode)) {
                removeChildlessNodes(child);
            }
        }
        if (node.getChildren().isEmpty() && !(node.getValue() instanceof PlayNode)
                && node.getParent() != null) {
            node.getParent().getChildren().remove(node);
        }
    }
}
