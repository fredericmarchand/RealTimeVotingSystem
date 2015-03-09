package model;

public class Vote {

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
