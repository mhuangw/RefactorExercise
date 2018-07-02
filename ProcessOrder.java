import Customer.*;
import java.io.IOException;
import java.io.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import static java.lang.Math.pow;

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

        String[] CustomerLines = readFileIntoArrayOfLine(customerFileName);
        String[] TransactionLines = readFileIntoArrayOfLine(transactionFileName);
        String[] PreferredLines;

        Customer[] Customers = readCustomerFile(CustomerLines);
        PreferredCustomer[] PreferredCustomers;

        Result result;

        boolean preferredFileExists = preferredFileName.exists();

        if (preferredFileExists) {
            PreferredLines = readFileIntoArrayOfLine(preferredFileName);
            PreferredCustomers = readPreferredFile(PreferredLines);
            result = new Result(Customers, PreferredCustomers);

            ProcessTransactionAndGetResult(true);

            writeToCustomerFile(result.Customers, customerFileName);
            writeToPreferredFile(result.PreferredCustomers, preferredFileName);
        }

        else if (!preferredFileExists) {
            preferredFileName.createNewFile();
            PreferredCustomers = new PreferredCustomer[0];

            ProcessTransactionAndGetResult(false);

            writeToPreferredFile(PreferredCustomers, preferredFileName);
            writeToCustomerFile(Customers, customerFileName);

            if (preferredFileName.length() <= 0) {
                preferredFileName.delete();
            }
        }
    }

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

    public void ProcessTransactionAndGetResult(boolean preferredFileExists) {
        for (int count = 0; count < TransactionLines.length; count++) {
            int ID = getCustomerID(TransactionLines[count]);
            double amountSpent = amountSpent(TransactionLines[count]);
            int isCustomer = isCustomer(Customers, ID);

            if (preferredFileExists) {
                int isPreferred = isPreferred(PreferredCustomers, ID);

                if (isCustomer < 0 && isPreferred >= 0) {
                    result = processPreferred(Customers, PreferredCustomers, isPreferred, amountSpent, preferredFileName);
                    Customers = result.Customers;
                    PreferredCustomers = result.PreferredCustomers;
                } else if (isCustomer >= 0 && isPreferred < 0) {
                    result = processCustomer(Customers, PreferredCustomers, isCustomer, amountSpent);
                    Customers = result.Customers;
                    PreferredCustomers = result.PreferredCustomers;
                }
            } else {
                result = processCustomer(Customers, PreferredCustomers, isCustomer, amountSpent);
                PreferredCustomers = result.PreferredCustomers;
                Customers = result.Customers;
            }
        }
    }

    //This function is called when the customer ID in transaction belongs to the regular customers
    public Result processCustomer(Customer[] arrayOfCustomer, PreferredCustomer[] PreferredCustomers,
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

    //This function is called when the customer ID in the transaction file belongs to preferred customers
    public Result processPreferred(Customer[] arrayOfCustomer, PreferredCustomer[] PreferredCustomers, int isPreferred,
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

    public Customer[] removePromotedCustomer(Customer[] arrayOfCustomer, int isCustomer) {
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

    public PreferredCustomer[] moveToPreferred(Customer customer, PreferredCustomer[] PreferredCustomers) {
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

    public int isCustomer(Customer[] arrayOfCustomer, int ID) {
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

    public int isPreferred(PreferredCustomer[] arrayOfCustomer, int ID) {
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

    public Customer[] readCustomerFile(String[] CustomerLines) {
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

    public static double amountSpent(String line) {
        String[] strArray = line.split(" ");
        double radius = Double.parseDouble(strArray[1]);
        double height = Double.parseDouble(strArray[2]);
        double ounces = Double.parseDouble(strArray[3]);
        double ouncePrice = Double.parseDouble(strArray[4]);
        double squareInchPrice = Double.parseDouble(strArray[5]);
        int quantity = Integer.parseInt(strArray[6]);
        double amountSpent = priceCalculation(radius, height, ounces, ouncePrice, squareInchPrice, quantity);

        return amountSpent;
    }

    public static int getCustomerID(String line) {
        String[] strArray = line.split(" ");
        int customerID = Integer.parseInt(strArray[0]);
        return customerID;
    }

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