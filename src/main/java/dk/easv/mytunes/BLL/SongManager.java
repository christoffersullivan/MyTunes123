package dk.easv.mytunes.BLL;

// Project imports
import dk.easv.mytunes.BE.Song;
import dk.easv.mytunes.DAL.ISongDataAccess;
import dk.easv.mytunes.DAL.db.SongDAO_DB;

// Java imports
import java.io.IOException;
import java.util.List;

/**
 * Business layer for songs. The GUI only talks to this class, never to the DAL directly.
 */
public class SongManager {

    private ISongDataAccess songDAO;

    public SongManager() throws IOException {
        //songDAO = new SongDAO_Mock();
        songDAO = new SongDAO_DB();
    }

    public List<Song> getAllSongs() throws Exception {
        return songDAO.getAllSongs();
    }

    public Song createSong(Song newSong) throws Exception {
        return songDAO.createSong(newSong);
    }

    public void updateSong(Song updatedSong) throws Exception {
        songDAO.updateSong(updatedSong);
    }

    public void deleteSong(Song selectedSong) throws Exception {
        // When playlists are added: remove the song from all playlists here (or via ON DELETE CASCADE)
        songDAO.deleteSong(selectedSong);
    }

    /**
     * Filter rule from the requirements: title or artist contains the query (case-insensitive).
     */
    public boolean matchesFilter(Song song, String query) {
        if (query == null || query.trim().isEmpty()) {
            return true;
        }
        String q = query.trim().toLowerCase();
        return song.getTitle().toLowerCase().contains(q)
                || song.getArtist().toLowerCase().contains(q);
    }
}
