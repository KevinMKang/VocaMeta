package main;

import java.awt.*;
import java.awt.dnd.DropTarget;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import classes.Items;
import gui.FileButton;
import org.apache.commons.io.FileUtils;
import parsers.NameParser;
import tools.JSONHandler;
import tools.MP3Tagger;
import gui.FileDrop;
import javax.swing.*;

public class Main extends JFrame{

    private JPanel buttonPanel = new JPanel();
    private JButton start;
    private boolean started;

    private enum LANGUAGES {
        ROMAJI, ENGLISH, JAPANESE
    }

    //Determines whether to save the new file in a new Folder
    private boolean newFolder = false;

    //Location of the new folder
    private String outputFolder = "";

    //Whether to make a new file with the name of the tag
    private boolean rename = false;

    //Whether or not to delete the old file after tagging
    private boolean deleteOld = false;


    //ActionListener made specifically for fileButtons
    ActionListener fileButtonListener = new ActionListener(){
        @Override
        public void actionPerformed(ActionEvent e) {
            ((FileButton)e.getSource()).removeSelf(buttonPanel);
            revalidate();
            repaint();
        }
    };

    //ActionListener made for the start button
    ActionListener startListener = new ActionListener(){
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!started) {
                started = true;
                start.setText("Working...");
                for (FileButton files : FileButton.buttonListing) {
                    File current = files.getFile();
                    if (!current.isDirectory()) {
                        tagSingleFile(current);

                    } else {
                        File[] directoryContents = MP3Tagger.parseFolder(current);
                        for (File file : directoryContents) {
                            tagSingleFile(file);
                        }
                    }
                    

                }
                revalidate();
                repaint();

                start.setText("Start");
                started = false;
            }
        }
    };

    // Tags a single File, starting from an API call to VocaDB.
    public int tagSingleFile(File file){
        String fileName = NameParser.parseName(file.getName());
        System.out.println(fileName);
        String json = JSONHandler.requestJSONVocaDB(fileName);
        System.out.println(json);
        Items items = JSONHandler.parseJSONVocaDB(json);
        return MP3Tagger.tagMP3(items, file);
    }

    //Creates a FileButton from a file and adds it to the list of files.
    public void dragAndDropFile(File f){
        if(f.isDirectory()){
            File[] files = MP3Tagger.parseFolder(f);
            for(File file : files){
                new FileButton(file,fileButtonListener,buttonPanel);
            }
        }else{
            new FileButton(f,fileButtonListener,buttonPanel);
        }

        revalidate();
        repaint();
    }



    //Main function
    public Main(){


        started = false;

        start = new JButton("Start");
        start.addActionListener(startListener);

        buttonPanel.setOpaque(true);
        buttonPanel.setBackground(Color.white);
        buttonPanel.setBorder(BorderFactory.createLoweredBevelBorder());
        buttonPanel.setLayout(new GridLayout(20, 0));

        //Filedrop Object to let area support dragging and dropping files.
        new FileDrop(buttonPanel, new FileDrop.Listener()
        {public void filesDropped(File[] files){
                for(File f : files){

                    if(!f.isDirectory()) {
                        if (!FileButton.fileListing.contains(f)) {
                            dragAndDropFile(f);
                        } else {
                            //System.out.println(f.getPath());
                        }
                    }else{
                        for(File file : MP3Tagger.parseFolder(f)){
                            if (!FileButton.fileListing.contains(file)) {
                                dragAndDropFile(file);
                            } else {
                               System.out.println(file.getPath());
                            }
                        }
                    }
                }
            }
        });


        JLabel dragInfo = new JLabel("Drag and Drop your files/folders below. Click to remove them.");
        dragInfo.setHorizontalTextPosition(SwingConstants.CENTER);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
        panel.add(dragInfo, BorderLayout.CENTER);
        panel.add(buttonPanel);
        panel.add(start);
        panel.setPreferredSize(new java.awt.Dimension(800, 600));
        getContentPane().add(panel);
        setVisible(true);
        setTitle("VocaMeta");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
    }

    public static void main(String[] args){

        File tempDir = new File("temp");
        tempDir.mkdir();

        new Main();

        try {
            FileUtils.deleteDirectory(tempDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}