package model;

import java.io.Serializable;

public class Vote implements Serializable {

	private static final long serialVersionUID = -1836802661858419647L;

	private Voter voter;
	private Candidate candidate;
	
	public Vote(Voter voter, Candidate candidate) {
		this.setVoter(voter);
		this.setCandidate(candidate);
	}

	public Voter getVoter() {
		return voter;
	}

	public void setVoter(Voter voter) {
		this.voter = voter;
	}

	public Candidate getCandidate() {
		return candidate;
	}

	public void setCandidate(Candidate candidate) {
		this.candidate = candidate;
	}
	
	
}
