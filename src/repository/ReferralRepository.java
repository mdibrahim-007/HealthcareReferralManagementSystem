package repository;

import model.Referral;
import util.AppPaths;
import util.CsvUtil;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class ReferralRepository {

    private static final String HEADER =
            "referral_id,patient_id,referring_clinician_id,referred_to_clinician_id," +
                    "referring_facility_id,referred_to_facility_id,referral_date,urgency_level," +
                    "referral_reason,clinical_summary,requested_investigations,status,appointment_id," +
                    "notes,created_date,last_updated";

    public List<Referral> loadAll() {
        List<Referral> list = new ArrayList<>();
        try {
            List<String[]> rows = CsvUtil.readAll(AppPaths.referralsCsv());
            for (String[] r : rows) {
                list.add(new Referral(
                        safe(r,0), safe(r,1), safe(r,2), safe(r,3),
                        safe(r,4), safe(r,5), safe(r,6), safe(r,7),
                        safe(r,8), safe(r,9), safe(r,10), safe(r,11),
                        safe(r,12), safe(r,13), safe(r,14), safe(r,15)
                ));
            }
        } catch (IOException e) {
            System.err.println("Failed to load referrals.csv: " + e.getMessage());
        }
        return list;
    }

    public void appendToOutput(Referral r) throws IOException {
        Path out = AppPaths.referralsOutCsv();
        ensureHeader(out);

        Files.writeString(out,
                r.toCsvRow() + System.lineSeparator(),
                StandardCharsets.UTF_8,
                StandardOpenOption.APPEND);
    }

    private void ensureHeader(Path out) throws IOException {
        Files.createDirectories(out.getParent());
        if (!Files.exists(out)) {
            Files.writeString(out, HEADER + System.lineSeparator(),
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
