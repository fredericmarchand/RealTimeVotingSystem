package model;

public class Voter extends Person {

    protected District district;
    protected boolean hasVoted;
    protected String password;

    public Voter(String firstName, String lastName, Address address, int SIN) {
        super(firstName, lastName, address, SIN);
        district = null;
        hasVoted = false;
        password = "";
    }

    public void setDistrict(District district) {
        this.district = district;
    }

    public void vote() {
        hasVoted = true;
    }

    public boolean hasVoted() {
        return hasVoted;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String pass) {
        password = pass;
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
