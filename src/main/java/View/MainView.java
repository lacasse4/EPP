package main.java.View;

import java.awt.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * MainView class. Main view of the EPP application
 * @author Vincent Lacasse
 */
public class MainView extends JFrame implements PropertyChangeListener {

	private static final long serialVersionUID = 1L;

	/**
	 * Create the frame.
	 */
	public MainView() {
		super("Évaluation par les pairs");

		final JPanel contentPane;
		final JPanel controlPanel;
		final JPanel paramPanel;
		final JPanel buttonPanel;
		final SpringLayout layout;
		final JScrollPane scrollPane;

		final JTable table;

		final JCheckBox minScaleCheckBox;
		final JComboBox<Integer> maxScaleComboBox;
		final JCheckBox normalizeCheckBox;
		final JButton readButton;
		final JButton writeButton;
		final JButton exitButton;

		final JMenuBar menuBar;
		final JMenu mnFile;
		final JMenu mnHelp;
		final JMenuItem readMenuItem;
		final JMenuItem writeMenuItem;
		final JMenuItem exitMenuItem;
		final JMenuItem aboutMenuItem;

		final JLabel minScaleLabel;
		final JLabel maxScaleLabel;
		final JLabel normalizeLabel;

		// Set frame and main panel
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1000, 500);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		// create menubar and menuitems
		menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		mnFile = new JMenu("Fichier");
		mnHelp = new JMenu("Aide");
		readMenuItem = new JMenuItem();
		writeMenuItem = new JMenuItem();
		exitMenuItem = new JMenuItem();
		aboutMenuItem = new JMenuItem();
		menuBar.add(mnFile);
		menuBar.add(mnHelp);
		mnFile.add(readMenuItem);
		mnFile.add(writeMenuItem);
		mnFile.addSeparator();
		mnFile.add(exitMenuItem);
		mnHelp.add(aboutMenuItem);

		// create table
		table = new JTable();
		table.setGridColor(Color.BLACK);
		table.setFillsViewportHeight(true);
		scrollPane = new JScrollPane(table);
		contentPane.add(scrollPane, BorderLayout.CENTER);

		// create control panel
		controlPanel = new JPanel();
		controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.PAGE_AXIS));
		contentPane.add(controlPanel, BorderLayout.SOUTH);

		// create param panel
		paramPanel = new JPanel();
		layout = new SpringLayout();
		paramPanel.setLayout(layout);
		controlPanel.add(paramPanel);
		controlPanel.add(new JSeparator());

		minScaleCheckBox = new JCheckBox();
		maxScaleComboBox = new JComboBox<Integer>();
		normalizeCheckBox = new JCheckBox();

		minScaleLabel = new JLabel("Barème ELE400 (valeur minimum du barème = 1) : ", JLabel.TRAILING);
		paramPanel.add(minScaleLabel);
		paramPanel.add(minScaleCheckBox);

		maxScaleLabel = new JLabel("Valeur maximum du barème : ", JLabel.TRAILING);
		paramPanel.add(maxScaleLabel);
		JPanel p = new JPanel();
		p.setLayout(new FlowLayout(FlowLayout.LEADING));
		p.add(maxScaleComboBox);
		paramPanel.add(p);

		normalizeLabel = new JLabel("Normaliser les notes sur 100 points : ", JLabel.TRAILING);
		paramPanel.add(normalizeLabel);
		paramPanel.add(normalizeCheckBox);

		SpringUtilities.makeCompactGrid(paramPanel, 3, 2, 6, 6, 6, 6);

		// create button panel
		buttonPanel = new JPanel();
		controlPanel.add(buttonPanel);
		readButton = new JButton();
		writeButton = new JButton();
		exitButton = new JButton();
		buttonPanel.add(readButton);
		buttonPanel.add(writeButton);
		buttonPanel.add(exitButton);

		// setup Mediator
		Mediator mediator = new Mediator(this);
		mediator.registerReadMenuItem(readMenuItem);
		mediator.registerWriteMenuItem(writeMenuItem);
		mediator.registerExitMenuItem(exitMenuItem);
		mediator.registerAboutMenuItem(aboutMenuItem);
		mediator.registerTable(table);
		mediator.registerMinScaleCheckBox(minScaleCheckBox);
		mediator.registerMaxScaleSpinner(maxScaleComboBox);
		mediator.registerNormalizeCheckBox(normalizeCheckBox);
		mediator.registerReadButton(readButton);
		mediator.registerWriteButton(writeButton);
		mediator.registerExitButton(exitButton);
	}

	public void propertyChange(PropertyChangeEvent evt) {

	}
}
