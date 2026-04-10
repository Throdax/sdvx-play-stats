package ch.sound.voltext.play.gui.listeners;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

import ch.sound.voltext.play.model.PlayLog.Grade;
import ch.sound.voltext.play.model.tree.PlayListTreeModel;
import ch.sound.voltext.play.model.tree.PlayNode;

public class GradeFilterActionListener implements ChangeListener<Grade> {

    private final TreeView<Object> playsTree;
    private final Supplier<PlayListTreeModel> modelSupplier;

    public GradeFilterActionListener(TreeView<Object> playsTree, Supplier<PlayListTreeModel> modelSupplier) {
        this.playsTree = playsTree;
        this.modelSupplier = modelSupplier;
    }

    @Override
    public void changed(ObservableValue<? extends Grade> observable, Grade oldValue, Grade newValue) {
        if (newValue == null) {
            return;
        }

        if (newValue == Grade.NONE) {
            playsTree.setRoot(modelSupplier.get().buildTreeItem());
        } else {
            TreeItem<Object> root = playsTree.getRoot();
            List<TreeItem<Object>> nodesToRemove = new ArrayList<>();

            for (TreeItem<Object> titleNode : new ArrayList<>(root.getChildren())) {
                nodesToRemove.addAll(iterateDifficulty(newValue, titleNode));
            }

            nodesToRemove.forEach(node -> {
                if (node.getParent() != null) {
                    node.getParent().getChildren().remove(node);
                }
            });
        }
    }

    private List<TreeItem<Object>> iterateDifficulty(Grade selectedGrade, TreeItem<Object> titleNode) {
        List<TreeItem<Object>> nodesToRemove = new ArrayList<>();
        for (TreeItem<Object> diffNode : titleNode.getChildren()) {
            nodesToRemove.addAll(iterateLamp(selectedGrade, diffNode));
        }
        return nodesToRemove;
    }

    private List<TreeItem<Object>> iterateLamp(Grade selectedGrade, TreeItem<Object> diffNode) {
        List<TreeItem<Object>> nodesToRemove = new ArrayList<>();
        for (TreeItem<Object> lampNode : diffNode.getChildren()) {
            nodesToRemove.addAll(iteratePlay(selectedGrade, lampNode));
        }
        return nodesToRemove;
    }

    private List<TreeItem<Object>> iteratePlay(Grade selectedGrade, TreeItem<Object> lampNode) {
        List<TreeItem<Object>> nodesToRemove = new ArrayList<>();
        for (TreeItem<Object> playItem : lampNode.getChildren()) {
            PlayNode playNode = (PlayNode) playItem.getValue();
            if (selectedGrade != Grade.fromScore(playNode.getLog().getScore())) {
                nodesToRemove.add(playItem);
            }
        }
        return nodesToRemove;
    }
}
