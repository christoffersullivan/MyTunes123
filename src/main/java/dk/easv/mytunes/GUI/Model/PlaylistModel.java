package dk.easv.mytunes.GUI.Model;

import dk.easv.mytunes.BE.Playlist;
import dk.easv.mytunes.BE.Song;
import dk.easv.mytunes.BLL.PlaylistManager;
import dk.easv.mytunes.BLL.SongManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

/**
 * Keeps the playlists, and the songs of the currently selected playlist, in sync with the data source.
 */
public class PlaylistModel {

    private PlaylistManager playlistManager;
    private ObservableList<Playlist> playlists;
    private ObservableList<Song> songsInPlaylist;
    private FilteredList<Song> filteredSongs;
    private Playlist currentPlaylist;

    public PlaylistModel() throws Exception {
        playlistManager = new PlaylistManager();
        playlists = FXCollections.observableArrayList(playlistManager.getAllPlaylists());
        songsInPlaylist = FXCollections.observableArrayList();
        filteredSongs = new FilteredList<>(songsInPlaylist);
    }

    public ObservableList<Playlist> getPlaylists() {
        return playlists;
    }

    /**
     * Songs of the current playlist, in playlist order, with the main window's filter applied.
     */
    public FilteredList<Song> getObservableSongs() {
        return filteredSongs;
    }

    /**
     * Makes the given playlist current and loads its songs (null = no playlist).
     */
    public void loadSongs(Playlist playlist) throws Exception {
        currentPlaylist = playlist;
        reload();
    }

    /**
     * Reloads the current playlist's songs, e.g. after a song was edited or deleted.
     */
    public void reload() throws Exception {
        if (currentPlaylist == null) {
            songsInPlaylist.clear();
        } else {
            songsInPlaylist.setAll(playlistManager.getSongsInPlaylist(currentPlaylist));
        }
    }

    public Playlist createPlaylist(String name) throws Exception {
        Playlist created = playlistManager.createPlaylist(new Playlist(-1, name));
        playlists.add(created);
        return created;
    }

    public void updatePlaylist(Playlist playlist, String newName) throws Exception {
        Playlist updated = new Playlist(playlist.getId(), newName);
        playlistManager.updatePlaylist(updated);

        int index = playlists.indexOf(playlist);
        if (index >= 0) {
            playlists.set(index, updated);
        }
        if (currentPlaylist != null && currentPlaylist.getId() == updated.getId()) {
            currentPlaylist = updated;
        }
    }

    /**
     * Deletes the playlist only; the songs in it are kept.
     */
    public void deletePlaylist(Playlist playlist) throws Exception {
        playlistManager.deletePlaylist(playlist);
        playlists.remove(playlist);

        if (currentPlaylist != null && currentPlaylist.getId() == playlist.getId()) {
            currentPlaylist = null;
            songsInPlaylist.clear();
        }
    }

    /**
     * @return false if the song was already in the playlist
     */
    public boolean addSongToPlaylist(Playlist playlist, Song song) throws Exception {
        boolean added = playlistManager.addSongToPlaylist(playlist, song);
        if (added && currentPlaylist != null && currentPlaylist.getId() == playlist.getId()) {
            reload();
        }
        return added;
    }

    public void removeSongFromPlaylist(Playlist playlist, Song song) throws Exception {
        playlistManager.removeSongFromPlaylist(playlist, song);
        songsInPlaylist.remove(song);
    }

    public void filter(String query) {
        filteredSongs.setPredicate(song -> SongManager.matchesFilter(song, query));
    }

    public void clearFilter() {
        filteredSongs.setPredicate(null);
    }
}
