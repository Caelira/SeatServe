package org.example.angelsburgerpos.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SalesLogger {

    private static final String LOG_DIR = "sales_logs/";

    public static void logSale(double totalAmount) {
        File dir = new File(LOG_DIR);
        if (!dir.exists()) {
            dir.mkdir();
        }

        String dateStr = LocalDate.now().toString();
        String filename = LOG_DIR + "sales_" + dateStr + ".csv";

        try (FileWriter fw = new FileWriter(filename, true);
             PrintWriter pw = new PrintWriter(fw)) {

            LocalDateTime now = LocalDateTime.now();
            String date = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String time = now.format(DateTimeFormatter.ofPattern("HH:mm:ss"));

            pw.printf("%s,%s,%.2f%n", date, time, totalAmount);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}