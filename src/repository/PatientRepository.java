package repository;

import model.Patient;
import util.AppPaths;
import util.CsvUtil;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class PatientRepository {

    private static final int COLS = 14;

    private static final String PATIENTS_HEADER =
            "patient_id,first_name,last_name,date_of_birth,nhs_number,gender,phone_number,email,address,postcode," +
                    "emergency_contact_name,emergency_contact_phone,registration_date,gp_surgery_id";

    public List<Patient> loadAll() {
        Path data = AppPaths.patientsCsv();
        Path out = AppPaths.patientsOutCsv();

        if (Files.exists(out)) {
            List<Patient> fromOut = tryLoad(out);
            if (fromOut != null) return fromOut;

            System.err.println("patients_out.csv is malformed -> falling back to data/patients.csv");
        }

        List<Patient> fromData = tryLoad(data);
        return (fromData == null) ? new ArrayList<>() : fromData;
    }

    private List<Patient> tryLoad(Path path) {
        List<Patient> patients = new ArrayList<>();
        try {
            List<String[]> rows = CsvUtil.readAll(path);

            for (String[] r : rows) {
                if (r.length > 0 && "patient_id".equalsIgnoreCase(safe(r, 0))) continue;
                if (r.length != COLS) return null;

                patients.add(new Patient(
                        safe(r, 0),
                        safe(r, 1),
                        safe(r, 2),
                        safe(r, 3),
                        safe(r, 4),
                        safe(r, 5),
                        safe(r, 6),
                        safe(r, 7),
                        safe(r, 8),
                        safe(r, 9),
                        safe(r, 10),
                        safe(r, 11),
                        safe(r, 12),
                        safe(r, 13)
                ));
            }
            return patients;
        } catch (IOException e) {
            System.err.println("Failed to load patients file (" + path + "): " + e.getMessage());
            return null;
        }
    }

    public void appendToOutput(Patient p) throws IOException {
        Path out = AppPaths.patientsOutCsv();
        ensureOutputHeader(out);

        Files.writeString(out,
                p.toCsvRow() + System.lineSeparator(),
                StandardCharsets.UTF_8,
                StandardOpenOption.APPEND);
    }

    public void saveAllToOutput(List<Patient> patients) throws IOException {
        Path out = AppPaths.patientsOutCsv();
        Files.createDirectories(out.getParent());

        StringBuilder sb = new StringBuilder();
        sb.append(PATIENTS_HEADER).append(System.lineSeparator());

        if (patients != null) {
            for (Patient p : patients) {
                sb.append(p.toCsvRow()).append(System.lineSeparator());
            }
        }

        Files.writeString(out,
                sb.toString(),
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING);
    }

    private void ensureOutputHeader(Path out) throws IOException {
        Files.createDirectories(out.getParent());
        if (!Files.exists(out)) {
            Files.writeString(out,
                    PATIENTS_HEADER + System.lineSeparator(),
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.WRITE);
        }
    }

    private String safe(String[] arr, int idx) {
        if (arr == null || idx < 0 || idx >= arr.length) return "";
        return arr[idx].trim();
    }
}
