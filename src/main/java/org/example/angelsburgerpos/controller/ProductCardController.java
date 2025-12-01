package org.example.angelsburgerpos.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import org.example.angelsburgerpos.model.Product;

import java.net.URL;
import java.util.ResourceBundle;

public class ProductCardController implements Initializable {

    @FXML
    private AnchorPane productCard;

    @FXML
    private ImageView productImage;

    @FXML
    private Label productLabel;

    @FXML
    private Label productPrice;

    private Product product;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void setData(Product product){
        this.product = product;
        productLabel.setText(product.getName());
        productPrice.setText(product.getPrice().toString());
        try {
            String path = product.getImagePath();
            if (path != null && !path.isEmpty()) {
                Image image = new Image(getClass().getResourceAsStream(path));
                productImage.setImage(image);
            }
        } catch (Exception e) {
            System.out.println("Image not found for: " + product.getName());
        }
    }
}
