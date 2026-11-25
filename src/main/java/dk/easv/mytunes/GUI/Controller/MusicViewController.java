package dk.easv.mytunes.GUI.Controller;

import dk.easv.mytunes.BE.Playlist;
import dk.easv.mytunes.BE.Song;
import dk.easv.mytunes.GUI.Model.PlaylistModel;
import dk.easv.mytunes.GUI.Model.SongModel;
import dk.easv.mytunes.GUI.Util.AlertUtil;
import dk.easv.mytunes.GUI.Util.TimeUtil;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller for the main window (MusicView.fxml).
 *
 * The right-hand table shows the songs of the selected playlist (in playlist order).
 * When no playlist is selected it shows all songs - Ctrl+click the selected playlist to deselect it.
 */
public class MusicViewController {

    // Songs
    @FXML private TableView<Song> tblSongs;
    @FXML private TableColumn<Song, String> colSongTitle, colArtist;
    @FXML private TableColumn<Song, Double> colTime;
    @FXML private Button btnNewSong, btnEditSong, btnDeleteSong, btnAddSong, btnRemoveSong;
    @FXML private TextField txtFilter;
    @FXML private Button btnFilter;

    // Playlists
    @FXML private TableView<Playlist> tblPlaylists;
    @FXML private TableColumn<Playlist, Integer> colPlaylistNumber;
    @FXML private TableColumn<Playlist, String> colPlaylistName;
    @FXML private Button btnCreatePlaylist, btnEditPlaylist, btnDeletePlaylist;
    @FXML private Label lblSelectedPlaylist;

    // Player
    @FXML private Button btnPlayPause;
    @FXML private Label lblCurrentTime, lblFullTime, lblNowPlayingTitle, lblNowPlayingArtist;
    @FXML private Slider sldTime, sldVolume;

    private static final String PLAY_TEXT = ">|";
    private static final String PAUSE_TEXT = "||";

    private SongModel songModel;
    private PlaylistModel playlistModel;
    private SortedList<Song> sortedAllSongs;
    private SortedList<Song> sortedPlaylistSongs;
    private boolean filterActive = false;

    private MediaPlayer mediaPlayer;
    private Song currentSong;
    private List<Song> playQueue = new ArrayList<>();

    @FXML
    public void initialize() {
        setupColumns();
        setupPlayerControls();
        updateButtonStates();

        // Load data. If the database is unreachable the window still opens, with an error message.
        try {
            songModel = new SongModel();
            playlistModel = new PlaylistModel();
        } catch (Exception e) {
            AlertUtil.showError(e);
            btnNewSong.setDisable(true);
            btnDeleteSong.setDisable(true);
            btnCreatePlaylist.setDisable(true);
            btnFilter.setDisable(true);
            return;
        }

        // model lists -> sorted lists (click column headers to sort) -> table
        sortedAllSongs = new SortedList<>(songModel.getObservableSongs());
        sortedAllSongs.comparatorProperty().bind(tblSongs.comparatorProperty());
        sortedPlaylistSongs = new SortedList<>(playlistModel.getObservableSongs());
        sortedPlaylistSongs.comparatorProperty().bind(tblSongs.comparatorProperty());
        tblSongs.setItems(sortedAllSongs);

        tblPlaylists.setItems(playlistModel.getPlaylists());
        // The # column shows row numbers, which change when playlists are added or removed
        playlistModel.getPlaylists().addListener(
                (javafx.collections.ListChangeListener<Playlist>) change -> Platform.runLater(tblPlaylists::refresh));

        tblPlaylists.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            try {
                playlistModel.loadSongs(newValue);
            } catch (Exception e) {
                AlertUtil.showError(e);
            }
            tblSongs.setItems(newValue == null ? sortedAllSongs : sortedPlaylistSongs);
            lblSelectedPlaylist.setText(newValue == null
                    ? "No playlist selected (showing all songs)" : newValue.getName());
            updateButtonStates();
        });

        tblSongs.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> updateButtonStates());

        // Double-click a song to play it
        tblSongs.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Song selected = tblSongs.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    startFromTable(selected);
                }
            }
        });
    }

    private void setupColumns() {
        colSongTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colArtist.setCellValueFactory(new PropertyValueFactory<>("artist"));
        colTime.setCellValueFactory(new PropertyValueFactory<>("time"));
        colTime.setCellFactory(TimeUtil.timeCellFactory());

        colPlaylistName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colPlaylistNumber.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<Integer>(0));
        colPlaylistNumber.setCellFactory(column -> new TableCell<Playlist, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : String.valueOf(getIndex() + 1));
            }
        });
        colPlaylistNumber.setSortable(false);
    }

    private void setupPlayerControls() {
        sldVolume.setMin(0);
        sldVolume.setMax(1);
        sldVolume.setValue(0.5);
        sldVolume.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (mediaPlayer != null) {
                mediaPlayer.setVolume(newValue.doubleValue());
            }
        });

        // Seek when the user clicks or drags the time slider
        sldTime.setOnMouseReleased(event -> {
            if (mediaPlayer != null) {
                mediaPlayer.seek(Duration.seconds(sldTime.getValue()));
            }
        });
    }

    /**
     * Enables/disables buttons depending on what is selected.
     */
    private void updateButtonStates() {
        boolean playlistSelected = tblPlaylists.getSelectionModel().getSelectedItem() != null;
        boolean songSelected = tblSongs.getSelectionModel().getSelectedItem() != null;

        btnEditPlaylist.setDisable(!playlistSelected);
        btnDeletePlaylist.setDisable(!playlistSelected);
        btnAddSong.setDisable(!playlistSelected);
        btnRemoveSong.setDisable(!(playlistSelected && songSelected));
        btnEditSong.setDisable(!songSelected);
    }

    // =====================================================================
    // Songs
    // =====================================================================

    @FXML
    private void handleNewSong(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/CreateNewSong.fxml"));
            Parent root = loader.load();
            CreateNewSongController controller = loader.getController();
            controller.setSongModel(songModel);
            showDialog(root, "Create a new song");
        } catch (IOException e) {
            AlertUtil.showError(e);
        }
    }

    @FXML
    private void handleEditSong(ActionEvent event) {
        Song selected = tblSongs.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/CreateNewSong.fxml"));
            Parent root = loader.load();
            CreateNewSongController controller = loader.getController();
            controller.setSongModel(songModel);
            controller.setSongToEdit(selected);
            showDialog(root, "Edit song");
            refreshAfterSongChange();
        } catch (IOException e) {
            AlertUtil.showError(e);
        }
    }

    @FXML
    private void handleDeleteSong(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/DeleteSong.fxml"));
            Parent root = loader.load();
            DeleteSongController controller = loader.getController();
            controller.setSongModel(songModel);
            showDialog(root, "Delete a song");
            refreshAfterSongChange();
        } catch (IOException e) {
            AlertUtil.showError(e);
        }
    }

    /**
     * The playlist view holds its own copies of songs, so reload them after a song was edited or deleted.
     */
    private void refreshAfterSongChange() {
        try {
            playlistModel.reload();
        } catch (Exception e) {
            AlertUtil.showError(e);
        }
        tblSongs.refresh();
    }

    /**
     * Filter / Clear toggle: filters on title or artist, and the button then changes to "Clear".
     */
    @FXML
    private void handleFilter(ActionEvent event) {
        if (songModel == null) return;

        if (!filterActive) {
            String query = txtFilter.getText();
            songModel.filter(query);
            playlistModel.filter(query);
            btnFilter.setText("Clear");
            filterActive = true;
        } else {
            txtFilter.clear();
            songModel.clearFilter();
            playlistModel.clearFilter();
            btnFilter.setText("Filter");
            filterActive = false;
        }
    }

    // =====================================================================
    // Playlists
    // =====================================================================

    @FXML
    public void handleCreatePlaylist(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/CreatePlaylist.fxml"));
            Parent root = loader.load();
            CreatePlaylistController controller = loader.getController();
            controller.setPlaylistModel(playlistModel);
            showDialog(root, "Create a playlist");
        } catch (IOException e) {
            AlertUtil.showError(e);
        }
    }

    @FXML
    public void handleEditPlaylist(ActionEvent event) {
        Playlist selected = tblPlaylists.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/EditPlaylist.fxml"));
            Parent root = loader.load();
            EditPlaylistController controller = loader.getController();
            controller.setData(playlistModel, selected);
            showDialog(root, "Edit playlist");
        } catch (IOException e) {
            AlertUtil.showError(e);
        }
    }

    @FXML
    public void handleDeletePlaylist(ActionEvent event) {
        Playlist selected = tblPlaylists.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        boolean confirmed = AlertUtil.confirm(
                "Delete the playlist \"" + selected.getName() + "\"?",
                "The songs in the playlist are not deleted.");
        if (!confirmed) return;

        try {
            playlistModel.deletePlaylist(selected);
        } catch (Exception e) {
            AlertUtil.showError(e);
        }
    }

    @FXML
    public void handleAddSong(ActionEvent event) {
        Playlist selected = tblPlaylists.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/AddSongToPlaylist.fxml"));
            Parent root = loader.load();
            AddSongToPlaylistController controller = loader.getController();
            controller.setData(songModel, playlistModel, selected);
            showDialog(root, "Add a song to " + selected.getName());
        } catch (IOException e) {
            AlertUtil.showError(e);
        }
    }

    @FXML
    public void handleRemoveSong(ActionEvent event) {
        Playlist playlist = tblPlaylists.getSelectionModel().getSelectedItem();
        Song song = tblSongs.getSelectionModel().getSelectedItem();
        if (playlist == null || song == null) return;

        // Removes the song from the playlist only - the song itself is kept
        try {
            playlistModel.removeSongFromPlaylist(playlist, song);
        } catch (Exception e) {
            AlertUtil.showError(e);
        }
    }

    // =====================================================================
    // Player
    // =====================================================================

    @FXML
    public void handlePlayPause(ActionEvent event) {
        // Playing -> pause
        if (mediaPlayer != null && mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            mediaPlayer.pause();
            btnPlayPause.setText(PLAY_TEXT);
            return;
        }

        Song selected = tblSongs.getSelectionModel().getSelectedItem();

        // Paused -> resume (unless the user selected a different song)
        if (mediaPlayer != null && currentSong != null
                && (selected == null || selected.getId() == currentSong.getId())) {
            mediaPlayer.play();
            btnPlayPause.setText(PAUSE_TEXT);
            return;
        }

        // Otherwise play the selected song, or the first one in the table
        Song toPlay = selected;
        if (toPlay == null && !tblSongs.getItems().isEmpty()) {
            toPlay = tblSongs.getItems().get(0);
        }
        if (toPlay != null) {
            startFromTable(toPlay);
        }
    }

    @FXML
    public void handleNext(ActionEvent event) {
        playNext();
    }

    @FXML
    public void handlePrevious(ActionEvent event) {
        int index = indexInQueue(currentSong);
        if (index > 0) {
            playSong(playQueue.get(index - 1));
        } else if (mediaPlayer != null) {
            mediaPlayer.seek(Duration.ZERO); // first song: restart it
        }
    }

    /**
     * Starts playing a song chosen by the user. The songs currently shown in the table
     * (playlist order, or all songs) become the queue, so the next song plays automatically.
     */
    private void startFromTable(Song song) {
        playQueue = new ArrayList<>(tblSongs.getItems());
        playSong(song);
    }

    private void playSong(Song song) {
        stopPlayback();

        if (song.getFilePath() == null || song.getFilePath().trim().isEmpty()) {
            AlertUtil.showWarning("\"" + song.getTitle() + "\" has no song file. Edit the song and choose a file.");
            return;
        }
        File file = new File(song.getFilePath());
        if (!file.exists()) {
            AlertUtil.showWarning("Could not find the song file:\n" + file.getAbsolutePath());
            return;
        }

        try {
            Media media = new Media(file.toURI().toString());

            media.setOnError(() -> {
                System.out.println("Media Error: " + media.getError());
            });

            mediaPlayer = new MediaPlayer(media);
            currentSong = song;

            mediaPlayer.setVolume(sldVolume.getValue());
            lblNowPlayingTitle.setText(song.getTitle());
            lblNowPlayingArtist.setText(song.getArtist());
            lblFullTime.setText(TimeUtil.format(song.getTime()));
            sldTime.setMax(song.getTime());
            sldTime.setValue(0);

            mediaPlayer.setOnReady(() -> {
                sldTime.setMax(media.getDuration().toSeconds());
                lblFullTime.setText(TimeUtil.format(media.getDuration().toSeconds()));
            });
            mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
                if (!sldTime.isPressed()) {
                    sldTime.setValue(newValue.toSeconds());
                }
                lblCurrentTime.setText(TimeUtil.format(newValue.toSeconds()));
            });
            // When the song ends, the next one in the queue starts automatically
            mediaPlayer.setOnEndOfMedia(this::playNext);
            mediaPlayer.setOnError(() -> {
                System.out.println("MediaPlayer Error: " + mediaPlayer.getError());
                AlertUtil.showWarning("Could not play \"" + song.getTitle() + "\".");
                stopPlayback();
            });

            mediaPlayer.play();
            btnPlayPause.setText(PAUSE_TEXT);
        } catch (Exception e) {
            stopPlayback();
            AlertUtil.showError(e);
        }
    }

    private void playNext() {
        int index = indexInQueue(currentSong);
        if (index >= 0 && index + 1 < playQueue.size()) {
            Song next = playQueue.get(index + 1);
            selectInTable(next);
            playSong(next);
        } else {
            stopPlayback(); // end of the list
        }
    }

    /**
     * Stops and releases the current player and resets the player controls.
     */
    private void stopPlayback() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
            mediaPlayer = null;
        }
        btnPlayPause.setText(PLAY_TEXT);
        sldTime.setValue(0);
        lblCurrentTime.setText("00:00");
    }

    private int indexInQueue(Song song) {
        if (song == null) return -1;
        for (int i = 0; i < playQueue.size(); i++) {
            if (playQueue.get(i).getId() == song.getId()) {
                return i;
            }
        }
        return -1;
    }

    private void selectInTable(Song song) {
        for (Song s : tblSongs.getItems()) {
            if (s.getId() == song.getId()) {
                tblSongs.getSelectionModel().select(s);
                return;
            }
        }
    }

    // =====================================================================
    // Helpers
    // =====================================================================

    /**
     * Shows a window on top of the main window and waits until it is closed.
     */
    private void showDialog(Parent root, String title) {
        Stage stage = new Stage();
        stage.setTitle(title);
        stage.setScene(new Scene(root));
        stage.setResizable(false);
        stage.initOwner(tblSongs.getScene().getWindow());
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }
}
