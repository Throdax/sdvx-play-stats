package ch.sound.voltext.play.gui;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

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

public class SoundVoltexStatsFrame extends JFrame {

	private static final ResourceBundle BUNDLE = ResourceBundle.getBundle("i18n/messages", Locale.ENGLISH); //$NON-NLS-1$

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField songListLocationText;
	private JLabel overallPlayStatsText;
	private JTextArea difficultyTextArea;
	private JTextArea ratingsTextArea;
	private JTextArea gradesTextArea;
	private JTree playsTree;
	private JPanel volforcePanel;
	private JTextArea lampsTextArea;
	private JTextField songSearchText;
	private JToggleButton quickFilterVFTop50;
	private JToggleButton quickFilterPUCButton;
	private JToggleButton quickFilterUCButton;
	private JToggleButton quickFilterHardButton;
	private JToggleButton quickFilterEXHardButton;
	private JComboBox<Grade> quickFilterGradeCombo;
	private ChartPanel volforceChartPanel;
	private JPanel byDatePanel;
	private ChartPanel byDateChartPanel;
	private JPanel playsListPanel;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SoundVoltexStatsFrame frame = new SoundVoltexStatsFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 * 
	 * @throws MalformedURLException
	 * @throws JAXBException
	 */
	public SoundVoltexStatsFrame() throws MalformedURLException, JAXBException {

		setTitle(BUNDLE.getString("SoundVoltexStatsFrame.this.title")); //$NON-NLS-1$
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1317, 763);

		setIconImage(new ImageIcon(getClass().getResource("/images/logoIcon.png")).getImage());

		setResizable(false);

		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}

		createComponents();
	}

	private void loadData(String path) throws JAXBException {
		Path dataPath = Paths.get(path);

		Songlist songData = FileDAO.loadSongData(dataPath);

		PlayStatsCalculator playsCalculator = PlayStatsCalculator.getInstance();
		playsCalculator.loadData(songData);

		overallPlayStatsText.setText(String.format(
				"Played %d songs with %d total plays in %d days for a total of %2dh%02dmin played",
				playsCalculator.getTotalSongs(), playsCalculator.getTotalPlays(),
				Duration.between(SongDataUtils.getFirstDate(songData), SongDataUtils.getLastDate(songData).plusDays(1))
						.toDays(),
				Duration.ofMinutes(2).plusMinutes(2 * playsCalculator.getTotalPlays()).toHoursPart(),
				Duration.ofMinutes(2).plusMinutes(2 * playsCalculator.getTotalPlays()).toMinutesPart()));

		overallPlayStatsText.updateUI();

		printDifficultyStatistics(playsCalculator);
		printRatingStatistics(playsCalculator);
		printLampStatistics(playsCalculator);
		printGradeStatistics(playsCalculator);
		VolforceCalculator volforceCalculator = drawVolforce(songData);
		fillPlaysTree(songData, volforceCalculator);

		drawByDate(playsCalculator.getPlaysPerDay());

	}

	private VolforceCalculator drawVolforce(Songlist songData) {
		VolforceCalculator volforceCalculator = new VolforceCalculator(songData);
		volforcePanel.setBorder(new TitledBorder("Volforce: " + volforceCalculator.calculateCurrent()));

		TimeSeries series = new TimeSeries("Volforce");
		volforceCalculator.calculateByDate().forEach((k, v) -> series
				.add(new Day(k.getDayOfMonth(), k.getMonth().getValue(), k.getYear()), v.doubleValue()));

		XYDataset dataset = new TimeSeriesCollection(series);

		JFreeChart chart = ChartFactory.createTimeSeriesChart("Volforce gains", "Date", "Volforce", dataset, false,
				true, false);
		((DateAxis) ((XYPlot) chart.getPlot()).getDomainAxis()).setDateFormatOverride(new SimpleDateFormat("yyyy-MM"));
		volforceChartPanel.setChart(chart);
		volforceChartPanel.updateUI();

		return volforceCalculator;
	}

	private void drawByDate(Map<LocalDate, List<PlayLog>> mapByDate) {

		TimeSeries series = new TimeSeries("Songs");
		mapByDate.entrySet().stream()
				.forEach(e -> series.add(
						new Day(e.getKey().getDayOfMonth(), e.getKey().getMonth().getValue(), e.getKey().getYear()),
						e.getValue().size()));

		XYDataset dataset = new TimeSeriesCollection(series);

		JFreeChart chart = ChartFactory.createTimeSeriesChart("Songs played by Date", "Date", "Number of songs",
				dataset, false, true, false);
		((DateAxis) ((XYPlot) chart.getPlot()).getDomainAxis()).setDateFormatOverride(new SimpleDateFormat("yyyy-MM"));

		byDateChartPanel.setChart(chart);
		byDateChartPanel.updateUI();
		
	}

	private void printDifficultyStatistics(PlayStatsCalculator playsCalculator) {
		
		difficultyTextArea.setText("");
		
		difficultyTextArea
				.append(Difficulty.NOVICE.getName() + ": " + playsCalculator.getTotalByDificulty(Difficulty.NOVICE));
		difficultyTextArea.append(System.lineSeparator());

		difficultyTextArea.append(
				Difficulty.ADVANCED.getName() + ": " + playsCalculator.getTotalByDificulty(Difficulty.ADVANCED));
		difficultyTextArea.append(System.lineSeparator());

		difficultyTextArea
				.append(Difficulty.EXHAUST.getName() + ": " + playsCalculator.getTotalByDificulty(Difficulty.EXHAUST));
		difficultyTextArea.append(System.lineSeparator());

		difficultyTextArea
				.append(Difficulty.APPEND.getName() + ": " + playsCalculator.getTotalByDificulty(Difficulty.APPEND));
	}

	private void printRatingStatistics(PlayStatsCalculator playsCalculator) {
		
		ratingsTextArea.setText("");
		
		playsCalculator.getTotalByRating().entrySet().stream()
				.sorted((g1, g2) -> Integer.compare(g1.getKey(), g2.getKey())).forEach(e -> {
					ratingsTextArea.append("Level " + e.getKey() + ": " + e.getValue());
					ratingsTextArea.append(System.lineSeparator());
				});

	}

	private void printLampStatistics(PlayStatsCalculator playsCalculator) {
		
		lampsTextArea.setText("");
		
		lampsTextArea.append(Lamp.FAILED.getName() + ": " + playsCalculator.getTotalByLamp(Lamp.FAILED));
		lampsTextArea.append(System.lineSeparator());

		lampsTextArea.append(Lamp.CLEAR.getName() + ": " + playsCalculator.getTotalByLamp(Lamp.CLEAR));
		lampsTextArea.append(System.lineSeparator());

		lampsTextArea.append(Lamp.EX_CLEAR.getName() + ": " + playsCalculator.getTotalByLamp(Lamp.EX_CLEAR));
		lampsTextArea.append(System.lineSeparator());

		lampsTextArea.append(Lamp.EX_HARD_CLEAR.getName() + ": " + playsCalculator.getTotalByLamp(Lamp.EX_HARD_CLEAR));
		lampsTextArea.append(System.lineSeparator());

		lampsTextArea.append(Lamp.UC.getName() + ": " + playsCalculator.getTotalByLamp(Lamp.UC));
		lampsTextArea.append(System.lineSeparator());

		lampsTextArea.append(Lamp.PUC.getName() + ": " + playsCalculator.getTotalByLamp(Lamp.PUC));
	}

	private void printGradeStatistics(PlayStatsCalculator playsCalculator) {
		
		gradesTextArea.setText("");

		playsCalculator.getTotalByGrade().entrySet().stream()
				.sorted((g1, g2) -> Integer.compare(g1.getKey().ordinal(), g2.getKey().ordinal())).forEach(e -> {
					gradesTextArea.append(e.getKey().getName() + ": " + e.getValue());
					gradesTextArea.append(System.lineSeparator());
				});
	}

	private void createComponents() {

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel songListLabel = new JLabel(BUNDLE.getString("SoundVoltexStatsFrame.songListLabel.text")); //$NON-NLS-1$
		songListLabel.setLocale(Locale.ENGLISH);
		songListLabel.setBounds(10, 11, 92, 14);
		contentPane.add(songListLabel);

		songListLocationText = new JTextField();
		songListLabel.setLabelFor(songListLocationText);
		songListLocationText.setBounds(112, 8, 750, 20);
		contentPane.add(songListLocationText);
		songListLocationText.setColumns(10);

		JButton browseSongListButton = new JButton(BUNDLE.getString("SoundVoltexStatsFrame.browseSongListButton.text")); //$NON-NLS-1$
		browseSongListButton.setLocale(Locale.ENGLISH);
		browseSongListButton.setBounds(872, 7, 89, 23);
		browseSongListButton.addActionListener(new BrowseSongListActionListener(songListLocationText));
		contentPane.add(browseSongListButton);

		JButton loadSongListButton = new JButton(BUNDLE.getString("SoundVoltexStatsFrame.loadSongListButton.text")); //$NON-NLS-1$
		loadSongListButton.setLocale(Locale.ENGLISH);
		loadSongListButton.setBounds(971, 7, 89, 23);
		loadSongListButton.addActionListener(evt -> loadSongList(evt));
		contentPane.add(loadSongListButton);

		JLabel overallPlayStatsLabel = new JLabel(BUNDLE.getString("SoundVoltexStatsFrame.overallPlayStatsLabel.text")); //$NON-NLS-1$
		overallPlayStatsLabel.setLocale(Locale.ENGLISH);
		overallPlayStatsLabel.setFont(new Font("Tahoma", Font.PLAIN, 16));
		overallPlayStatsLabel.setBounds(10, 50, 102, 20);
		contentPane.add(overallPlayStatsLabel);

		overallPlayStatsText = new JLabel();
		overallPlayStatsText.setLocale(Locale.ENGLISH);
		overallPlayStatsText.setBackground(new Color(255, 255, 255));
		overallPlayStatsText.setFont(new Font("Tahoma", Font.PLAIN, 16));
		overallPlayStatsText.setBounds(112, 50, 674, 20);
		contentPane.add(overallPlayStatsText);

		JSeparator separator = new JSeparator();
		separator.setBounds(10, 35, 1281, 9);
		contentPane.add(separator);

		JTabbedPane graphsPane = new JTabbedPane(JTabbedPane.TOP);
		graphsPane.setBounds(10, 289, 776, 424);
		contentPane.add(graphsPane);

		volforcePanel = new JPanel();
		graphsPane.addTab("Volforce", null, volforcePanel, null);
		volforcePanel.setBorder(new TitledBorder(BUNDLE.getString("SoundVoltexStatsFrame.volforcePanel.borderTitle")));
		volforcePanel.setLayout(null);

		volforceChartPanel = new ChartPanel((JFreeChart) null);
		volforceChartPanel.setMouseWheelEnabled(true);
		volforceChartPanel.setFillZoomRectangle(false);
		volforceChartPanel.setBounds(5, 17, 761, 375);
		volforcePanel.add(volforceChartPanel);

		byDatePanel = new JPanel();
		graphsPane.addTab("By date", null, byDatePanel, null);
		byDatePanel.setLayout(null);

		byDateChartPanel = new ChartPanel((JFreeChart) null);
		byDateChartPanel.setMouseWheelEnabled(false);
		byDateChartPanel.setFillZoomRectangle(false);
		byDateChartPanel.setBounds(0, 0, 771, 396);
		byDatePanel.add(byDateChartPanel);

		JPanel statsPanel = new JPanel();
		statsPanel.setBounds(10, 81, 777, 202);
		contentPane.add(statsPanel);
		statsPanel.setLayout(null);

		JPanel dificultyPanel = new JPanel();
		dificultyPanel.setBounds(0, 11, 190, 188);
		statsPanel.add(dificultyPanel);
		dificultyPanel
				.setBorder(new TitledBorder(BUNDLE.getString("SoundVoltexStatsFrame.dificultyPanel.borderTitle"))); //$NON-NLS-1$
		dificultyPanel.setLayout(null);

		JScrollPane diffilcultyScrollPane = new JScrollPane();
		diffilcultyScrollPane.setBounds(10, 17, 164, 160);
		dificultyPanel.add(diffilcultyScrollPane);

		difficultyTextArea = new JTextArea();
		diffilcultyScrollPane.setViewportView(difficultyTextArea);
		difficultyTextArea.setEditable(false);
		difficultyTextArea.setBorder(new BevelBorder(BevelBorder.LOWERED));

		JPanel ratingPanel = new JPanel();
		ratingPanel.setBounds(194, 11, 190, 188);
		statsPanel.add(ratingPanel);
		ratingPanel.setBorder(new TitledBorder(BUNDLE.getString("SoundVoltexStatsFrame.ratingPanel.borderTitle"))); //$NON-NLS-1$
		ratingPanel.setLayout(null);

		JScrollPane ratingsScrollPane = new JScrollPane();
		ratingsScrollPane.setBounds(10, 17, 161, 160);
		ratingPanel.add(ratingsScrollPane);

		ratingsTextArea = new JTextArea();
		ratingsScrollPane.setViewportView(ratingsTextArea);
		ratingsTextArea.setEditable(false);
		ratingsTextArea.setBorder(new BevelBorder(BevelBorder.LOWERED));

		JPanel lampPanel = new JPanel();
		lampPanel.setBounds(385, 11, 190, 188);
		statsPanel.add(lampPanel);
		lampPanel.setBorder(new TitledBorder(BUNDLE.getString("SoundVoltexStatsFrame.lampPanel.borderTitle"))); //$NON-NLS-1$
		lampPanel.setLayout(null);

		JScrollPane lampScrollPane = new JScrollPane();
		lampScrollPane.setBounds(10, 17, 167, 160);
		lampPanel.add(lampScrollPane);

		lampsTextArea = new JTextArea();
		lampScrollPane.setViewportView(lampsTextArea);
		lampsTextArea.setEditable(false);
		lampsTextArea.setBorder(new BevelBorder(BevelBorder.LOWERED));

		JPanel gradePanel = new JPanel();
		gradePanel.setBounds(582, 11, 190, 188);
		statsPanel.add(gradePanel);
		gradePanel.setBorder(new TitledBorder(BUNDLE.getString("SoundVoltexStatsFrame.gradePanel.borderTitle"))); //$NON-NLS-1$
		gradePanel.setLayout(null);

		JScrollPane gradesScrollPane = new JScrollPane();
		gradesScrollPane.setBounds(10, 17, 170, 160);
		gradePanel.add(gradesScrollPane);

		gradesTextArea = new JTextArea();
		gradesScrollPane.setViewportView(gradesTextArea);
		gradesTextArea.setEditable(false);
		gradesTextArea.setBorder(new BevelBorder(BevelBorder.LOWERED));

		playsListPanel = new JPanel();
		playsListPanel.setBounds(794, 91, 500, 622);
		playsListPanel
				.setBorder(new TitledBorder(BUNDLE.getString("SoundVoltexStatsFrame.playsListPanel.borderTitle"))); //$NON-NLS-1$
		contentPane.add(playsListPanel);
		playsListPanel.setLayout(null);

		JScrollPane playsScrollPane = new JScrollPane();
		playsScrollPane.setBounds(10, 18, 480, 569);
		playsListPanel.add(playsScrollPane);

		playsTree = new JTree();
		playsScrollPane.setViewportView(playsTree);
		playsTree.setModel(new DefaultTreeModel(new DefaultMutableTreeNode("Songs")));
		playsTree.setBorder(new BevelBorder(BevelBorder.LOWERED));

		JButton byDatePlaysButton = new JButton(BUNDLE.getString("SoundVoltexStatsFrame.byDatePlaysButton.text"));
		byDatePlaysButton.setEnabled(false);
		byDatePlaysButton.setBounds(10, 588, 129, 23);
		playsListPanel.add(byDatePlaysButton);

		songSearchText = new JTextField();
		songSearchText.setBounds(997, 75, 294, 20);
		contentPane.add(songSearchText);
		songSearchText.setColumns(10);

		JLabel lblNewLabel = new JLabel(BUNDLE.getString("SoundVoltexStatsFrame.lblNewLabel.text")); //$NON-NLS-1$
		lblNewLabel.setLocale(Locale.ENGLISH);
		lblNewLabel.setBounds(929, 78, 64, 14);
		contentPane.add(lblNewLabel);

		quickFilterHardButton = new JToggleButton(BUNDLE.getString("SoundVoltexStatsFrame.quickFilterHardButton.text")); //$NON-NLS-1$
		quickFilterHardButton.setLocale(Locale.ENGLISH);
		quickFilterHardButton.setBounds(1220, 51, 71, 23);
		contentPane.add(quickFilterHardButton);

		quickFilterEXHardButton = new JToggleButton(
				BUNDLE.getString("SoundVoltexStatsFrame.quickFilterExtraHardButton.text")); //$NON-NLS-1$
		quickFilterEXHardButton.setLocale(Locale.ENGLISH);
		quickFilterEXHardButton.setBounds(1140, 51, 80, 23);
		contentPane.add(quickFilterEXHardButton);

		quickFilterUCButton = new JToggleButton(BUNDLE.getString("SoundVoltexStatsFrame.quickFilterUCButton.text")); //$NON-NLS-1$
		quickFilterUCButton.setLocale(Locale.ENGLISH);
		quickFilterUCButton.setBounds(1089, 51, 51, 23);
		contentPane.add(quickFilterUCButton);

		quickFilterPUCButton = new JToggleButton(BUNDLE.getString("SoundVoltexStatsFrame.quickFilterPUCButton.text")); //$NON-NLS-1$
		quickFilterPUCButton.setLocale(Locale.ENGLISH);
		quickFilterPUCButton.setBounds(1034, 51, 55, 23);
		contentPane.add(quickFilterPUCButton);

		JLabel quickFiltersLabel = new JLabel(BUNDLE.getString("SoundVoltexStatsFrame.lblNewLabel_1.text")); //$NON-NLS-1$
		quickFiltersLabel.setLocale(Locale.ENGLISH);
		quickFiltersLabel.setBounds(825, 55, 64, 14);
		contentPane.add(quickFiltersLabel);

		quickFilterVFTop50 = new JToggleButton(BUNDLE.getString("SoundVoltexStatsFrame.quickFilterVFTop50.text")); //$NON-NLS-1$
		quickFilterVFTop50.setLocale(Locale.ENGLISH);
		quickFilterVFTop50.setBounds(953, 51, 81, 23);
		contentPane.add(quickFilterVFTop50);

		quickFilterGradeCombo = new JComboBox<>();
		quickFilterGradeCombo.setModel(new DefaultComboBoxModel<>(Grade.values()));
		quickFilterGradeCombo.setBounds(889, 51, 64, 22);
		contentPane.add(quickFilterGradeCombo);
	}

	private void loadSongList(ActionEvent evt) {

		String statsXmls = songListLocationText.getText();

		if (statsXmls == null || statsXmls.isBlank()) {
			return;
		}

		try {
			loadData(statsXmls);
		} catch (JAXBException e) {
			e.printStackTrace();
		}

	}

	private void fillPlaysTree(Songlist songData, VolforceCalculator volforceCalculator) {
		PlayListTreeModel treeModel = new PlayListTreeModel(new DefaultMutableTreeNode("Songs"), songData);
		treeModel.buildTreeModel();

		playsTree.setModel(treeModel);

		createQuickFilters(volforceCalculator, treeModel);

	}

	@SuppressWarnings("rawtypes")
	private void createQuickFilters(VolforceCalculator volforceCalculator, PlayListTreeModel treeModel) {
		songSearchText.addKeyListener(new SongSearchkeyListerner(playsTree, treeModel));
		quickFilterHardButton.addActionListener(new LampFilterActionListener(playsTree, treeModel, Lamp.EX_CLEAR));
		quickFilterHardButton.addActionListener(e -> {
			if (((JToggleButton) e.getSource()).isSelected()) {
				quickFilterEXHardButton.setSelected(false);
				quickFilterUCButton.setSelected(false);
				quickFilterPUCButton.setSelected(false);
				quickFilterVFTop50.setSelected(false);
				quickFilterGradeCombo.setSelectedIndex(0);
			}
		});

		quickFilterEXHardButton
				.addActionListener(new LampFilterActionListener(playsTree, treeModel, Lamp.EX_HARD_CLEAR));
		quickFilterEXHardButton.addActionListener(e -> {
			if (((JToggleButton) e.getSource()).isSelected()) {
				quickFilterHardButton.setSelected(false);
				quickFilterUCButton.setSelected(false);
				quickFilterPUCButton.setSelected(false);
				quickFilterVFTop50.setSelected(false);
				quickFilterGradeCombo.setSelectedIndex(0);
			}
		});

		quickFilterUCButton.addActionListener(new LampFilterActionListener(playsTree, treeModel, Lamp.UC));
		quickFilterUCButton.addActionListener(e -> {
			if (((JToggleButton) e.getSource()).isSelected()) {
				quickFilterEXHardButton.setSelected(false);
				quickFilterHardButton.setSelected(false);
				quickFilterPUCButton.setSelected(false);
				quickFilterVFTop50.setSelected(false);
				quickFilterGradeCombo.setSelectedIndex(0);
			}
		});

		quickFilterPUCButton.addActionListener(new LampFilterActionListener(playsTree, treeModel, Lamp.PUC));
		quickFilterPUCButton.addActionListener(e -> {
			if (((JToggleButton) e.getSource()).isSelected()) {
				quickFilterEXHardButton.setSelected(false);
				quickFilterHardButton.setSelected(false);
				quickFilterUCButton.setSelected(false);
				quickFilterVFTop50.setSelected(false);
				quickFilterGradeCombo.setSelectedIndex(0);
			}
		});

		quickFilterVFTop50
				.addActionListener(new VolforceFilterActionListener(playsTree, treeModel, volforceCalculator));
		quickFilterVFTop50.addActionListener(e -> {
			if (((JToggleButton) e.getSource()).isSelected()) {
				quickFilterEXHardButton.setSelected(false);
				quickFilterHardButton.setSelected(false);
				quickFilterUCButton.setSelected(false);
				quickFilterPUCButton.setSelected(false);
				quickFilterGradeCombo.setSelectedIndex(0);
			}
		});

		quickFilterGradeCombo.addItemListener(new GradeFilterActionListener(playsTree, treeModel));
		quickFilterGradeCombo.addItemListener(e -> {
			if (((JComboBox) e.getSource()).getSelectedIndex() > 0) {
				quickFilterEXHardButton.setSelected(false);
				quickFilterHardButton.setSelected(false);
				quickFilterUCButton.setSelected(false);
				quickFilterPUCButton.setSelected(false);
				quickFilterVFTop50.setSelected(false);

			}
		});
	}
}
