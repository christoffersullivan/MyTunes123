package dk.easv.mytunes.GUI.Controller;

import dk.easv.mytunes.GUI.Model.PlaylistModel;
import dk.easv.mytunes.GUI.Util.AlertUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


public class CreatePlaylistController {

    @FXML private TextField txtPlaylistName;
    @FXML private Button btnCreate, btnCancel;

    private PlaylistModel playlistModel;

    public void setPlaylistModel(PlaylistModel playlistModel) {
        this.playlistModel = playlistModel;
    }

    @FXML
    private void handleCreate(ActionEvent event) {
        String name = txtPlaylistName.getText().trim();
        if (name.isEmpty()) {
            AlertUtil.showWarning("Please give the playlist a name.");
            return;
        }

        try {
            playlistModel.createPlaylist(name);
            closeStage();
        } catch (Exception e) {
            AlertUtil.showError(e);
        }
    }

    @FXML
    private void handleCancel(ActionEvent event) {

        closeStage();
    }

    private void closeStage() {

        ((Stage) btnCancel.getScene().getWindow()).close();
    }
}
