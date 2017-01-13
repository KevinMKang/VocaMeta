package tools;

import classes.Items;
import classes.Metadata;
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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by KMFK on 2017-01-13.
 * Uses JAudioTagger to tag given mp3s and to save them. Also checks whether the given data is correct or not.
 */
public class MP3Tagger {

    private MP3Tagger(){}

    public static File[] parseFolder(String directoryPath){
        File folder = new File(directoryPath);
        new File(directoryPath+"taggedSongs/").mkdir();
        File[] files = folder.listFiles();
        ArrayList<File> output = new ArrayList<>();

        for(File i : files){
            if((i.getName().substring(i.getName().length()-3,i.getName().length()).equals("mp3"))){
                output.add(i);
            }
        }
        return output.toArray(new File[output.size()]);
    }

    public static void tagMP3 (Items metadatas, File songFile){

        Metadata md = null;

        AudioFile song = null;
        try {
            song = AudioFileIO.read(songFile);
            for(Metadata mData : metadatas.items){
                if(song.getAudioHeader().getTrackLength() > (mData.getLengthSeconds()-5) && song.getAudioHeader().getTrackLength() < (mData.getLengthSeconds()+5)){
                    md = mData;
                }
            }
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

        Tag tag = song.getTag();
        try {
            tag.addField(FieldKey.ARTIST, md.getArtistString());
            tag.addField(FieldKey.TITLE, md.getName());
            tag.addField(FieldKey.ORIGINAL_YEAR, md.getPublishDate());
            tag.setField(FieldKey.ARTIST, md.getArtistString());
            tag.setField(FieldKey.TITLE, md.getName());
            tag.setField(FieldKey.ORIGINAL_YEAR, md.getPublishDate().toString());
            Artwork cover = ArtworkFactory.createLinkedArtworkFromURL(md.getThumbUrl());

            System.out.println(cover.getWidth());
            tag.addField(cover);
            tag.setField(cover);

            song.setTag(tag);
            AudioFileIO.writeAs(song, "testfiles/songs/taggedSongs/" + md.getName());
        } catch (FieldDataInvalidException e) {
            e.printStackTrace();
        } catch (TagException e) {
            e.printStackTrace();
        } catch (CannotWriteException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
