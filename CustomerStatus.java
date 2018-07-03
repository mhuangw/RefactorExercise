public class CustomerStatus {
	public static int isCustomer(Customer[] arrayOfCustomer, int ID) {
        int isCustomer = -1;
        int c = 0;

        do{
            if (ID == arrayOfCustomer[c].getID())
                isCustomer = c;
            else isCustomer = -1;
            c++;
        } while (isCustomer == -1 && c < arrayOfCustomer.length);
        return isCustomer;
    }

    public static int isPreferred(PreferredCustomer[] arrayOfCustomer, int ID) {
        int isCustomer = -1;
        int c = 0;

        while (isCustomer == -1 && c < arrayOfCustomer.length) {
            if (ID == arrayOfCustomer[c].getID())
                isCustomer = c;
            else isCustomer = -1;
            c++;
        }
        return isCustomer;
    }
}