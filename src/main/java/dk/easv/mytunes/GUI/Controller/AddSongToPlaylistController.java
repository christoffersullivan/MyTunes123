package dk.easv.mytunes.GUI.Controller;

import dk.easv.mytunes.BE.Playlist;
import dk.easv.mytunes.BE.Song;
import dk.easv.mytunes.BLL.SongManager;
import dk.easv.mytunes.GUI.Model.PlaylistModel;
import dk.easv.mytunes.GUI.Model.SongModel;
import dk.easv.mytunes.GUI.Util.AlertUtil;
import dk.easv.mytunes.GUI.Util.TimeUtil;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

/**
 * Controller for the "Add a song to a playlist" dialog (AddSongToPlaylist.fxml).
 * The song is added to the end of the playlist.!!!!!!
 */
public class AddSongToPlaylistController {

    @FXML private TextField txtSearchSong;
    @FXML private TableView<Song> tblSongs;
    @FXML private TableColumn<Song, String> colSongTitle, colArtist;
    @FXML private TableColumn<Song, Double> colTime;
    @FXML private Button btnSave, btnCancel;

    private PlaylistModel playlistModel;
    private Playlist playlist;

    @FXML
    public void initialize() {
        colSongTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colArtist.setCellValueFactory(new PropertyValueFactory<>("artist"));
        colTime.setCellValueFactory(new PropertyValueFactory<>("time"));
        colTime.setCellFactory(TimeUtil.timeCellFactory());
    }


    public void setData(SongModel songModel, PlaylistModel playlistModel, Playlist playlist) {
        this.playlistModel = playlistModel;
        this.playlist = playlist;

        /*
        searchbar filters.
         */
        FilteredList<Song> filtered = new FilteredList<>(songModel.getAllSongs());
        SortedList<Song> sorted = new SortedList<>(filtered);
        sorted.comparatorProperty().bind(tblSongs.comparatorProperty()); //makes it possible to press top of coulmn and sort.
        tblSongs.setItems(sorted);

        txtSearchSong.textProperty().addListener((observable, oldValue, newValue) ->
                filtered.setPredicate(song -> SongManager.matchesFilter(song, newValue)));
    }

    @FXML
    private void handleSave(ActionEvent event) {
        Song selected = tblSongs.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtil.showWarning("Select a song to add.");
            return;
        }

        try {
            boolean added = playlistModel.addSongToPlaylist(playlist, selected);
            if (!added) {
                AlertUtil.showInfo("\"" + selected.getTitle() + "\" is already in " + playlist.getName() + ".");
                return;
            }
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
