public class ProcessUser {
	public static Result processCustomer(Customer[] arrayOfCustomer, PreferredCustomer[] PreferredCustomers,
                                  int isCustomer, double amountSpent) {
        arrayOfCustomer[isCustomer].updateAmountSpent(amountSpent);

        if (arrayOfCustomer[isCustomer].isPromoted()) {
            PreferredCustomers = moveToPreferred(arrayOfCustomer[isCustomer], PreferredCustomers);
            arrayOfCustomer = removePromotedCustomer(arrayOfCustomer, isCustomer);
            return new Result(arrayOfCustomer, PreferredCustomers);
        } else {
            for (int i = 0; i < arrayOfCustomer.length; i++) {
                arrayOfCustomer[i] = arrayOfCustomer[i];
            }
            return new Result(arrayOfCustomer, PreferredCustomers);
        }
    }

    public static Result processPreferred(Customer[] arrayOfCustomer, PreferredCustomer[] PreferredCustomers, int isPreferred,
                                 double amountSpent, File fileName) throws IOException {
        double discount = PreferredCustomers[isPreferred].getDiscountPercentage();
        double amountAfterDiscount = amountSpent - (amountSpent * discount);
        PreferredCustomers[isPreferred].updateAmountSpent(amountAfterDiscount);
        PreferredCustomers[isPreferred].updateDiscountPercentage();
        PreferredCustomer[] PreferredCustomers = new PreferredCustomer[PreferredCustomers.length];

        for (int count = 0; count < PreferredCustomers.length; count++) {
            PreferredCustomers[count] = PreferredCustomers[count];
        }
        return new Result(arrayOfCustomer, PreferredCustomers);
    }
}