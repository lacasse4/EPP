package main.java.View;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
 * CellRenderer class to modify the way information appears in the JTable
 * - floating number rounding
 * - team grouping
 */
public class CelRenderer extends JLabel implements TableCellRenderer {
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
