package dk.easv.mytunes.BE;

/**
 * Business entity representing a song in MyTunes.
 */
public class Song {

    private int id;
    private String title;
    private String artist;
    private String genre;
    private double time; // duration in seconds

    /**
     * @param id     database id (use -1 for a song that has not been saved yet)
     * @param title  song title
     * @param artist song artist
     * @param genre  song genre / category (free text)
     * @param time   duration of the song in seconds
     */
    public Song(int id, String title, String artist, String genre, double time) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.genre = genre;
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    /**
     * @return duration of the song in seconds
     */
    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return artist + " - " + title;
    }
}
