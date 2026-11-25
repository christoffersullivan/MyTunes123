-- MyTunes schema (SQL Server), matching the existing chrsul01_MyTunes database.
-- Only needed on a fresh database - do NOT run on a database that already has these tables.

CREATE TABLE dbo.Song (
    Id         INT IDENTITY(1,1) PRIMARY KEY,
    Title      VARCHAR(255) NOT NULL,
    Artist     VARCHAR(255) NOT NULL,
    Category   VARCHAR(100) NULL,
    SongLength INT NOT NULL,          -- seconds
    FilePath   VARCHAR(500) NULL
);

CREATE TABLE dbo.Playlist (
    Id   INT IDENTITY(1,1) PRIMARY KEY,
    Name VARCHAR(255) NOT NULL
);

CREATE TABLE dbo.PlaylistSong (
    PlaylistId INT NOT NULL REFERENCES dbo.Playlist(Id) ON DELETE CASCADE,
    SongId     INT NOT NULL REFERENCES dbo.Song(Id) ON DELETE CASCADE,
    SongOrder  INT NOT NULL,
    PRIMARY KEY (PlaylistId, SongId)
);
