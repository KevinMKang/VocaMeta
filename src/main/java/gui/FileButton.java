package gui;

import javax.swing.*;
import java.io.File;

/**
 * Created by Kevin on 1/16/2017.
 */
public class FileButton extends JButton {
    private File file;

    public FileButton(File f){
        this.file = f;
        this.setText(f.getName());
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
