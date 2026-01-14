package view;

import controller.PrescriptionController;
import model.Prescription;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.io.IOException;
import java.util.List;

public class PrescriptionsPanel extends JPanel {

    private final PrescriptionController controller;
    private final PrescriptionTableModel tableModel = new PrescriptionTableModel();
    private final JTable table = new JTable(tableModel);

    private final JScrollPane sp;

    public PrescriptionsPanel(PrescriptionController controller) {
        this.controller = controller;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JButton btnLoad = new JButton("Load Prescriptions");
        JButton btnAdd = new JButton("Add Prescription");

        for (JButton b : new JButton[]{btnLoad, btnAdd}) {
            b.setFocusPainted(false);
        }

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        top.add(btnLoad);
        top.add(btnAdd);
        add(top, BorderLayout.NORTH);
        table.setRowHeight(26);
        table.setFillsViewportHeight(true);
        table.setAutoCreateRowSorter(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        JTableHeader header = table.getTableHeader();
        header.setFont(header.getFont().deriveFont(Font.BOLD, 12f));
        header.setReorderingAllowed(false);
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

        sp = new JScrollPane(
                table,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED
        );
        sp.getViewport().setBackground(Color.WHITE);
        add(sp, BorderLayout.CENTER);
        btnLoad.addActionListener(e -> {
            List<Prescription> list = controller.loadPrescriptions();
            tableModel.setData(list);
            autoFitColumns(table, 200, 90, 320);
            SwingUtilities.invokeLater(() -> sp.getViewport().setViewPosition(new Point(0, 0)));

            JOptionPane.showMessageDialog(this,
                    "Loaded " + list.size() + " prescriptions",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
        });

        btnAdd.addActionListener(e -> {
            PrescriptionFormDialog dialog =
                    new PrescriptionFormDialog(SwingUtilities.getWindowAncestor(this));
            dialog.setVisible(true);

            Prescription p = dialog.getPrescriptionOrNull();
            if (p != null) {
                try {
                    controller.addPrescription(p); // ✅ append/save
                    tableModel.setData(controller.getPrescriptions());

                    autoFitColumns(table, 200, 90, 320);
                    SwingUtilities.invokeLater(() -> sp.getViewport().setViewPosition(new Point(0, 0)));

                    JOptionPane.showMessageDialog(this,
                            "Prescription added and saved",
                            "Saved",
                            JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException ex) {
                    showError(ex);
                }
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
        JOptionPane.showMessageDialog(this,
                ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
    }
}
