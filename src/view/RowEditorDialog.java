package view;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class RowEditorDialog extends JDialog {

    private final String[] header;
    private String[] result;

    private final Map<String, JComponent> fieldMap = new LinkedHashMap<>();

    public RowEditorDialog(Window owner, String title, String[] header, String[] existingRow) {
        super(owner, title, ModalityType.APPLICATION_MODAL);
        this.header = header == null ? new String[0] : header;

        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(lblTitle.getFont().deriveFont(Font.BOLD, 18f));

        JLabel lblSub = new JLabel(existingRow == null
                ? "Fill the fields below and click Save."
                : "Update the fields below and click Save.");
        lblSub.setForeground(new Color(90, 90, 90));

        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.add(lblTitle);
        headerPanel.add(Box.createVerticalStrut(4));
        headerPanel.add(lblSub);

        root.add(headerPanel, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(4, 2, 4, 2));

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(8, 8, 8, 8);
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1.0;
        gc.anchor = GridBagConstraints.NORTHWEST;

        int leftY = 0;
        int rightY = 0;

        int half = (int) Math.ceil(this.header.length / 2.0);

        for (int i = 0; i < this.header.length; i++) {
            String col = this.header[i];

            String existingVal = (existingRow != null && i < existingRow.length && existingRow[i] != null)
                    ? existingRow[i]
                    : "";

            boolean isRight = i >= half;

            int xBase = isRight ? 2 : 0;
            int y = isRight ? rightY : leftY;

            JLabel lbl = new JLabel(prettyLabel(col));
            lbl.setFont(lbl.getFont().deriveFont(Font.PLAIN, 12.5f));

            GridBagConstraints cL = (GridBagConstraints) gc.clone();
            cL.gridx = xBase;
            cL.gridy = y;
            cL.weightx = 0;
            cL.fill = GridBagConstraints.NONE;
            form.add(lbl, cL);

            JComponent field = createFieldComponent(col, existingVal);
            fieldMap.put(col, field);

            GridBagConstraints cF = (GridBagConstraints) gc.clone();
            cF.gridx = xBase + 1;
            cF.gridy = y;
            cF.weightx = 1.0;
            cF.fill = GridBagConstraints.HORIZONTAL;

            form.add(field, cF);

            if (isRight) rightY++;
            else leftY++;
        }

        GridBagConstraints spacer = new GridBagConstraints();
        spacer.gridx = 0;
        spacer.gridy = Math.max(leftY, rightY);
        spacer.weighty = 1.0;
        spacer.fill = GridBagConstraints.VERTICAL;
        form.add(Box.createVerticalGlue(), spacer);

        JScrollPane scroll = new JScrollPane(form);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        scroll.getVerticalScrollBar().setUnitIncrement(18);

        root.add(scroll, BorderLayout.CENTER);

        JButton btnSave = new JButton("Save");
        JButton btnCancel = new JButton("Cancel");
        btnSave.setFocusPainted(false);
        btnCancel.setFocusPainted(false);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttons.add(btnSave);
        buttons.add(btnCancel);

        root.add(buttons, BorderLayout.SOUTH);

        btnSave.addActionListener(e -> onSave(existingRow));
        btnCancel.addActionListener(e -> {
            result = null;
            dispose();
        });

        setContentPane(root);

        setMinimumSize(new Dimension(920, 560));
        setSize(980, 600);
        setLocationRelativeTo(owner);
    }

    private void onSave(String[] existingRow) {
        String idCol = findIdColumn(header);
        if (idCol != null) {
            String idVal = getValue(idCol).trim();
            if (idVal.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        prettyLabel(idCol) + " is required.",
                        "Validation",
                        JOptionPane.WARNING_MESSAGE);
                focusField(idCol);
                return;
            }
        }

        result = new String[header.length];
        for (int i = 0; i < header.length; i++) {
            String col = header[i];
            result[i] = getValue(col).trim();
        }
        dispose();
    }

    private void focusField(String col) {
        JComponent c = fieldMap.get(col);
        if (c == null) return;
        if (c instanceof JScrollPane sp) {
            Component view = sp.getViewport().getView();
            view.requestFocusInWindow();
        } else {
            c.requestFocusInWindow();
        }
    }

    private String getValue(String col) {
        JComponent c = fieldMap.get(col);
        if (c == null) return "";
        if (c instanceof JTextField tf) return tf.getText();
        if (c instanceof JScrollPane sp) {
            Component view = sp.getViewport().getView();
            if (view instanceof JTextArea ta) return ta.getText();
        }
        return "";
    }

    private JComponent createFieldComponent(String col, String existingVal) {
        String lower = col.toLowerCase(Locale.ROOT);

        boolean isLongText = lower.contains("address")
                || lower.contains("summary")
                || lower.contains("notes")
                || lower.contains("instruction")
                || lower.contains("comment")
                || lower.contains("reason")
                || lower.contains("description");

        boolean isDate = lower.contains("date") || lower.endsWith("_dob");

        boolean isId = lower.endsWith("_id") || lower.equals("id");

        if (isLongText) {
            JTextArea ta = new JTextArea(3, 28);
            ta.setLineWrap(true);
            ta.setWrapStyleWord(true);
            ta.setText(existingVal == null ? "" : existingVal);
            ta.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(200, 200, 200)),
                    BorderFactory.createEmptyBorder(6, 8, 6, 8)
            ));

            JScrollPane sp = new JScrollPane(ta);
            sp.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
            sp.getVerticalScrollBar().setUnitIncrement(16);

            return sp;
        }

        JTextField tf = new JTextField(28);
        tf.setText(existingVal == null ? "" : existingVal);

        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));

        if (isDate) {
            tf.setToolTipText("Format: YYYY-MM-DD");
        }

        if (isId && existingVal != null && !existingVal.isBlank()) {
            tf.setEditable(false);
            tf.setBackground(new Color(245, 245, 245));
        }

        return tf;
    }

    private String findIdColumn(String[] header) {
        if (header == null) return null;
        for (String h : header) {
            String l = h.toLowerCase(Locale.ROOT).trim();
            if (l.endsWith("_id") || l.equals("id")) return h;
        }
        return null;
    }

    private String prettyLabel(String raw) {
        if (raw == null) return "";
        String s = raw.trim();

        s = s.replace('_', ' ').replace('-', ' ');

        String[] parts = s.split("\\s+");
        List<String> out = new ArrayList<>();
        for (String p : parts) {
            if (p.isEmpty()) continue;
            if (p.equalsIgnoreCase("id")) {
                out.add("ID");
                continue;
            }
            out.add(Character.toUpperCase(p.charAt(0)) + p.substring(1).toLowerCase(Locale.ROOT));
        }
        return String.join(" ", out);
    }

    public String[] getRowOrNull() {
        return result;
    }
}
