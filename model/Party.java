package model;
import java.io.Serializable;

public class Party implements Serializable {
	private static final long serialVersionUID = -4517388610617393544L;
    private String name;
    private Candidate leader;

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

    @Override
    public String toString() {
        return "Party: " + name + ", headed by " + leader;
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
