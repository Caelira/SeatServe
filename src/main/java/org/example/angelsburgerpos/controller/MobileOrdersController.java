package org.example.angelsburgerpos.controller;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.FlowPane;
import org.example.angelsburgerpos.model.MobileOrder;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MobileOrdersController implements Initializable {

    @FXML private FlowPane pendingContainer;
    @FXML private FlowPane acceptedContainer;

    private ObservableList<MobileOrder> orderList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {}

    public void setOrderList(ObservableList<MobileOrder> orderList) {
        this.orderList = orderList;
        refreshQueue();
        orderList.addListener((ListChangeListener<MobileOrder>) change -> {
            while (change.next()) {
                refreshQueue();
            }
        });
    }

    private void refreshQueue() {
        pendingContainer.getChildren().clear();
        acceptedContainer.getChildren().clear();

        for (MobileOrder order : orderList) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/angelsburgerpos/order-card-view.fxml"));
                Parent cardNode = loader.load();

                OrderCardController cardController = loader.getController();
                cardController.setData(order, this);

                if ("ACCEPTED".equals(order.getStatus())) {
                    acceptedContainer.getChildren().add(cardNode);

                } else {
                    pendingContainer.getChildren().add(cardNode);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void acceptOrder(MobileOrder order) {
        order.setStatus("ACCEPTED");
        refreshQueue();
    }

    public void declineOrder(MobileOrder order) {
        orderList.remove(order);
    }
}