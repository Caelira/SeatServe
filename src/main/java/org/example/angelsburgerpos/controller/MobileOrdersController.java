package org.example.angelsburgerpos.controller;

import javafx.animation.PauseTransition;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.util.Duration;
import org.example.angelsburgerpos.model.MobileOrder;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MobileOrdersController implements Initializable {

    @FXML private FlowPane acceptedContainer;
    @FXML private TextField codeField;

    private ObservableList<MobileOrder> orderList;
    private DashboardController dashboard;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    public void setOrderList(ObservableList<MobileOrder> orderList) {
        this.orderList = orderList;
        refreshQueue();
        orderList.addListener((ListChangeListener<MobileOrder>) change -> {
            while (change.next()) refreshQueue();
        });
    }

    public void setDashboard(DashboardController dashboard) {
        this.dashboard = dashboard;
    }

    private void refreshQueue() {
        acceptedContainer.getChildren().clear();

        if (orderList == null) return;

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

        for (MobileOrder order : orderList) {
            if (order.getOrderId().equals(code)) {
                if ("PENDING_PAYMENT".equals(order.getStatus())) {

                    if (dashboard != null) {
                        dashboard.finalizeMobileOrder(order);
                    }
                    order.setStatus("PREPARING");
                    refreshQueue();
                    codeField.clear();
                    return;
                }
            }
        }
    }

    public void markAsReady(MobileOrder order) {
        order.setStatus("READY");
        refreshQueue();

        PauseTransition delay = new PauseTransition(Duration.minutes(5));
        delay.setOnFinished(event -> {
            orderList.remove(order);
            System.out.println("Order #" + order.getOrderId() + " auto-deleted.");
        });
        delay.play();
    }
}