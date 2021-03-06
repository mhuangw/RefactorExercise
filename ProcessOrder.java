import Customer.*;
import java.io.IOException;
import java.io.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import static java.lang.Math.pow;

public class ProcessOrder {
    protected class Result {
        protected PreferredCustomer[] PreferredCustomers;
        protected Customer[] Customers;

        protected Result(Customer[] Customers, PreferredCustomer[] PreferredCustomers) {
            this.Customers = Customers;
            this.PreferredCustomers = PreferredCustomers;
        }
    }

    public void run() throws IOException {
        File transactionFileName = new File("orders4.dat");
        File preferredFileName = new File("preferred4.dat");
        File customerFileName = new File("customer4.dat");

        String[] CustomerLines = ReadFile.readFileIntoArrayOfLine(customerFileName);
        String[] TransactionLines = ReadFile.readFileIntoArrayOfLine(transactionFileName);
        String[] PreferredLines;

        Customer[] Customers = ReadFile.readCustomerFile(CustomerLines);
        PreferredCustomer[] PreferredCustomers;

        Result result;

        boolean preferredFileExists = preferredFileName.exists();

        if (preferredFileExists) {
            PreferredLines = ReadFile.readFileIntoArrayOfLine(preferredFileName);
            PreferredCustomers = ReadFile.readPreferredFile(PreferredLines);
            result = new Result(Customers, PreferredCustomers);

            ProcessTransactionAndGetResult(true);

            writeToCustomerFile(result.Customers, customerFileName);
            writeToPreferredFile(result.PreferredCustomers, preferredFileName);
        }

        else if (!preferredFileExists) {
            preferredFileName.createNewFile();
            PreferredCustomers = new PreferredCustomer[0];

            ProcessTransactionAndGetResult(false);

            WriteToFile.writeToPreferredFile(PreferredCustomers, preferredFileName);
            WriteToFile.writeToCustomerFile(Customers, customerFileName);

            if (preferredFileName.length() <= 0) {
                preferredFileName.delete();
            }
        }
    }

    public void processTransactionAndGetResult(boolean preferredFileExists) {
        for (int count = 0; count < TransactionLines.length; count++) {
            int ID = getCustomerID(TransactionLines[count]);
            double amountSpent = amountSpent(TransactionLines[count]);
            int isCustomer = CustomerStatus.isCustomer(Customers, ID);
            int isPreferred = CustomerStatus.isPreferred(PreferredCustomers, ID);

            if (CustomerIsPreferred) {
                result = ProcessUser.processPreferred(Customers, PreferredCustomers, isPreferred, amountSpent, preferredFileName);
            } else {
                result = ProcessUser.processCustomer(Customers, PreferredCustomers, isCustomer, amountSpent);
            }

            PreferredCustomers = result.PreferredCustomers;
            Customers = result.Customers;
        }
    }

    public boolean customerIsPreferred() {
        if (isCustomer < 0 && isPreferred >= 0) {
            return true;
        } else {
            return false;
        }
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