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

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
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
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

import ch.sound.voltext.play.model.Difficulty;
import ch.sound.voltext.play.model.Lamp;
import ch.sound.voltext.play.model.PlayLog.Grade;
import ch.sound.voltext.play.model.Songlist;
import ch.sound.voltext.play.statistic.FileDAO;
import ch.sound.voltext.play.statistic.PlayStatsCalculator;
import ch.sound.voltext.play.statistic.SongDataUtils;
import ch.sound.voltext.play.statistic.VolforceCalculator;
import jakarta.xml.bind.JAXBException;

public class SoundVoltexStatsFrame extends JFrame {

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
	private JButton quickFilterVFTop50;
	private JButton quickFilterPUCButton;
	private JButton quickFilterUCButton;
	private JButton quickFilterHardButton;
	private JComboBox<Grade> quickFilterGradeCombo;
	private ChartPanel volforceChartPanel;

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

		setTitle("SoundVoltex Statistics Viewer");
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

		overallPlayStatsText.setText("Played " + playsCalculator.getTotalSongs() + " songs with "
				+ playsCalculator.getTotalPlays() + " total plays in "
				+ Duration
						.between(SongDataUtils.getFirstDate(songData), SongDataUtils.getLastDate(songData).plusDays(1))
						.toDays()
				+ " days");
		overallPlayStatsText.updateUI();


		printDifficultyStatistics(playsCalculator);
		printRatingStatistics(playsCalculator);
		printLampStatistics(playsCalculator);
		printGradeStatistics(playsCalculator);
		printVolforce(songData);
		fillSongTree(songData);
		

	}

	private void printVolforce(Songlist songData) {
		VolforceCalculator volforceCalculator = new VolforceCalculator(songData);
		volforcePanel.setBorder(new TitledBorder("Volforce: "+volforceCalculator.calculateCurrent()));
		
		TimeSeries series = new TimeSeries("Temperature");
		volforceCalculator.calculateByDate().forEach((k,v) -> series.add(new Day(k.getDayOfMonth(),k.getMonth().getValue(),k.getYear()), v.doubleValue()));
		
		XYDataset dataset =  new TimeSeriesCollection(series);
		
		JFreeChart chart = ChartFactory.createTimeSeriesChart("Volforce gains", "Date", "Volforce", dataset, false,true,false);
		((DateAxis) ((XYPlot)chart.getPlot()).getDomainAxis()).setDateFormatOverride(new SimpleDateFormat("yyyy-MM"));
		volforceChartPanel.setChart(chart);
		volforceChartPanel.updateUI();
	}

	private void printDifficultyStatistics(PlayStatsCalculator playsCalculator) {
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
		playsCalculator.getTotalByRating().entrySet().stream()
				.sorted((g1, g2) -> Integer.compare(g1.getKey(), g2.getKey())).forEach(e -> {
					ratingsTextArea.append("Level " + e.getKey() + ": " + e.getValue());
					ratingsTextArea.append(System.lineSeparator());
				});

	}
	
	private  void printLampStatistics(PlayStatsCalculator playsCalculator) {
		lampsTextArea.append(Lamp.FAILED.getName() + ": " + playsCalculator.getTotalByLamp(Lamp.FAILED));
		lampsTextArea.append(System.lineSeparator());
		
		lampsTextArea.append(Lamp.CLEAR.getName() + ": " + playsCalculator.getTotalByLamp(Lamp.CLEAR));
		lampsTextArea.append(System.lineSeparator());
		
		lampsTextArea.append(Lamp.EX_CLEAR.getName() + ": " + playsCalculator.getTotalByLamp(Lamp.EX_CLEAR));
		lampsTextArea.append(System.lineSeparator());
		
		lampsTextArea.append(Lamp.UC.getName() + ": " + playsCalculator.getTotalByLamp(Lamp.UC));
		lampsTextArea.append(System.lineSeparator());
		
		lampsTextArea.append(Lamp.PUC.getName() + ": " + playsCalculator.getTotalByLamp(Lamp.PUC));
	}
	
	private void printGradeStatistics(PlayStatsCalculator playsCalculator) {

		playsCalculator.getTotalByGrade().entrySet().stream()
				.sorted((g1, g2) -> Integer.compare(g1.getKey().ordinal(), g2.getKey().ordinal()))
				.forEach(e -> {
					gradesTextArea.append(e.getKey().getName() + ": " + e.getValue());
					gradesTextArea.append(System.lineSeparator());
				});
	}

	private void createComponents() {

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel songListLabel = new JLabel("Song List Location");
		songListLabel.setBounds(10, 11, 92, 14);
		contentPane.add(songListLabel);

		songListLocationText = new JTextField();
		songListLocationText.setBounds(112, 8, 750, 20);
		contentPane.add(songListLocationText);
		songListLocationText.setColumns(10);

		JButton browseSongListButton = new JButton("Browse...");
		browseSongListButton.setBounds(872, 7, 89, 23);
		browseSongListButton.addActionListener(new BrowseSongListAction(songListLocationText));
		contentPane.add(browseSongListButton);

		JButton loadSongListButton = new JButton("Load");
		loadSongListButton.setBounds(971, 7, 89, 23);
		loadSongListButton.addActionListener(evt -> loadSongList(evt));
		contentPane.add(loadSongListButton);

		JLabel overallPlayStatsLabel = new JLabel("Overal Stats:");
		overallPlayStatsLabel.setFont(new Font("Tahoma", Font.PLAIN, 16));
		overallPlayStatsLabel.setBounds(20, 50, 92, 20);
		contentPane.add(overallPlayStatsLabel);

		overallPlayStatsText = new JLabel();
		overallPlayStatsText.setBackground(new Color(255, 255, 255));
		overallPlayStatsText.setFont(new Font("Tahoma", Font.PLAIN, 16));
		overallPlayStatsText.setBounds(112, 50, 674, 20);
		contentPane.add(overallPlayStatsText);

		JSeparator separator = new JSeparator();
		separator.setBounds(10, 35, 1281, 9);
		contentPane.add(separator);

		volforcePanel = new JPanel();
		volforcePanel.setBounds(10, 289, 776, 424);
		volforcePanel.setBorder(new TitledBorder("Volforce: "));
		contentPane.add(volforcePanel);
		volforcePanel.setLayout(null);
		
		volforceChartPanel = new ChartPanel((JFreeChart) null);
		volforceChartPanel.setMouseWheelEnabled(true);
		volforceChartPanel.setFillZoomRectangle(false);
		volforceChartPanel.setBounds(10, 17, 756, 396);
		volforcePanel.add(volforceChartPanel);

		JPanel statsPanel = new JPanel();
		statsPanel.setBounds(10, 81, 777, 202);
		contentPane.add(statsPanel);
		statsPanel.setLayout(null);

		JPanel dificultyPanel = new JPanel();
		dificultyPanel.setBounds(0, 11, 190, 188);
		statsPanel.add(dificultyPanel);
		dificultyPanel.setBorder(new TitledBorder("Difficultiess"));
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
		ratingPanel.setBorder(new TitledBorder("Ratings"));
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
		lampPanel.setBorder(new TitledBorder("Lamps"));
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
		gradePanel.setBorder(new TitledBorder("Grades"));
		gradePanel.setLayout(null);

		JScrollPane gradesScrollPane = new JScrollPane();
		gradesScrollPane.setBounds(10, 17, 170, 160);
		gradePanel.add(gradesScrollPane);

		gradesTextArea = new JTextArea();
		gradesScrollPane.setViewportView(gradesTextArea);
		gradesTextArea.setEditable(false);
		gradesTextArea.setBorder(new BevelBorder(BevelBorder.LOWERED));

		JPanel playsListPanel = new JPanel();
		playsListPanel.setBounds(794, 91, 500, 622);
		playsListPanel.setBorder(new TitledBorder("Plays"));
		contentPane.add(playsListPanel);
		playsListPanel.setLayout(null);

		JScrollPane playsScrollPane = new JScrollPane();
		playsScrollPane.setBounds(10, 18, 480, 593);
		playsListPanel.add(playsScrollPane);

		playsTree = new JTree();
		playsScrollPane.setViewportView(playsTree);
		playsTree.setModel(new DefaultTreeModel(new DefaultMutableTreeNode("Songs")));
		playsTree.setBorder(new BevelBorder(BevelBorder.LOWERED));
		
		songSearchText = new JTextField();
		songSearchText.setBounds(997, 75, 294, 20);
		contentPane.add(songSearchText);
		songSearchText.setColumns(10);
		
		
		JLabel lblNewLabel = new JLabel("Search Song:");
		lblNewLabel.setBounds(929, 78, 64, 14);
		contentPane.add(lblNewLabel);
		
		quickFilterHardButton = new JButton("HARD");
		quickFilterHardButton.setBounds(1202, 51, 89, 23);
		contentPane.add(quickFilterHardButton);
		
		quickFilterUCButton = new JButton("UC");
		quickFilterUCButton.setBounds(1113, 51, 89, 23);
		contentPane.add(quickFilterUCButton);
		
		quickFilterPUCButton = new JButton("PUC");
		quickFilterPUCButton.setBounds(1023, 51, 89, 23);
		contentPane.add(quickFilterPUCButton);
		
		JLabel lblNewLabel_1 = new JLabel("Quick Filters:");
		lblNewLabel_1.setBounds(796, 55, 64, 14);
		contentPane.add(lblNewLabel_1);
		
		quickFilterVFTop50 = new JButton("VF Top 50");
		quickFilterVFTop50.setBounds(933, 51, 89, 23);
		contentPane.add(quickFilterVFTop50);
		
		quickFilterGradeCombo = new JComboBox<>();
		quickFilterGradeCombo.setModel(new DefaultComboBoxModel<>(Grade.values()));
		quickFilterGradeCombo.setBounds(869, 51, 64, 22);
		contentPane.add(quickFilterGradeCombo);
	}

	private void loadSongList(ActionEvent evt) {
		
		String statsXmls = songListLocationText.getText();
		
		if(statsXmls == null || statsXmls.isBlank()) {
			return;
		}
		
		try {
			loadData(statsXmls);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		
	}

	private void fillSongTree(Songlist songData) {
		PlayListTreeModel treeModel = new PlayListTreeModel(new DefaultMutableTreeNode("Songs"),songData);
		treeModel.buildTreeModel();
		
		playsTree.setModel(treeModel);
		
		songSearchText.addKeyListener(new SongSearchkeyListerner(playsTree,treeModel));
		quickFilterHardButton.addActionListener(new LampFilterAction(playsTree,treeModel,Lamp.EX_CLEAR));
		quickFilterUCButton.addActionListener(new LampFilterAction(playsTree,treeModel,Lamp.UC));
		quickFilterPUCButton.addActionListener(new LampFilterAction(playsTree,treeModel,Lamp.PUC));
		quickFilterVFTop50.addActionListener(new VolforceFilterAction(playsTree,treeModel));
		quickFilterGradeCombo.addItemListener(new GradeFilterAction(playsTree,treeModel));
	}
}
