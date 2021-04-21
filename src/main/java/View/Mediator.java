package main.java.View;

import main.java.EPP.EPP;
import main.java.EPP.Team;
import main.java.IO.EPPReader;
import main.java.IO.ExcelFileWriter;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

public class Mediator {
    public static final int SCALE_MIN_DEFAULT = 0;          // scale starts at 0, ELE795 default
    public static final int SCALE_MAX_DEFAULT = 3;			// max scale startup value, ELE795 default
    public static final int SCALE_MAX_MAX = 10;             // maximum scale available

    private JTable table;
    private JRadioButton ELE400RadioButton;
    private JRadioButton ELE795RadioButton;
    private JRadioButton OtherRadioButton;
    private JComboBox<Integer> minScaleComboBox;
    private JComboBox<Integer> maxScaleComboBox;
    private JButton readButton;
    private JButton writeButton;
    private JButton exitButton;
    private JMenuItem readMenuItem;
    private JMenuItem writeMenuItem;
    private JMenuItem exitMenuItem;
    private JMenuItem aboutMenuItem;

    private WindowListener windowListener;
    private ReadAction readAction;
    private Action writeAction;
    private Action exitAction;
    private Action aboutAction;

    private EPP epp = null;

    public Mediator(MainView parent) {
        JFileChooser fileChooser = new JFileChooser();

        readAction = new ReadAction(parent, fileChooser);
        writeAction = new WriteAction(parent, fileChooser);
        exitAction = new ExitAction();
        aboutAction = new AboutAction(parent);

        windowListener = new MainViewListener();
        parent.addWindowListener(windowListener);
    }

    public void registerTable(JTable table) {
        this.table = table;
    }

    public void registerReadButton(JButton readButton) {
        this.readButton = readButton;
        readButton.setAction(readAction);
    }

    public void registerWriteButton(JButton writeButton) {
        this.writeButton = writeButton;
        writeButton.setAction(writeAction);
    }

    public void registerExitButton(JButton exitButton) {
        this.exitButton = exitButton;
        exitButton.setAction(exitAction);
    }

    public void registerReadMenuItem(JMenuItem readMenuItem) {
        this.readMenuItem = readMenuItem;
        readMenuItem.setAction(readAction);
    }

    public void registerWriteMenuItem(JMenuItem writeMenuItem) {
        this.writeMenuItem = writeMenuItem;
        writeMenuItem.setAction(writeAction);
    }

    public void registerExitMenuItem(JMenuItem exitMenuItem) {
        this.exitMenuItem = exitMenuItem;
        exitMenuItem.setAction(exitAction);
    }

    public void registerAboutMenuItem(JMenuItem aboutMenuItem) {
        this.aboutMenuItem = aboutMenuItem;
        aboutMenuItem.setAction(aboutAction);
    }

    public void registerELE400RadioButton(JRadioButton ELE400RadioButton) {
        this.ELE400RadioButton = ELE400RadioButton;
        ELE400RadioButton.addActionListener(l ->computeAndShow());
    }

    public void registerELE795RadioButton(JRadioButton ELE795RadioButton) {
        this.ELE795RadioButton = ELE795RadioButton;
        ELE795RadioButton.addActionListener(l ->computeAndShow());
    }

    public void registerOtherRadioButton(JRadioButton OtherRadioButton) {
        this.OtherRadioButton = OtherRadioButton;
        OtherRadioButton.addActionListener(l ->computeAndShow());
    }

    public void registerMinScaleComboBox(JComboBox<Integer> minScaleComboBox) {
        this.minScaleComboBox = minScaleComboBox;
        minScaleComboBox.addActionListener(l ->computeAndShow());
    }

    public void registerMaxScaleComboBox(JComboBox<Integer> maxScaleComboBox) {
        this.maxScaleComboBox = maxScaleComboBox;
         maxScaleComboBox.addActionListener(l ->computeAndShow());
    }

    /**
     * Must be call when data must be refreshed
     * - when one of the controls on the MainView has changed
     * - when a new file is read
     */
    public void computeAndShow() {
        enforcesRules();

        epp = readAction.getEPP();
        if (epp == null) return;

        // perform epp calculations
        int minScale = (int) minScaleComboBox.getSelectedItem();
        int maxScale = (int) maxScaleComboBox.getSelectedItem();
        epp.compute(minScale, maxScale);

        // set JTable grouping in white and gray
        CelRenderer cr = new CelRenderer(epp.getGrouping());
        table.setDefaultRenderer(Object.class, cr);

        // show epp in the MainView Jtable
        table.setModel(epp);
        setColumnWidth();
    }

    /**
     * Enforces screen mediator rules
     */
    public void enforcesRules() {
        if (ELE400RadioButton.isSelected()) {
            minScaleComboBox.setSelectedItem(1);
            minScaleComboBox.setEnabled(false);
            maxScaleComboBox.setSelectedItem(5);
            maxScaleComboBox.setEnabled(false);
        }
        else if (ELE795RadioButton.isSelected()) {
            minScaleComboBox.setSelectedItem(0);
            minScaleComboBox.setEnabled(false);
            maxScaleComboBox.setSelectedItem(3);
            maxScaleComboBox.setEnabled(false);
        }
        else {
            // OtherRadioButton is selected
            minScaleComboBox.setEnabled(true);
            maxScaleComboBox.setEnabled(true);
        }

        writeAction.setEnabled(epp != null);
    }

    /**
     * Sets Table column width at startup
     */
    public void setColumnWidth() {
        int[] width = { 300, 200, 200, 100, 100, 100 };
        TableColumn column;
        for (int i = 0; i < Team.NB_FIELDS; i++) {
            column = table.getColumnModel().getColumn(i);
            column.setPreferredWidth(width[i]);
        }
    }

    /**
     * Provides initialisation upon display of the MainView
     */
    private class MainViewListener extends WindowAdapter {
        @Override
        public void  windowOpened(WindowEvent e) {
            ELE400RadioButton.setSelected(false);
            ELE795RadioButton.setSelected(true);
            OtherRadioButton.setSelected(false);

            fillMinScaleComboBox();
            minScaleComboBox.setSelectedItem(SCALE_MIN_DEFAULT);
            minScaleComboBox.setEditable(false);
            minScaleComboBox.setEnabled(false);

            fillMaxScaleComboBox();
            maxScaleComboBox.setSelectedItem(SCALE_MAX_DEFAULT);
            maxScaleComboBox.setEditable(false);
            maxScaleComboBox.setEnabled(false);

            writeAction.setEnabled(false);
        }

        private void fillMaxScaleComboBox() {
            int startIndex = (int)minScaleComboBox.getSelectedItem() + 1;
            int endIndex = SCALE_MAX_MAX;
            for (int i = startIndex; i <= endIndex; i++) {
                maxScaleComboBox.addItem(i);
            }
        }

        private void fillMinScaleComboBox() {
            minScaleComboBox.addItem(0);
            minScaleComboBox.addItem(1);
        }

    }

    /**
     * Command class to read the EPP file (Export des evaluations, sans multiligne)
     */
    private class ReadAction extends AbstractAction {
        private static final long serialVersionUID = 1L;
        private FileFilter filter = null;
        private Component parent;
        private JFileChooser fileChooser;
        private EPP epp = null;

        public ReadAction(Component parent, JFileChooser fileChooser) {
            super("Lire CSV");
            this.parent = parent;
            this.fileChooser = fileChooser;
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

            option = fileChooser.showOpenDialog(parent);

            if (option == JFileChooser.APPROVE_OPTION) {
                String CSVFileName = fileChooser.getSelectedFile().getPath();

                // read CSV file and create and epp structure
                epp = EPPReader.read(CSVFileName);
                if (epp == null) return;

                computeAndShow();
            }
        }

        public EPP getEPP() {
            return epp;
        }
    }

    /**
     * Command class to write the EPP information to a XLSX file
     */
    public class WriteAction extends AbstractAction {
        private static final long serialVersionUID = 1L;
        FileFilter filter = null;
        JFileChooser fileChooser;
        Component parent;

        public WriteAction(Component parent, JFileChooser fileChooser) {
            super("Écrire XLSX");
            this.fileChooser = fileChooser;
            this.parent = parent;
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

            option = fileChooser.showSaveDialog(parent);

            if (option == JFileChooser.APPROVE_OPTION) {
                String XLSXFileName = fileChooser.getSelectedFile().getPath();
                if (ExcelFileWriter.write(XLSXFileName, epp)) {
                    JOptionPane.showMessageDialog(parent, "Le fichier " + XLSXFileName + " a été sauvegardé.");
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
    public class ExitAction extends AbstractAction {
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
    public class AboutAction extends AbstractAction {
        private static final long serialVersionUID = 1L;
        Component parent;

        public AboutAction(Component parent) {
            super("A propos");
            this.parent = parent;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            JOptionPane.showMessageDialog(parent, "EPP version 1.3\nAuteur: Vincent Lacasse");
        }
    }
}
