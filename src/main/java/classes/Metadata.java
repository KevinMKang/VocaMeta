package classes;

/**
 * Basic Metadata class to hold the data.
 */
public class Metadata {
    public String name;
    public String artistString;
    public String publishDate;
    public int lengthSeconds;

    public String getName(){
        return name;
    }


    public String getArtistString() {
        return artistString;
    }


    //Only get the year. Fix later using regex
    public String getPublishDate() {
        return publishDate.split("-")[0];
    }

}
