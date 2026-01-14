package util;

import java.nio.file.Path;
import java.nio.file.Paths;

public class AppPaths {

    private static final String DATA_DIR = "data";
    private static final String OUTPUT_DIR = "output";

    public static Path patientsCsv() {
        return Paths.get(DATA_DIR, "patients.csv");
    }

    public static Path cliniciansCsv() {
        return Paths.get(DATA_DIR, "clinicians.csv");
    }

    public static Path facilitiesCsv() {
        return Paths.get(DATA_DIR, "facilities.csv");
    }

    public static Path appointmentsCsv() {
        return Paths.get(DATA_DIR, "appointments.csv");
    }

    public static Path prescriptionsCsv() {
        return Paths.get(DATA_DIR, "prescriptions.csv");
    }

    public static Path referralsCsv() {
        return Paths.get(DATA_DIR, "referrals.csv");
    }

    public static Path staffCsv() {
        return Paths.get(DATA_DIR, "staff.csv");
    }

    public static Path patientsOutCsv() {
        return Paths.get(OUTPUT_DIR, "patients_out.csv");
    }

    public static Path referralsOutCsv() {
        return Paths.get(OUTPUT_DIR, "referrals_out.csv");
    }

    public static Path cliniciansOutCsv() {
        return Paths.get(OUTPUT_DIR, "clinicians_out.csv");
    }

    public static Path facilitiesOutCsv() {
        return Paths.get(OUTPUT_DIR, "facilities_out.csv");
    }

    public static Path appointmentsOutCsv() {
        return Paths.get(OUTPUT_DIR, "appointments_out.csv");
    }

    public static Path staffOutCsv() {
        return Paths.get(OUTPUT_DIR, "staff_out.csv");
    }

    public static Path referralOutput(String referralId) {
        return Paths.get(OUTPUT_DIR, "referral_" + referralId + ".txt");
    }

    public static Path referralLetter(String referralId) {
        return Paths.get(OUTPUT_DIR, "referral_" + referralId + ".txt");
    }

    public static Path referralAuditLog() {
        return Paths.get(OUTPUT_DIR, "referral_audit.log");
    }
}
