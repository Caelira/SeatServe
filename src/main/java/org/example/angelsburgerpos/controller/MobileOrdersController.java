package org.example.angelsburgerpos.controller;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.angelsburgerpos.model.CartItem;
import org.example.angelsburgerpos.model.MobileOrder;
import org.example.angelsburgerpos.model.Product;
import org.example.angelsburgerpos.service.ProductService;
import org.example.angelsburgerpos.service.ProductServiceImp;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MobileOrdersController implements Initializable {

    @FXML
    private FlowPane acceptedContainer;
    @FXML
    private TextField codeField;

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
        if (acceptedContainer == null) return;

        Platform.runLater(() -> {
            acceptedContainer.getChildren().clear();

            if (orderList != null) {
                for (MobileOrder order : orderList) {
                    if ("PREPARING".equals(order.getStatus()) || "READY".equals(order.getStatus())) {
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
        });
    }

    @FXML
    private void confirmPayment() {
        String code = codeField.getText().trim();
        if (code.isEmpty()) {
            return;
        }

        if (orderList != null) {
            for (MobileOrder order : orderList) {
                if (order.getOrderId().equals(code) && "PENDING_PAYMENT".equals(order.getStatus())) {

                    openPaymentWindow(order);

                    codeField.clear();
                    return;
                }
            }
        }
    }

    private void openPaymentWindow(MobileOrder order) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/angelsburgerpos/checkout-view.fxml"));
            Parent root = loader.load();

            CheckoutController controller = loader.getController();

            ProductService productService = new ProductServiceImp();
            List<CartItem> cartItems = new ArrayList<>();
            double total = 0;

            if (order.getItems() != null) {
                for (var mobileItem : order.getItems()) {
                    Product p = productService.findById(mobileItem.productId);
                    if (p != null) {
                        CartItem item = new CartItem(p, mobileItem.quantity);
                        cartItems.add(item);
                        total += item.getTotal();
                    }
                }
            }

            String cashierName = "Unknown";
            if (dashboard != null && dashboard.getUser() != null) {
                cashierName = dashboard.getUser().getFullname();
            }

            controller.setOrderData(total, FXCollections.observableArrayList(cartItems), cashierName, () -> {
                if (dashboard != null) {
                    dashboard.finalizeMobileOrder(order);
                }
                order.setStatus("PREPARING");
                refreshQueue();
            });

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Payment - Mobile Order #" + order.getOrderId());
            stage.setScene(new Scene(root));
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void markAsReady(MobileOrder order) {
        order.setStatus("READY");
        refreshQueue();
        PauseTransition delay = new PauseTransition(Duration.minutes(5));
        delay.setOnFinished(event -> {
            if (orderList != null) {
                orderList.remove(order);
                System.out.println("Order #" + order.getOrderId() + " auto-deleted.");
            }
        });
        delay.play();
    }
}