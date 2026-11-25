package dk.easv.mytunes.GUI.Controller;

import dk.easv.mytunes.BE.Song;
import dk.easv.mytunes.BLL.SongManager;
import dk.easv.mytunes.GUI.Model.SongModel;
import dk.easv.mytunes.GUI.Util.AlertUtil;
import dk.easv.mytunes.GUI.Util.TimeUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

/**
 * Controller for the New/Edit Song dialog (CreateNewSong.fxml).
 * Used for creating a song, or for editing one when setSongToEdit is called.
 */
public class CreateNewSongController {

    @FXML private Label lblHeading;
    @FXML private TextField txtSongTitle, txtArtist, txtTime;
    @FXML private ComboBox<String> cmbCategory;
    @FXML private ListView<String> lstSongFile;
    @FXML private Button btnChooseFile, btnSave, btnCancel;

    private SongModel songModel;
    private Song songToEdit;       // null when creating a new song
    private File chosenFile;       // file picked by the user (null = keep the song's current file)
    private MediaPlayer durationProbe; // kept in a field so it is not garbage collected while loading

    @FXML
    public void initialize() {
        cmbCategory.getItems().setAll(SongManager.getCategories());
    }

    /**
     * Must be called after the FXML is loaded.
     */
    public void setSongModel(SongModel songModel) {
        this.songModel = songModel;
    }

    /**
     * Switches the dialog to edit mode and fills in the song's current values.
     */
    public void setSongToEdit(Song song) {
        this.songToEdit = song;
        lblHeading.setText(" Edit song");

        txtSongTitle.setText(song.getTitle());
        txtArtist.setText(song.getArtist());
        txtTime.setText(TimeUtil.format(song.getTime()));

        if (song.getGenre() != null && !cmbCategory.getItems().contains(song.getGenre())) {
            cmbCategory.getItems().add(song.getGenre());
        }
        cmbCategory.setValue(song.getGenre());

        if (song.getFilePath() != null) {
            lstSongFile.getItems().setAll(song.getFilePath());
        }
    }

    @FXML
    private void handleChooseFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose a song file");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Audio files (*.mp3, *.wav)", "*.mp3", "*.wav"));

        File file = fileChooser.showOpenDialog(btnChooseFile.getScene().getWindow());
        if (file == null) {
            return;
        }

        chosenFile = file;
        lstSongFile.getItems().setAll(file.getAbsolutePath());
        detectDuration(file);
    }

    /**
     * Reads the song length from the file and fills in the time field.
     * If it fails, the user can type the time manually.
     */
    private void detectDuration(File file) {
        try {
            Media media = new Media(file.toURI().toString());
            durationProbe = new MediaPlayer(media);
            durationProbe.setOnReady(() -> {
                txtTime.setText(TimeUtil.format(media.getDuration().toSeconds()));
                durationProbe.dispose();
                durationProbe = null;
            });
            durationProbe.setOnError(() -> txtTime.setPromptText("Could not read the length - enter it manually"));
        } catch (Exception e) {
            txtTime.setPromptText("Could not read the length - enter it manually");
        }
    }

    @FXML
    private void handleSave(ActionEvent event) {
        String title = txtSongTitle.getText().trim();
        String artist = txtArtist.getText().trim();
        String category = cmbCategory.getValue();

        if (title.isEmpty() || artist.isEmpty()) {
            AlertUtil.showWarning("Title and artist are required.");
            return;
        }
        if (category == null) {
            AlertUtil.showWarning("Please select a category.");
            return;
        }

        double time;
        try {
            time = TimeUtil.parse(txtTime.getText());
        } catch (NumberFormatException e) {
            AlertUtil.showWarning("Time must be seconds (e.g. 215) or m:ss (e.g. 3:35).");
            return;
        }

        if (songToEdit == null && chosenFile == null) {
            AlertUtil.showWarning("Please choose a song file (mp3 or wav).");
            return;
        }

        try {
            String filePath = songToEdit != null ? songToEdit.getFilePath() : null;
            if (chosenFile != null) {
                filePath = songModel.importSongFile(chosenFile);
            }

            if (songToEdit == null) {
                songModel.createSong(new Song(-1, title, artist, category, time, filePath));
            } else {
                songModel.updateSong(new Song(songToEdit.getId(), title, artist, category, time, filePath));
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
