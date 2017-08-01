package tools;

import classes.Items;
import classes.Metadata;
import classes.PVData;
import classes.WebLink;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.CannotWriteException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldDataInvalidException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.images.Artwork;
import org.jaudiotagger.tag.images.ArtworkFactory;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Uses JAudioTagger to tag given mp3s and to save them. Also checks whether the given data is correct or not.
 */
public class MP3Tagger {

    //private static final String MUSIC_EXTS = "([^.]+(\\.(?i)(mp3|wav|m4a))$)";
    private static final String[] MUSIC_EXTS = {"mp3","wav","m4a", "wma"};
    private static final String[] validSources = {"Vocaloid Wiki", "Vocaloid Lyrics Wiki"};
    private static final String[] requestBases = {"http://vocaloid.wikia.com", "http://vocaloidlyrics.wikia.com"};
    private static final String hqThumbnailURL = "https://img.youtube.com/vi/%s/hqdefault.jpg";
    private static final String maxresThumbnailURL = "https://img.youtube.com/vi/%s/maxresdefault.jpg";

    //private static Pattern pattern = Pattern.compile(MUSIC_EXTS);
    //private static Matcher matcher;

    private MP3Tagger(){}

    private static boolean check_ext(String ext){
        for(String s: MUSIC_EXTS){
            if(ext.equals(s)){
                return true;
            }
        }
        return false;
    }



    private static boolean downloadThumbnail(Metadata metadata, File tempThumb) throws IOException{
        for(WebLink link : metadata.webLinks){
            if(Arrays.asList(validSources).contains(link.description)){
                String title = link.url.substring(link.url.lastIndexOf('/')+1);
                String imgURL = JSONHandler.requestWikiGetThumbURL(title, requestBases[Arrays.asList(validSources).indexOf(link.description)]);
                BufferedImage image;
                image = ImageIO.read(new URL(imgURL));
                ImageIO.write(image, "png", tempThumb);
                return true;
            }
        }

        for(WebLink link: metadata.webLinks){
            if(Arrays.asList(validSources).contains(link.description)){

                    String titleAttempt = metadata.defaultName + "_(" + metadata.name +")";
                    String imgURL = JSONHandler.requestWikiGetThumbURL(titleAttempt, requestBases[0]);
                    BufferedImage image;
                    image = ImageIO.read(new URL(imgURL));
                    ImageIO.write(image, "png", tempThumb);
                    return true;

            }
        }

    /*
        for(PVData pv : metadata.pvs){
            if(pv.service.equals("Youtube") && (pv.pvType.equals("Original") || pv.pvType.equals("Reprint"))){
                BufferedImage image;

                try {
                    image = ImageIO.read(new URL(String.format(maxresThumbnailURL, pv.pvId)));
                }catch(IOException e){
                    image = ImageIO.read(new URL(String.format(hqThumbnailURL, pv.pvId)));
                }

                ImageIO.write(image, "jpg", tempThumb);
                return true;
            }
        }
        */
        return false;
    }

    public static File[] parseFile(File file){

        ArrayList<File> output = new ArrayList<>();
        if(!file.isDirectory()){
            if(check_ext(FilenameUtils.getExtension(file.getName())))
                output.add(file);
        }
        else {
            File[] files = file.listFiles();
            for (File i : files) {
                for(File inDir: parseFile(i)){
                    output.add(inDir);
                }
            }
        }
        return output.toArray(new File[output.size()]);
    }

    public static int tagMP3 (Items metadatas, File songFile){

        Metadata md = null;

        AudioFile song = null;
        try {
            song = AudioFileIO.read(songFile);



        } catch (TagException e) {
            e.printStackTrace();
        } catch (ReadOnlyFileException e) {
            e.printStackTrace();
        } catch (CannotReadException e) {
            e.printStackTrace();
        } catch (InvalidAudioFrameException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        for(Metadata mData : metadatas.items){

            if(song.getAudioHeader().getTrackLength() > (mData.getLengthSeconds()-5) && song.getAudioHeader().getTrackLength() < (mData.getLengthSeconds()+5)){
                md = mData;
            }
        }
        if (md == null){
            if(metadatas.items.length == 1) //Defaulted to first one if only one song is found
                md = metadatas.items[0];
            else
                return 1;
        }
        song.setTag(song.createDefaultTag());
        Tag tag = song.getTag();
        try {
            //tag.addField(FieldKey.ARTIST, md.getArtistString());
            tag.addField(FieldKey.TITLE, md.getName());
            tag.addField(FieldKey.YEAR, md.getPublishDate());
            //tag.setField(FieldKey.ARTIST, md.getArtistString());
            tag.setField(FieldKey.TITLE, md.getName());
            tag.setField(FieldKey.YEAR, md.getPublishDate());
            tag.addField(FieldKey.ALBUM, md.getName());
            //tag.addField(FieldKey.ALBUM_ARTIST, md.getArtistString());
            tag.setField(FieldKey.ALBUM, md.getName());
            //tag.setField(FieldKey.ALBUM_ARTIST, md.getArtistString());

            File tempThumb = new File("temp");

            if(downloadThumbnail(md, tempThumb)) {
                Artwork cover = ArtworkFactory.createArtworkFromFile(new File("temp"));
                tag.addField(cover);
                tag.setField(cover);
            }
            FileUtils.deleteQuietly(new File("temp"));

            song.setTag(tag);
            song.commit();


        } catch (FieldDataInvalidException e) {
            e.printStackTrace();
        } catch (CannotWriteException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return 0;
    }


}
