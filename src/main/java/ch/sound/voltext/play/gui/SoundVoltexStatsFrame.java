package ch.sound.voltext.play.gui;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.swing.SwingUtilities;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

import javafx.embed.swing.SwingNode;

import ch.sound.voltext.play.gui.listeners.BrowseSongListActionListener;
import ch.sound.voltext.play.gui.listeners.GradeFilterActionListener;
import ch.sound.voltext.play.gui.listeners.LampFilterActionListener;
import ch.sound.voltext.play.gui.listeners.SongSearchkeyListerner;
import ch.sound.voltext.play.gui.listeners.VolforceFilterActionListener;
import ch.sound.voltext.play.model.Difficulty;
import ch.sound.voltext.play.model.Lamp;
import ch.sound.voltext.play.model.PlayLog;
import ch.sound.voltext.play.model.PlayLog.Grade;
import ch.sound.voltext.play.model.Songlist;
import ch.sound.voltext.play.model.tree.PlayListTreeModel;
import ch.sound.voltext.play.statistic.FileDAO;
import ch.sound.voltext.play.statistic.PlayStatsCalculator;
import ch.sound.voltext.play.statistic.SongDataUtils;
import ch.sound.voltext.play.statistic.VolforceCalculator;
import jakarta.xml.bind.JAXBException;

public class SoundVoltexStatsFrame {

    private static final ResourceBundle BUNDLE =
            ResourceBundle.getBundle("i18n/messages", Locale.ENGLISH);

    // JavaFX controls
    private TextField songListLocationText;
    private Label overallPlayStatsText;
    private TextArea difficultyTextArea;
    private TextArea ratingsTextArea;
    private TextArea gradesTextArea;
    private TextArea lampsTextArea;
    private TreeView<Object> playsTree;
    private TextField songSearchText;
    private ToggleButton quickFilterVFTop50;
    private ToggleButton quickFilterPUCButton;
    private ToggleButton quickFilterUCButton;
    private ToggleButton quickFilterHardButton;
    private ToggleButton quickFilterEXHardButton;
    private ComboBox<Grade> quickFilterGradeCombo;
    private Label volforcePanelTitle;

    // JFreeChart panels embedded via SwingNode
    private SwingNode volforceSwingNode;
    private SwingNode byDateSwingNode;
    private ChartPanel volforceChartPanel;
    private ChartPanel byDateChartPanel;

    // Data state
    private PlayListTreeModel treeModel;
    private VolforceCalculator volforceCalculator;

    public SoundVoltexStatsFrame(Stage primaryStage) {
        primaryStage.setTitle(BUNDLE.getString("SoundVoltexStatsFrame.this.title"));
        primaryStage.setResizable(false);

        try (InputStream iconStream = getClass().getResourceAsStream("/images/logoIcon.png")) {
            if (iconStream != null) {
                primaryStage.getIcons().add(new Image(iconStream));
            }
        } catch (Exception e) {
            // icon not available, continue without it
        }

        Pane contentPane = createComponents();
        Scene scene = new Scene(contentPane);
        primaryStage.setScene(scene);
        primaryStage.setWidth(1317);
        primaryStage.setHeight(763);

        // ── SwingNode first-frame black-flash quirk ──────────────────────────────────
        //
        // SwingNode.setContent(JComponent) must be called on the Swing EDT. Internally
        // it posts a Platform.runLater task back to the FX thread to create the native
        // render surface for the embedded component. If Stage.show() is called before
        // that surface-init task has been processed, the FX renderer finds an
        // uninitialised SwingNode and fills its area with black on the very first frame.
        //
        // The naive fix — SwingUtilities.invokeLater + Stage.show() right after — does
        // not work: invokeLater returns immediately, so show() fires before the EDT has
        // even called setContent(), let alone before the resulting Platform.runLater
        // surface-init task has been processed by the FX thread.
        //
        // The correct fix exploits the FIFO ordering of both thread queues:
        //
        //   1. SwingUtilities.invokeAndWait blocks the FX thread while the Swing EDT
        //      runs. Both setContent() calls complete, each posting a Platform.runLater
        //      surface-init task. Because the FX thread is blocked, those tasks queue up
        //      but are not yet processed.
        //
        //   2. invokeAndWait returns. The FX event queue now contains:
        //        [ surface-init-volforce, surface-init-byDate ]
        //
        //   3. We call Platform.runLater(stage::show), appending it after them:
        //        [ surface-init-volforce, surface-init-byDate, show ]
        //
        //   4. The FX thread processes the queue in order: both SwingNode surfaces are
        //      fully initialised before show() is reached, so the very first rendered
        //      frame already contains the white ChartPanel backgrounds.
        //
        // Note: invokeAndWait is safe here because we are on the FX thread (not the
        // Swing EDT) and setContent() does not block waiting for the FX thread — it
        // only schedules work on it — so there is no risk of deadlock.
        try {
            SwingUtilities.invokeAndWait(() -> {
                volforceChartPanel = new ChartPanel(null);
                volforceChartPanel.setBackground(java.awt.Color.WHITE);
                volforceChartPanel.setMouseWheelEnabled(true);
                volforceChartPanel.setFillZoomRectangle(false);
                volforceChartPanel.setPreferredSize(new java.awt.Dimension(761, 375));
                volforceSwingNode.setContent(volforceChartPanel);

                byDateChartPanel = new ChartPanel(null);
                byDateChartPanel.setBackground(java.awt.Color.WHITE);
                byDateChartPanel.setMouseWheelEnabled(false);
                byDateChartPanel.setFillZoomRectangle(false);
                byDateChartPanel.setPreferredSize(new java.awt.Dimension(771, 396));
                byDateSwingNode.setContent(byDateChartPanel);
            });
        } catch (InvocationTargetException | InterruptedException ex) {
            ex.printStackTrace();
        }

        // show() is deferred via Platform.runLater so that it is appended to the FX
        // event queue after the surface-init tasks posted above (see explanation).
        Platform.runLater(primaryStage::show);
    }

    private void loadData(String path) throws JAXBException {
        Path dataPath = Paths.get(path);
        Songlist songData = FileDAO.loadSongData(dataPath);

        PlayStatsCalculator playsCalculator = PlayStatsCalculator.getInstance();
        playsCalculator.loadData(songData);

        String statsText = String.format(
                "Played %d songs with %d total plays in %d days for a total of %2dh%02dmin played",
                playsCalculator.getTotalSongs(), playsCalculator.getTotalPlays(),
                Duration.between(SongDataUtils.getFirstDate(songData),
                        SongDataUtils.getLastDate(songData).plusDays(1)).toDays(),
                Duration.ofMinutes(2).plusMinutes(2 * playsCalculator.getTotalPlays()).toHoursPart(),
                Duration.ofMinutes(2).plusMinutes(2 * playsCalculator.getTotalPlays()).toMinutesPart());
        overallPlayStatsText.setText(statsText);

        printDifficultyStatistics(playsCalculator);
        printRatingStatistics(playsCalculator);
        printLampStatistics(playsCalculator);
        printGradeStatistics(playsCalculator);

        volforceCalculator = drawVolforce(songData);
        treeModel = new PlayListTreeModel(songData);
        fillPlaysTree();
        drawByDate(playsCalculator.getPlaysPerDay());
    }

    private VolforceCalculator drawVolforce(Songlist songData) {
        VolforceCalculator calc = new VolforceCalculator(songData);
        volforcePanelTitle.setText(" Volforce: " + calc.calculateCurrent() + " ");

        TimeSeries series = new TimeSeries("Volforce");
        calc.calculateByDate().forEach((k, v) ->
                series.add(new Day(k.getDayOfMonth(), k.getMonth().getValue(), k.getYear()),
                        v.doubleValue()));

        XYDataset dataset = new TimeSeriesCollection(series);
        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                "Volforce gains", "Date", "Volforce", dataset, false, true, false);
        ((DateAxis) ((XYPlot) chart.getPlot()).getDomainAxis())
                .setDateFormatOverride(new SimpleDateFormat("yyyy-MM"));

        SwingUtilities.invokeLater(() -> volforceChartPanel.setChart(chart));
        return calc;
    }

    private void drawByDate(Map<LocalDate, List<PlayLog>> mapByDate) {
        TimeSeries series = new TimeSeries("Songs");
        mapByDate.entrySet().forEach(e ->
                series.add(new Day(e.getKey().getDayOfMonth(),
                        e.getKey().getMonth().getValue(), e.getKey().getYear()),
                        e.getValue().size()));

        XYDataset dataset = new TimeSeriesCollection(series);
        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                "Songs played by Date", "Date", "Number of songs", dataset, false, true, false);
        ((DateAxis) ((XYPlot) chart.getPlot()).getDomainAxis())
                .setDateFormatOverride(new SimpleDateFormat("yyyy-MM"));

        SwingUtilities.invokeLater(() -> byDateChartPanel.setChart(chart));
    }

    private void printDifficultyStatistics(PlayStatsCalculator playsCalculator) {
        difficultyTextArea.setText("");
        difficultyTextArea.appendText(Difficulty.NOVICE.getName() + ": "
                + playsCalculator.getTotalByDificulty(Difficulty.NOVICE) + System.lineSeparator());
        difficultyTextArea.appendText(Difficulty.ADVANCED.getName() + ": "
                + playsCalculator.getTotalByDificulty(Difficulty.ADVANCED) + System.lineSeparator());
        difficultyTextArea.appendText(Difficulty.EXHAUST.getName() + ": "
                + playsCalculator.getTotalByDificulty(Difficulty.EXHAUST) + System.lineSeparator());
        difficultyTextArea.appendText(Difficulty.APPEND.getName() + ": "
                + playsCalculator.getTotalByDificulty(Difficulty.APPEND));
    }

    private void printRatingStatistics(PlayStatsCalculator playsCalculator) {
        ratingsTextArea.setText("");
        playsCalculator.getTotalByRating().entrySet().stream()
                .sorted((g1, g2) -> Integer.compare(g1.getKey(), g2.getKey()))
                .forEach(e -> ratingsTextArea.appendText(
                        "Level " + e.getKey() + ": " + e.getValue() + System.lineSeparator()));
    }

    private void printLampStatistics(PlayStatsCalculator playsCalculator) {
        lampsTextArea.setText("");
        lampsTextArea.appendText(Lamp.FAILED.getName() + ": "
                + playsCalculator.getTotalByLamp(Lamp.FAILED) + System.lineSeparator());
        lampsTextArea.appendText(Lamp.CLEAR.getName() + ": "
                + playsCalculator.getTotalByLamp(Lamp.CLEAR) + System.lineSeparator());
        lampsTextArea.appendText(Lamp.EX_CLEAR.getName() + ": "
                + playsCalculator.getTotalByLamp(Lamp.EX_CLEAR) + System.lineSeparator());
        lampsTextArea.appendText(Lamp.EX_HARD_CLEAR.getName() + ": "
                + playsCalculator.getTotalByLamp(Lamp.EX_HARD_CLEAR) + System.lineSeparator());
        lampsTextArea.appendText(Lamp.UC.getName() + ": "
                + playsCalculator.getTotalByLamp(Lamp.UC) + System.lineSeparator());
        lampsTextArea.appendText(Lamp.PUC.getName() + ": "
                + playsCalculator.getTotalByLamp(Lamp.PUC));
    }

    private void printGradeStatistics(PlayStatsCalculator playsCalculator) {
        gradesTextArea.setText("");
        playsCalculator.getTotalByGrade().entrySet().stream()
                .sorted((g1, g2) -> Integer.compare(g1.getKey().ordinal(), g2.getKey().ordinal()))
                .forEach(e -> gradesTextArea.appendText(
                        e.getKey().getName() + ": " + e.getValue() + System.lineSeparator()));
    }

    private void fillPlaysTree() {
        playsTree.setRoot(treeModel.buildTreeItem());
    }

    // -------------------------------------------------------------------------
    // Component creation
    // -------------------------------------------------------------------------

    private Pane createComponents() {
        Pane contentPane = new Pane();
        contentPane.setPadding(new Insets(5));

        // --- Top row: file path + buttons ---
        Label songListLabel = new Label(BUNDLE.getString("SoundVoltexStatsFrame.songListLabel.text"));
        placeLabel(songListLabel, 10, 11);
        contentPane.getChildren().add(songListLabel);

        songListLocationText = new TextField();
        place(songListLocationText, 112, 8, 750, 20);
        contentPane.getChildren().add(songListLocationText);

        Button browseSongListButton = new Button(
                BUNDLE.getString("SoundVoltexStatsFrame.browseSongListButton.text"));
        place(browseSongListButton, 872, 7, 89, 23);
        browseSongListButton.setOnAction(new BrowseSongListActionListener(songListLocationText));
        contentPane.getChildren().add(browseSongListButton);

        Button loadSongListButton = new Button(
                BUNDLE.getString("SoundVoltexStatsFrame.loadSongListButton.text"));
        place(loadSongListButton, 971, 7, 89, 23);
        loadSongListButton.setOnAction(e -> {
            String path = songListLocationText.getText();
            if (path == null || path.isBlank()) return;
            try {
                loadData(path);
            } catch (JAXBException ex) {
                ex.printStackTrace();
            }
        });
        contentPane.getChildren().add(loadSongListButton);

        // --- Separator ---
        Separator separator = new Separator();
        place(separator, 10, 35, 1281, 9);
        contentPane.getChildren().add(separator);

        // --- Overall stats row ---
        Label overallPlayStatsLabel = new Label(
                BUNDLE.getString("SoundVoltexStatsFrame.overallPlayStatsLabel.text"));
        overallPlayStatsLabel.setFont(Font.font("Tahoma", 16));
        placeLabel(overallPlayStatsLabel, 10, 50);
        contentPane.getChildren().add(overallPlayStatsLabel);

        overallPlayStatsText = new Label();
        overallPlayStatsText.setFont(Font.font("Tahoma", 16));
        place(overallPlayStatsText, 112, 50, 674, 20);
        contentPane.getChildren().add(overallPlayStatsText);

        // --- Quick filters row ---
        Label quickFiltersLabel = new Label(
                BUNDLE.getString("SoundVoltexStatsFrame.lblNewLabel_1.text"));
        placeLabel(quickFiltersLabel, 825, 55);
        contentPane.getChildren().add(quickFiltersLabel);

        quickFilterGradeCombo = new ComboBox<>(FXCollections.observableArrayList(Grade.values()));
        quickFilterGradeCombo.setValue(Grade.NONE);
        place(quickFilterGradeCombo, 889, 51, 64, 22);
        contentPane.getChildren().add(quickFilterGradeCombo);

        quickFilterVFTop50 = new ToggleButton(
                BUNDLE.getString("SoundVoltexStatsFrame.quickFilterVFTop50.text"));
        place(quickFilterVFTop50, 953, 51, 81, 23);
        contentPane.getChildren().add(quickFilterVFTop50);

        quickFilterPUCButton = new ToggleButton(
                BUNDLE.getString("SoundVoltexStatsFrame.quickFilterPUCButton.text"));
        place(quickFilterPUCButton, 1034, 51, 55, 23);
        contentPane.getChildren().add(quickFilterPUCButton);

        quickFilterUCButton = new ToggleButton(
                BUNDLE.getString("SoundVoltexStatsFrame.quickFilterUCButton.text"));
        place(quickFilterUCButton, 1089, 51, 51, 23);
        contentPane.getChildren().add(quickFilterUCButton);

        quickFilterEXHardButton = new ToggleButton(
                BUNDLE.getString("SoundVoltexStatsFrame.quickFilterExtraHardButton.text"));
        place(quickFilterEXHardButton, 1140, 51, 80, 23);
        contentPane.getChildren().add(quickFilterEXHardButton);

        quickFilterHardButton = new ToggleButton(
                BUNDLE.getString("SoundVoltexStatsFrame.quickFilterHardButton.text"));
        place(quickFilterHardButton, 1220, 51, 71, 23);
        contentPane.getChildren().add(quickFilterHardButton);

        // --- Search row ---
        Label songSearchLabel = new Label(
                BUNDLE.getString("SoundVoltexStatsFrame.lblNewLabel.text"));
        placeLabel(songSearchLabel, 929, 78);
        contentPane.getChildren().add(songSearchLabel);

        songSearchText = new TextField();
        place(songSearchText, 997, 75, 294, 20);
        contentPane.getChildren().add(songSearchText);

        // --- Stats panel (4 titled sub-panels) ---
        Pane statsPanel = new Pane();
        place(statsPanel, 10, 81, 777, 202);
        contentPane.getChildren().add(statsPanel);

        // Difficulty panel
        Pane dificultyPanel = createTitledPanel(statsPanel,
                BUNDLE.getString("SoundVoltexStatsFrame.dificultyPanel.borderTitle"),
                0, 11, 190, 188);

        difficultyTextArea = new TextArea();
        difficultyTextArea.setEditable(false);
        difficultyTextArea.setWrapText(false);
        place(difficultyTextArea, 10, 17, 164, 160);
        dificultyPanel.getChildren().add(difficultyTextArea);

        // Rating panel
        Pane ratingPanel = createTitledPanel(statsPanel,
                BUNDLE.getString("SoundVoltexStatsFrame.ratingPanel.borderTitle"),
                194, 11, 190, 188);

        ratingsTextArea = new TextArea();
        ratingsTextArea.setEditable(false);
        ratingsTextArea.setWrapText(false);
        place(ratingsTextArea, 10, 17, 161, 160);
        ratingPanel.getChildren().add(ratingsTextArea);

        // Lamp panel
        Pane lampPanel = createTitledPanel(statsPanel,
                BUNDLE.getString("SoundVoltexStatsFrame.lampPanel.borderTitle"),
                385, 11, 190, 188);

        lampsTextArea = new TextArea();
        lampsTextArea.setEditable(false);
        lampsTextArea.setWrapText(false);
        place(lampsTextArea, 10, 17, 167, 160);
        lampPanel.getChildren().add(lampsTextArea);

        // Grade panel
        Pane gradePanel = createTitledPanel(statsPanel,
                BUNDLE.getString("SoundVoltexStatsFrame.gradePanel.borderTitle"),
                582, 11, 190, 188);

        gradesTextArea = new TextArea();
        gradesTextArea.setEditable(false);
        gradesTextArea.setWrapText(false);
        place(gradesTextArea, 10, 17, 170, 160);
        gradePanel.getChildren().add(gradesTextArea);

        // --- Plays list panel (right column) ---
        Pane playsListPanel = createTitledPanel(contentPane,
                BUNDLE.getString("SoundVoltexStatsFrame.playsListPanel.borderTitle"),
                794, 91, 500, 622);

        playsTree = new TreeView<>();
        playsTree.setRoot(new TreeItem<>("Songs"));
        place(playsTree, 10, 18, 480, 569);
        playsListPanel.getChildren().add(playsTree);

        Button byDatePlaysButton = new Button(
                BUNDLE.getString("SoundVoltexStatsFrame.byDatePlaysButton.text"));
        byDatePlaysButton.setDisable(true);
        place(byDatePlaysButton, 10, 588, 129, 23);
        playsListPanel.getChildren().add(byDatePlaysButton);

        // --- Tabbed pane: charts ---
        TabPane graphsPane = new TabPane();
        place(graphsPane, 10, 289, 776, 424);
        contentPane.getChildren().add(graphsPane);

        // Volforce tab
        Pane volforcePaneContainer = new Pane();

        volforcePanelTitle = new Label(
                " " + BUNDLE.getString("SoundVoltexStatsFrame.volforcePanel.borderTitle") + " ");
        volforcePanelTitle.setStyle("-fx-background-color: -fx-background;");
        volforcePanelTitle.setLayoutX(5);
        volforcePanelTitle.setLayoutY(2);
        volforcePaneContainer.getChildren().add(volforcePanelTitle);

        volforceSwingNode = new SwingNode();
        place(volforceSwingNode, 5, 17, 761, 375);
        volforcePaneContainer.getChildren().add(volforceSwingNode);

        Tab volforceTab = new Tab("Volforce", volforcePaneContainer);
        volforceTab.setClosable(false);
        graphsPane.getTabs().add(volforceTab);

        // By date tab
        Pane byDatePaneContainer = new Pane();

        byDateSwingNode = new SwingNode();
        place(byDateSwingNode, 0, 0, 771, 396);
        byDatePaneContainer.getChildren().add(byDateSwingNode);

        Tab byDateTab = new Tab("By date", byDatePaneContainer);
        byDateTab.setClosable(false);
        // ── SwingNode repaint quirk for hidden tabs ───────────────────────────────────
        // A SwingNode whose tab is not selected during startup is never laid out or
        // painted by the Swing EDT while it is hidden. When the user first selects the
        // tab, the native surface exists (created by the invokeAndWait block in the
        // constructor) but the Swing component inside it has never had paintComponent
        // called, so the surface still contains the initialisation-time black pixels.
        // Forcing a repaint on the Swing EDT the moment the tab becomes selected causes
        // the ChartPanel to repaint itself into the already-initialised surface, making
        // the chart appear correctly on first display.
        byDateTab.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
            if (isNowSelected && byDateChartPanel != null) {
                SwingUtilities.invokeLater(byDateChartPanel::repaint);
            }
        });
        graphsPane.getTabs().add(byDateTab);

        // --- Wire filter event handlers (registered once) ---
        wireFilterHandlers();

        return contentPane;
    }

    /**
     * Registers all quick-filter event handlers exactly once.
     * Each handler references the current {@code treeModel} / {@code volforceCalculator}
     * instance fields, so they always use the latest data.
     */
    private void wireFilterHandlers() {
        LampFilterActionListener hardListener =
                new LampFilterActionListener(playsTree, () -> treeModel, Lamp.EX_CLEAR);
        quickFilterHardButton.setOnAction(e -> {
            hardListener.handle(e);
            if (quickFilterHardButton.isSelected()) {
                quickFilterEXHardButton.setSelected(false);
                quickFilterUCButton.setSelected(false);
                quickFilterPUCButton.setSelected(false);
                quickFilterVFTop50.setSelected(false);
                quickFilterGradeCombo.setValue(Grade.NONE);
            }
        });

        LampFilterActionListener exHardListener =
                new LampFilterActionListener(playsTree, () -> treeModel, Lamp.EX_HARD_CLEAR);
        quickFilterEXHardButton.setOnAction(e -> {
            exHardListener.handle(e);
            if (quickFilterEXHardButton.isSelected()) {
                quickFilterHardButton.setSelected(false);
                quickFilterUCButton.setSelected(false);
                quickFilterPUCButton.setSelected(false);
                quickFilterVFTop50.setSelected(false);
                quickFilterGradeCombo.setValue(Grade.NONE);
            }
        });

        LampFilterActionListener ucListener =
                new LampFilterActionListener(playsTree, () -> treeModel, Lamp.UC);
        quickFilterUCButton.setOnAction(e -> {
            ucListener.handle(e);
            if (quickFilterUCButton.isSelected()) {
                quickFilterHardButton.setSelected(false);
                quickFilterEXHardButton.setSelected(false);
                quickFilterPUCButton.setSelected(false);
                quickFilterVFTop50.setSelected(false);
                quickFilterGradeCombo.setValue(Grade.NONE);
            }
        });

        LampFilterActionListener pucListener =
                new LampFilterActionListener(playsTree, () -> treeModel, Lamp.PUC);
        quickFilterPUCButton.setOnAction(e -> {
            pucListener.handle(e);
            if (quickFilterPUCButton.isSelected()) {
                quickFilterHardButton.setSelected(false);
                quickFilterEXHardButton.setSelected(false);
                quickFilterUCButton.setSelected(false);
                quickFilterVFTop50.setSelected(false);
                quickFilterGradeCombo.setValue(Grade.NONE);
            }
        });

        VolforceFilterActionListener vfListener =
                new VolforceFilterActionListener(playsTree, () -> treeModel, () -> volforceCalculator);
        quickFilterVFTop50.setOnAction(e -> {
            vfListener.handle(e);
            if (quickFilterVFTop50.isSelected()) {
                quickFilterHardButton.setSelected(false);
                quickFilterEXHardButton.setSelected(false);
                quickFilterUCButton.setSelected(false);
                quickFilterPUCButton.setSelected(false);
                quickFilterGradeCombo.setValue(Grade.NONE);
            }
        });

        GradeFilterActionListener gradeListener =
                new GradeFilterActionListener(playsTree, () -> treeModel);
        quickFilterGradeCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            gradeListener.changed(obs, oldVal, newVal);
            if (newVal != null && newVal != Grade.NONE) {
                quickFilterHardButton.setSelected(false);
                quickFilterEXHardButton.setSelected(false);
                quickFilterUCButton.setSelected(false);
                quickFilterPUCButton.setSelected(false);
                quickFilterVFTop50.setSelected(false);
            }
        });

        songSearchText.setOnKeyReleased(
                new SongSearchkeyListerner(playsTree, () -> treeModel));
    }

    // -------------------------------------------------------------------------
    // Layout helpers
    // -------------------------------------------------------------------------

    /**
     * Positions a node at exact (x, y) with preferred (w, h) — equivalent to
     * Swing's {@code setBounds(x, y, w, h)} with a null layout manager.
     */
    private static void place(javafx.scene.Node node, double x, double y, double w, double h) {
        node.setLayoutX(x);
        node.setLayoutY(y);
        if (node instanceof javafx.scene.layout.Region r) {
            r.setPrefWidth(w);
            r.setPrefHeight(h);
        }
    }

    /**
     * Positions a {@link Label} at exact (x, y) without constraining its size,
     * so the text is never clipped regardless of font rendering differences
     * between Swing and JavaFX.
     */
    private static void placeLabel(Label label, double x, double y) {
        label.setLayoutX(x);
        label.setLayoutY(y);
    }

    /**
     * Simulates a Swing {@code TitledBorder} by adding a bordered {@link Pane} and an
     * overlapping title {@link Label} to {@code parent}, then returns the bordered pane
     * so callers can add children to it.
     */
    private static Pane createTitledPanel(Pane parent, String title, double x, double y, double w, double h) {
        Pane borderPane = new Pane();
        borderPane.setLayoutX(x);
        borderPane.setLayoutY(y + 7);
        borderPane.setPrefWidth(w);
        borderPane.setPrefHeight(h - 7);
        borderPane.setStyle("-fx-border-color: #888888; -fx-border-width: 1;");

        Label titleLabel = new Label(" " + title + " ");
        titleLabel.setStyle("-fx-background-color: -fx-background; -fx-font-size: 11px;");
        titleLabel.setLayoutX(x + 7);
        titleLabel.setLayoutY(y - 1);

        // Add border pane first, then label so label renders on top of border
        parent.getChildren().addAll(borderPane, titleLabel);
        return borderPane;
    }
}
