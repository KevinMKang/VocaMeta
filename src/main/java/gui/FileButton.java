package gui;

import javax.swing.*;
import java.awt.*;
import java.io.File;


public class FileButton extends JButton {
    private File file;

    private boolean finished;

    public FileButton(File f){
        this.file = f;
        this.setText(f.getPath());
        this.setBorderPainted(false);
        this.setBackground(Color.white);
        this.finished = false;
        setHorizontalAlignment(SwingConstants.LEFT);
    }

    public void markDone(){
        this.finished = true;
    }

    public boolean isDone(){
        return this.finished;
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
