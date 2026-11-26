package dk.easv.mytunes.BE;


public class Song {

    private int id;
    private String title;
    private String artist;
    private String genre;
    private double time;
    private String filePath;


    /*
    setters for song information.
     */
    public Song(int id, String title, String artist, String genre, double time, String filePath) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.genre = genre;
        this.time = time;
        this.filePath = filePath;
    }

    /*
    getter to get information of songs.
     */
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

   /*
   song timer.
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
