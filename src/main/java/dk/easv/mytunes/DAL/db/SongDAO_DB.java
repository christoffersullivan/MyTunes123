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
        String sql = "SELECT Id, Title, Artist, Category, SongLength, FilePath FROM dbo.Song;";

        try (Connection conn = dbConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql))
        {
            while (rs.next()) {
                allSongs.add(new Song(
                        rs.getInt("Id"),
                        rs.getString("Title"),
                        rs.getString("Artist"),
                        rs.getString("Category"),
                        rs.getDouble("SongLength"),
                        rs.getString("FilePath")));
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
        String sql = "INSERT INTO dbo.Song (Title, Artist, Category, SongLength, FilePath) VALUES (?,?,?,?,?);";

        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS))
        {
            stmt.setString(1, song.getTitle());
            stmt.setString(2, song.getArtist());
            stmt.setString(3, song.getGenre());
            stmt.setDouble(4, song.getTime());
            stmt.setString(5, song.getFilePath());

            stmt.executeUpdate();

            // Get the generated id from the DB - the GUI needs it
            int id = 0;
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    id = rs.getInt(1);
                }
            }

            return new Song(id, song.getTitle(), song.getArtist(), song.getGenre(),
                    song.getTime(), song.getFilePath());
        }
        catch (SQLException ex)
        {
            throw new Exception("Could not create song", ex);
        }
    }

    @Override
    public void updateSong(Song song) throws Exception {
        String sql = "UPDATE dbo.Song SET Title = ?, Artist = ?, Category = ?, SongLength = ?, FilePath = ? WHERE Id = ?;";

        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setString(1, song.getTitle());
            stmt.setString(2, song.getArtist());
            stmt.setString(3, song.getGenre());
            stmt.setDouble(4, song.getTime());
            stmt.setString(5, song.getFilePath());
            stmt.setInt(6, song.getId());

            stmt.executeUpdate();
        }
        catch (SQLException ex)
        {
            throw new Exception("Could not update song", ex);
        }
    }

    /**
     * Deletes the song and removes it from all playlists, in one transaction.
     */
    @Override
    public void deleteSong(Song song) throws Exception {
        try (Connection conn = dbConnector.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement removeFromPlaylists =
                         conn.prepareStatement("DELETE FROM dbo.PlaylistSong WHERE SongId = ?;");
                 PreparedStatement deleteSong =
                         conn.prepareStatement("DELETE FROM dbo.Song WHERE Id = ?;"))
            {
                removeFromPlaylists.setInt(1, song.getId());
                removeFromPlaylists.executeUpdate();

                deleteSong.setInt(1, song.getId());
                deleteSong.executeUpdate();

                conn.commit();
            }
            catch (SQLException ex)
            {
                conn.rollback();
                throw ex;
            }
        }
        catch (SQLException ex)
        {
            throw new Exception("Could not delete song", ex);
        }
    }
}
