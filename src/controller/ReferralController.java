package controller;

import model.Referral;
import repository.ReferralRepository;
import service.ReferralService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ReferralController {

    private final ReferralRepository repo;
    private final ReferralService service;
    private final List<Referral> cache = new ArrayList<>();

    public ReferralController(ReferralRepository repo) {
        this.repo = repo;
        this.service = ReferralService.getInstance(repo);
    }

    public List<Referral> loadReferrals() {
        cache.clear();
        cache.addAll(repo.loadAll());
        return new ArrayList<>(cache);
    }

    public List<Referral> getReferrals() {
        return new ArrayList<>(cache);
    }

    public void createAndQueue(Referral r) {
        cache.add(r);
        service.enqueue(r);
    }

    public int getQueueSize() {
        return service.queueSize();
    }

    public Referral processNext() throws IOException {
        return service.processNext();
    }

    public int processAll() throws IOException {
        return service.processAll();
    }
}
