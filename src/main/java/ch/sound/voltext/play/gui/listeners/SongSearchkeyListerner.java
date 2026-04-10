package ch.sound.voltext.play.gui.listeners;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import javafx.event.EventHandler;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyEvent;

import ch.sound.voltext.play.model.tree.PlayListTreeModel;

public class SongSearchkeyListerner implements EventHandler<KeyEvent> {

    private final TreeView<Object> playsTree;
    private final Supplier<PlayListTreeModel> modelSupplier;

    public SongSearchkeyListerner(TreeView<Object> playsTree, Supplier<PlayListTreeModel> modelSupplier) {
        this.playsTree = playsTree;
        this.modelSupplier = modelSupplier;
    }

    @Override
    public void handle(KeyEvent event) {
        TextField songSearchText = (TextField) event.getSource();
        String text = songSearchText.getText();

        if (text.length() < 3) {
            playsTree.setRoot(modelSupplier.get().buildTreeItem());
        } else {
            String criteria = text;
            TreeItem<Object> root = playsTree.getRoot();

            List<TreeItem<Object>> nodesToRemove = new ArrayList<>();
            for (TreeItem<Object> treeNode : new ArrayList<>(root.getChildren())) {
                String nodeTitle = (String) treeNode.getValue();
                if (!nodeTitle.contains(criteria)) {
                    nodesToRemove.add(treeNode);
                }
            }

            nodesToRemove.forEach(node -> {
                if (node.getParent() != null) {
                    node.getParent().getChildren().remove(node);
                }
            });
        }
    }
}
