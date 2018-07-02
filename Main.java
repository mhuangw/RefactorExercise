import Customer.*;
import java.io.IOException;
import java.io.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import static java.lang.Math.pow;

public class Main {
    public static void main(String[] args) throws IOException {
        ProcessOrder processOrder = new ProcessOrder();
        processOrder.run();
    }
}