package ch.sound.voltext.play.gui;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class ByDatePlaysDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ByDatePlaysDialog frame = new ByDatePlaysDialog();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public ByDatePlaysDialog() {
		setTitle("Plays by Date");
		setResizable(false);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 640, 480);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[] { 0, 0, 0, 0 };
		gbl_contentPane.rowHeights = new int[] { 0, 0, 0, 0 };
		gbl_contentPane.columnWeights = new double[] { 0.0, 1.0, 0.0, Double.MIN_VALUE };
		gbl_contentPane.rowWeights = new double[] { 0.0, 0.0, 1.0, Double.MIN_VALUE };
		contentPane.setLayout(gbl_contentPane);

		JButton previousYearButton = new JButton("<");
		GridBagConstraints gbc_previousYearButton = new GridBagConstraints();
		gbc_previousYearButton.insets = new Insets(0, 0, 5, 5);
		gbc_previousYearButton.gridx = 0;
		gbc_previousYearButton.gridy = 0;
		contentPane.add(previousYearButton, gbc_previousYearButton);

		JButton yearButton = new JButton(Integer.toString(LocalDate.now().getYear()));
		GridBagConstraints gbc_yearButton = new GridBagConstraints();
		gbc_yearButton.fill = GridBagConstraints.HORIZONTAL;
		gbc_yearButton.insets = new Insets(0, 0, 5, 5);
		gbc_yearButton.gridx = 1;
		gbc_yearButton.gridy = 0;
		contentPane.add(yearButton, gbc_yearButton);

		JButton nextYearButton = new JButton(">");
		GridBagConstraints gbc_nextYearButton = new GridBagConstraints();
		gbc_nextYearButton.insets = new Insets(0, 0, 5, 0);
		gbc_nextYearButton.gridx = 2;
		gbc_nextYearButton.gridy = 0;
		contentPane.add(nextYearButton, gbc_nextYearButton);

		JButton previousMonthButton = new JButton("<");
		GridBagConstraints gbc_previousMonthButton = new GridBagConstraints();
		gbc_previousMonthButton.insets = new Insets(0, 0, 5, 5);
		gbc_previousMonthButton.gridx = 0;
		gbc_previousMonthButton.gridy = 1;
		contentPane.add(previousMonthButton, gbc_previousMonthButton);

		JButton monthButton = new JButton(
				LocalDate.now().getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault()));
		GridBagConstraints gbc_monthButton = new GridBagConstraints();
		gbc_monthButton.fill = GridBagConstraints.HORIZONTAL;
		gbc_monthButton.insets = new Insets(0, 0, 5, 5);
		gbc_monthButton.gridx = 1;
		gbc_monthButton.gridy = 1;
		contentPane.add(monthButton, gbc_monthButton);

		JButton nextMonthButton = new JButton(">");
		GridBagConstraints gbc_nextMonthButton = new GridBagConstraints();
		gbc_nextMonthButton.insets = new Insets(0, 0, 5, 0);
		gbc_nextMonthButton.gridx = 2;
		gbc_nextMonthButton.gridy = 1;
		contentPane.add(nextMonthButton, gbc_nextMonthButton);

		createDaysPanel();
	}

	private void createDaysPanel() {
		JPanel daysPanel = new JPanel();
		daysPanel.setBackground(Color.WHITE);
		GridBagConstraints gbc_daysPanel = new GridBagConstraints();
		gbc_daysPanel.gridwidth = 3;
		gbc_daysPanel.insets = new Insets(0, 0, 0, 5);
		gbc_daysPanel.fill = GridBagConstraints.BOTH;
		gbc_daysPanel.gridx = 0;
		gbc_daysPanel.gridy = 2;
		contentPane.add(daysPanel, gbc_daysPanel);
		GridBagLayout gbl_daysPanel = new GridBagLayout();

		int daysColumnsWidth = (getWidth() / 7);
		int daysRowsHeight = (getHeight() / 6) - 10;

		gbl_daysPanel.columnWidths = new int[] { daysColumnsWidth, daysColumnsWidth, daysColumnsWidth, daysColumnsWidth,
				daysColumnsWidth, daysColumnsWidth, daysColumnsWidth };
		gbl_daysPanel.rowHeights = new int[] { 0, daysRowsHeight, daysRowsHeight, daysRowsHeight, daysRowsHeight,
				daysRowsHeight };
		gbl_daysPanel.columnWeights = new double[] { 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0 };
		gbl_daysPanel.rowWeights = new double[] { 0.0, 1.0, 1.0, 1.0, 1.0, 1.0 };
		daysPanel.setLayout(gbl_daysPanel);

		JLabel mondayLabel = new JLabel("M");
		mondayLabel.setHorizontalAlignment(SwingConstants.CENTER);
		mondayLabel.setBackground(Color.WHITE);
		mondayLabel.setFont(new Font("Tahoma", Font.BOLD, 11));
		mondayLabel.setBorder(new LineBorder(Color.BLACK, 2));

		GridBagConstraints gbc_mondayLabel = new GridBagConstraints();
		gbc_mondayLabel.fill = GridBagConstraints.BOTH;
		gbc_mondayLabel.insets = new Insets(0, 0, 5, 2);
		gbc_mondayLabel.gridx = 0;
		gbc_mondayLabel.gridy = 0;
		daysPanel.add(mondayLabel, gbc_mondayLabel);

		JLabel tuesdayLabel = new JLabel("T");
		tuesdayLabel.setHorizontalAlignment(SwingConstants.CENTER);
		tuesdayLabel.setFont(new Font("Tahoma", Font.BOLD, 11));
		tuesdayLabel.setBorder(new LineBorder(Color.BLACK, 2));

		GridBagConstraints gbc_tuesdayLabel = new GridBagConstraints();
		gbc_tuesdayLabel.fill = GridBagConstraints.BOTH;
		gbc_tuesdayLabel.insets = new Insets(0, 0, 5, 2);
		gbc_tuesdayLabel.gridx = 1;
		gbc_tuesdayLabel.gridy = 0;
		daysPanel.add(tuesdayLabel, gbc_tuesdayLabel);

		JLabel wednesdayLabel = new JLabel("W");
		wednesdayLabel.setHorizontalAlignment(SwingConstants.CENTER);
		wednesdayLabel.setFont(new Font("Tahoma", Font.BOLD, 11));
		wednesdayLabel.setBorder(new LineBorder(Color.BLACK, 2));

		GridBagConstraints gbc_wednesdayLabel = new GridBagConstraints();
		gbc_wednesdayLabel.fill = GridBagConstraints.BOTH;
		gbc_wednesdayLabel.insets = new Insets(0, 0, 5, 2);
		gbc_wednesdayLabel.gridx = 2;
		gbc_wednesdayLabel.gridy = 0;
		daysPanel.add(wednesdayLabel, gbc_wednesdayLabel);

		JLabel thursdayLabel = new JLabel("T");
		thursdayLabel.setHorizontalAlignment(SwingConstants.CENTER);
		thursdayLabel.setFont(new Font("Tahoma", Font.BOLD, 11));
		thursdayLabel.setBorder(new LineBorder(Color.BLACK, 2));

		GridBagConstraints gbc_thursdayLabel = new GridBagConstraints();
		gbc_thursdayLabel.fill = GridBagConstraints.BOTH;
		gbc_thursdayLabel.insets = new Insets(0, 0, 5, 2);
		gbc_thursdayLabel.gridx = 3;
		gbc_thursdayLabel.gridy = 0;
		daysPanel.add(thursdayLabel, gbc_thursdayLabel);

		JLabel fridayLabel = new JLabel("F");
		fridayLabel.setHorizontalAlignment(SwingConstants.CENTER);
		fridayLabel.setFont(new Font("Tahoma", Font.BOLD, 11));
		fridayLabel.setBorder(new LineBorder(Color.BLACK, 2));

		GridBagConstraints gbc_fridayLabel = new GridBagConstraints();
		gbc_fridayLabel.fill = GridBagConstraints.BOTH;
		gbc_fridayLabel.insets = new Insets(0, 0, 5, 2);
		gbc_fridayLabel.gridx = 4;
		gbc_fridayLabel.gridy = 0;
		daysPanel.add(fridayLabel, gbc_fridayLabel);

		JLabel saturdayLabel = new JLabel("S");
		saturdayLabel.setHorizontalAlignment(SwingConstants.CENTER);
		saturdayLabel.setFont(new Font("Tahoma", Font.BOLD, 11));
		saturdayLabel.setBorder(new LineBorder(Color.BLACK, 2));

		GridBagConstraints gbc_saturdayLabel = new GridBagConstraints();
		gbc_saturdayLabel.fill = GridBagConstraints.BOTH;
		gbc_saturdayLabel.insets = new Insets(0, 0, 5, 2);
		gbc_saturdayLabel.gridx = 5;
		gbc_saturdayLabel.gridy = 0;
		daysPanel.add(saturdayLabel, gbc_saturdayLabel);

		JLabel sundayLabel = new JLabel("S");
		sundayLabel.setHorizontalAlignment(SwingConstants.CENTER);
		sundayLabel.setFont(new Font("Tahoma", Font.BOLD, 11));
		sundayLabel.setBorder(new LineBorder(Color.BLACK, 2));

		GridBagConstraints gbc_sundayLabel = new GridBagConstraints();
		gbc_sundayLabel.insets = new Insets(0, 0, 5, 0);
		gbc_sundayLabel.fill = GridBagConstraints.BOTH;
		gbc_sundayLabel.gridx = 6;
		gbc_sundayLabel.gridy = 0;
		daysPanel.add(sundayLabel, gbc_sundayLabel);

		for (int y = 1; y <= 5; y++) {
			for (int x = 0; x < 7; x++) {

				JPanel dayPanel = new JPanel();
				dayPanel.setName(Integer.toString((x + 1) * y));
				dayPanel.setBorder(new LineBorder(Color.BLACK, 1));
				GridBagConstraints gbc_panel = new GridBagConstraints();
				gbc_panel.insets = new Insets(0, 0, 2, 2);
				gbc_panel.fill = GridBagConstraints.BOTH;

				gbc_panel.gridx = x;
				gbc_panel.gridy = y;

				daysPanel.add(dayPanel, gbc_panel);
			}
		}

	}
}
