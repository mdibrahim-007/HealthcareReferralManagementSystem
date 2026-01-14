package view;

import controller.PatientController;
import model.Patient;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.io.IOException;
import java.util.List;

public class PatientsPanel extends JPanel {

    private final PatientController controller;
    private final PatientTableModel tableModel = new PatientTableModel();
    private final JTable table = new JTable(tableModel);

    public PatientsPanel(PatientController patientController) {
        this.controller = patientController;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        JButton btnLoad = new JButton("Load Patients");
        JButton btnAdd = new JButton("Add");
        JButton btnEdit = new JButton("Edit");
        JButton btnDelete = new JButton("Delete");

        for (JButton b : new JButton[]{btnLoad, btnAdd, btnEdit, btnDelete}) {
            b.setFocusPainted(false);
        }

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        top.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        top.add(btnLoad);
        top.add(btnAdd);
        top.add(btnEdit);
        top.add(btnDelete);

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

        JScrollPane sp = new JScrollPane(
                table,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED
        );
        sp.getViewport().setBackground(Color.WHITE);
        add(sp, BorderLayout.CENTER);
        btnLoad.addActionListener(e -> {
            List<Patient> patients = controller.loadPatients();
            tableModel.setData(patients);
            autoFitColumns(table, 200, 90, 320);

            JOptionPane.showMessageDialog(this,
                    "Loaded " + patients.size() + " patients",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
        });

        btnAdd.addActionListener(e -> {
            PatientFormDialog dialog = new PatientFormDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "Add Patient",
                    null,
                    true
            );
            dialog.setVisible(true);

            Patient p = dialog.getPatientOrNull();
            if (p != null) {
                try {
                    controller.addPatient(p);
                    tableModel.setData(controller.getPatients());
                    autoFitColumns(table, 200, 90, 320);
                } catch (IOException ex) {
                    showError(ex);
                }
            }
        });

        btnEdit.addActionListener(e -> {
            int viewRow = table.getSelectedRow();
            if (viewRow < 0) {
                JOptionPane.showMessageDialog(this,
                        "Please select a patient to edit",
                        "No Selection",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            int modelRow = table.convertRowIndexToModel(viewRow);
            Patient existing = tableModel.getAt(modelRow);

            PatientFormDialog dialog = new PatientFormDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "Edit Patient",
                    existing,
                    false
            );
            dialog.setVisible(true);

            Patient updated = dialog.getPatientOrNull();
            if (updated != null) {
                try {
                    controller.updatePatient(modelRow, updated);
                    tableModel.setData(controller.getPatients());
                    autoFitColumns(table, 200, 90, 320);
                } catch (IOException ex) {
                    showError(ex);
                }
            }
        });

        btnDelete.addActionListener(e -> {
            int viewRow = table.getSelectedRow();
            if (viewRow < 0) {
                JOptionPane.showMessageDialog(this,
                        "Please select a patient to delete",
                        "No Selection",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to delete this patient?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    int modelRow = table.convertRowIndexToModel(viewRow);
                    controller.deletePatient(modelRow);
                    tableModel.setData(controller.getPatients());
                    autoFitColumns(table, 200, 90, 320);
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
        JOptionPane.showMessageDialog(
                this,
                ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}
