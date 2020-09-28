package sim.utils;

import java.io.*;
import java.util.logging.Logger;

public final class Utils {
    private static final Logger LOG = Logger.getLogger(Utils.class.getName());

    public static synchronized int getTrackNumber(String path) throws IOException {

        File file = new File(path);
        int trackNumber = readIntFromFile(file);
        LOG.info("Tracking Number "+trackNumber);
        return readIntFromFile(file);
    }

    private static int readIntFromFile(File file) throws IOException,NumberFormatException {
        int data = 1;
        if(file.exists()){
            try(BufferedReader bufferedReader = new BufferedReader(new FileReader(file))){
                data = Integer.parseInt(bufferedReader.readLine());
            }

        }
        return data;
    }

    private static void writeIntToFile(int data, File file, boolean create) throws IOException {
        if (create) {
            data = 1;
            LOG.info("Creating Tracking Number File: "+file.getName());
            if (!file.createNewFile()) {
                LOG.info("Creating Tracking Number File: Creation Failed ");
                throw new IOException("Failed to create Number Tracker File");

            }
            LOG.info("Creating Tracking Number File: Creation Success ");
        }

        try (FileWriter fileWriter = new FileWriter(file);
             PrintWriter printWriter = new PrintWriter(fileWriter)) {
            printWriter.println(data);
            printWriter.flush();
            LOG.info("Writing Tracking Number to File: Success :"+data);
        }
    }


}
