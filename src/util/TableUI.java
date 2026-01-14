package util;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;

public class TableUI {

    private TableUI() {}

    public static void styleTable(JTable table) {
        table.setRowHeight(26);
        table.setFillsViewportHeight(true);
        table.setAutoCreateRowSorter(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        JTableHeader header = table.getTableHeader();
        header.setReorderingAllowed(false);
        header.setFont(header.getFont().deriveFont(Font.BOLD, 12f));
        table.setShowGrid(true);
        table.setIntercellSpacing(new Dimension(1, 1));
        table.setFont(table.getFont().deriveFont(12f));
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, col);

                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 245, 245));
                    c.setForeground(Color.BLACK);
                }
                if (c instanceof JComponent jc) {
                    String s = value == null ? "" : String.valueOf(value);
                    jc.setToolTipText(s.isEmpty() ? null : s);
                }
                setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 6)); // padding
                return c;
            }
        };

        table.setDefaultRenderer(Object.class, renderer);
        table.setDefaultRenderer(String.class, renderer);
    }

    public static JScrollPane wrapWithScroll(JTable table) {
        JScrollPane sp = new JScrollPane(table,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        sp.getViewport().setBackground(Color.WHITE);
        return sp;
    }

    public static void autoFitColumns(JTable table, int sampleRows, int minWidth, int maxWidth) {
        TableColumnModel columnModel = table.getColumnModel();
        JTableHeader header = table.getTableHeader();

        int rows = Math.min(table.getRowCount(), Math.max(0, sampleRows));

        for (int col = 0; col < table.getColumnCount(); col++) {
            TableColumn column = columnModel.getColumn(col);

            int width = minWidth;

            TableCellRenderer headerRenderer = header.getDefaultRenderer();
            Component headerComp = headerRenderer.getTableCellRendererComponent(
                    table, column.getHeaderValue(), false, false, -1, col);
            width = Math.max(width, headerComp.getPreferredSize().width + 16);

            for (int row = 0; row < rows; row++) {
                TableCellRenderer cellRenderer = table.getCellRenderer(row, col);
                Object value = table.getValueAt(row, col);
                Component comp = cellRenderer.getTableCellRendererComponent(table, value, false, false, row, col);
                width = Math.max(width, comp.getPreferredSize().width + 16);
                if (width >= maxWidth) { // cap
                    width = maxWidth;
                    break;
                }
            }

            column.setPreferredWidth(Math.min(Math.max(width, minWidth), maxWidth));
        }
    }
}
