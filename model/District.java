package model;

import java.util.Collection;
import java.util.HashMap;
import java.io.Serializable;

public class District implements Serializable {

    private static final long serialVersionUID = -4166628343233841359L;

    private Province province;
    private String name;
    private HashMap<Party, Candidate> candidates;
    private static int staticID = 0;
    private int id;

    public District(String name) {
        this.name = name;
        this.setProvince(Province.Alberta);
        candidates = new HashMap<Party, Candidate>();
        this.id = ++staticID;
    }
    
    public District(String name, Province province) {
        this.name = name;
        this.setProvince(province);
        candidates = new HashMap<Party, Candidate>();
        this.id = ++staticID;
    }

    public void addCandidate(Candidate candidate) {
        candidates.put(candidate.getParty(), candidate);
    }

    public Candidate getCandidate(Party party) {
        if (candidates.containsKey(party)) {
            return candidates.get(party);
        }
        
        return null;
    }

    public Collection<Candidate> getAllCandidates() {
        return candidates.values();
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

	public Province getProvince() {
		return province;
	}

	public void setProvince(Province province) {
		this.province = province;
	}
}
