package model;

public class Candidate extends Voter {
    
    private Party party;

    public Candidate(String firstName, String lastName, Address address, int SIN) {
        super(firstName, lastName, address, SIN);
        party = null;
    }

    public void runFor(Party party, District district) {
        this.party = party;
        setDistrict(district);
    }

    public Party getParty() {
        return party;
    }

    @Override
    public String toString() {
        String desc = "";
        if (party != null) {
            desc += "Candidate: Running for " + party + "\n";
        }
        desc += super.toString();
        return desc;
    }

}
