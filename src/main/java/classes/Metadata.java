package classes;

/**
 * Basic Metadata class to hold the data.
 */
public class Metadata {
    public String name;
    public String artistString;
    public String publishDate;
    public int lengthSeconds;
    public String defaultName;
    public String largeThumbUrl = "";
    public String hqresThumbUrl="";
    public PVData[] pvs;
    public WebLink[] webLinks;
    public String getLargeThumbUrl() {
        return largeThumbUrl;
    }


    public String getThumbUrl() {
        return thumbUrl;
    }

    public int getLengthSeconds() {
        return lengthSeconds;
    }

    public String thumbUrl;

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
