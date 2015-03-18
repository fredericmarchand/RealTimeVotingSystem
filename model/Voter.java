package model;

import java.io.Serializable;

public class Voter extends Person implements Serializable {
	private static final long serialVersionUID  = -4588489610617393544L;
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
    
    public boolean hasVoted() {
    	return hasVoted;
    }
    
    public void setHasVoted ( boolean hasVoted ) { 
    	this.hasVoted = hasVoted;
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
