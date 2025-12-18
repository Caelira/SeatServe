package org.example.angelsburgerpos.util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SalesLogger {

    public static void logSale(double totalAmount) {
        try (FileWriter fw = new FileWriter(AppConfig.SALES_LOG_FILE, true);
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