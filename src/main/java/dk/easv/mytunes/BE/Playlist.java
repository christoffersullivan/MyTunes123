package dk.easv.mytunes.BE;


public class Playlist {

    private int id;
    private String name;
/*
 setters and getters for id and name.
 */
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
