package ch.sound.voltext.play.gui.listeners;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

import ch.sound.voltext.play.model.Lamp;
import ch.sound.voltext.play.model.tree.PlayListTreeModel;

public class LampFilterActionListener implements EventHandler<ActionEvent> {

    private final TreeView<Object> playsTree;
    private final Supplier<PlayListTreeModel> modelSupplier;
    private final Lamp lamp;

    public LampFilterActionListener(TreeView<Object> playsTree, Supplier<PlayListTreeModel> modelSupplier, Lamp lamp) {
        this.playsTree = playsTree;
        this.modelSupplier = modelSupplier;
        this.lamp = lamp;
    }

    @Override
    public void handle(ActionEvent event) {
        javafx.scene.control.ToggleButton button =
                (javafx.scene.control.ToggleButton) event.getSource();

        if (!button.isSelected()) {
            playsTree.setRoot(modelSupplier.get().buildTreeItem());
        } else {
            TreeItem<Object> root = playsTree.getRoot();
            List<TreeItem<Object>> nodesToRemove = new ArrayList<>();

            for (TreeItem<Object> titleNode : new ArrayList<>(root.getChildren())) {
                nodesToRemove.addAll(iterateDifficulty(titleNode));
            }

            nodesToRemove.forEach(node -> {
                if (node.getParent() != null) {
                    node.getParent().getChildren().remove(node);
                }
            });
        }
    }

    private List<TreeItem<Object>> iterateDifficulty(TreeItem<Object> titleNode) {
        List<TreeItem<Object>> nodesToRemove = new ArrayList<>();
        List<TreeItem<Object>> childrenToRemove = new ArrayList<>();

        for (TreeItem<Object> diffNode : titleNode.getChildren()) {
            childrenToRemove.addAll(iterateLamp(titleNode, diffNode));
        }

        if (childrenToRemove.size() == titleNode.getChildren().size()) {
            nodesToRemove.add(titleNode);
        } else {
            nodesToRemove.addAll(childrenToRemove);
        }

        return nodesToRemove;
    }

    private List<TreeItem<Object>> iterateLamp(TreeItem<Object> titleNode, TreeItem<Object> diffNode) {
        List<TreeItem<Object>> nodesToRemove = new ArrayList<>();
        List<TreeItem<Object>> childrenToRemove = new ArrayList<>();

        for (TreeItem<Object> lampNode : diffNode.getChildren()) {
            if (!lamp.getName().equalsIgnoreCase((String) lampNode.getValue())) {
                childrenToRemove.add(lampNode);
            }
        }

        if (childrenToRemove.size() == diffNode.getChildren().size()) {
            nodesToRemove.add(diffNode);
        } else {
            nodesToRemove.addAll(childrenToRemove);
        }

        return nodesToRemove;
    }
}
