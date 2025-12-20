package org.example.angelsburgerpos.controller;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable; // Import this!
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.angelsburgerpos.model.CartItem;
import org.example.angelsburgerpos.model.Receipt;
import org.example.angelsburgerpos.service.TransactionService;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
public class CheckoutController implements Initializable {

    @FXML private Label totalLabel;
    @FXML private TextField cashInput;
    @FXML private Label changeLabel;

    private double totalAmount;
    private ObservableList<CartItem> cartItems;
    private Runnable onComplete;
    private String cashierName;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cashInput.textProperty().addListener((observable, oldValue, newValue) -> {
            calculateChange(newValue);
        });
    }

    // calculate and update label
    private void calculateChange(String cashText) {
        try {
            if (cashText.isEmpty()) {
                changeLabel.setText("Change: P 0.00");
                changeLabel.setStyle("-fx-text-fill: black;");
                return;
            }

            double cash = Double.parseDouble(cashText);
            double change = cash - totalAmount;

            if (change < 0) {
                changeLabel.setText("Not enough cash");
                changeLabel.setStyle("-fx-text-fill: red;");
            } else {
                changeLabel.setText("Change: P " + String.format("%.2f", change));
                changeLabel.setStyle("-fx-text-fill: #2e7d32;");
            }

        } catch (NumberFormatException e) {
            changeLabel.setText("Invalid Amount");
        }
    }

    public void setOrderData(double total, ObservableList<CartItem> items, String cashierName, Runnable onComplete) {
        this.totalAmount = total;
        this.cartItems = items;
        this.cashierName = cashierName;
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

            Receipt receipt = TransactionService.processTransaction(cartItems, totalAmount, cash, cashierName);

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
        stage.setResizable(false);
        stage.show();
    }

    @FXML
    private void closeWindow() {
        Stage stage = (Stage) totalLabel.getScene().getWindow();
        stage.close();
    }
}