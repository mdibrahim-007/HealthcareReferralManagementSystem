package view;

import model.Patient;

import javax.swing.*;
import java.awt.*;

public class PatientFormDialog extends JDialog {

    private JTextField tfId = new JTextField(18);
    private JTextField tfFirst = new JTextField(18);
    private JTextField tfLast = new JTextField(18);
    private JTextField tfDob = new JTextField(18);
    private JTextField tfNhs = new JTextField(18);
    private JTextField tfGender = new JTextField(18);
    private JTextField tfPhone = new JTextField(18);

    private JTextField tfEmail = new JTextField(18);
    private JTextArea taAddress = new JTextArea(3, 18);
    private JTextField tfPostcode = new JTextField(18);
    private JTextField tfEName = new JTextField(18);
    private JTextField tfEPhone = new JTextField(18);
    private JTextField tfRegDate = new JTextField(18);
    private JTextField tfGp = new JTextField(18);

    private Patient result;

    public PatientFormDialog(Window owner, String title, Patient existing, boolean idEditable) {
        super(owner, title, ModalityType.APPLICATION_MODAL);
        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(titleLbl.getFont().deriveFont(Font.BOLD, 16f));
        root.add(titleLbl, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridLayout(1, 2, 12, 0));
        JPanel left = new JPanel(new GridBagLayout());
        JPanel right = new JPanel(new GridBagLayout());

        center.add(left);
        center.add(right);

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6, 6, 6, 6);
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1.0;

        int yL = 0;
        yL = addRow(left, gc, yL, "Patient ID", tfId);
        yL = addRow(left, gc, yL, "First Name", tfFirst);
        yL = addRow(left, gc, yL, "Last Name", tfLast);
        yL = addRow(left, gc, yL, "Date of Birth (YYYY-MM-DD)", tfDob);
        yL = addRow(left, gc, yL, "NHS Number", tfNhs);
        yL = addRow(left, gc, yL, "Gender", tfGender);
        yL = addRow(left, gc, yL, "Phone", tfPhone);

        int yR = 0;
        yR = addRow(right, gc, yR, "Email", tfEmail);

        taAddress.setLineWrap(true);
        taAddress.setWrapStyleWord(true);
        JScrollPane addressScroll = new JScrollPane(taAddress);
        addressScroll.setPreferredSize(new Dimension(240, 70));
        yR = addRow(right, gc, yR, "Address", addressScroll);

        yR = addRow(right, gc, yR, "Postcode", tfPostcode);
        yR = addRow(right, gc, yR, "Emergency Name", tfEName);
        yR = addRow(right, gc, yR, "Emergency Phone", tfEPhone);
        yR = addRow(right, gc, yR, "Registration Date (YYYY-MM-DD)", tfRegDate);
        yR = addRow(right, gc, yR, "GP Surgery ID", tfGp);
        root.add(center, BorderLayout.CENTER);

        JButton btnSave = new JButton("Save");
        JButton btnCancel = new JButton("Cancel");
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttons.add(btnSave);
        buttons.add(btnCancel);

        btnSave.addActionListener(e -> onSave());
        btnCancel.addActionListener(e -> {
            result = null;
            dispose();
        });

        root.add(buttons, BorderLayout.SOUTH);

        tfId.setEditable(idEditable);

        if (existing != null) {
            tfId.setText(existing.getPatientId());
            tfFirst.setText(existing.getFirstName());
            tfLast.setText(existing.getLastName());
            tfDob.setText(existing.getDateOfBirth());
            tfNhs.setText(existing.getNhsNumber());
            tfGender.setText(existing.getGender());
            tfPhone.setText(existing.getPhoneNumber());
            tfEmail.setText(existing.getEmail());
            taAddress.setText(existing.getAddress());
            tfPostcode.setText(existing.getPostcode());
            tfEName.setText(existing.getEmergencyContactName());
            tfEPhone.setText(existing.getEmergencyContactPhone());
            tfRegDate.setText(existing.getRegistrationDate());
            tfGp.setText(existing.getGpSurgeryId());
        }

        setContentPane(root);
        pack();
        setMinimumSize(new Dimension(720, getHeight()));
        setLocationRelativeTo(owner);
    }

    private void onSave() {
        if (tfId.getText().trim().isEmpty()
                || tfFirst.getText().trim().isEmpty()
                || tfLast.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Patient ID, First Name, and Last Name are required.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        result = new Patient(
                tfId.getText().trim(),
                tfFirst.getText().trim(),
                tfLast.getText().trim(),
                tfDob.getText().trim(),
                tfNhs.getText().trim(),
                tfGender.getText().trim(),
                tfPhone.getText().trim(),
                tfEmail.getText().trim(),
                taAddress.getText().trim(),
                tfPostcode.getText().trim(),
                tfEName.getText().trim(),
                tfEPhone.getText().trim(),
                tfRegDate.getText().trim(),
                tfGp.getText().trim()
        );
        dispose();
    }

    public Patient getPatientOrNull() {
        return result;
    }

    private int addRow(JPanel panel, GridBagConstraints base, int row, String label, JComponent field) {
        GridBagConstraints c1 = (GridBagConstraints) base.clone();
        c1.gridx = 0;
        c1.gridy = row;
        c1.weightx = 0;
        c1.anchor = GridBagConstraints.WEST;

        JLabel lbl = new JLabel(label);
        panel.add(lbl, c1);

        GridBagConstraints c2 = (GridBagConstraints) base.clone();
        c2.gridx = 1;
        c2.gridy = row;
        c2.weightx = 1.0;

        panel.add(field, c2);

        return row + 1;
    }
}
