package org.example.angelsburgerpos;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.angelsburgerpos.DAO.ProductDAO;
import org.example.angelsburgerpos.model.Product;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public class AngelsBurgerPOS extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(AngelsBurgerPOS.class.getResource("login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Login");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {


        launch();
    }
}