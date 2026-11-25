-- Test data for MyTunes: 4 short generated songs + a "Test playlist".
-- The audio files are in the project's music/ folder (committed with the code).
-- Safe to run more than once - nothing is inserted twice.
-- Run the whole script at once (select all, then execute).

IF NOT EXISTS (SELECT 1 FROM dbo.Song WHERE FilePath = 'music/test_1_scale.wav')
    INSERT INTO dbo.Song (Title, Artist, Category, SongLength, FilePath)
    VALUES ('Test 1 - Scale (wav)', 'Test Tones', 'Other', 8, 'music/test_1_scale.wav');

IF NOT EXISTS (SELECT 1 FROM dbo.Song WHERE FilePath = 'music/test_2_arpeggio.wav')
    INSERT INTO dbo.Song (Title, Artist, Category, SongLength, FilePath)
    VALUES ('Test 2 - Arpeggio (wav)', 'Test Tones', 'Other', 10, 'music/test_2_arpeggio.wav');

IF NOT EXISTS (SELECT 1 FROM dbo.Song WHERE FilePath = 'music/test_3_melody.wav')
    INSERT INTO dbo.Song (Title, Artist, Category, SongLength, FilePath)
    VALUES ('Test 3 - Melody (wav)', 'Test Tones', 'Other', 12, 'music/test_3_melody.wav');

IF NOT EXISTS (SELECT 1 FROM dbo.Song WHERE FilePath = 'music/test_4_scale.mp3')
    INSERT INTO dbo.Song (Title, Artist, Category, SongLength, FilePath)
    VALUES ('Test 4 - Scale (mp3)', 'Test Tones', 'Other', 8, 'music/test_4_scale.mp3');

IF NOT EXISTS (SELECT 1 FROM dbo.Playlist WHERE Name = 'Test playlist')
    INSERT INTO dbo.Playlist (Name) VALUES ('Test playlist');

DECLARE @playlistId INT = (SELECT TOP 1 Id FROM dbo.Playlist WHERE Name = 'Test playlist');

-- Songs in a fixed order (SongOrder 1..4)
INSERT INTO dbo.PlaylistSong (PlaylistId, SongId, SongOrder)
SELECT @playlistId, s.Id, t.Ord
FROM (VALUES ('music/test_1_scale.wav', 1),
             ('music/test_2_arpeggio.wav', 2),
             ('music/test_3_melody.wav', 3),
             ('music/test_4_scale.mp3', 4)) AS t(Path, Ord)
JOIN dbo.Song s ON s.FilePath = t.Path
WHERE NOT EXISTS (SELECT 1 FROM dbo.PlaylistSong ps
                  WHERE ps.PlaylistId = @playlistId AND ps.SongId = s.Id);
