package model;

public class Voter extends Person{
    protected District district;
    protected boolean hasVoted;

    public Voter(String firstName, String lastName, Address address, int SIN) {
        super(firstName, lastName, address, SIN);
        district = null;
        hasVoted = false;
    }

    public void setDistrict(District district) {
        this.district = district;
    }

    public void vote() {
        hasVoted = true;
    }

    @Override
    public String toString() {
        String desc = "Voter: " + super.toString();

        if (district != null) {
            desc += ", for " + district.toString();
        }

        return desc;
    }

}
