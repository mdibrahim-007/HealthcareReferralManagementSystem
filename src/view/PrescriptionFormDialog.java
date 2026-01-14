package view;

import model.Prescription;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

public class PrescriptionFormDialog extends JDialog {

    private JTextField tfId = new JTextField(18);
    private JTextField tfPatientId = new JTextField(18);
    private JTextField tfClinicianId = new JTextField(18);
    private JTextField tfAppointmentId = new JTextField(18);
    private JTextField tfPrescriptionDate = new JTextField(18);
    private JTextField tfMedication = new JTextField(18);
    private JTextField tfDosage = new JTextField(18);
    private JTextField tfFrequency = new JTextField(18);
    private JTextField tfDurationDays = new JTextField(18);
    private JTextField tfQuantity = new JTextField(18);
    private JTextField tfPharmacy = new JTextField(18);
    private JTextField tfStatus = new JTextField(18);
    private JTextField tfIssueDate = new JTextField(18);
    private JTextField tfCollectionDate = new JTextField(18);

    private JTextArea taInstructions = new JTextArea(3, 18);

    private Prescription result;

    public PrescriptionFormDialog(Window owner) {
        super(owner, "Add Prescription", ModalityType.APPLICATION_MODAL);

        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel title = new JLabel("Add Prescription");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        root.add(title, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridLayout(1, 2, 12, 0));
        JPanel left = new JPanel(new GridBagLayout());
        JPanel right = new JPanel(new GridBagLayout());
        center.add(left);
        center.add(right);

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6, 6, 6, 6);
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1.0;
        tfId.setText("RX" + System.currentTimeMillis());
        tfPrescriptionDate.setText(LocalDate.now().toString());
        tfIssueDate.setText(LocalDate.now().toString());

        int yL = 0;
        yL = addRow(left, gc, yL, "Prescription ID", tfId);
        yL = addRow(left, gc, yL, "Patient ID", tfPatientId);
        yL = addRow(left, gc, yL, "Clinician ID", tfClinicianId);
        yL = addRow(left, gc, yL, "Appointment ID", tfAppointmentId);
        yL = addRow(left, gc, yL, "Prescription Date", tfPrescriptionDate);
        yL = addRow(left, gc, yL, "Medication Name", tfMedication);
        yL = addRow(left, gc, yL, "Dosage", tfDosage);

        int yR = 0;
        yR = addRow(right, gc, yR, "Frequency", tfFrequency);
        yR = addRow(right, gc, yR, "Duration Days", tfDurationDays);
        yR = addRow(right, gc, yR, "Quantity", tfQuantity);

        taInstructions.setLineWrap(true);
        taInstructions.setWrapStyleWord(true);
        JScrollPane insScroll = new JScrollPane(taInstructions);
        insScroll.setPreferredSize(new Dimension(240, 70));
        yR = addRow(right, gc, yR, "Instructions", insScroll);

        yR = addRow(right, gc, yR, "Pharmacy Name", tfPharmacy);
        yR = addRow(right, gc, yR, "Status", tfStatus);
        yR = addRow(right, gc, yR, "Issue Date", tfIssueDate);
        yR = addRow(right, gc, yR, "Collection Date", tfCollectionDate);

        tfId.setEditable(false);

        root.add(center, BorderLayout.CENTER);

        JButton btnSave = new JButton("Save");
        JButton btnCancel = new JButton("Cancel");

        btnSave.setFocusPainted(false);
        btnCancel.setFocusPainted(false);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttons.add(btnSave);
        buttons.add(btnCancel);

        btnSave.addActionListener(e -> onSave());
        btnCancel.addActionListener(e -> { result = null; dispose(); });

        root.add(buttons, BorderLayout.SOUTH);

        setContentPane(root);
        pack();
        setMinimumSize(new Dimension(760, getHeight()));
        setLocationRelativeTo(owner);
    }

    private void onSave() {
        if (tfPatientId.getText().trim().isEmpty() || tfMedication.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Patient ID and Medication Name are required.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        result = new Prescription(
                tfId.getText().trim(),
                tfPatientId.getText().trim(),
                tfClinicianId.getText().trim(),
                tfAppointmentId.getText().trim(),
                tfPrescriptionDate.getText().trim(),
                tfMedication.getText().trim(),
                tfDosage.getText().trim(),
                tfFrequency.getText().trim(),
                tfDurationDays.getText().trim(),
                tfQuantity.getText().trim(),
                taInstructions.getText().trim(),
                tfPharmacy.getText().trim(),
                tfStatus.getText().trim(),
                tfIssueDate.getText().trim(),
                tfCollectionDate.getText().trim()
        );

        dispose();
    }

    public Prescription getPrescriptionOrNull() {
        return result;
    }

    private int addRow(JPanel panel, GridBagConstraints base, int row, String label, JComponent field) {
        GridBagConstraints c1 = (GridBagConstraints) base.clone();
        c1.gridx = 0;
        c1.gridy = row;
        c1.weightx = 0;
        c1.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel(label), c1);

        GridBagConstraints c2 = (GridBagConstraints) base.clone();
        c2.gridx = 1;
        c2.gridy = row;
        c2.weightx = 1.0;
        panel.add(field, c2);

        return row + 1;
    }
}
