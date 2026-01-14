package controller;

import model.Prescription;
import repository.PrescriptionRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PrescriptionController {

    private final PrescriptionRepository repo;
    private final List<Prescription> cache = new ArrayList<>();

    public PrescriptionController(PrescriptionRepository repo) {
        this.repo = repo;
    }

    public List<Prescription> loadPrescriptions() {
        cache.clear();
        cache.addAll(repo.loadAll());
        return new ArrayList<>(cache);
    }

    public List<Prescription> getPrescriptions() {
        return new ArrayList<>(cache);
    }

    public void addPrescription(Prescription p) throws IOException {
        cache.add(p);
        repo.append(p);
    }
}
