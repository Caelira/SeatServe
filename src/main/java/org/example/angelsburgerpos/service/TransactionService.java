package org.example.angelsburgerpos.service;

import org.example.angelsburgerpos.model.CartItem;
import org.example.angelsburgerpos.model.Receipt;
import org.example.angelsburgerpos.util.InventoryManager;
import org.example.angelsburgerpos.util.SalesLogger;

import java.util.List;

public class TransactionService {

    public static Receipt processTransaction(List<CartItem> items, double totalAmount, double cash, String cashierName) {
        double change = cash - totalAmount;

        Receipt receipt = new Receipt(items, totalAmount, cash, change);
        receipt.saveToFile();
        SalesLogger.logSale(totalAmount, cashierName);

        InventoryManager.reduceStock(items);

        return receipt;
    }
}