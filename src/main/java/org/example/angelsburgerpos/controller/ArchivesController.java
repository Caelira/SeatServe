package org.example.angelsburgerpos.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.angelsburgerpos.model.ArchiveRecord;
import org.example.angelsburgerpos.util.AppConfig;
import org.example.angelsburgerpos.util.FileUtil;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ArchivesController implements Initializable {

    @FXML private TableView<ArchiveRecord> archivesTable;
    @FXML private TableColumn<ArchiveRecord, String> colDate;
    @FXML private TableColumn<ArchiveRecord, String> colType;
    @FXML private TableColumn<ArchiveRecord, String> colId;
    @FXML private TableColumn<ArchiveRecord, String> colName;
    @FXML private TableColumn<ArchiveRecord, String> colDetails;
    @FXML private TableColumn<ArchiveRecord, String> colReason;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colDetails.setCellValueFactory(new PropertyValueFactory<>("details"));
        colReason.setCellValueFactory(new PropertyValueFactory<>("reason"));

        loadArchives();
    }

    private void loadArchives() {
        ObservableList<ArchiveRecord> list = FXCollections.observableArrayList();
        List<String> lines = FileUtil.readLines(AppConfig.ARCHIVES_FILE);

        for (String line : lines) {
            String[] parts = line.split(",");

            if (parts.length >= 7) {
                String date = parts[0];
                String type = parts[1];
                String id = parts[2];
                String name = parts[3];

                String details = parts[4];
                if (!parts[5].equals("-")) {
                    details += " " + parts[5];
                }

                String reason = parts[6];

                list.add(new ArchiveRecord(date, type, id, name, details, reason));
            }
        }

        FXCollections.reverse(list);
        archivesTable.setItems(list);
    }
}