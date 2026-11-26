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
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;


public class SongManager {

  /*
  the folder where song files get saved.
   */
    private static final String MUSIC_FOLDER = "music";

    private ISongDataAccess songDAO;

    /*
    creates new songmanger, and connects to database through songDAO_DB.
     */
    public SongManager() throws IOException {
        songDAO = new SongDAO_DB();
    }

    /*
    a getter to get all songs from the database in an arrey, from songDAO.
     */
    public List<Song> getAllSongs() throws Exception {
        return songDAO.getAllSongs();
    }

    /*
    test next time newsong.settime!!!!!!!!
    math.round rounds the song to whole numbers and creates the new song in the database.
     */
    public Song createSong(Song newSong) throws Exception {
        newSong.setTime(Math.round(newSong.getTime()));
        return songDAO.createSong(newSong);
    }

    /*
    updates song and time to whole seconds.
     */
    public void updateSong(Song updatedSong) throws Exception {
        updatedSong.setTime(Math.round(updatedSong.getTime()));
        songDAO.updateSong(updatedSong);
    }


    /*
    delete song from database with songDAO.
     */
    public void deleteSong(Song selectedSong) throws Exception {
        songDAO.deleteSong(selectedSong);
    }


    /*
    creates a array list with catagories.
     */
    public static List<String> getCategories() {
        return Arrays.asList("Pop", "Rock", "Hip-Hop", "R&B", "Electronic", "Jazz",
                "Classical", "Country", "Metal", "Folk", "Soundtrack","indie","techno","Other");
    }


    /*
    the searchbar for filtering songs
     */
    public static boolean matchesFilter(Song song, String query) {
       /*
       if search is null or white space, nothing will happen.
        */
        if (query == null || query.trim().isEmpty()) {
            return true;
        }
        /*
        if search contains letters, it filters songs, and it doenst matter if you use upper or lowercase letters.
         */
        String q = query.trim().toLowerCase();
        return song.getTitle().toLowerCase().contains(q)
                || song.getArtist().toLowerCase().contains(q);
    }


    /*


    public String importSongFile(File source) throws IOException {
        Path musicDirectory = Paths.get(MUSIC_FOLDER);
        Files.createDirectories(musicDirectory);

        String name = source.getName();
        int dot = name.lastIndexOf('.');
        String base = dot > 0 ? name.substring(0, dot) : name;
        String ext = dot > 0 ? name.substring(dot) : "";

        Path target = musicDirectory.resolve(name);
        int counter = 1;
        while (Files.exists(target)) {
            // Same name and size: it is already imported, reuse it
            if (Files.size(target) == source.length()) {
                return MUSIC_FOLDER + "/" + target.getFileName();
            }
            target = musicDirectory.resolve(base + "_" + counter++ + ext);
        }

        Files.copy(source.toPath(), target);
        return MUSIC_FOLDER + "/" + target.getFileName();
    }
    */
    /*
    tester!!!!!!!!!!!!!
     */
    public String importSongFile(File source) throws IOException {
        Path musicDirectory = Paths.get(MUSIC_FOLDER);
        Files.createDirectories(musicDirectory);

        Path target = musicDirectory.resolve(source.getName());
        Files.copy(source.toPath(), target, StandardCopyOption.REPLACE_EXISTING);

        return MUSIC_FOLDER + "/" + source.getName();
    }
}
