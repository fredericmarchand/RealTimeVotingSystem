package model;

import java.io.Serializable;
import java.util.HashMap;

public class ResultSet implements Serializable {

	private static final long serialVersionUID = 458344541008756254L;
	public static final int DISTRICT = 0;
	public static final int NATIONAL = 1;

	private int type;
	private HashMap<Candidate, Integer> districtVotes;
	private HashMap<String, Integer> totalVotes;

	public ResultSet(int type) {
		this.setType(type);

		if (type == DISTRICT) {
			setDistrictVotes(new HashMap<Candidate, Integer>());
		} else if (type == NATIONAL) {
			setTotalVotes(new HashMap<String, Integer>());
		}
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public HashMap<Candidate, Integer> getDistrictVotes() {
		return districtVotes;
	}

	public void setDistrictVotes(HashMap<Candidate, Integer> districtVotes) {
		this.districtVotes = districtVotes;
	}

	public HashMap<String, Integer> getTotalVotes() {
		return totalVotes;
	}

	public void setTotalVotes(HashMap<String, Integer> totalVotes) {
		this.totalVotes = totalVotes;
	}

	public String getPartyWithMostVotes() {
		String party = null;
		int maxVotes = 0;
		for (Candidate can: districtVotes.keySet()) {
			if (districtVotes.get(can) > maxVotes) {
				maxVotes = districtVotes.get(can);
				party = can.getParty().getName();
			}
		}
		return party;
	}
	
}
