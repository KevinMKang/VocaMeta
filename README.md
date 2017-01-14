## VocaMeta

VocaMeta is a small Java-based utility that automatically tags vocaloid music for users. All music files within the "songs" folder are searched on VocaDB, and tagged appropriately with title, artist, thumbnail, and year of creation if found.

## Compilation

1. Clone this respository.
2. Navigate into it using a command line.
3. Run mvn validate.
4. run mvn clean install.

## Executing

After compilation, an executable jar will be created in the cloned repository named target. Create a folder named songs, and add your songs to it. Currently, only songs with their title + file extension will be acceped (i.e. "TITLE.mp3"). This will work regardless of language.

After running the jar, the tagged songs will be in target/songs/taggedSongs.

## Built with:

- JAudioTagger
- RESTeasy/ JAX-RS

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for detail.
