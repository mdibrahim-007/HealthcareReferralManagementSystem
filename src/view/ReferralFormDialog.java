package view;

import model.Referral;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class ReferralFormDialog extends JDialog {

    private JTextField tfReferralId = new JTextField(18);
    private JTextField tfPatientId = new JTextField(18);
    private JTextField tfReferringClinicianId = new JTextField(18);
    private JTextField tfReferredToClinicianId = new JTextField(18);
    private JTextField tfReferringFacilityId = new JTextField(18);
    private JTextField tfReferredToFacilityId = new JTextField(18);
    private JTextField tfReferralDate = new JTextField(18);

    private JComboBox<String> cbUrgency = new JComboBox<>(new String[]{"Routine", "Urgent", "Emergency"});
    private JTextField tfAppointmentId = new JTextField(18);

    private JTextArea taReason = new JTextArea(3, 18);
    private JTextArea taSummary = new JTextArea(4, 18);
    private JTextArea taInvestigations = new JTextArea(3, 18);
    private JTextArea taNotes = new JTextArea(2, 18);

    private Referral result;

    public ReferralFormDialog(Window owner) {
        super(owner, "Create Referral", ModalityType.APPLICATION_MODAL);

        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel title = new JLabel("Create Referral");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        root.add(title, BorderLayout.NORTH);

        tfReferralId.setText("RF" + System.currentTimeMillis());
        tfReferralDate.setText(LocalDate.now().toString());
        tfReferralId.setEditable(false);

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
        yL = addRow(left, gc, yL, "Referral ID", tfReferralId);
        yL = addRow(left, gc, yL, "Patient ID *", tfPatientId);
        yL = addRow(left, gc, yL, "Referring Clinician ID", tfReferringClinicianId);
        yL = addRow(left, gc, yL, "Referred To Clinician ID", tfReferredToClinicianId);
        yL = addRow(left, gc, yL, "From Facility ID", tfReferringFacilityId);
        yL = addRow(left, gc, yL, "To Facility ID", tfReferredToFacilityId);
        yL = addRow(left, gc, yL, "Referral Date", tfReferralDate);

        int yR = 0;
        yR = addRow(right, gc, yR, "Urgency Level", cbUrgency);
        yR = addRow(right, gc, yR, "Appointment ID", tfAppointmentId);

        JScrollPane reasonSp = makeArea(taReason);
        yR = addRow(right, gc, yR, "Reason *", reasonSp);

        JScrollPane summarySp = makeArea(taSummary);
        yR = addRow(right, gc, yR, "Clinical Summary", summarySp);

        JScrollPane invSp = makeArea(taInvestigations);
        yR = addRow(right, gc, yR, "Investigations", invSp);

        JScrollPane notesSp = makeArea(taNotes);
        yR = addRow(right, gc, yR, "Notes", notesSp);

        root.add(center, BorderLayout.CENTER);

        JButton btnSave = new JButton("Queue Referral");
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
        setMinimumSize(new Dimension(860, getHeight()));
        setLocationRelativeTo(owner);
    }

    private JScrollPane makeArea(JTextArea ta) {
        ta.setLineWrap(true);
        ta.setWrapStyleWord(true);
        JScrollPane sp = new JScrollPane(ta);
        sp.setPreferredSize(new Dimension(280, 80));
        return sp;
    }

    private void onSave() {
        if (tfPatientId.getText().trim().isEmpty() || taReason.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Patient ID and Reason are required.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String now = LocalDateTime.now().toString();

        result = new Referral(
                tfReferralId.getText().trim(),              // referral_id
                tfPatientId.getText().trim(),               // patient_id
                tfReferringClinicianId.getText().trim(),    // referring_clinician_id
                tfReferredToClinicianId.getText().trim(),   // referred_to_clinician_id
                tfReferringFacilityId.getText().trim(),     // referring_facility_id
                tfReferredToFacilityId.getText().trim(),    // referred_to_facility_id
                tfReferralDate.getText().trim(),            // referral_date
                String.valueOf(cbUrgency.getSelectedItem()),// urgency_level
                taReason.getText().trim(),                  // referral_reason
                taSummary.getText().trim(),                 // clinical_summary
                taInvestigations.getText().trim(),          // requested_investigations
                "Queued",                                   // status
                tfAppointmentId.getText().trim(),           // appointment_id
                taNotes.getText().trim(),                   // notes
                now,                                        // created_date
                now                                         // last_updated
        );

        dispose();
    }

    public Referral getReferralOrNull() {
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
