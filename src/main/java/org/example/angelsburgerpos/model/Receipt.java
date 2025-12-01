package org.example.angelsburgerpos.model;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Receipt {
    private List<CartItem> items;
    private double total;
    private double cash;
    private double change;
    private String content;

    public Receipt(List<CartItem> items, double total, double cash, double change) {
        this.items = items;
        this.total = total;
        this.cash = cash;
        this.change = change;
        this.content = generateContent();
    }

    private String generateContent() {
        StringBuilder sb = new StringBuilder();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        String header = "ANGELS BURGER POS";
        int pad = (40 - header.length()) / 2;
        sb.append(" ".repeat(Math.max(0, pad))).append(header).append("\n");

        sb.append("----------------------------------------\n");
        sb.append("Date: ").append(LocalDateTime.now().format(dtf)).append("\n");
        sb.append("----------------------------------------\n");

        for (CartItem item : items) {
            sb.append(item.getName()).append("\n");

            String qtyPrice = String.format("   x%d", item.getQuantity());
            String total = String.format("P %.2f", item.getTotal());

            int spaceCount = 40 - qtyPrice.length() - total.length();
            sb.append(qtyPrice).append(" ".repeat(Math.max(0, spaceCount))).append(total).append("\n");
        }

        sb.append("----------------------------------------\n");

        String totalLabel = "TOTAL:";
        String totalVal = String.format("P %.2f", total);
        sb.append(totalLabel).append(" ".repeat(40 - totalLabel.length() - totalVal.length())).append(totalVal).append("\n");

        String cashLabel = "CASH:";
        String cashVal = String.format("P %.2f", cash);
        sb.append(cashLabel).append(" ".repeat(40 - cashLabel.length() - cashVal.length())).append(cashVal).append("\n");

        String changeLabel = "CHANGE:";
        String changeVal = String.format("P %.2f", change);
        sb.append(changeLabel).append(" ".repeat(40 - changeLabel.length() - changeVal.length())).append(changeVal).append("\n");

        sb.append("----------------------------------------\n");

        String footer = "THANK YOU!";
        int footerPad = (40 - footer.length()) / 2;
        sb.append(" ".repeat(Math.max(0, footerPad))).append(footer).append("\n");

        return sb.toString();
    }

    public String getContent() {
        return content;
    }

    public void saveToFile() {
        String filename = "receipt_" + System.currentTimeMillis() + ".txt";
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}