package com.example.mytuness.GUI.Controller;

// java imports

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

// project imports


public class MusicViewController {
        @FXML
        private Button btnNewSong, btnDeleteSong, btnCreatePlaylist, btnDeletePlaylist, btnAddSong, btnRemoveSong, btnEditPlaylist1, btnPlayPause, btnNext, btnPrev;

        @FXML
        private TableView<?> tblSong, tblPlaylistsongs;

        @FXML
        private TableColumn<?, ?> tblSongTitle, tblArtist, tblTime, tblPlaylistNumber, tblPlaylist;

        @FXML
        private Label selectedPlaylist, currentTime, fullTime, songTitle, Artist;

        @FXML
        private Slider timeSlider, volumeSlider;

        @FXML
        public void initialize() {

        }

        public void handleCreatePlaylist(ActionEvent actionEvent) {

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/dk/easv/mrs/GUI/View/CreatePlaylistView.fxml"));
                Parent root = loader.load();

                Stage stage = new Stage();
                stage.setTitle("Create Playlist");
                stage.setScene(new Scene(root));

                // Blokerer hovedvinduet, indtil det nye vindue lukkes (valgfrit)
                stage.initModality(Modality.APPLICATION_MODAL);

                // showAndWait() venter, til vinduet lukkes – show() lader begge vinduer være aktive
                stage.showAndWait();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void handleDeletePlaylist(ActionEvent actionEvent) {

        }

        public void handleAddSong(ActionEvent actionEvent) {

        }

        public void handleRemoveSong(ActionEvent actionEvent) {

        }

        public void handleEditPlaylist(ActionEvent actionEvent) {

        }

        public void handlePlayPause(ActionEvent actionEvent) {

        }

        public void handleNext(ActionEvent actionEvent) {

        }

        public void handlePrevious(ActionEvent actionEvent) {

        }



    }



