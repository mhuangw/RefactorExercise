public class HandleCustomer {
    public static Customer[] removePromotedCustomer(Customer[] arrayOfCustomer, int isCustomer) {
        int newArraySize = arrayOfCustomer.length - 1;
        Customer[] Customers = new Customer[newArraySize];

        if(newArraySize == 0)
            Customers = new Customer[0];
        //If the removed customer locates at the end of the file
        else if (newArraySize > 0 && isCustomer == arrayOfCustomer.length - 1)
            System.arraycopy(arrayOfCustomer, 0, Customers, 0, isCustomer);
            //If the removed customer locates at the beginning of the file
        else if (newArraySize > 0 && isCustomer == 0)
            System.arraycopy(arrayOfCustomer, 1, Customers, 0, newArraySize);
        //If the removed customer locates in the middle of the file
        else {
            System.arraycopy(arrayOfCustomer, 0, Customers, 0, isCustomer);
            System.arraycopy(arrayOfCustomer, isCustomer + 1, Customers, isCustomer,
                    newArraySize - isCustomer);
        }
        return Customers;
    }

    public static PreferredCustomer[] moveToPreferred(Customer customer, PreferredCustomer[] PreferredCustomers) {
        int newArraySize;
        PreferredCustomer newPreferred = new PreferredCustomer(customer.getID(), customer.getFirstName(),
                customer.getLastName(), customer.getAmountSpent(), 0.0);

        newPreferred.updateDiscountPercentage();

        if(PreferredCustomers.length >= 1){
            newArraySize = PreferredCustomers.length + 1;
            PreferredCustomer[] PreferredCustomers = new PreferredCustomer[newArraySize];
            System.arraycopy(PreferredCustomers, 0, PreferredCustomers, 0, newArraySize - 1);
            PreferredCustomers[newArraySize - 1] = newPreferred;
            return PreferredCustomers;
        } else {
            newArraySize = 1;
            PreferredCustomer[] PreferredCustomers = new PreferredCustomer[newArraySize];
            PreferredCustomers[0] = newPreferred;
            return PreferredCustomers;
        }
    }
}