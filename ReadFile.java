public class ReadFile {
    public static Customer[] readCustomerFile(String[] CustomerLines) {
        Customer[] Customers = new Customer[CustomerLines.length];
        for (int count = 0; count < CustomerLines.length; count++) {
            String[] strArray = CustomerLines[count].split(" ");
            int ID = Integer.parseInt(strArray[0]);
            String firstName = strArray[1];
            String lastName = strArray[2];
            double amountSpent = (Double.parseDouble(strArray[3]));

            Customers[count] = new Customer(ID, firstName, lastName, amountSpent);
        }
        return Customers;
    }

    public static PreferredCustomer[] readPreferredFile(String[] PreferredCustomersCustomerLines) {
        PreferredCustomer[] PreferredCustomers = new PreferredCustomer[PreferredCustomersCustomerLines.length];

        for (int count = 0; count < PreferredCustomersCustomerLines.length; count++) {
            String[] strArray = PreferredCustomersCustomerLines[count].split(" ");
            int ID = Integer.parseInt(strArray[0]);
            String firstName = strArray[1];
            String lastName = strArray[2];
            double amountSpent = Double.parseDouble(strArray[3]);
            double discountPercentage = (Double.parseDouble(strArray[4].split("%")[0]) / 100);

            PreferredCustomers[count] = new PreferredCustomer(ID, firstName, lastName, amountSpent, discountPercentage);
        }
        return PreferredCustomers;
    }
}