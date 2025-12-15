package org.example.angelsburgerpos.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
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
    private Label orderText;
    private ProductService service;
    @FXML
    private Button rejectButton;
    private MobileOrder order;
    private MobileOrdersController parentController;

    public OrderCardController() {
        service = new ProductServiceImp();
    }

    public void setData(MobileOrder order, MobileOrdersController parent){
        this.order = order;
        this.parentController = parent;

        customerNameLabel.setText(order.getCustomerName());
        orderText.setText(buildOrderString(order));
        if(order.getStatus().equalsIgnoreCase("accepted")){
            acceptButton.setVisible(false);
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
    @FXML
    void onAcceptClicked() {
        parentController.acceptOrder(order);
    }

    @FXML
    void onRejectClicked() {
        parentController.declineOrder(order);
    }
}
