package org.example.angelsburgerpos.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.angelsburgerpos.model.SaleRecord;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Scanner;

public class SalesController implements Initializable {

    @FXML
    private TableView<SaleRecord> salesTable;

    @FXML
    private TableColumn<SaleRecord, String> colDate;

    @FXML
    private TableColumn<SaleRecord, String> colTime;

    @FXML
    private TableColumn<SaleRecord, Double> colAmount;

    @FXML
    private Label totalSalesLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colTime.setCellValueFactory(new PropertyValueFactory<>("time"));
        colAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));

        loadSalesData();
    }

    private void loadSalesData() {
        ObservableList<SaleRecord> list = FXCollections.observableArrayList();
        double grandTotal = 0;

        File file = new File("sales_log.csv");
        if (file.exists()) {
            try (Scanner scanner = new Scanner(file)) {
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    String[] parts = line.split(",");
                    if (parts.length == 3) {
                        String date = parts[0];
                        String time = parts[1];
                        double amount = Double.parseDouble(parts[2]);

                        list.add(new SaleRecord(date, time, amount));
                        grandTotal += amount;
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        salesTable.setItems(list);
        totalSalesLabel.setText("Total Revenue: P " + String.format("%.2f", grandTotal));
    }
}