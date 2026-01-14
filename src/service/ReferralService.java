package service;

import model.Referral;
import repository.ReferralRepository;
import util.AppPaths;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.Deque;

public class ReferralService {

    private static ReferralService instance;

    private final ReferralRepository repo;
    private final Deque<Referral> queue = new ArrayDeque<>();

    private ReferralService(ReferralRepository repo) {
        this.repo = repo;
    }

    public static synchronized ReferralService getInstance(ReferralRepository repo) {
        if (instance == null) {
            instance = new ReferralService(repo);
        }
        return instance;
    }

    public void enqueue(Referral r) {
        if (r == null) return;
        queue.addLast(r);
        audit("ENQUEUE", r.getReferralId(), "Queued referral");
    }

    public int queueSize() {
        return queue.size();
    }

    public Referral processNext() throws IOException {
        Referral r = queue.pollFirst();
        if (r == null) return null;
        repo.appendToOutput(r);
        String letter = generateReferralLetter(r);
        writeReferralLetter(r.getReferralId(), letter);

        audit("PROCESS", r.getReferralId(), "Persisted + letter generated");
        return r;
    }

    public int processAll() throws IOException {
        int count = 0;
        while (!queue.isEmpty()) {
            Referral r = processNext();
            if (r != null) count++;
        }
        return count;
    }

    public String generateReferralLetter(Referral r) {
        return ""
                + "===== REFERRAL LETTER (SIMULATED EMAIL) =====\n"
                + "Referral ID: " + r.getReferralId() + "\n"
                + "Created: " + now() + "\n\n"
                + "Patient ID: " + r.getPatientId() + "\n"
                + "Referring Clinician ID: " + r.getReferringClinicianId() + "\n"
                + "Referred To Clinician ID: " + r.getReferredToClinicianId() + "\n"
                + "Referring Facility ID: " + r.getReferringFacilityId() + "\n"
                + "Referred To Facility ID: " + r.getReferredToFacilityId() + "\n\n"
                + "Referral Date: " + r.getReferralDate() + "\n"
                + "Urgency Level: " + r.getUrgencyLevel() + "\n"
                + "Reason: " + r.getReferralReason() + "\n\n"
                + "Clinical Summary:\n" + r.getClinicalSummary() + "\n\n"
                + "Requested Investigations:\n" + r.getRequestedInvestigations() + "\n\n"
                + "Appointment ID: " + r.getAppointmentId() + "\n"
                + "Notes: " + r.getNotes() + "\n\n"
                + "Status: " + r.getStatus() + "\n"
                + "===========================================\n";
    }

    private void writeReferralLetter(String referralId, String content) throws IOException {
        var file = AppPaths.referralLetter(referralId);
        Files.createDirectories(file.getParent());
        Files.writeString(file, content, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    private void audit(String action, String referralId, String message) {
        try {
            var log = AppPaths.referralAuditLog();
            Files.createDirectories(log.getParent());
            String line = now() + " | " + action + " | " + referralId + " | " + message + System.lineSeparator();
            Files.writeString(log, line, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException ignored) {}
    }

    private String now() {
        return LocalDateTime.now().toString();
    }
}
