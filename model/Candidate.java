package model;

public class Candidate extends Voter {
    private String position;

    public Candidate(String firstName, String lastName, Address address, District district) {
        super(firstName, lastName, address);
        position = "";
    }

    public void runFor(String position, District district) {
        this.position = position;
        setDistrict(district);
    }

    @Override
    public String toString() {
        String desc = "";
        if (position.length() > 0) {
            desc += "Candidate: Running for " + position + "\n";
        }
        desc += super.toString();
        return desc;
    }

}
