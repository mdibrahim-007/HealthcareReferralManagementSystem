package view;

import controller.PatientController;
import controller.PrescriptionController;
import controller.ReferralController;
import repository.PatientRepository;
import repository.PrescriptionRepository;
import repository.ReferralRepository;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {

    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            System.err.println("Unexpected error on thread: " + t.getName());
            e.printStackTrace();
        });

        SwingUtilities.invokeLater(() -> {
            PatientRepository patientRepo = new PatientRepository();
            PatientController patientController = new PatientController(patientRepo);
            PrescriptionRepository prescriptionRepo = new PrescriptionRepository();
            PrescriptionController prescriptionController = new PrescriptionController(prescriptionRepo);
            ReferralRepository referralRepo = new ReferralRepository();
            ReferralController referralController = new ReferralController(referralRepo);

            MainFrame frame = new MainFrame(patientController, prescriptionController, referralController);
            frame.setVisible(true);
        });
    }
}
