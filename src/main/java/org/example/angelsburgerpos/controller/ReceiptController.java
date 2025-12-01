package org.example.angelsburgerpos.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class ReceiptController {

    @FXML
    private Label receiptLabel;

    public void setReceiptText(String text) {
        receiptLabel.setText(text);
    }

    @FXML
    private void closeWindow() {
        Stage stage = (Stage) receiptLabel.getScene().getWindow();
        stage.close();
    }
}