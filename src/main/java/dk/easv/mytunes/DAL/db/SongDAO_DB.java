package dk.easv.mytunes.DAL.db;

// Project imports
import dk.easv.mytunes.BE.Song;
import dk.easv.mytunes.DAL.ISongDataAccess;

// Java imports
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * JDBC implementation of the song data access (SQL Server).
 * All statements are prepared statements to avoid SQL injection.
 */
public class SongDAO_DB implements ISongDataAccess {

    private DBConnector dbConnector = new DBConnector();

    public SongDAO_DB() throws IOException {}

    @Override
    public List<Song> getAllSongs() throws Exception {
        List<Song> allSongs = new ArrayList<>();
        String sql = "SELECT Id, Title, Artist, Genre, [Time] FROM dbo.Song;";

        try (Connection conn = dbConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql))
        {
            while (rs.next()) {
                int id = rs.getInt("Id");
                String title = rs.getString("Title");
                String artist = rs.getString("Artist");
                String genre = rs.getString("Genre");
                double time = rs.getDouble("Time");

                allSongs.add(new Song(id, title, artist, genre, time));
            }
            return allSongs;
        }
        catch (SQLException ex)
        {
            throw new Exception("Could not get songs from database", ex);
        }
    }

    @Override
    public Song createSong(Song song) throws Exception {
        String sql = "INSERT INTO dbo.Song (Title, Artist, Genre, [Time]) VALUES (?,?,?,?);";

        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS))
        {
            stmt.setString(1, song.getTitle());
            stmt.setString(2, song.getArtist());
            stmt.setString(3, song.getGenre());
            stmt.setDouble(4, song.getTime());

            stmt.executeUpdate();

            // Get the generated id from the DB - the GUI needs it
            int id = 0;
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    id = rs.getInt(1);
                }
            }

            return new Song(id, song.getTitle(), song.getArtist(), song.getGenre(), song.getTime());
        }
        catch (SQLException ex)
        {
            throw new Exception("Could not create song", ex);
        }
    }

    @Override
    public void updateSong(Song song) throws Exception {
        String sql = "UPDATE dbo.Song SET Title = ?, Artist = ?, Genre = ?, [Time] = ? WHERE Id = ?;";

        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setString(1, song.getTitle());
            stmt.setString(2, song.getArtist());
            stmt.setString(3, song.getGenre());
            stmt.setDouble(4, song.getTime());
            stmt.setInt(5, song.getId());

            stmt.executeUpdate();
        }
        catch (SQLException ex)
        {
            throw new Exception("Could not update song", ex);
        }
    }

    @Override
    public void deleteSong(Song song) throws Exception {
        String sql = "DELETE FROM dbo.Song WHERE Id = ?;";

        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setInt(1, song.getId());
            stmt.executeUpdate();
        }
        catch (SQLException ex)
        {
            throw new Exception("Could not delete song", ex);
        }
    }
}
