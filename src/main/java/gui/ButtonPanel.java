package gui;

import main.Main;
import tools.MP3Tagger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.concurrent.Callable;

/**
 * Created by KMFK on 2017-01-23.
 */
public class ButtonPanel extends JPanel {

    private ArrayList<File> fileListing;
    private ArrayList<FileButton> buttonListing;

    //ActionListener made specifically for fileButtons
    ActionListener fileButtonListener = new ActionListener(){
        @Override
        public void actionPerformed(ActionEvent e) {

            removeFileButton((FileButton) e.getSource());
            revalidate();
            repaint();
        }
    };

    public ButtonPanel(){

        fileListing = new ArrayList<>();
        buttonListing = new ArrayList<>();

        //Filedrop Object to let area support dragging and dropping files.
        new FileDrop(this, new FileDrop.Listener()
        {public void filesDropped(File[] files){

                for(File f : files){
                    File[] contents = MP3Tagger.parseFile(f);
                    for(File file : contents){
                        if(!fileListing.contains(file)) {
                            FileButton fb = new FileButton(file);
                            fb.addActionListener(fileButtonListener);
                            addFileButton(fb);

                        }
                    }
                    revalidate();
                    repaint();
                }
            }
        });
        setPreferredSize(new Dimension(750,300));
        setOpaque(true);
        setBackground(Color.white);
        setBorder(BorderFactory.createLoweredBevelBorder());
        setLayout(new BoxLayout(this,BoxLayout.PAGE_AXIS));
    }

    private void addFileButton(FileButton fb){
        add(fb);
        fileListing.add(fb.getFile());
        buttonListing.add(fb);
    }

    public void removeFileButton(FileButton fb){
        remove(fb);
        fileListing.remove(fb.getFile());
        buttonListing.remove(fb);
    }

    public ArrayList<File> getFiles(){
        return fileListing;
    }

    public ArrayList<FileButton> getFileButtons(){ return buttonListing; }
}
