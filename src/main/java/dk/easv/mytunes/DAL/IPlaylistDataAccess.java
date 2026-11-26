package dk.easv.mytunes.DAL;

// Project imports
import dk.easv.mytunes.BE.Playlist;
import dk.easv.mytunes.BE.Song;

// Java imports
import java.util.List;

/*
updates on playlists.
 */
public interface IPlaylistDataAccess {

    List<Playlist> getAllPlaylists() throws Exception;
    Playlist createPlaylist(Playlist newPlaylist) throws Exception;
    void updatePlaylist(Playlist playlist) throws Exception;

    /*
    deletes playlist.
     */
    void deletePlaylist(Playlist playlist) throws Exception;

    /*
    song order in playlist.
     */
    List<Song> getSongsInPlaylist(Playlist playlist) throws Exception;

    /*
    add song ti playlist.
     */
    void addSongToPlaylist(Playlist playlist, Song song) throws Exception;

    /*
    deletes a song from playlist.
     */
    void removeSongFromPlaylist(Playlist playlist, Song song) throws Exception;
}
