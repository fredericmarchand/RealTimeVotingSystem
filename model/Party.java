package model;

import java.io.Serializable;

public class Party implements Serializable {
	
	public static final String LIBERALS = "Liberals";
	public static final String CONSERVATIVES = "Conservatives";
	public static final String NDP = "NDP";
	
    private static final long serialVersionUID = -8743695743565242934L;
    
    private String name;
    private Candidate leader;

    public Party(String name) {
        this(name, null);
    }

    public Party(String name, Candidate leader) {
        this.name = name;
        this.leader = leader;
    }

    public Candidate getLeader() {
        return leader;
    }

    public String getName() {
        return name;
    }

    public void setLeader(Candidate leader) {
        this.leader = leader;
    }

    @Override
    public String toString() {
        String desc = "Party: " + name;
        if (leader != null) {
            desc += ", headed by " + leader.getName();
        }
        return desc;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Party))
            return false;

        if (obj == this)
            return true;

        Party rhs = (Party) obj;
        return rhs.name.equals(name) && rhs.leader.equals(leader);
    }
}
