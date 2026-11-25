package com.example.mytuness.GUI.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;


public class EditPlaylistController {
    @FXML
    private TextField SongTitle;

    @FXML
    private Button btnConfirm, btnCancel;

    @FXML
    public void initialize() {
    }

    @FXML
    private void handleConfirm(ActionEvent event) {
        String newName = SongTitle.getText().trim();
        if (newName.isEmpty()) {
            return;
        }
        closeStage();
    }

    @FXML
    private void handleCancel(ActionEvent event) {
       // closeStage();
    }

    private void closeStage() {
     //   Stage stage = (Stage) btnCancel.getScene().getWindow();
      //  stage.close();
    }
}

