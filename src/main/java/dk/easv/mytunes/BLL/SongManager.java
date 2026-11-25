package dk.easv.mytunes.BLL;

// Project imports
import dk.easv.mytunes.BE.Song;
import dk.easv.mytunes.DAL.ISongDataAccess;
import dk.easv.mytunes.DAL.db.SongDAO_DB;

// Java imports
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

/**
 * Business layer for songs. The GUI only talks to this class, never to the DAL directly.
 */
public class SongManager {

    /** Folder (relative to the program's working directory) where imported song files are kept. */
    private static final String MUSIC_FOLDER = "music";

    private ISongDataAccess songDAO;

    public SongManager() throws IOException {
        //songDAO = new SongDAO_Mock();
        songDAO = new SongDAO_DB();
    }

    public List<Song> getAllSongs() throws Exception {
        return songDAO.getAllSongs();
    }

    public Song createSong(Song newSong) throws Exception {
        newSong.setTime(Math.round(newSong.getTime())); // stored as whole seconds
        return songDAO.createSong(newSong);
    }

    public void updateSong(Song updatedSong) throws Exception {
        updatedSong.setTime(Math.round(updatedSong.getTime()));
        songDAO.updateSong(updatedSong);
    }

    /**
     * Deletes the song. The DAL also removes it from all playlists.
     */
    public void deleteSong(Song selectedSong) throws Exception {
        songDAO.deleteSong(selectedSong);
    }

    /**
     * The categories the user can choose from (a fixed list).
     */
    public static List<String> getCategories() {
        return Arrays.asList("Pop", "Rock", "Hip-Hop", "R&B", "Electronic", "Jazz",
                "Classical", "Country", "Metal", "Folk", "Soundtrack", "Other");
    }

    /**
     * Filter rule from the requirements: title or artist contains the query (case-insensitive).
     */
    public static boolean matchesFilter(Song song, String query) {
        if (query == null || query.trim().isEmpty()) {
            return true;
        }
        String q = query.trim().toLowerCase();
        return song.getTitle().toLowerCase().contains(q)
                || song.getArtist().toLowerCase().contains(q);
    }

    /**
     * Copies a song file into the project's music folder and returns the relative path to store
     * in the database (e.g. "music/song.mp3"). A relative path keeps the database usable on
     * every group member's machine.
     */
    public String importSongFile(File source) throws IOException {
        Path musicDir = Paths.get(MUSIC_FOLDER);
        Files.createDirectories(musicDir);

        String name = source.getName();
        int dot = name.lastIndexOf('.');
        String base = dot > 0 ? name.substring(0, dot) : name;
        String ext = dot > 0 ? name.substring(dot) : "";

        Path target = musicDir.resolve(name);
        int counter = 1;
        while (Files.exists(target)) {
            // Same name and size: it is already imported, reuse it
            if (Files.size(target) == source.length()) {
                return MUSIC_FOLDER + "/" + target.getFileName();
            }
            target = musicDir.resolve(base + "_" + counter++ + ext);
        }

        Files.copy(source.toPath(), target);
        return MUSIC_FOLDER + "/" + target.getFileName();
    }
}
