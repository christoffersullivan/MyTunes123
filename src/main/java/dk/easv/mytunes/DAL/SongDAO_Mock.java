package dk.easv.mytunes.DAL;

import dk.easv.mytunes.BE.Song;

import java.util.ArrayList;
import java.util.List;


public class SongDAO_Mock implements ISongDataAccess {

    private List<Song> allSongs;
    private int nextId = 5;

    public SongDAO_Mock() {
        allSongs = new ArrayList<>();
        allSongs.add(new Song(1, "Song A", "Artist A", "Pop", 215, "music/demo.mp3"));
        allSongs.add(new Song(2, "Song B", "Artist B", "Rock", 187.5, "music/demo.mp3"));
        allSongs.add(new Song(3, "Song C", "Artist A", "Jazz", 301, "music/demo.mp3"));
        allSongs.add(new Song(4, "Song D", "Artist C", "Pop", 164, "music/demo.mp3"));
    }

    @Override
    public List<Song> getAllSongs() {
        return new ArrayList<>(allSongs);
    }

    @Override
    public Song createSong(Song newSong) {
        Song created = new Song(nextId++, newSong.getTitle(), newSong.getArtist(),
                newSong.getGenre(), newSong.getTime(), newSong.getFilePath());
        allSongs.add(created);
        return created;
    }

    @Override
    public void updateSong(Song song) {
        for (int i = 0; i < allSongs.size(); i++) {
            if (allSongs.get(i).getId() == song.getId()) {
                allSongs.set(i, song);
                return;
            }
        }
    }

    @Override
    public void deleteSong(Song song) {
        allSongs.removeIf(s -> s.getId() == song.getId());
    }
}
