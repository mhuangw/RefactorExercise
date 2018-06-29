package Customer;

public class PreferredCustomer extends Customer {
    public double discountPercentage;

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

    public void updateAmountSpend(double spent){
        this.amountSpent += spent;
    }

    public void updateDiscountPercentage(){
        double spent = getAmountSpent();
        if (spent >= 150.0 && spent < 200.0)
            setDiscountPercentage(0.05);
        else if (spent >= 200.0 && spent < 350.0)
            setDiscountPercentage(0.07);
        else if(spent > 350.0)
            setDiscountPercentage(0.1);
    }

    @Override
    public String toString(){
        String string = ID + " " + firstName + " " + lastName + " " + amountSpent + " " + (int)(discountPercentage*100) + "%";
        return string;
    }
}