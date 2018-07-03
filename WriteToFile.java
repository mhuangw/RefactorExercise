public class WriteToFile {
	public static void writeToCustomerFile(Customer[] array, File oldFile) throws IOException {
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

    public static void writeToPreferredFile(PreferredCustomer[] array, File oldFile) throws IOException {
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
}