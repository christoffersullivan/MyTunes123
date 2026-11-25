package dk.easv.mytunes.DAL;

// Project imports
import dk.easv.mytunes.BE.Playlist;
import dk.easv.mytunes.BE.Song;

// Java imports
import java.util.List;

/**
 * Operations on playlists and on the songs in them.
 */
public interface IPlaylistDataAccess {

    List<Playlist> getAllPlaylists() throws Exception;
    Playlist createPlaylist(Playlist newPlaylist) throws Exception;
    void updatePlaylist(Playlist playlist) throws Exception;

    /** Deletes the playlist only - the songs in it are NOT deleted. */
    void deletePlaylist(Playlist playlist) throws Exception;

    /** Songs in the playlist, in playlist order. */
    List<Song> getSongsInPlaylist(Playlist playlist) throws Exception;

    /** Adds the song to the end of the playlist. */
    void addSongToPlaylist(Playlist playlist, Song song) throws Exception;

    /** Removes the song from the playlist only - the song itself is NOT deleted. */
    void removeSongFromPlaylist(Playlist playlist, Song song) throws Exception;
}
