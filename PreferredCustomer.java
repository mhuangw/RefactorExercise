package Customer;

public class PreferredCustomer extends Customer {
    private double discountPercentage;

    public PreferredCustomer(int ID, String firstName, String lastName, double amountSpent, double discountPercentage) {
        super(ID, firstName, lastName, amountSpent);
        this.discountPercentage = discountPercentage;
    }

    public double getDiscountPercentage() {
        return this.discountPercentage;
    }

    public void setDiscountPercentage(double discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public void updateAmountSpent(double additionalAmountSpent) {
        this.amountSpent += additionalAmountSpent;
    }

    public void updateDiscountPercentage() {
        if (discountRateApplies(150.0, 200.0)) {
            setDiscountPercentage(0.05);
        }
        else if (discountRateApplies(200.0, 350.0)) {
            setDiscountPercentage(0.07);
        }
        else if (discountRateApplies(350.0)) {
            setDiscountPercentage(0.1);
        }
    }

    public boolean discountRateApplies(double min, double max) {
        if (getAmountSpent() >= min && getAmountSpent() < max) {
            return true;
        } else {
            return false;
        }
    }

    public boolean discountRateApplies(double min) {
        if (getAmountSpent() >= min) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        String string = ID + " " + firstName + " " + lastName + " " + amountSpent + " " + (int)(discountPercentage*100) + "%";
        return string;
    }
}