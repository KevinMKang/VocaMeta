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

    public FileButton(File f, ActionListener listener, JPanel panel){
        this.file = f;
        this.setText(f.getPath() + "    " +f.getName());
        this.setBorderPainted(false);
        this.setBackground(Color.white);
        this.addActionListener(listener);
        this.finished = false;
        panel.add(this);
        fileListing.add(f);
        buttonListing.add(this);
    }

    public void removeSelf(JPanel panel){
        fileListing.remove(file);
        buttonListing.remove(this);
        panel.remove(this);
    }

    public void markDone(){
        this.finished = true;
    }

    public boolean isDone(){
        return this.finished;
    }

    public void clearAllFinished(JPanel buttonPanel){
        for(int i = 0; i < buttonListing.size(); i++){
            if(buttonListing.get(i).isDone()){
                removeSelf(buttonPanel);
                i-=1;
            }
        }
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
