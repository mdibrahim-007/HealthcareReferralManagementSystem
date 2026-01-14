package repository;

import model.Prescription;
import util.AppPaths;
import util.CsvUtil;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class PrescriptionRepository {

    public List<Prescription> loadAll() {
        List<Prescription> list = new ArrayList<>();
        try {
            List<String[]> rows = CsvUtil.readAll(AppPaths.prescriptionsCsv());

            for (String[] r : rows) {
                list.add(new Prescription(
                        safe(r,0),  safe(r,1),  safe(r,2),  safe(r,3),  safe(r,4),
                        safe(r,5),  safe(r,6),  safe(r,7),  safe(r,8),  safe(r,9),
                        safe(r,10), safe(r,11), safe(r,12), safe(r,13), safe(r,14)
                ));
            }
        } catch (IOException e) {
            System.err.println("Failed to load prescriptions.csv: " + e.getMessage());
        }
        return list;
    }

    public void append(Prescription p) throws IOException {
        Files.writeString(AppPaths.prescriptionsCsv(),
                p.toCsvRow() + System.lineSeparator(),
                StandardCharsets.UTF_8,
                StandardOpenOption.APPEND);
    }

    private String safe(String[] arr, int idx) {
        if (arr == null || idx < 0 || idx >= arr.length) return "";
        return arr[idx].trim();
    }
}
