public class ProcessOrder {
    // Result class has PreferredCustomersLines and Customers Lines
    protected class Result {
        protected PreferredCustomer[] PreferredCustomers;
        protected Customer[] Customers;

        protected Result(Customer[] Customers, PreferredCustomer[] PreferredCustomers) {
            this.Customers = Customers;
            this.PreferredCustomers = PreferredCustomers;
        }
    }

    public void run() throws IOException {
        //Wrap file name into File
        File transactionFileName = new File("orders4.dat");
        File preferredFileName = new File("preferred4.dat");
        File customerFileName = new File("customer4.dat");

        //Read files into array of lines of string
        String[] arrayOfCustomerLines = readFileIntoArrayOfLine(customerFileName);
        String[] arrayOfTransactionLines = readFileIntoArrayOfLine(transactionFileName);
        String[] arrayOfPreferredLines;

        //put each line into an array of object of Customer/Preferred
        Customer[] arrayOfCustomers = readCustomerFile(arrayOfCustomerLines);
        PreferredCustomer[] arrayOfPreferred;

        Result result;

        //Check whether the preferred exists
        boolean preferredFileExists = preferredFileName.exists();

        //If the preferred file exists
        if (preferredFileExists) {
            arrayOfPreferredLines = readFileIntoArrayOfLine(preferredFileName);
            arrayOfPreferred = readPreferredFile(arrayOfPreferredLines);
            result = new Result(arrayOfCustomers, arrayOfPreferred);
            for (int count = 0; count < arrayOfTransactionLines.length; count++) {
                //get customer ID of the transaction
                int ID = ID(arrayOfTransactionLines[count]);

                // calculate how much the customer spent
                double amountSpent = amountSpent(arrayOfTransactionLines[count]);

                //get the index of the ID in customer or preferred
                int isCustomer = isCustomer(arrayOfCustomers, ID);
                int isPreferred = isPreferred(arrayOfPreferred, ID);

                //If the ID belongs to preferred, call the processPreferred function
                if (isCustomer < 0 && isPreferred >= 0) {
                    result = processPreferred(arrayOfCustomers, arrayOfPreferred, isPreferred, amountSpent, preferredFileName);
                    arrayOfCustomers = result.Customers;
                    arrayOfPreferred = result.PreferredCustomers;
                    // If the ID belongs to customer, call the processCustomer function
                } else if (isCustomer >= 0 && isPreferred < 0) {
                    result = processCustomer(arrayOfCustomers, arrayOfPreferred, isCustomer, amountSpent);
                    arrayOfCustomers = result.Customers;
                    arrayOfPreferred = result.PreferredCustomers;
                }
            }
            writeToCustomerFile(result.Customers, customerFileName);
            writeToPreferredFile(result.PreferredCustomers, preferredFileName);
        }

        //If the preferred file does not exist or exist but is empty
        else if (!preferredFileExists) {
            //If the preferred file does not exist, create one
            preferredFileName.createNewFile();
            arrayOfPreferred = new PreferredCustomer[0];

            for (int count = 0; count < arrayOfTransactionLines.length; count++) {
                //get customer ID of the transaction
                int ID = ID(arrayOfTransactionLines[count]);

                // calculate how much the customer spent
                double amountSpent = amountSpent(arrayOfTransactionLines[count]);
                int isCustomer = isCustomer(arrayOfCustomers, ID);
                //Process customer
                result = processCustomer(arrayOfCustomers, arrayOfPreferred, isCustomer, amountSpent);
                arrayOfPreferred = result.PreferredCustomers;
                arrayOfCustomers = result.Customers;
            }
            writeToPreferredFile(arrayOfPreferred, preferredFileName);
            writeToCustomerFile(arrayOfCustomers, customerFileName);

            //If the preferred file is empty, delete it
            if (preferredFileName.length() <= 0) {
                preferredFileName.delete();
            }
        }
    }

    // Write the updated array of customer into a temporary file,
    // then delete the old file, and use the old name to rename the new
    public void writeToCustomerFile(Customer[] array, File oldFile) throws IOException {
        String tmp = "tmp.txt";
        BufferedWriter out = new BufferedWriter((new FileWriter(tmp)));
        for (int count = 0; count < array.length; count++) {
            out.write(array[count].toString());
            if(count != array.length - 1)
                out.write("\n");
        }
        out.flush();
        out.close();

        oldFile.delete();

        File newFile = new File(tmp);
        newFile.renameTo(oldFile);

    }

    public void writeToPreferredFile(PreferredCustomer[] array, File oldFile) throws IOException {
        String tmp = "tmp.txt";
        BufferedWriter out = new BufferedWriter((new FileWriter(tmp)));
        for (int count = 0; count < array.length; count++) {
            out.write(array[count].toString());
            if(count != array.length - 1)
                out.write("\n");
        }
        out.flush();
        out.close();

        oldFile.delete();

        File newFile = new File(tmp);
        newFile.renameTo(oldFile);

    }

    //This function is called when the customer ID in transaction belongs to the regular customers
    public Result processCustomer(Customer[] arrayOfCustomer, PreferredCustomer[] arrayOfPreferred,
                                  int isCustomer, double amountSpent) {

        //Add the current amount spent into the previous amount spent
        arrayOfCustomer[isCustomer].updateAmountSpent(amountSpent);

        //If the customer gets promoted
        if (arrayOfCustomer[isCustomer].isPromoted()) {

            //move the customer from regular to preferred
            arrayOfPreferred = moveToPreferred(arrayOfCustomer[isCustomer], arrayOfPreferred);
            //PreferredCustomer[] PreferredCustomers = readPreferredFile(PreferredCustomersLines);

            //remove that customer in the regular customer array
            arrayOfCustomer = removePromotedCustomer(arrayOfCustomer, isCustomer);
            //Customer[] Customers = readCustomerFile(CustomersLines);

            return new Result(arrayOfCustomer, arrayOfPreferred);
        } else {
            for (int i = 0; i < arrayOfCustomer.length; i++) {
                arrayOfCustomer[i] = arrayOfCustomer[i];
            }
            return new Result(arrayOfCustomer, arrayOfPreferred);
        }

    }

    //This function is called when the customer ID in the transaction file belongs to preferred customers
    public Result processPreferred(Customer[] arrayOfCustomer, PreferredCustomer[] arrayOfPreferred, int isPreferred,
                                 double amountSpent, File fileName) throws IOException {
        //get the discount percentage
        double discount = arrayOfPreferred[isPreferred].getDiscountPercentage();
        //get the amount spent after discount
        double amountAfterDiscount = amountSpent - (amountSpent * discount);
        //update the amountSpent
        arrayOfPreferred[isPreferred].updateAmountSpent(amountAfterDiscount);
        //update the discount percentage
        arrayOfPreferred[isPreferred].updateDiscountPercentage();
        PreferredCustomer[] PreferredCustomers = new PreferredCustomer[arrayOfPreferred.length];
        //loop through the array of preferred Customer, and convert each object into string
        for (int count = 0; count < arrayOfPreferred.length; count++) {
            PreferredCustomers[count] = arrayOfPreferred[count];
        }
        return new Result(arrayOfCustomer, PreferredCustomers);
    }

    //Remove the promoted customer out of the regular customer array
    public Customer[] removePromotedCustomer(Customer[] arrayOfCustomer, int isCustomer) {

        //the new array size decreases by 1
        int newArraySize = arrayOfCustomer.length - 1;
        Customer[] Customers = new Customer[newArraySize];

        //If there is only one customer in the customer file
        if(newArraySize == 0)
            Customers = new Customer[0];
        //If the removed customer locates at the end of the file
        else if (newArraySize > 0 && isCustomer == arrayOfCustomer.length - 1)
            //copy the old array into a new array except the last customer
            System.arraycopy(arrayOfCustomer, 0, Customers, 0, isCustomer);
            //If the removed customer locates at the beginning of the file
        else if (newArraySize > 0 && isCustomer == 0)
            //start to copy from the second element in the old array into a new array
            System.arraycopy(arrayOfCustomer, 1, Customers, 0, newArraySize);
        //If the removed customer locates in the middle of the file
        else {
            //copy from the beginning of the old array into the new array except the promoted customer
            System.arraycopy(arrayOfCustomer, 0, Customers, 0, isCustomer);
            System.arraycopy(arrayOfCustomer, isCustomer + 1, Customers, isCustomer,
                    newArraySize - isCustomer);
        }
        return Customers;
    }

    //Move to promoted customer to preferred array
    public PreferredCustomer[] moveToPreferred(Customer customer, PreferredCustomer[] arrayOfPreferred) {
        int newArraySize;
        PreferredCustomer newPreferred = new PreferredCustomer(customer.getID(), customer.getFirstName(),
                customer.getLastName(), customer.getAmountSpent(), 0.0);
        // get the discount percentage based on the total amountSpent
        newPreferred.updateDiscountPercentage();
        // increase the new array size by 1
        if(arrayOfPreferred.length >= 1){
            newArraySize = arrayOfPreferred.length + 1;
            PreferredCustomer[] PreferredCustomers = new PreferredCustomer[newArraySize];
            //copy all the preferred customers into a new array
            System.arraycopy(arrayOfPreferred, 0, PreferredCustomers, 0, newArraySize - 1);
            //copy the new promoted customer at the end of the new array
            PreferredCustomers[newArraySize - 1] = newPreferred;
            return PreferredCustomers;
        } else {
            newArraySize = 1;
            PreferredCustomer[] PreferredCustomers = new PreferredCustomer[newArraySize];
            PreferredCustomers[0] = newPreferred;
            return PreferredCustomers;
        }
    }

    //check whether the customer in the transaction belongs to regular customer
    public int isCustomer(Customer[] arrayOfCustomer, int ID) {
        int isCustomer = -1;
        int c = 0;
        do{
            if (ID == arrayOfCustomer[c].getID())
                //get the index where the ID in transaction matches the ID in regular customer file
                isCustomer = c;
                // if the customer is not the the regular customers file, return -1
            else isCustomer = -1;
            c++;
        } while (isCustomer == -1 && c < arrayOfCustomer.length);

        return isCustomer;
    }

    //check whether the customer in the transaction belongs to preferred customers
    public int isPreferred(PreferredCustomer[] arrayOfCustomer, int ID) {
        int isCustomer = -1;
        int c = 0;
        while (isCustomer == -1 && c < arrayOfCustomer.length) {
            if (ID == arrayOfCustomer[c].getID())
                //get the index where the ID in transaction matches the ID in preferred customer file
                isCustomer = c;
                // if the customer is not the the preferred customers file, return -1
            else isCustomer = -1;
            c++;
        }
        return isCustomer;
    }

    //read a file into an array of String
    public String[] readFileIntoArrayOfLine(File filename) throws IOException {
        FileReader fileReader = new FileReader(filename);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        List<String> lines = new ArrayList<>();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            lines.add(line);
        }
        bufferedReader.close();
        return lines.toArray(new String[lines.size()]);
    }

    //process each line into a customer object
    public Customer[] readCustomerFile(String[] arrayOfCustomerLines) {
        Customer[] arrayOfCustomers = new Customer[arrayOfCustomerLines.length];
        for (int count = 0; count < arrayOfCustomerLines.length; count++) {
            String[] strArray = arrayOfCustomerLines[count].split(" ");
            int ID = Integer.parseInt(strArray[0]);
            String firstName = strArray[1];
            String lastName = strArray[2];
            double amountSpent = (Double.parseDouble(strArray[3]));

            arrayOfCustomers[count] = new Customer(ID, firstName, lastName, amountSpent);
        }
        return arrayOfCustomers;
    }

    //process each line into a preferred object
    public static PreferredCustomer[] readPreferredFile(String[] arrayOfPreferredCustomerLines) {
        PreferredCustomer[] arrayOfPreferred = new PreferredCustomer[arrayOfPreferredCustomerLines.length];
        for (int count = 0; count < arrayOfPreferredCustomerLines.length; count++) {
            String[] strArray = arrayOfPreferredCustomerLines[count].split(" ");
            int ID = Integer.parseInt(strArray[0]);
            String firstName = strArray[1];
            String lastName = strArray[2];
            double amountSpent = Double.parseDouble(strArray[3]);
            double discountPercentage = (Double.parseDouble(strArray[4].split("%")[0]) / 100);

            arrayOfPreferred[count] = new PreferredCustomer(ID, firstName, lastName, amountSpent, discountPercentage);
        }
        return arrayOfPreferred;
    }

    //get the amountSpent for each transaction
    public static double amountSpent(String line) {
        String[] strArray = line.split(" ");
        double radius = Double.parseDouble(strArray[1]);
        double height = Double.parseDouble(strArray[2]);
        double ounces = Double.parseDouble(strArray[3]);
        double ouncePrice = Double.parseDouble(strArray[4]);
        double squareInchPrice = Double.parseDouble(strArray[5]);
        int quantity = Integer.parseInt(strArray[6]);
        //calculate the amountSpent
        double amountSpent = priceCalculation(radius, height, ounces, ouncePrice, squareInchPrice, quantity);

        return amountSpent;
    }

    //get the customer ID for each transaction
    public static int ID(String line) {
        String[] strArray = line.split(" ");
        int ID = Integer.parseInt(strArray[0]);
        return ID;
    }

    //calculate the drink and/or design of each transaction
    public static double priceCalculation(double radius, double height, double ounces, double ouncePrice,
                                          double squareInchPrice, int quantity) {
        NumberFormat formatter = new DecimalFormat("##.##");
        double containerSize = 2 * Math.PI * pow(radius, 2) + 2 * Math.PI * radius * height;
        double drinkPrice = ounces * ouncePrice;
        double designPrice = containerSize * squareInchPrice;
        double total = Double.parseDouble(formatter.format((drinkPrice + designPrice) * quantity));
        return total;
    }
}