package model;

import java.io.Serializable;

public class Party implements Serializable {

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

    public void setLeader(Candidate leader) {
        this.leader = leader;
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
