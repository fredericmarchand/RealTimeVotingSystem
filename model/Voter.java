package model;

public class Voter extends Person{
    protected District district;

    public Voter(String firstName, String lastName, Address address) {
        super(firstName, lastName, address);
        district = null;
    }

    public void setDistrict(District district) {
        this.district = district;
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
