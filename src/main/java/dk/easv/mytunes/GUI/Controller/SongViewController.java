package dk.easv.mytunes.GUI.Controller;

import dk.easv.mytunes.BE.Song;
import dk.easv.mytunes.GUI.Model.SongModel;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class SongViewController implements Initializable {

    @FXML private TextField txtTitle, txtArtist, txtGenre, txtTime, txtFilter;
    @FXML private Button btnCreate, btnUpdate, btnDelete, btnFilter;
    @FXML private TableView<Song> tblSongs;
    @FXML private TableColumn<Song, String> colTitle, colArtist, colGenre;
    @FXML private TableColumn<Song, Double> colTime;

    private SongModel songModel;
    private boolean filterActive = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            songModel = new SongModel();
        } catch (Exception e) {
            displayError(e);
            e.printStackTrace();
            // Nothing works without the model, so lock the UI
            btnCreate.setDisable(true);
            btnFilter.setDisable(true);
            btnUpdate.setDisable(true);
            btnDelete.setDisable(true);
            return;
        }

        // Setup columns in the table
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colArtist.setCellValueFactory(new PropertyValueFactory<>("artist"));
        colGenre.setCellValueFactory(new PropertyValueFactory<>("genre"));
        colTime.setCellValueFactory(new PropertyValueFactory<>("time"));

        // Show time as mm:ss (the value is still a double, so sorting is numeric)
        colTime.setCellFactory(column -> new TableCell<Song, Double>() {
            @Override
            protected void updateItem(Double seconds, boolean empty) {
                super.updateItem(seconds, empty);
                setText(empty || seconds == null ? null : formatTime(seconds));
            }
        });

        // Filtered list -> sorted list -> table (column header sorting)
        SortedList<Song> sortedData = new SortedList<>(songModel.getObservableSongs());
        sortedData.comparatorProperty().bind(tblSongs.comparatorProperty());
        tblSongs.setItems(sortedData);

        // Fill the form when the user selects a song
        tblSongs.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                txtTitle.setText(newValue.getTitle());
                txtArtist.setText(newValue.getArtist());
                txtGenre.setText(newValue.getGenre());
                txtTime.setText(formatTime(newValue.getTime()));
            } else {
                clearForm();
            }
            btnUpdate.setDisable(newValue == null);
            btnDelete.setDisable(newValue == null);
        });

        btnUpdate.setDisable(true);
        btnDelete.setDisable(true);
    }

    @FXML
    private void onCreate(ActionEvent actionEvent) {
        Song input = readForm(-1);
        if (input == null) return;

        try {
            songModel.createSong(input);
            clearForm();
        } catch (Exception e) {
            displayError(e);
        }
    }

    @FXML
    private void onUpdate(ActionEvent actionEvent) {
        Song selected = tblSongs.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        Song updated = readForm(selected.getId());
        if (updated == null) return;

        try {
            songModel.updateSong(updated);
        } catch (Exception e) {
            displayError(e);
        }
    }

    @FXML
    private void onDelete(ActionEvent actionEvent) {
        Song selected = tblSongs.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        // Ask the user to confirm before deleting
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete song");
        confirm.setHeaderText("Delete \"" + selected.getTitle() + "\" by " + selected.getArtist() + "?");
        confirm.setContentText("This cannot be undone.");
        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                songModel.deleteSong(selected);
            } catch (Exception e) {
                displayError(e);
            }
        }
    }

    /**
     * The button toggles between "Filter" and "Clear".
     */
    @FXML
    private void onFilter(ActionEvent actionEvent) {
        if (!filterActive) {
            songModel.filter(txtFilter.getText());
            btnFilter.setText("Clear");
            filterActive = true;
        } else {
            txtFilter.clear();
            songModel.clearFilter();
            btnFilter.setText("Filter");
            filterActive = false;
        }
    }

    /**
     * Reads and validates the form. Returns null (after telling the user) if the input is invalid.
     */
    private Song readForm(int id) {
        String title = txtTitle.getText().trim();
        String artist = txtArtist.getText().trim();
        String genre = txtGenre.getText().trim();

        if (title.isEmpty() || artist.isEmpty()) {
            displayWarning("Title and artist are required.");
            return null;
        }

        double time;
        try {
            time = parseTime(txtTime.getText());
        } catch (NumberFormatException e) {
            displayWarning("Time must be seconds (e.g. 215) or mm:ss (e.g. 3:35).");
            return null;
        }

        return new Song(id, title, artist, genre, time);
    }

    private void clearForm() {
        txtTitle.clear();
        txtArtist.clear();
        txtGenre.clear();
        txtTime.clear();
    }

    /**
     * Accepts "215", "215.5" or "3:35" and returns seconds.
     */
    private static double parseTime(String text) {
        String t = text.trim();
        double seconds;
        if (t.contains(":")) {
            String[] parts = t.split(":");
            if (parts.length != 2) throw new NumberFormatException("Bad time format");
            seconds = Integer.parseInt(parts[0].trim()) * 60 + Double.parseDouble(parts[1].trim());
        } else {
            seconds = Double.parseDouble(t);
        }
        if (seconds <= 0) throw new NumberFormatException("Time must be positive");
        return seconds;
    }

    /**
     * Formats seconds as mm:ss.
     */
    private static String formatTime(double totalSeconds) {
        int rounded = (int) Math.round(totalSeconds);
        return String.format("%d:%02d", rounded / 60, rounded % 60);
    }

    private void displayError(Throwable t) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Something went wrong");
        alert.setHeaderText(t.getMessage());
        alert.showAndWait();
    }

    private void displayWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Invalid input");
        alert.setHeaderText(message);
        alert.showAndWait();
    }
}
