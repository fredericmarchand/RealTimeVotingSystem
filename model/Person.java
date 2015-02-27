package model;

public class Person {

    protected String firstName;
    protected String lastName;
    protected Address address;
    protected static int staticID = 1000;
    protected int id;

    public Person(String firstName, String lastName, Address address) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.id = ++staticID;
    }

    public String getName() {
        return firstName + " " + lastName;
    }

    public int getID() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Person))
            return false;

        if (obj == this)
            return true;

        Person rhs = (Person) obj;
        return rhs.id == id;
    }

}
