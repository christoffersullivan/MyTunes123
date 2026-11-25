package dk.easv.mytunes.GUI.Model;

import dk.easv.mytunes.BE.Song;
import dk.easv.mytunes.BLL.SongManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

import java.io.File;
import java.io.IOException;

/**
 * Keeps the songs shown in the GUI in sync with the data source.
 */
public class SongModel {

    private SongManager songManager;
    private ObservableList<Song> songsToBeViewed;
    private FilteredList<Song> filteredList;

    public SongModel() throws Exception {
        songManager = new SongManager();
        songsToBeViewed = FXCollections.observableArrayList();
        songsToBeViewed.addAll(songManager.getAllSongs());
        filteredList = new FilteredList<>(songsToBeViewed);
    }

    /**
     * The songs with the main window's filter applied.
     */
    public FilteredList<Song> getObservableSongs() {
        return filteredList;
    }

    /**
     * All songs, unfiltered (for dialogs that do their own searching).
     */
    public ObservableList<Song> getAllSongs() {
        return songsToBeViewed;
    }

    public Song createSong(Song newSong) throws Exception {
        Song created = songManager.createSong(newSong);
        songsToBeViewed.add(created);
        return created;
    }

    /**
     * @param updatedSong a song with the id of the song to update and the new values
     */
    public void updateSong(Song updatedSong) throws Exception {
        // Save first - only touch the GUI list if the database update succeeded
        songManager.updateSong(updatedSong);

        for (int i = 0; i < songsToBeViewed.size(); i++) {
            if (songsToBeViewed.get(i).getId() == updatedSong.getId()) {
                songsToBeViewed.set(i, updatedSong);
                break;
            }
        }
    }

    public void deleteSong(Song selectedSong) throws Exception {
        songManager.deleteSong(selectedSong);
        songsToBeViewed.remove(selectedSong);
    }

    /**
     * Copies the chosen audio file into the project's music folder.
     * @return the relative path to store on the song
     */
    public String importSongFile(File file) throws IOException {
        return songManager.importSongFile(file);
    }

    /**
     * Show only songs where title or artist contains the query.
     */
    public void filter(String query) {
        filteredList.setPredicate(song -> SongManager.matchesFilter(song, query));
    }

    /**
     * Show all songs again.
     */
    public void clearFilter() {
        filteredList.setPredicate(null);
    }
}
