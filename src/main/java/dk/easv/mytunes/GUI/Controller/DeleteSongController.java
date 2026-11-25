package dk.easv.mytunes.GUI.Controller;

import dk.easv.mytunes.BE.Song;
import dk.easv.mytunes.BLL.SongManager;
import dk.easv.mytunes.GUI.Model.SongModel;
import dk.easv.mytunes.GUI.Util.AlertUtil;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Controller for the "Delete a song" dialog (DeleteSong.fxml).
 * Deleting a song also removes it from all playlists (done in the DAL).
 */
public class DeleteSongController {

    @FXML private TextField txtSearchSong;
    @FXML private ListView<Song> lstSongs;
    @FXML private Button btnConfirm, btnCancel;

    private SongModel songModel;

    @FXML
    public void initialize() {
        btnConfirm.setDisable(true);
        lstSongs.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> btnConfirm.setDisable(newValue == null));
    }

    /**
     * Must be called after the FXML is loaded.
     */
    public void setSongModel(SongModel songModel) {
        this.songModel = songModel;

        FilteredList<Song> filtered = new FilteredList<>(songModel.getAllSongs());
        lstSongs.setItems(filtered);

        // Search while typing (title or artist)
        txtSearchSong.textProperty().addListener((observable, oldValue, newValue) ->
                filtered.setPredicate(song -> SongManager.matchesFilter(song, newValue)));
    }

    @FXML
    private void handleConfirm(ActionEvent event) {
        Song selected = lstSongs.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtil.showWarning("Select the song you want to delete.");
            return;
        }

        try {
            songModel.deleteSong(selected);
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
