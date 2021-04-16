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
    public static final boolean SCALE_MIN_DEFAULT = false;  // scale starts at 0, ELE795 default
    public static final int SCALE_MAX_DEFAULT = 3;			// max scale startup value, ELE795 default
    public static final int SCALE_MAX_MAX = 10;             // maximum scale available
    public static final boolean NORMALIZE_DEFAULT = false;  // do not normalize

    private JTable table;
    private JCheckBox minScaleCheckBox;
    private JComboBox<Integer> maxScaleComboBox;
    private JCheckBox normalizeCheckBox;
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
    private Action minScaleAction;
    private Action maxScaleAction;
    private Action normalizeAction;

    private EPP epp = null;

    public Mediator(MainView parent) {
        JFileChooser fileChooser = new JFileChooser();

        readAction = new ReadAction(parent, fileChooser);
        writeAction = new WriteAction(parent, fileChooser);
        exitAction = new ExitAction();
        aboutAction = new AboutAction(parent);
        minScaleAction = new MinScaleAction();
        maxScaleAction = new MaxScaleAction();
        normalizeAction = new NormalizeAction();

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

    public void registerMinScaleCheckBox(JCheckBox minScaleCheckBox) {
        this.minScaleCheckBox = minScaleCheckBox;
        minScaleCheckBox.setAction(minScaleAction);
    }

    public void registerMaxScaleSpinner(JComboBox<Integer> maxScaleComboBox) {
        this.maxScaleComboBox = maxScaleComboBox;
         maxScaleComboBox.setAction(maxScaleAction);
    }

    public void registerNormalizeCheckBox(JCheckBox normalizeCheckBox) {
        this.normalizeCheckBox = normalizeCheckBox;
        normalizeCheckBox.setAction(normalizeAction);
    }

    public void computeAndShow() {
        enforcesRules();

        epp = readAction.getEPP();
        if (epp == null) return;

        // perform epp calculations
        epp.setMinScaleIs1(minScaleCheckBox.isSelected());
        epp.setMaxScale((int) maxScaleComboBox.getSelectedItem());
        epp.setNormalize(normalizeCheckBox.isSelected());
        epp.compute();

        // set JTable grouping in white and gray
        CelRenderer cr = new CelRenderer(epp.getGrouping());
        table.setDefaultRenderer(Object.class, cr);

        // show epp in the MainView Jtable
        table.setModel(epp);
        setColumnWidth();
    }

    public void enforcesRules() {
        maxScaleAction.setEnabled(normalizeCheckBox.isSelected());
        writeAction.setEnabled(epp != null);
    }

    public void setColumnWidth() {
        int[] width = { 300, 200, 200, 100, 100, 100 };
        TableColumn column;
        for (int i = 0; i < Team.NB_FIELDS; i++) {
            column = table.getColumnModel().getColumn(i);
            column.setPreferredWidth(width[i]);
        }
    }


    private class MainViewListener extends WindowAdapter {
        @Override
        public void  windowOpened(WindowEvent e) {
            minScaleAction.setEnabled(true);
            minScaleCheckBox.setSelected(SCALE_MIN_DEFAULT);

            fillComboBox();
            maxScaleComboBox.setSelectedItem(SCALE_MAX_DEFAULT);
            maxScaleComboBox.setEditable(false);

            normalizeAction.setEnabled(true);
            normalizeCheckBox.setSelected(NORMALIZE_DEFAULT);

            writeAction.setEnabled(false);
        }

        private void fillComboBox() {
            int startIndex = (minScaleCheckBox.isSelected() ? 1 : 0) + 1;
            int endIndex = SCALE_MAX_MAX;
            for (int i = startIndex; i <= endIndex; i++) {
                maxScaleComboBox.addItem(i);
            }
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
            JOptionPane.showMessageDialog(parent, "EPP version 1.2\nAuteur: Vincent Lacasse");
        }
    }

    /**
     * Command class when min scale changes
     */
    public class MinScaleAction extends AbstractAction {
        private static final long serialVersionUID = 1L;

        public MinScaleAction() {
            super();
//            super("Barème ELE400 (valeur minimum du barème = 1)");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            computeAndShow();
        }
    }

    /**
     * Command class when max scale changes
     */
    public class MaxScaleAction extends AbstractAction {
        private static final long serialVersionUID = 1L;

        public MaxScaleAction() {
            super();
//            super("Valeur maximum du barème");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            computeAndShow();
        }
    }

    /**
     * Command class when normalization has changed
     */
    public class NormalizeAction extends AbstractAction {
        private static final long serialVersionUID = 1L;

        public NormalizeAction() {
            super();
//            super("Normaliser les notes sur 100 points");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            computeAndShow();
        }
    }

}
