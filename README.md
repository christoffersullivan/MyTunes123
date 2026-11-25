# MyTunes

JavaFX music player (portfolio assignment). Three-layer architecture:

- `dk.easv.mytunes.GUI` – views, controllers and models (MVC)
- `dk.easv.mytunes.BLL` – business logic
- `dk.easv.mytunes.DAL` – data access (JDBC, SQL Server)
- `dk.easv.mytunes.BE` – business entities

## Setup
1. Run `database/create_tables.sql` on your SQL Server database.
2. Copy `config/config.settings` to `config/config.settings` and fill in Server, Database, User, Password.
3. Run with `mvn clean javafx:run` (or run `dk.easv.mytunes.GUI.Main` from the IDE).

## Test songs
`music/` contains four short generated test songs (3 wav, 1 mp3). Run `database/test_songs.sql` to add them
and a "Test playlist" to the database. The app must be started from the project root so `music/...` paths resolve.
