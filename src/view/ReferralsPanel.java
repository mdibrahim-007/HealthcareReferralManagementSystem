package view;

import controller.ReferralController;
import model.Referral;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.io.IOException;
import java.util.List;

public class ReferralsPanel extends JPanel {

    private final ReferralController controller;
    private final ReferralTableModel tableModel = new ReferralTableModel();
    private final JTable table = new JTable(tableModel);

    private final JLabel lblQueue = new JLabel("Queue: 0");

    private final JScrollPane sp;

    public ReferralsPanel(ReferralController controller) {
        this.controller = controller;

        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(8, 8, 8, 8));

        JButton btnLoad = new JButton("Load Referrals");
        JButton btnCreate = new JButton("Create Referral");
        JButton btnProcessNext = new JButton("Process Next");
        JButton btnProcessAll = new JButton("Process All");

        for (JButton b : new JButton[]{btnLoad, btnCreate, btnProcessNext, btnProcessAll}) {
            b.setFocusPainted(false);
        }

        lblQueue.setFont(lblQueue.getFont().deriveFont(Font.BOLD, 12f));
        lblQueue.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 210, 210)),
                BorderFactory.createEmptyBorder(4, 10, 4, 10)
        ));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        top.add(btnLoad);
        top.add(btnCreate);
        top.add(btnProcessNext);
        top.add(btnProcessAll);

        top.add(Box.createHorizontalStrut(18));
        top.add(lblQueue);

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
            List<Referral> list = controller.loadReferrals();
            tableModel.setData(list);

            autoFitColumns(table, 200, 90, 320);
            SwingUtilities.invokeLater(() -> sp.getViewport().setViewPosition(new Point(0, 0)));

            refreshQueue();

            JOptionPane.showMessageDialog(this,
                    "Loaded " + list.size() + " referrals",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
        });

        btnCreate.addActionListener(e -> {
            ReferralFormDialog dialog = new ReferralFormDialog(SwingUtilities.getWindowAncestor(this));
            dialog.setVisible(true);

            Referral r = dialog.getReferralOrNull();
            if (r != null) {
                controller.createAndQueue(r); // ✅ goes to Singleton queue
                tableModel.setData(controller.getReferrals());

                autoFitColumns(table, 200, 90, 320);
                SwingUtilities.invokeLater(() -> sp.getViewport().setViewPosition(new Point(0, 0)));

                refreshQueue();

                JOptionPane.showMessageDialog(this,
                        "Referral queued. Now click Process Next / Process All to generate letter + save.",
                        "Queued",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });

        btnProcessNext.addActionListener(e -> {
            try {
                Referral processed = controller.processNext();
                refreshQueue();

                if (processed == null) {
                    JOptionPane.showMessageDialog(this,
                            "Queue is empty.",
                            "Info",
                            JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                tableModel.setData(controller.getReferrals());
                autoFitColumns(table, 200, 90, 320);

                JOptionPane.showMessageDialog(this,
                        "Processed referral: " + processed.getReferralId()
                                + "\nSaved to output/referrals_out.csv"
                                + "\nLetter: output/referral_" + processed.getReferralId() + ".txt"
                                + "\nAudit: output/referral_audit.log",
                        "Processed",
                        JOptionPane.INFORMATION_MESSAGE);

            } catch (IOException ex) {
                showError(ex);
            }
        });

        btnProcessAll.addActionListener(e -> {
            try {
                int count = controller.processAll();
                refreshQueue();

                tableModel.setData(controller.getReferrals());
                autoFitColumns(table, 200, 90, 320);

                JOptionPane.showMessageDialog(this,
                        "Processed " + count + " referrals.\nCheck output folder for letters and logs.",
                        "Processed All",
                        JOptionPane.INFORMATION_MESSAGE);

            } catch (IOException ex) {
                showError(ex);
            }
        });

        refreshQueue();
    }

    private void refreshQueue() {
        lblQueue.setText("Queue: " + controller.getQueueSize());
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
