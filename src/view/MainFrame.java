package view;

import controller.PatientController;
import controller.PrescriptionController;
import controller.ReferralController;
import util.AppPaths;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    public MainFrame(PatientController patientController,
                     PrescriptionController prescriptionController,
                     ReferralController referralController) {

        super("Healthcare Referral Management System");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1250, 720);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Patients", new PatientsPanel(patientController));
        tabs.addTab("Prescriptions", new PrescriptionsPanel(prescriptionController));
        tabs.addTab("Referrals", new ReferralsPanel(referralController));
        tabs.addTab("Clinicians", new GenericCsvCrudPanel(
                "Clinicians",
                AppPaths.cliniciansCsv(),
                AppPaths.cliniciansOutCsv()
        ));

        tabs.addTab("Facilities", new GenericCsvCrudPanel(
                "Facilities",
                AppPaths.facilitiesCsv(),
                AppPaths.facilitiesOutCsv()
        ));

        tabs.addTab("Appointments", new GenericCsvCrudPanel(
                "Appointments",
                AppPaths.appointmentsCsv(),
                AppPaths.appointmentsOutCsv()
        ));

        tabs.addTab("Staff", new GenericCsvCrudPanel(
                "Staff",
                AppPaths.staffCsv(),
                AppPaths.staffOutCsv()
        ));

        add(tabs, BorderLayout.CENTER);
    }
}
