package ch.sound.voltext.play.gui.listeners;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

import ch.sound.voltext.play.model.Difficulty;
import ch.sound.voltext.play.model.FullPlayInformation;
import ch.sound.voltext.play.model.Lamp;
import ch.sound.voltext.play.model.tree.PlayListTreeModel;
import ch.sound.voltext.play.model.tree.PlayNode;
import ch.sound.voltext.play.statistic.VolforceCalculator;

public class VolforceFilterActionListener implements EventHandler<ActionEvent> {

    private final TreeView<Object> playsTree;
    private final Supplier<PlayListTreeModel> modelSupplier;
    private final Supplier<VolforceCalculator> volforceSupplier;

    public VolforceFilterActionListener(TreeView<Object> playsTree,
            Supplier<PlayListTreeModel> modelSupplier,
            Supplier<VolforceCalculator> volforceSupplier) {
        this.playsTree = playsTree;
        this.modelSupplier = modelSupplier;
        this.volforceSupplier = volforceSupplier;
    }

    @Override
    public void handle(ActionEvent event) {
        ToggleButton vfButton = (ToggleButton) event.getSource();

        if (!vfButton.isSelected()) {
            playsTree.setRoot(modelSupplier.get().buildTreeItem());
        } else {
            List<FullPlayInformation> topPlays = volforceSupplier.get().getTopPlays();
            TreeItem<Object> root = playsTree.getRoot();
            List<TreeItem<Object>> nodesToRemove = new ArrayList<>();

            for (TreeItem<Object> titleNode : new ArrayList<>(root.getChildren())) {
                List<FullPlayInformation> titleMatches = topPlays.stream()
                        .filter(tp -> tp.getTitle().equalsIgnoreCase((String) titleNode.getValue()))
                        .collect(Collectors.toList());

                if (titleMatches.isEmpty()) {
                    nodesToRemove.add(titleNode);
                } else {
                    nodesToRemove.addAll(iterateDifficulty(titleMatches, titleNode));
                }
            }

            nodesToRemove.forEach(node -> {
                if (node.getParent() != null) {
                    node.getParent().getChildren().remove(node);
                }
            });
        }
    }

    private List<TreeItem<Object>> iterateDifficulty(List<FullPlayInformation> topPlays,
            TreeItem<Object> titleNode) {
        List<TreeItem<Object>> nodesToRemove = new ArrayList<>();

        for (TreeItem<Object> diffNode : titleNode.getChildren()) {
            String diffStr = ((String) diffNode.getValue()).split("-")[0].trim();
            String ratingStr = ((String) diffNode.getValue()).split("-")[1].trim();

            List<FullPlayInformation> diffMatches = topPlays.stream()
                    .filter(tp -> tp.getDifficulty() == Difficulty.fromName(diffStr)
                            && tp.getRating() == Integer.valueOf(ratingStr))
                    .collect(Collectors.toList());

            if (diffMatches.isEmpty()) {
                nodesToRemove.add(diffNode);
            } else {
                nodesToRemove.addAll(iterateLamp(diffMatches, diffNode));
            }
        }

        return nodesToRemove;
    }

    private List<TreeItem<Object>> iterateLamp(List<FullPlayInformation> topPlays,
            TreeItem<Object> diffNode) {
        List<TreeItem<Object>> nodesToRemove = new ArrayList<>();

        for (TreeItem<Object> lampNode : diffNode.getChildren()) {
            List<FullPlayInformation> lampMatches = topPlays.stream()
                    .filter(tp -> tp.getLamp() == Lamp.fromName((String) lampNode.getValue()))
                    .collect(Collectors.toList());

            if (lampMatches.isEmpty()) {
                nodesToRemove.add(lampNode);
            } else {
                nodesToRemove.addAll(iteratePlay(lampMatches, lampNode));
            }
        }

        return nodesToRemove;
    }

    private List<TreeItem<Object>> iteratePlay(List<FullPlayInformation> topPlays,
            TreeItem<Object> lampNode) {
        List<TreeItem<Object>> nodesToRemove = new ArrayList<>();

        for (TreeItem<Object> playItem : lampNode.getChildren()) {
            PlayNode playNode = (PlayNode) playItem.getValue();
            for (FullPlayInformation topPlay : topPlays) {
                if (playNode.getLog().getScore() != topPlay.getScore()) {
                    nodesToRemove.add(playItem);
                }
            }
        }

        return nodesToRemove;
    }
}
