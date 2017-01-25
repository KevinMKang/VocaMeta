## VocaMeta

VocaMeta is a small Java-based utility that automatically tags vocaloid music for users. All music files given are searched on VocaDB, and tagged appropriately with title, artist, thumbnail, and year of creation if found.

## Compilation

1. Clone this respository.
2. Navigate into it using a command line.
3. Run mvn validate.
4. run mvn clean install.

## Executing

After compilation, an executable jar will be created in the cloned repository named target. Open the jar, and drag and drop your files into the program and press start to begin the searching and tagging process.

The original files are tagged, new files are not created.

## Built with:

- JAudioTagger
- RESTeasy/ JAX-RS

