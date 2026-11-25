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
    private String filePath; // relative path to the audio file, e.g. music/song.mp3

    /**
     * @param id     database id (use -1 for a song that has not been saved yet)
     * @param title  song title
     * @param artist song artist
     * @param genre  song genre / category (free text)
     * @param time   duration of the song in seconds
     * @param filePath path to the audio file (mp3 or wav)
     */
    public Song(int id, String title, String artist, String genre, double time, String filePath) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.genre = genre;
        this.time = time;
        this.filePath = filePath;
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

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public String toString() {
        return artist + " - " + title;
    }
}
