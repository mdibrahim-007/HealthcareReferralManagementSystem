package view;

import util.CsvIO;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class GenericCsvCrudPanel extends JPanel {

    private final Path sourceCsv;
    private final Path outputCsv;

    private final GenericCsvTableModel tableModel = new GenericCsvTableModel();
    private final JTable table = new JTable(tableModel);

    private String[] header = new String[0];

    public GenericCsvCrudPanel(String title, Path sourceCsv, Path outputCsv) {
        this.sourceCsv = sourceCsv;
        this.outputCsv = outputCsv;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JLabel lbl = new JLabel(title);
        lbl.setFont(lbl.getFont().deriveFont(Font.BOLD, 14f));

        JButton btnLoad = new JButton("Load");
        JButton btnAdd = new JButton("Add");
        JButton btnEdit = new JButton("Edit");
        JButton btnDelete = new JButton("Delete");
        JButton btnSave = new JButton("Save to Output");

        for (JButton b : new JButton[]{btnLoad, btnAdd, btnEdit, btnDelete, btnSave}) {
            b.setFocusPainted(false);
        }

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        top.add(lbl);
        top.add(Box.createHorizontalStrut(12));
        top.add(btnLoad);
        top.add(btnAdd);
        top.add(btnEdit);
        top.add(btnDelete);
        top.add(btnSave);

        add(top, BorderLayout.NORTH);
        table.setRowHeight(26);
        table.setFillsViewportHeight(true);
        table.setAutoCreateRowSorter(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        JTableHeader th = table.getTableHeader();
        th.setFont(th.getFont().deriveFont(Font.BOLD, 12f));
        th.setReorderingAllowed(false);
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, col);

                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 245, 245));
                    c.setForeground(Color.BLACK);
                } else {
                    c.setBackground(t.getSelectionBackground());
                    c.setForeground(t.getSelectionForeground());
                }

                setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 6));

                if (c instanceof JComponent jc) {
                    String s = value == null ? "" : String.valueOf(value);
                    jc.setToolTipText(s.isEmpty() ? null : s);
                }

                return c;
            }
        };
        table.setDefaultRenderer(Object.class, renderer);
        table.setDefaultRenderer(String.class, renderer);

        JScrollPane sp = new JScrollPane(
                table,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED
        );
        sp.getViewport().setBackground(Color.WHITE);
        add(sp, BorderLayout.CENTER);
        btnLoad.addActionListener(e -> {
            try {
                CsvIO.CsvData data = CsvIO.readWithHeader(sourceCsv);
                header = data.header;
                tableModel.setData(header, data.rows);
                autoFitColumns(table, 200, 90, 320);
                SwingUtilities.invokeLater(() -> {
                    JViewport vp = sp.getViewport();
                    if (vp != null) vp.setViewPosition(new Point(0, 0));
                });

                JOptionPane.showMessageDialog(this,
                        "Loaded " + data.rows.size() + " rows.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                showError(ex);
            }
        });

        btnAdd.addActionListener(e -> {
            if (header.length == 0) {
                JOptionPane.showMessageDialog(this, "Please Load first.");
                return;
            }

            RowEditorDialog d = new RowEditorDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "Add Row",
                    header,
                    null
            );
            d.setVisible(true);

            String[] row = d.getRowOrNull();
            if (row != null) {
                tableModel.addRow(row);
                autoFitColumns(table, 200, 90, 320);
            }
        });

        btnEdit.addActionListener(e -> {
            if (header.length == 0) {
                JOptionPane.showMessageDialog(this, "Please Load first.");
                return;
            }

            int viewRow = table.getSelectedRow();
            if (viewRow < 0) {
                JOptionPane.showMessageDialog(this, "Select a row to edit.");
                return;
            }
            int modelRow = table.convertRowIndexToModel(viewRow);
            String[] existing = tableModel.getAt(modelRow);

            RowEditorDialog d = new RowEditorDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "Edit Row",
                    header,
                    existing
            );
            d.setVisible(true);

            String[] updated = d.getRowOrNull();
            if (updated != null) {
                tableModel.updateRow(modelRow, updated);
                autoFitColumns(table, 200, 90, 320);
            }
        });

        btnDelete.addActionListener(e -> {
            int viewRow = table.getSelectedRow();
            if (viewRow < 0) {
                JOptionPane.showMessageDialog(this, "Select a row to delete.");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Delete selected row?",
                    "Confirm",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm == JOptionPane.YES_OPTION) {
                int modelRow = table.convertRowIndexToModel(viewRow);
                tableModel.deleteRow(modelRow);
                autoFitColumns(table, 200, 90, 320);
            }
        });

        btnSave.addActionListener(e -> {
            if (header.length == 0) {
                JOptionPane.showMessageDialog(this, "Nothing to save. Load first.");
                return;
            }
            try {
                List<String[]> rows = tableModel.getRowsCopy();
                CsvIO.writeAll(outputCsv, header, rows);

                JOptionPane.showMessageDialog(this,
                        "Saved to: " + outputCsv,
                        "Saved",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                showError(ex);
            }
        });
    }

    private void autoFitColumns(JTable table, int sampleRows, int minWidth, int maxWidth) {
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
                Component comp = cellRenderer.getTableCellRendererComponent(
                        table, value, false, false, row, col);

                width = Math.max(width, comp.getPreferredSize().width + 16);
                if (width >= maxWidth) {
                    width = maxWidth;
                    break;
                }
            }

            column.setPreferredWidth(Math.min(Math.max(width, minWidth), maxWidth));
        }
    }

    private void showError(Exception ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}
