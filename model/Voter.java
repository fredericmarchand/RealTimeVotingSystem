package model;

public class Voter extends Person {

	private static final long serialVersionUID = -6943752036828144655L;
	protected District district;
	protected boolean hasVoted;
	protected String username;
	protected String password;

	public Voter(String firstName, String lastName, Address address, int SIN) {
		this(firstName, lastName, address, SIN, "" + SIN, "password", null);
	}

	public Voter(String firstName, String lastName, Address address, int SIN,
			String user, String pass) {
		this(firstName, lastName, address, SIN, user, pass, null);
	}

	public Voter(String firstName, String lastName, Address address, int SIN,
			String pass, District district) {
		super(firstName, lastName, address, SIN);
		setDistrict(district);
		this.hasVoted = false;
		password = pass;
	}

	public Voter(String firstName, String lastName, Address address, int SIN,
			String user, String pass, District district) {
		super(firstName, lastName, address, SIN);
		setDistrict(district);
		this.hasVoted = false;
		password = pass;
		username = user;
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

	public String getUsername() {
		return username;
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
