package main.java.View;

import java.awt.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

/**
 * MainView class. Main view of the EPP application
 * @author Vincent Lacasse
 */
public class MainView extends JFrame {

	private static final long serialVersionUID = 1L;

	/**
	 * Create the frame.
	 */
	public MainView() {
		super("Évaluation par les pairs");

		final JPanel contentPane = new JPanel();
		final JPanel commandPanel = new JPanel();
		final JPanel radioButtonPanel = new JPanel();
		final JPanel comboBoxPanel = new JPanel();
		final JPanel buttonPanel = new JPanel();

		final JTable table = new JTable();
		final JScrollPane tableScrollPane = new JScrollPane(table);

		final JRadioButton ELE400RadioButton = new JRadioButton("ELE400");
		final JRadioButton ELE795RadioButton = new JRadioButton("ELE795");
		final JRadioButton OtherRadioButton = new JRadioButton("Autre cours");
		final ButtonGroup radioButtonGroup = new ButtonGroup();
		final JLabel minScaleLabel = new JLabel("Valeur minimum du barème : ", JLabel.TRAILING);
		final JComboBox<Integer> minScaleComboBox= new JComboBox<Integer>();
		final JLabel maxScaleLabel = new JLabel("Valeur maximum du barème : ", JLabel.TRAILING);
		final JComboBox<Integer> maxScaleComboBox = new JComboBox<Integer>();
		final JButton readButton = new JButton();
		final JButton writeButton = new JButton();
		final JButton exitButton = new JButton();

		final JMenuBar menuBar = new JMenuBar();
		final JMenu mnFile = new JMenu("Fichier");
		final JMenu mnHelp = new JMenu("Aide");
		final JMenuItem readMenuItem = new JMenuItem();
		final JMenuItem writeMenuItem = new JMenuItem();
		final JMenuItem exitMenuItem = new JMenuItem();
		final JMenuItem aboutMenuItem = new JMenuItem();

		final Mediator mediator = new Mediator(this);

		// Set frame and main panel
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1000, 500);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		contentPane.add(tableScrollPane, BorderLayout.CENTER);
		contentPane.add(commandPanel, BorderLayout.SOUTH);

		// Set menu
		menuBar.add(mnFile);
		menuBar.add(mnHelp);
		mnFile.add(readMenuItem);
		mnFile.add(writeMenuItem);
		mnFile.addSeparator();
		mnFile.add(exitMenuItem);
		mnHelp.add(aboutMenuItem);
		setJMenuBar(menuBar);

		// Set table table
		table.setGridColor(Color.BLACK);
		table.setFillsViewportHeight(true);

		// Set command panel
		commandPanel.setLayout(new BoxLayout(commandPanel, BoxLayout.PAGE_AXIS));
		commandPanel.add(radioButtonPanel);
		commandPanel.add(comboBoxPanel);
		commandPanel.add(buttonPanel);

		// Set radio button panel
		radioButtonGroup.add(ELE400RadioButton);
		radioButtonGroup.add(ELE795RadioButton);
		radioButtonGroup.add(OtherRadioButton);
		radioButtonPanel.add(ELE400RadioButton);
		radioButtonPanel.add(ELE795RadioButton);
		radioButtonPanel.add(OtherRadioButton);
		radioButtonPanel.setBorder(new EtchedBorder());

		// Set comboBoxPanel panel
		comboBoxPanel.add(makePanel(minScaleLabel, minScaleComboBox));
		comboBoxPanel.add(makePanel(maxScaleLabel, maxScaleComboBox));
		comboBoxPanel.setBorder(new EtchedBorder());

		// Set button panel
		buttonPanel.add(readButton);
		buttonPanel.add(writeButton);
		buttonPanel.add(exitButton);

		// setup Mediator
		mediator.registerReadMenuItem(readMenuItem);
		mediator.registerWriteMenuItem(writeMenuItem);
		mediator.registerExitMenuItem(exitMenuItem);
		mediator.registerAboutMenuItem(aboutMenuItem);
		mediator.registerTable(table);
		mediator.registerELE400RadioButton(ELE400RadioButton);
		mediator.registerELE795RadioButton(ELE795RadioButton);
		mediator.registerOtherRadioButton(OtherRadioButton);
		mediator.registerMinScaleComboBox(minScaleComboBox);
		mediator.registerMaxScaleComboBox(maxScaleComboBox);
		mediator.registerReadButton(readButton);
		mediator.registerWriteButton(writeButton);
		mediator.registerExitButton(exitButton);
	}

	/**
	 * Makes the component use a reasonable width
	 * @param c1 - Component for which we want a limited width
	 * @return JPanel containing the Component
	 */
	private JPanel makePanel(Component c1, Component c2) {
		JPanel p = new JPanel();
		p.setLayout(new FlowLayout(FlowLayout.LEADING));
		p.add(c1);
		p.add(c2);
		return p;
	}
}
