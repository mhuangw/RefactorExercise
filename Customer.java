package Customer;

public class Customer {
    private String firstName;
    private String lastName;
    private int ID;
    private double amountSpent;
    private boolean isPromoted;

    public Customer(int ID, String firstName, String lastName, double amountSpent) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.ID = ID;
        this.amountSpent = amountSpent;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getID() {
        return this.ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public double getAmountSpent() {
        return this.amountSpent;
    }

    public void setAmountSpent(double amountSpent) {
        this.amountSpent = amountSpent;
    }

    public void updateAmountSpent(double additionalAmountSpent) {
        this.amountSpent += additionalAmountSpent;
    }

    public boolean isPromoted() {
        if (amountSpent >= 150.00) {
            isPromoted = true;
        }
        return this.isPromoted;
    }

    @Override
    public String toString() {
        String string = ID + " " + firstName + " " + lastName + " " + amountSpent;
        return string;
    }
}