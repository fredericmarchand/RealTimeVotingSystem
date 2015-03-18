package model;

import java.util.Arrays;

public class Candidate extends Voter {
    
    private Party party;

    public Candidate(String firstName, String lastName, Address address, int SIN) {
        super(firstName, lastName, address, SIN);
        party = null;
    }

    public Candidate(String firstName, String lastName, Address address, int SIN, String user, String pass) {
        super(firstName, lastName, address, SIN, user, pass);
        party = null;
    }

    public void runFor(Party party) {
        this.party = party;
    }

    public Party getParty() {
        return party;
    }

    @Override
    public String toString() {
        String desc = "";
        if (party != null) {
            desc += "Candidate: Running for " + party + "-";
        }
        desc += super.toString();
        return desc;
    }

}
