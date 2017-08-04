package classes;

/**
 * Basic Metadata class to hold the data.
 */
public class Metadata {
    public String name;
    public String artistString;
    private String publishDate;
    public int lengthSeconds;
    public String defaultName;
    public WebLink[] webLinks;

    private Metadata(){}

    public String getYear() {
        return publishDate.split("-")[0];
    }

}
