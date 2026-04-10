package ch.sound.voltext.play.gui;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ByDatePlaysDialog {

    private final Stage stage;

    public ByDatePlaysDialog(Stage owner) {
        stage = new Stage();
        stage.setTitle("Plays by Date");
        stage.setResizable(false);
        stage.initModality(Modality.NONE);
        if (owner != null) {
            stage.initOwner(owner);
        }

        GridPane contentPane = buildContentPane();
        Scene scene = new Scene(contentPane);
        stage.setScene(scene);
        stage.setWidth(640);
        stage.setHeight(480);
    }

    public void show() {
        stage.show();
    }

    private GridPane buildContentPane() {
        GridPane root = new GridPane();
        root.setPadding(new Insets(5));

        // 3 columns: [nav-left], [center-stretch], [nav-right]
        ColumnConstraints colLeft = new ColumnConstraints();
        ColumnConstraints colCenter = new ColumnConstraints();
        colCenter.setHgrow(Priority.ALWAYS);
        colCenter.setFillWidth(true);
        ColumnConstraints colRight = new ColumnConstraints();
        root.getColumnConstraints().addAll(colLeft, colCenter, colRight);

        // 3 rows: [year-row], [month-row], [days-grid]
        RowConstraints rowYear = new RowConstraints();
        RowConstraints rowMonth = new RowConstraints();
        RowConstraints rowDays = new RowConstraints();
        rowDays.setVgrow(Priority.ALWAYS);
        rowDays.setFillHeight(true);
        root.getRowConstraints().addAll(rowYear, rowMonth, rowDays);

        root.setHgap(5);
        root.setVgap(5);

        // --- Year navigation row ---
        Button previousYearButton = new Button("<");
        GridPane.setMargin(previousYearButton, new Insets(0, 0, 5, 0));
        root.add(previousYearButton, 0, 0);

        Button yearButton = new Button(Integer.toString(LocalDate.now().getYear()));
        yearButton.setMaxWidth(Double.MAX_VALUE);
        GridPane.setHgrow(yearButton, Priority.ALWAYS);
        GridPane.setFillWidth(yearButton, true);
        GridPane.setMargin(yearButton, new Insets(0, 0, 5, 0));
        root.add(yearButton, 1, 0);

        Button nextYearButton = new Button(">");
        GridPane.setMargin(nextYearButton, new Insets(0, 0, 5, 0));
        root.add(nextYearButton, 2, 0);

        // --- Month navigation row ---
        Button previousMonthButton = new Button("<");
        GridPane.setMargin(previousMonthButton, new Insets(0, 0, 5, 0));
        root.add(previousMonthButton, 0, 1);

        Button monthButton = new Button(
                LocalDate.now().getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault()));
        monthButton.setMaxWidth(Double.MAX_VALUE);
        GridPane.setHgrow(monthButton, Priority.ALWAYS);
        GridPane.setFillWidth(monthButton, true);
        GridPane.setMargin(monthButton, new Insets(0, 0, 5, 0));
        root.add(monthButton, 1, 1);

        Button nextMonthButton = new Button(">");
        GridPane.setMargin(nextMonthButton, new Insets(0, 0, 5, 0));
        root.add(nextMonthButton, 2, 1);

        // --- Days grid (spans all 3 columns) ---
        GridPane daysPanel = buildDaysPanel();
        daysPanel.setStyle("-fx-background-color: white;");
        GridPane.setColumnSpan(daysPanel, 3);
        GridPane.setHgrow(daysPanel, Priority.ALWAYS);
        GridPane.setVgrow(daysPanel, Priority.ALWAYS);
        GridPane.setFillWidth(daysPanel, true);
        GridPane.setFillHeight(daysPanel, true);
        root.add(daysPanel, 0, 2);

        return root;
    }

    private GridPane buildDaysPanel() {
        GridPane daysPanel = new GridPane();
        daysPanel.setStyle("-fx-background-color: white;");

        // 7 equal columns (Mon–Sun), 6 rows (1 header + 5 week rows)
        for (int i = 0; i < 7; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(100.0 / 7);
            col.setHgrow(Priority.ALWAYS);
            col.setFillWidth(true);
            daysPanel.getColumnConstraints().add(col);
        }

        RowConstraints headerRow = new RowConstraints();
        daysPanel.getRowConstraints().add(headerRow);

        for (int i = 0; i < 5; i++) {
            RowConstraints row = new RowConstraints();
            row.setVgrow(Priority.ALWAYS);
            row.setFillHeight(true);
            daysPanel.getRowConstraints().add(row);
        }

        // Day header labels (M, T, W, T, F, S, S)
        String[] dayHeaders = {"M", "T", "W", "T", "F", "S", "S"};
        for (int x = 0; x < 7; x++) {
            Label header = new Label(dayHeaders[x]);
            header.setFont(Font.font("Tahoma", FontWeight.BOLD, 11));
            header.setMaxWidth(Double.MAX_VALUE);
            header.setMaxHeight(Double.MAX_VALUE);
            header.setStyle("-fx-border-color: black; -fx-border-width: 2; "
                    + "-fx-alignment: center; -fx-background-color: white;");
            GridPane.setHalignment(header, HPos.CENTER);
            GridPane.setValignment(header, VPos.CENTER);
            GridPane.setHgrow(header, Priority.ALWAYS);
            GridPane.setFillWidth(header, true);
            GridPane.setMargin(header, new Insets(0, 2, 5, 0));
            daysPanel.add(header, x, 0);
        }

        // Day cell panels (5 rows × 7 columns)
        for (int y = 1; y <= 5; y++) {
            for (int x = 0; x < 7; x++) {
                Pane dayPanel = new Pane();
                dayPanel.setStyle("-fx-border-color: black; -fx-border-width: 1; "
                        + "-fx-background-color: white;");
                dayPanel.setUserData(Integer.toString((x + 1) * y));
                GridPane.setHgrow(dayPanel, Priority.ALWAYS);
                GridPane.setVgrow(dayPanel, Priority.ALWAYS);
                GridPane.setFillWidth(dayPanel, true);
                GridPane.setFillHeight(dayPanel, true);
                GridPane.setMargin(dayPanel, new Insets(0, 2, 2, 0));
                daysPanel.add(dayPanel, x, y);
            }
        }

        return daysPanel;
    }
}
