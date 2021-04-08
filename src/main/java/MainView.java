package main.java;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

/**
 * MainView class. Main view and launcher of the EPP application
 * @author Vincent Lacasse
 */
public class MainView extends JFrame {

	private static final long serialVersionUID = 1L;
	private JTable table;
	private Action readAction;
	private Action writeAction;
	private Action exitAction;
	private Action aboutAction;
	private JFileChooser fileChooser = new JFileChooser();

	private EPP epp;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainView frame = new MainView();
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
	public MainView() {
		super("Évaluation par les pairs");

		final JPanel contentPane;
		final JButton btnRead;
		final JButton btnWrite;
		final JButton btnExit;
		final JMenuBar menuBar;
		final JMenu mnFile;
		final JMenu mnHelp;
		final JMenuItem miRead;
		final JMenuItem miWrite;
		final JMenuItem miExit;
		final JMenuItem miAbout;

		// Set frame and main panel
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1000, 500);
		contentPane = new JPanel();
//		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		// Set button panel
		JPanel buttonPanel = new JPanel();
		contentPane.add(buttonPanel, BorderLayout.SOUTH);

		// create actions
		readAction = new ReadAction();
		writeAction = new WriteAction();
		exitAction = new ExitAction();
		aboutAction = new AboutAction();

		// create button and link to actions
		btnRead = new JButton(readAction);
		btnWrite = new JButton(writeAction);
		btnExit = new JButton(exitAction);

		// add buttons to button panels
		buttonPanel.add(btnRead);
		buttonPanel.add(btnWrite);
		buttonPanel.add(btnExit);

		// setup menu and link to actions
		menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		mnFile = new JMenu("Fichier");
		mnHelp = new JMenu("Aide");
		miRead = new JMenuItem(readAction);
		miWrite = new JMenuItem(writeAction);
		miExit = new JMenuItem(exitAction);
		miAbout = new JMenuItem(aboutAction);
		menuBar.add(mnFile);
		menuBar.add(mnHelp);
		mnFile.add(miRead);
		mnFile.add(miWrite);
		mnFile.addSeparator();
		mnFile.add(miExit);
		mnHelp.add(miAbout);

		// set table
		table = new JTable();
		table.setGridColor(Color.BLACK);
		JScrollPane scrollPane = new JScrollPane(table);
		contentPane.add(scrollPane, BorderLayout.CENTER);
	}

	/**
	 * Command class to read the EPP file (Export des evaluations, sans multiligne)
	 */
	private class ReadAction extends AbstractAction {
		private static final long serialVersionUID = 1L;
		FileFilter filter = null;

		public ReadAction() {
			super("Lire CSV");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			int option;

			if (filter == null) {
				filter = new FileNameExtensionFilter("Fichier CSV", "csv");
				fileChooser.setFileFilter(filter);
			}
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			fileChooser.setSelectedFile(null);

			option = fileChooser.showOpenDialog(MainView.this);

			if (option == JFileChooser.APPROVE_OPTION) {   
				String CSVFileName = fileChooser.getSelectedFile().getPath();

				// populate the JTable
				epp = EPPBuilder.build(CSVFileName);
				if (epp == null) return;

				// perform eep calculations
				epp.compute();

				// transform epp information to be displayed in a JTable
				table.setModel(createTableModel());

				// Show grouping in white and gray in the JTable
				CelRenderer cr = new CelRenderer(epp.getGrouping());
				table.setDefaultRenderer(Object.class, cr);

				// Enable the Write action
				writeAction.setEnabled(true);
			}
		}

		private TableModel createTableModel() {
			Object[] header = epp.getHeader();
			Object[][] tableData = epp.getTableData();
			return new DefaultTableModel(tableData, header);
		}		
	}


	/**
	 * Command class to write the EPP information to a XLSX file
	 */
	private class WriteAction extends AbstractAction {
		private static final long serialVersionUID = 1L;
		FileFilter filter = null;

		public WriteAction() {
			super("Écrire XLSX");
			setEnabled(false);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			int option;

			File oldFile = fileChooser.getSelectedFile();
			fileChooser.setSelectedFile(changeExtension(oldFile, "xlsx"));
			fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			if (filter == null) {
				FileFilter filter = new FileNameExtensionFilter("Fichier XLSX", "xlsx");
				fileChooser.setFileFilter(filter);
			}

			option = fileChooser.showSaveDialog(MainView.this);

			if (option == JFileChooser.APPROVE_OPTION) {   
				String XLSXFileName = fileChooser.getSelectedFile().getPath();
				if (ExcelFileWriter.write(XLSXFileName, epp)) {
					JOptionPane.showMessageDialog(MainView.this, "Le fichier " + XLSXFileName + " a été sauvegardé.");
				}
			}
		}

		// from https://stackoverflow.com/questions/12209801/how-to-change-file-extension-at-runtime-in-java
		private File changeExtension(File f, String newExtension) {
			int i = f.getName().lastIndexOf('.');
			String name = f.getName().substring(0,i);
			return new File(f.getParent(), name + "." + newExtension);
		}
	}

	/**
	 * Command class to exit the application
	 */
	private class ExitAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public ExitAction() {
			super("Sortir");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			System.exit(0);
		}
	}

	/**
	 * Command class to show the About dialog
	 */
	private class AboutAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public AboutAction() {
			super("A propos");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			JOptionPane.showMessageDialog(MainView.this, "EPP version 1.0\nAuteur: Vincent Lacasse");
		}
	}

	/**
	 * CellRenderer class to modify the way information appears in the JTable
	 * - floating number rounding
	 * - team grouping
	 */
	private class CelRenderer extends JLabel implements TableCellRenderer {
		private static final long serialVersionUID = 1L;
		private boolean[] grouping;

		public CelRenderer(boolean[] grouping) {
			this.grouping = grouping;
		}

		public Component getTableCellRendererComponent(JTable table, Object value, 
				boolean isSelected, boolean hasFocus, int row, int column) {

			if (value instanceof Double) {
				setHorizontalAlignment(SwingConstants.TRAILING);
				setText(String.format("%.2f", value));
			}
			else {
				setHorizontalAlignment(SwingConstants.LEADING);
				setText((String)value);
			}

			if (grouping[row]) {
				setBackground(Color.GRAY);
				setForeground(table.getSelectionForeground());
			} else {
				setBackground(table.getBackground());
				setForeground(table.getForeground());
			}
			setOpaque(true);
			return this;
		}
	}
}
