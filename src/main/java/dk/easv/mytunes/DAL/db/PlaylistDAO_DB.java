package dk.easv.mytunes.DAL.db;

// Project imports
import dk.easv.mytunes.BE.Playlist;
import dk.easv.mytunes.BE.Song;
import dk.easv.mytunes.DAL.IPlaylistDataAccess;

// Java imports
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * JDBC implementation of playlist data access (tables Playlist and PlaylistSong).
 */
public class PlaylistDAO_DB implements IPlaylistDataAccess {

    private DBConnector dbConnector = new DBConnector();

    public PlaylistDAO_DB() throws IOException {}

    @Override
    public List<Playlist> getAllPlaylists() throws Exception {
        List<Playlist> playlists = new ArrayList<>();
        String sql = "SELECT Id, Name FROM dbo.Playlist ORDER BY Id;";

        try (Connection conn = dbConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql))
        {
            while (rs.next()) {
                playlists.add(new Playlist(rs.getInt("Id"), rs.getString("Name")));
            }
            return playlists;
        }
        catch (SQLException ex)
        {
            throw new Exception("Could not get playlists from database", ex);
        }
    }

    @Override
    public Playlist createPlaylist(Playlist playlist) throws Exception {
        String sql = "INSERT INTO dbo.Playlist (Name) VALUES (?);";

        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS))
        {
            stmt.setString(1, playlist.getName());
            stmt.executeUpdate();

            int id = 0;
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    id = rs.getInt(1);
                }
            }
            return new Playlist(id, playlist.getName());
        }
        catch (SQLException ex)
        {
            throw new Exception("Could not create playlist", ex);
        }
    }

    @Override
    public void updatePlaylist(Playlist playlist) throws Exception {
        String sql = "UPDATE dbo.Playlist SET Name = ? WHERE Id = ?;";

        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setString(1, playlist.getName());
            stmt.setInt(2, playlist.getId());
            stmt.executeUpdate();
        }
        catch (SQLException ex)
        {
            throw new Exception("Could not update playlist", ex);
        }
    }

    @Override
    public void deletePlaylist(Playlist playlist) throws Exception {
        try (Connection conn = dbConnector.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement removeSongs =
                         conn.prepareStatement("DELETE FROM dbo.PlaylistSong WHERE PlaylistId = ?;");
                 PreparedStatement deletePlaylist =
                         conn.prepareStatement("DELETE FROM dbo.Playlist WHERE Id = ?;"))
            {
                removeSongs.setInt(1, playlist.getId());
                removeSongs.executeUpdate();

                deletePlaylist.setInt(1, playlist.getId());
                deletePlaylist.executeUpdate();

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
            throw new Exception("Could not delete playlist", ex);
        }
    }

    @Override
    public List<Song> getSongsInPlaylist(Playlist playlist) throws Exception {
        List<Song> songs = new ArrayList<>();
        String sql = "SELECT s.Id, s.Title, s.Artist, s.Category, s.SongLength, s.FilePath " +
                "FROM dbo.Song s " +
                "JOIN dbo.PlaylistSong ps ON ps.SongId = s.Id " +
                "WHERE ps.PlaylistId = ? " +
                "ORDER BY ps.SongOrder;";

        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setInt(1, playlist.getId());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    songs.add(new Song(
                            rs.getInt("Id"),
                            rs.getString("Title"),
                            rs.getString("Artist"),
                            rs.getString("Category"),
                            rs.getDouble("SongLength"),
                            rs.getString("FilePath")));
                }
            }
            return songs;
        }
        catch (SQLException ex)
        {
            throw new Exception("Could not get songs in playlist", ex);
        }
    }

    @Override
    public void addSongToPlaylist(Playlist playlist, Song song) throws Exception {
        // SongOrder = current highest + 1, so the song ends up last
        String sql = "INSERT INTO dbo.PlaylistSong (PlaylistId, SongId, SongOrder) " +
                "SELECT ?, ?, ISNULL(MAX(SongOrder), 0) + 1 FROM dbo.PlaylistSong WHERE PlaylistId = ?;";

        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setInt(1, playlist.getId());
            stmt.setInt(2, song.getId());
            stmt.setInt(3, playlist.getId());
            stmt.executeUpdate();
        }
        catch (SQLException ex)
        {
            throw new Exception("Could not add song to playlist", ex);
        }
    }

    @Override
    public void removeSongFromPlaylist(Playlist playlist, Song song) throws Exception {
        String sql = "DELETE FROM dbo.PlaylistSong WHERE PlaylistId = ? AND SongId = ?;";

        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setInt(1, playlist.getId());
            stmt.setInt(2, song.getId());
            stmt.executeUpdate();
        }
        catch (SQLException ex)
        {
            throw new Exception("Could not remove song from playlist", ex);
        }
    }
}
