package dk.easv.mytunes.GUI.Controller;

import dk.easv.mytunes.BE.Playlist;
import dk.easv.mytunes.GUI.Model.PlaylistModel;
import dk.easv.mytunes.GUI.Util.AlertUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Controller for the "Edit Playlist" dialog (EditPlaylist.fxml). Only the name can be edited.
 */
public class EditPlaylistController {

    @FXML private TextField txtPlaylistName;
    @FXML private Button btnConfirm, btnCancel;

    private PlaylistModel playlistModel;
    private Playlist playlist;

    /**
     * Must be called after the FXML is loaded.
     */
    public void setData(PlaylistModel playlistModel, Playlist playlist) {
        this.playlistModel = playlistModel;
        this.playlist = playlist;
        txtPlaylistName.setText(playlist.getName());
        txtPlaylistName.selectAll();
    }

    @FXML
    private void handleConfirm(ActionEvent event) {
        String newName = txtPlaylistName.getText().trim();
        if (newName.isEmpty()) {
            AlertUtil.showWarning("The playlist needs a name.");
            return;
        }

        try {
            playlistModel.updatePlaylist(playlist, newName);
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
