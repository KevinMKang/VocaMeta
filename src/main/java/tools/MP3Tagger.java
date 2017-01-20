package tools;

import classes.Items;
import classes.Metadata;
import org.apache.commons.io.FileUtils;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by KMFK on 2017-01-13.
 * Uses JAudioTagger to tag given mp3s and to save them. Also checks whether the given data is correct or not.
 */
public class MP3Tagger {

    private static final String MUSIC_EXTS = "([^\\s]+(\\.(?i)(mp3|wav|m4a))$)";

    private static Pattern pattern = Pattern.compile(MUSIC_EXTS);
    private static Matcher matcher;

    private MP3Tagger(){}

    public static File[] parseFolder(File directory){


        File[] files = directory.listFiles();
        ArrayList<File> output = new ArrayList<>();

        for(File i : files){
            matcher = pattern.matcher(i.getName());
            if(matcher.matches()){
                output.add(i);
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
        System.out.println(song.getAudioHeader().getTrackLength());
        for(Metadata mData : metadatas.items){




            if(song.getAudioHeader().getTrackLength() > (mData.getLengthSeconds()-5) && song.getAudioHeader().getTrackLength() < (mData.getLengthSeconds()+5)){
                md = mData;
            }
        }
        if (md == null){
            return 1;
        }
        song.setTag(song.createDefaultTag());
        Tag tag = song.getTag();
        try {
            tag.addField(FieldKey.ARTIST, md.getArtistString());
            tag.addField(FieldKey.TITLE, md.getName());
            tag.addField(FieldKey.YEAR, md.getPublishDate());
            tag.setField(FieldKey.ARTIST, md.getArtistString());
            tag.setField(FieldKey.TITLE, md.getName());
            tag.setField(FieldKey.YEAR, md.getPublishDate());

            BufferedImage image = ImageIO.read(new URL(md.getThumbUrl()));

            if(image != null) {
                File tempThumb = new File("temp");
                ImageIO.write(image, "jpg", tempThumb);

                Artwork cover = ArtworkFactory.createArtworkFromFile(tempThumb);
                tag.addField(cover);
                tag.setField(cover);
                FileUtils.deleteQuietly(tempThumb);
            }

            song.setTag(tag);
            song.commit();
            //AudioFileIO.writeAs(song, "testfiles/songs/taggedSongs/" + md.getName());


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
