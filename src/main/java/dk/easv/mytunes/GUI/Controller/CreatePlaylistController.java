package com.example.mytuness.GUI.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class CreatePlaylistController {

    @FXML
    private TextField SongTitle;

    @FXML
    private Button btnCreate, btnCancel;

    @FXML
    public void initialize() {
    }

    @FXML
    private void handleCreate(ActionEvent event) {
        String playlistName = SongTitle.getText().trim();
        if (playlistName.isEmpty()) {
            return;
        }
        // Opret playlisten i din model eller database her
        closeStage();
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        closeStage();
    }

    private void closeStage() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }
}