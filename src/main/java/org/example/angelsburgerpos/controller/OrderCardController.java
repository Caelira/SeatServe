package org.example.angelsburgerpos.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import org.example.angelsburgerpos.model.MobileOrder;
import org.example.angelsburgerpos.model.Product;
import org.example.angelsburgerpos.service.ProductService;
import org.example.angelsburgerpos.service.ProductServiceImp;
import org.example.angelsburgerpos.util.OrderRequest;

import java.util.List;

public class OrderCardController {
    @FXML
    private Button acceptButton;

    @FXML
    private Label customerNameLabel;
    @FXML
    private AnchorPane cardContainer;
    @FXML
    private Button acceptBtn;
    @FXML
    private Label itemsLabel;


    @FXML
    private Label timeLabel;
    @FXML
    private Label orderIdLabel;
    @FXML
    private Label orderText;
    private ProductService service;
    @FXML
    private Button rejectButton;
    private MobileOrder order;
    private MobileOrdersController parentController;
    public OrderCardController() {
        service = new ProductServiceImp();
    }

    public void setData(MobileOrder order, MobileOrdersController parent) {
        this.order = order;
        this.parentController = parent;

        orderIdLabel.setText("ORDER #" + order.getOrderId());
        timeLabel.setText(order.getTimestamp());
        customerNameLabel.setText(order.getCustomerName());

        StringBuilder sb = new StringBuilder();
        if (order.getItems() != null) {
            for (var item : order.getItems()) {
                Product prodName = service.findById(item.productId);
                sb.append(item.quantity).append("x ").append(prodName.getName()).append("\n");
            }
        }
        itemsLabel.setText(sb.toString());
        if ("PENDING_PAYMENT".equals(order.getStatus())) {
            acceptBtn.setVisible(false);
            acceptBtn.setManaged(false);

            cardContainer.setStyle("-fx-background-color: #fff3e0; -fx-border-color: #ff9800; -fx-border-width: 2;");
        }
        else if ("PREPARING".equals(order.getStatus())) {
            acceptBtn.setVisible(true);
            acceptBtn.setManaged(true);
            acceptBtn.setText("NOTIFY (READY)");
            acceptBtn.setStyle("-fx-background-color: #2e7d32; -fx-text-fill: white; -fx-font-weight: bold;");

            acceptBtn.setOnAction(event -> {
                if (parentController != null) {
                    parentController.markAsReady(order);
                }
            });


            cardContainer.setStyle("-fx-background-color: #e8f5e9; -fx-border-color: #2e7d32; -fx-border-width: 2;");
        }
        else if ("READY".equals(order.getStatus())) {
            acceptBtn.setText("ALARMING...");
            acceptBtn.setDisable(true);
            acceptBtn.setStyle("-fx-background-color: #d32f2f; -fx-text-fill: white;");

        }
    }

    private String buildOrderString(MobileOrder order){

        StringBuilder builder = new StringBuilder();
        String orderToShow;
        for(OrderRequest req : order.getItems()){
            Product prod = service.findById(req.productId);
            builder.append("Product: " + prod.getName() + " \nQuantity: " + req.quantity).append("\n") ;

        }
        orderToShow = builder.toString();
        return orderToShow;
    }
}
