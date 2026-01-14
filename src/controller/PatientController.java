package controller;

import model.Patient;
import repository.PatientRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PatientController {

    private final PatientRepository repo;

    private final List<Patient> cache = new ArrayList<>();

    public PatientController(PatientRepository repo) {
        this.repo = repo;
    }

    public List<Patient> loadPatients() {
        cache.clear();
        cache.addAll(repo.loadAll());
        return new ArrayList<>(cache);
    }

    public List<Patient> getPatients() {
        return new ArrayList<>(cache);
    }

    public void addPatient(Patient p) throws IOException {
        if (p == null) return;
        cache.add(p);
        repo.appendToOutput(p);
    }

    public void updatePatient(int index, Patient updated) throws IOException {
        if (updated == null) return;
        if (index < 0 || index >= cache.size()) return;

        cache.set(index, updated);
        repo.saveAllToOutput(cache);
    }

    public void deletePatient(int index) throws IOException {
        if (index < 0 || index >= cache.size()) return;

        cache.remove(index);
        repo.saveAllToOutput(cache);
    }
}
