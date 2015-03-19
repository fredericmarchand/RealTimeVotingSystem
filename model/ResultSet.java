package model;

import java.io.Serializable;
import java.util.HashMap;

public class ResultSet implements Serializable {

	private static final long serialVersionUID = 458344541008756254L;
	public static final int DISTRICT = 0;
	public static final int TOTAL = 1;
	
	private int type;
	private HashMap<Candidate, Integer> districtVotes;
	private HashMap<Party, Integer> totalVotes;
	
	public ResultSet(int type) {
		this.setType(type);
		
		if (type == DISTRICT) {
			setDistrictVotes(new HashMap<Candidate, Integer>());
		}
		else if (type == TOTAL) {
			setTotalVotes(new HashMap<Party, Integer>());
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

	public HashMap<Party, Integer> getTotalVotes() {
		return totalVotes;
	}

	public void setTotalVotes(HashMap<Party, Integer> totalVotes) {
		this.totalVotes = totalVotes;
	}
	
}
