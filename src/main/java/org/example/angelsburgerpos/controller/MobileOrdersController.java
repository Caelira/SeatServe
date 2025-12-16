package org.example.angelsburgerpos.controller;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import org.example.angelsburgerpos.model.MobileOrder;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MobileOrdersController implements Initializable {

    @FXML private FlowPane acceptedContainer; // Only one container now
    @FXML private TextField codeField;

    private ObservableList<MobileOrder> orderList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {}

    public void setOrderList(ObservableList<MobileOrder> orderList) {
        this.orderList = orderList;
        refreshQueue();
        orderList.addListener((ListChangeListener<MobileOrder>) change -> {
            while (change.next()) refreshQueue();
        });
    }

    private void refreshQueue() {
        acceptedContainer.getChildren().clear();

        for (MobileOrder order : orderList) {
            if ("PREPARING".equals(order.getStatus())) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/angelsburgerpos/order-card-view.fxml"));
                    Parent cardNode = loader.load();
                    OrderCardController cardController = loader.getController();
                    cardController.setData(order, this);
                    acceptedContainer.getChildren().add(cardNode);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @FXML
    private void confirmPayment() {
        String code = codeField.getText().trim();
        if (code.isEmpty()) return;

        boolean found = false;
        for (MobileOrder order : orderList) {
            // Match the code
            if (order.getOrderId().equals(code)) {
                if ("PENDING_PAYMENT".equals(order.getStatus())) {
                    order.setStatus("PREPARING");
                    refreshQueue();
                    codeField.clear();
                    found = true;
                    return;
                }
            }
        }
        if (!found) {
            System.out.println("Code not found or already paid: " + code);
        }
    }

    public void markAsReady(MobileOrder order) {
        order.setStatus("READY");
        orderList.remove(order);
        refreshQueue();

        new Thread(() -> {
            try {
                Thread.sleep(300000); // 5 minutes waits
                orderList.remove(order);
                System.out.println("Order #" + order.getOrderId() + " auto-deleted from memory.");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}