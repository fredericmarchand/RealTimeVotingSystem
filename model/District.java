package model;

import java.util.HashMap;

public class District {

    private Province province;
    private String name;
    private HashMap<String, Candidate> candidates;
    private static int staticID = 0;
    private int id;

    public District(String name) {
        this.name = name;
        this.province = Province.Alberta;
        candidates = new HashMap<String, Candidate>();
        this.id = ++staticID;
    }
    
    public District(String name, Province province) {
        this.name = name;
        this.province = province;
        candidates = new HashMap<String, Candidate>();
        this.id = ++staticID;
    }

    public void addCandidate(String position, Candidate candidate) {
        candidates.put(position, candidate);
    }

    public Candidate getCandidate(String position) {
        return candidates.getOrDefault(position, null);
    }

    public String getName() {
        return name;
    }

    public int getID() {
        return id;
    }

    @Override
    public String toString() {
        return "District: " + id + ", " + name;
    }
}
