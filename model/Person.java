package model;

import java.io.Serializable;

public class Person implements Serializable {

    private static final long serialVersionUID = 3103840932947226274L;

    protected String firstName;
    protected String lastName;
    protected Address address;
    protected int SIN;


    public Person(String firstName, String lastName, Address address, int SIN) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.SIN = SIN;
    }

    public String getName() {
        return firstName + " " + lastName;
    }

    public int getSIN() {
        return SIN;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Person))
            return false;

        if (obj == this)
            return true;

        Person rhs = (Person) obj;
        return rhs.SIN == SIN;
    }

    @Override
    public String toString() {
        return getName();
    }

}
