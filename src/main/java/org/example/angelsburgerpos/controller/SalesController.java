package org.example.angelsburgerpos.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.angelsburgerpos.model.SaleRecord;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Scanner;

public class SalesController implements Initializable {

    @FXML private TableView<SaleRecord> salesTable;
    @FXML private TableColumn<SaleRecord, String> colDate;
    @FXML private TableColumn<SaleRecord, String> colTime;
    @FXML private TableColumn<SaleRecord, Double> colAmount;
    @FXML private Label totalSalesLabel;

    @FXML private ComboBox<String> filterCombo;

    private ObservableList<SaleRecord> masterList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colTime.setCellValueFactory(new PropertyValueFactory<>("time"));
        colAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        filterCombo.setItems(FXCollections.observableArrayList(
                "All Time", "Daily (Today)", "Weekly (This Week)", "Monthly (This Month)", "Yearly (This Year)"
        ));
        filterCombo.getSelectionModel().selectFirst();

        filterCombo.setOnAction(e -> filterData());

        loadSalesData();
    }

    private void loadSalesData() {
        masterList.clear();
        File file = new File("sales_log.csv");

        if (file.exists()) {
            try (Scanner scanner = new Scanner(file)) {
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    String[] parts = line.split(",");
                    if (parts.length == 3) {
                        try {
                            String date = parts[0];
                            String time = parts[1];
                            double amount = Double.parseDouble(parts[2]);

                            masterList.add(new SaleRecord(date, time, amount));
                        } catch (NumberFormatException e) {
                        }
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        filterData();
    }

    private void filterData() {
        String selected = filterCombo.getValue();
        ObservableList<SaleRecord> filteredList = FXCollections.observableArrayList();
        double grandTotal = 0;

        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        WeekFields weekFields = WeekFields.of(Locale.getDefault());

        for (SaleRecord record : masterList) {
            boolean include = false;

            try {
                LocalDate recordDate = LocalDate.parse(record.getDate(), formatter);

                if (selected.startsWith("All")) {
                    include = true;
                }
                else if (selected.startsWith("Daily")) {
                    if (recordDate.isEqual(today)) include = true;
                }
                else if (selected.startsWith("Weekly")) {
                    int currentWeek = today.get(weekFields.weekOfWeekBasedYear());
                    int recordWeek = recordDate.get(weekFields.weekOfWeekBasedYear());

                    if (recordDate.getYear() == today.getYear() && currentWeek == recordWeek) {
                        include = true;
                    }
                }
                else if (selected.startsWith("Monthly")) {
                    if (recordDate.getYear() == today.getYear() &&
                            recordDate.getMonth() == today.getMonth()) {
                        include = true;
                    }
                }
                else if (selected.startsWith("Yearly")) {
                    if (recordDate.getYear() == today.getYear()) include = true;
                }

            } catch (Exception e) {
                System.out.println("Date parse error for: " + record.getDate());
            }

            if (include) {
                filteredList.add(record);
                grandTotal += record.getAmount();
            }
        }

        salesTable.setItems(filteredList);
        totalSalesLabel.setText("Total Revenue: P " + String.format("%.2f", grandTotal));
    }
}