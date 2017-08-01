package parsers;

/**
 * A class that is designed to parse file names to make searching easier.
 */
public class NameParser {

    public static String parseName(String fileName){
        String parsedName = fileName.substring(0,fileName.lastIndexOf('.'));
        return parsedName;
    }
}
