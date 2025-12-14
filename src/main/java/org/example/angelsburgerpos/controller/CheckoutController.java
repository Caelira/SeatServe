package org.example.angelsburgerpos.controller;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.angelsburgerpos.model.CartItem;
import org.example.angelsburgerpos.model.Receipt;
import org.example.angelsburgerpos.util.InventoryManager;
import org.example.angelsburgerpos.util.SalesLogger;

import java.io.IOException;

public class CheckoutController {

    @FXML private Label totalLabel;
    @FXML private TextField cashInput;
    @FXML private Label changeLabel;

    private double totalAmount;
    private ObservableList<CartItem> cartItems;
    private Runnable onComplete;

    public void setOrderData(double total, ObservableList<CartItem> items, Runnable onComplete) {
        this.totalAmount = total;
        this.cartItems = items;
        this.onComplete = onComplete;
        totalLabel.setText("P " + String.format("%.2f", total));
    }

    @FXML
    private void confirmPayment() {
        try {
            double cash = Double.parseDouble(cashInput.getText());

            if (cash < totalAmount) {
                changeLabel.setText("Not enough cash!");
                return;
            }

            double change = cash - totalAmount;

            Receipt receipt = new Receipt(cartItems, totalAmount, cash, change);
            receipt.saveToFile();

            SalesLogger.logSale(totalAmount);

            InventoryManager.reduceStock(cartItems);

            openReceiptWindow(receipt.getContent());
            closeWindow();

            if (onComplete != null) {
                onComplete.run();
            }

        } catch (NumberFormatException e) {
            changeLabel.setText("Invalid Amount");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openReceiptWindow(String receiptText) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/angelsburgerpos/receipt-view.fxml"));
        Parent root = loader.load();

        ReceiptController controller = loader.getController();
        controller.setReceiptText(receiptText);

        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Receipt");
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    private void closeWindow() {
        Stage stage = (Stage) totalLabel.getScene().getWindow();
        stage.close();
    }
}