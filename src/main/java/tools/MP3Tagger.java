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
import org.jaudiotagger.tag.*;
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

    private static final String[] MUSIC_EXTS = {"mp3","wav","m4a", "wma"};
    private static final String[] validSources = {"Vocaloid Wiki"};
    private static final String[] requestBases = {"http://vocaloid.wikia.com", "http://vocaloidlyrics.wikia.com"};

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
                String imgURL = JSONHandler.requestWikiGetThumbURL(title, requestBases[Arrays.asList(validSources).indexOf(link.description)],1,1);
                if(imgURL.equals(""))
                    continue;
                BufferedImage image;
                image = ImageIO.read(new URL(imgURL));
                ImageIO.write(image, "png", tempThumb);
                return true;
            }
        }

        for(WebLink link: metadata.webLinks){
            if(Arrays.asList(validSources).contains(link.description)){

                    String titleAttempt = metadata.defaultName + "_(" + metadata.name +")";
                    String imgURL = JSONHandler.requestWikiGetThumbURL(titleAttempt, requestBases[0],1,1);
                    if(imgURL.equals(""))
                        continue;
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

        TagOptionSingleton.getInstance().setId3v2PaddingWillShorten(true);



        Metadata md = null;

        AudioFile song = null;
        try {
            song = AudioFileIO.read(songFile);

        } catch (CannotReadException | TagException | ReadOnlyFileException | InvalidAudioFrameException| IOException e) {
            e.printStackTrace();
            return 1;
        }
        for(Metadata mData : metadatas.items){

            if(Math.abs(song.getAudioHeader().getTrackLength()-(mData.lengthSeconds)) < 5){
                md = mData;
            }
        }
        if (md == null){
            if(metadatas.items.length == 1) //Defaulted to first one if only one song is found
                md = metadatas.items[0];
            else
                return 1;
        }

        if(song.getTag()==null)
            song.setTag(song.createDefaultTag());
        Tag tag = song.getTag();

        try {
            /*
            tag.addField(FieldKey.ARTIST, md.artistString);
            tag.addField(FieldKey.TITLE, md.name);
            tag.addField(FieldKey.YEAR, md.getYear());
            tag.addField(FieldKey.ALBUM, md.name);
            tag.addField(FieldKey.ALBUM_ARTIST, md.artistString);
            */

            tag.setField(FieldKey.ARTIST, md.artistString);
            tag.setField(FieldKey.TITLE, md.name);
            tag.setField(FieldKey.YEAR, md.getYear());
            tag.setField(FieldKey.ALBUM, md.name);
            tag.setField(FieldKey.ALBUM_ARTIST, md.artistString);

            File tempThumb = new File("temp");

            if(downloadThumbnail(md, tempThumb)) {
                Artwork cover = ArtworkFactory.createArtworkFromFile(new File("temp"));
                //tag.addField(cover);
                tag.deleteArtworkField();
                tag.setField(cover);
            }
            FileUtils.deleteQuietly(new File("temp"));

            song.setTag(tag);
            song.commit();


        } catch (FieldDataInvalidException | CannotWriteException | IOException e) {
            e.printStackTrace();
            return 1;
        }

        return 0;
    }
}
