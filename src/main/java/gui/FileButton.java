package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

/**
 * Created by Kevin on 1/16/2017.
 */
public class FileButton extends JButton {
    private File file;

    public static ArrayList<File> fileListing = new ArrayList<>();
    public static ArrayList<FileButton> buttonListing = new ArrayList<>();
    private boolean finished;

    public FileButton(File f){
        this.file = f;
        //this.setText(f.getPath() + "    " +f.getName());
        this.setText(f.getPath());
        this.setBorderPainted(false);
        this.setBackground(Color.white);
        this.finished = false;
        fileListing.add(f);
        buttonListing.add(this);
        setHorizontalAlignment(SwingConstants.LEFT);
    }

    public void markDone(){
        this.finished = true;
    }

    public boolean isDone(){
        return this.finished;
    }

    public void removeSelf(){
        fileListing.remove(this.file);
        buttonListing.remove(this);
    }


    public File getFile(){
        return this.file;
    }

    public String getFileName(){
        return this.file.getName();
    }

    public String getFilePath(){
        return this.file.getPath();
    }

}
