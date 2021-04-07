package main.java;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JTable;
import javax.swing.SwingConstants;

public class MainView extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTable table;
	private JButton btnRead;
	private JButton btnWrite;
	private JButton btnExit;
	JFileChooser fileChooser = new JFileChooser();

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
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 800, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		JPanel buttonPanel = new JPanel();
		contentPane.add(buttonPanel, BorderLayout.SOUTH);

		btnRead = new JButton(new ReadAction());
		buttonPanel.add(btnRead);

		btnWrite = new JButton(new WriteAction());
		buttonPanel.add(btnWrite);

		btnExit = new JButton("Sortir");
		btnExit.addActionListener(l->System.exit(0));
		buttonPanel.add(btnExit);

		table = new JTable();
		table.setGridColor(Color.BLACK);
		JScrollPane scrollPane = new JScrollPane(table);
		contentPane.add(scrollPane, BorderLayout.CENTER);
	}

	/**
	 * Command class to read the EPP file (Export des evaluation, sans multiligne)
	 */
	private class ReadAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public ReadAction() {
			super("Lire CSV");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			int option;

			FileFilter filter = new FileNameExtensionFilter("Fichier CSV", "csv");
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			fileChooser.setFileFilter(filter);
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

				// Enable the Write button
				btnWrite.getAction().setEnabled(true);
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
			FileFilter filter = new FileNameExtensionFilter("Fichier XLSX", "xlsx");
			fileChooser.setFileFilter(filter);

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
	 * CellRenderer modifies the way information appears in the JTable
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
				setText(String.format("%.2f", (Double)value));
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
