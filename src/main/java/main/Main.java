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
import gui.LanguagePanel;
import org.apache.commons.io.FileUtils;
import parsers.NameParser;
import tools.JSONHandler;
import tools.MP3Tagger;
import gui.FileDrop;
import javax.swing.*;

public class Main extends JFrame{

    private JPanel buttonPanel = new JPanel();
    private JButton start;
    private LanguagePanel lp;

    private boolean started;


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
            removeFileButton((FileButton) e.getSource());
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
                revalidate();
                repaint();

                for (FileButton fileButton : FileButton.buttonListing) {
                    if(tagSingleFile(fileButton.getFile())==0)
                        fileButton.markDone();
                }

                for(int i = 0; i < FileButton.buttonListing.size(); i++){
                    if(FileButton.buttonListing.get(i).isDone()){
                        removeFileButton(FileButton.buttonListing.get(i));
                        i-=1;
                    }
                }

                revalidate();
                repaint();

                start.setText("Start");
                started = false;
            }
        }
    };

    public void removeFileButton(FileButton fb){
        buttonPanel.remove(fb);
        FileButton.fileListing.remove(fb.getFile());
        FileButton.buttonListing.remove(fb);
    }

    // Tags a single File, starting from an API call to VocaDB.
    public int tagSingleFile(File file){
        String fileName = NameParser.parseName(file.getName());
        String json = JSONHandler.requestJSONVocaDB(fileName, lp.getLanguageSetting());
        Items items = JSONHandler.parseJSONVocaDB(json);
        return MP3Tagger.tagMP3(items, file);
    }

    //Creates a FileButton from a file and adds it to the list of files.
    public void dragAndDropFile(File f){
        File[] files = MP3Tagger.parseFile(f);
        for(File file : files){
            FileButton fb = new FileButton(file);
            fb.addActionListener(fileButtonListener);
            buttonPanel.add(fb);
        }
        revalidate();
        repaint();
    }

    //Main function
    public Main(){

        started = false;

        start = new JButton("Start");
        start.addActionListener(startListener);

        lp = new LanguagePanel();

        buttonPanel.setOpaque(true);
        buttonPanel.setBackground(Color.white);
        buttonPanel.setBorder(BorderFactory.createLoweredBevelBorder());
        buttonPanel.setLayout(new GridLayout(0, 1));

        JScrollPane scrollPane = new JScrollPane(buttonPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);

        //Filedrop Object to let area support dragging and dropping files.
        new FileDrop(buttonPanel, new FileDrop.Listener()
        {public void filesDropped(File[] files){
                for(File f : files){
                    File[] contents = MP3Tagger.parseFile(f);
                    for(File file : contents){
                        FileButton fb = new FileButton(file);
                        fb.addActionListener(fileButtonListener);
                        buttonPanel.add(fb);

                    }
                    revalidate();
                    repaint();
                }
            }
        });

        JLabel dragInfo = new JLabel("Drag and Drop your files/folders below. Click to remove them.");
        dragInfo.setHorizontalTextPosition(SwingConstants.CENTER);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
        panel.add(lp);
        panel.add(dragInfo, BorderLayout.CENTER);
        panel.add(scrollPane);
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