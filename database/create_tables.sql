-- MyTunes database (SQL Server)
-- Run against the database named in config/config.settings

CREATE TABLE dbo.Song (
    Id     INT IDENTITY(1,1) PRIMARY KEY,
    Title  NVARCHAR(255) NOT NULL,
    Artist NVARCHAR(255) NOT NULL,
    Genre  NVARCHAR(100) NULL,
    [Time] FLOAT NOT NULL      -- duration in seconds
);

-- Optional test data
INSERT INTO dbo.Song (Title, Artist, Genre, [Time]) VALUES
('Song A', 'Artist A', 'Pop', 215),
('Song B', 'Artist B', 'Rock', 187.5),
('Song C', 'Artist A', 'Jazz', 301);
