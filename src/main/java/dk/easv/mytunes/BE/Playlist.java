package dk.easv.mytunes.BE;

/**
 * Business entity representing a playlist. The songs in a playlist are stored separately (PlaylistSong table).
 */
public class Playlist {

    private int id;
    private String name;

    public Playlist(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
