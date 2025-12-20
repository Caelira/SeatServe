package org.example.angelsburgerpos.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SalesLogger {

    public static void logSale(double totalAmount, String cashierName) {
        FileUtil.createDirectory(AppConfig.SALES_LOG_DIR);
        String dateStr = LocalDate.now().toString();
        String filename = AppConfig.SALES_LOG_DIR + "sales_" + dateStr + ".csv";
        LocalDateTime now = LocalDateTime.now();
        String date = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String time = now.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        String logEntry = String.format("%s,%s,%.2f,%s", date, time, totalAmount, cashierName);
        FileUtil.appendLine(filename, logEntry);
    }
}