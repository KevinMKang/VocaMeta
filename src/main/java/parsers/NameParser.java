package parsers;

/**
 * Created by KMFK on 2017-01-13.
 * A class that is designed to parse file names to make searching easier.
 */
public class NameParser {

    public static String parseName(String fileName){
        String parsedName = fileName.substring(0,fileName.length()-4);
        parsedName = parsedName.replaceAll("_", " ");

        return parsedName;
    }
}
