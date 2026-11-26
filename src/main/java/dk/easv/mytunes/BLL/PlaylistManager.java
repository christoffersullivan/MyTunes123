package dk.easv.mytunes.BLL;

// Project imports
import dk.easv.mytunes.BE.Playlist;
import dk.easv.mytunes.BE.Song;
import dk.easv.mytunes.DAL.IPlaylistDataAccess;
import dk.easv.mytunes.DAL.db.PlaylistDAO_DB;

// Java imports
import java.io.IOException;
import java.util.List;


public class PlaylistManager {

    private IPlaylistDataAccess playlistDAO;
/*
 create new playlist maneger and gets information from DAL through playListDAO_DB.
 */
    public PlaylistManager() throws IOException {
        playlistDAO = new PlaylistDAO_DB();
    }

/*
 a getter to get all playlists from database in an array, through playLisyDAO.
 */
    public List<Playlist> getAllPlaylists() throws Exception {
        return playlistDAO.getAllPlaylists();
    }

    /*
    create new playlist to database.
     */
    public Playlist createPlaylist(Playlist newPlaylist) throws Exception {
        return playlistDAO.createPlaylist(newPlaylist);
    }


    /*
    updates playlist from database.
     */
    public void updatePlaylist(Playlist playlist) throws Exception {
        playlistDAO.updatePlaylist(playlist);
    }

    /*
    deletes playlist from database.
     */
    public void deletePlaylist(Playlist playlist) throws Exception {
        playlistDAO.deletePlaylist(playlist);
    }

    /*
    a getter that gets all songs in a playlist.
     */
    public List<Song> getSongsInPlaylist(Playlist playlist) throws Exception {
        return playlistDAO.getSongsInPlaylist(playlist);
    }

   /*
    checks if a song already exist, if it does exist it returns false, and if it returns true it adds the song.
    */
    public boolean addSongToPlaylist(Playlist playlist, Song song) throws Exception {
        for (Song existing : playlistDAO.getSongsInPlaylist(playlist)) {
            if (existing.getId() == song.getId()) {
                return false;
            }
        }
        playlistDAO.addSongToPlaylist(playlist, song);
        return true;
    }

    /*
    remove selected song from playlist.
     */
    public void removeSongFromPlaylist(Playlist playlist, Song song) throws Exception {
        playlistDAO.removeSongFromPlaylist(playlist, song);
    }
}
