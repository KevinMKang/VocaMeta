package main;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import classes.Items;
import gui.*;
import org.apache.commons.io.FileUtils;
import parsers.NameParser;
import tools.JSONHandler;
import tools.MP3Tagger;
import javax.swing.*;

public class Main extends JFrame{

    private JButton start;
    private LanguagePanel lPanel;
    private TextLog log;
    private ButtonPanel bPanel;

    private boolean started;

    //Determines whether to save the new file in a new Folder
    private boolean newFolder = false;

    //Location of the new folder
    private String outputFolder = "";

    //Whether to make a new file with the name of the tag
    private boolean rename = false;

    //Whether or not to delete the old file after tagging
    private boolean deleteOld = false;

    //ActionListener made for the start button
    ActionListener startListener = new ActionListener(){
        @Override
        public void actionPerformed(ActionEvent e) {
            log.addText("Starting tagging process.\n");
            log.updateArea();
            if (!started) {
                started = true;

                for (FileButton fileButton : bPanel.getFileButtons()) {

                    if(tagSingleFile(fileButton.getFile())==0) {
                        fileButton.markDone();
                        log.addText(fileButton.getFileName()+ " tagged succesfully!.\n");

                    }else{
                        //log.addText("Failed to tag " + fileButton.getFileName() +". Try renaming the file?\n");
                    }
                    log.updateArea();
                }

                for(int i = 0; i < bPanel.getFileButtons().size(); i++){
                    if(bPanel.getFileButtons().get(i).isDone()){
                        bPanel.removeFileButton(bPanel.getFileButtons().get(i));
                        i-=1;
                    }
                }

                revalidate();
                repaint();
                started = false;
            }
        }
    };

    // Tags a single File, starting from an API call to VocaDB.
    public int tagSingleFile(File file){
        String fileName = NameParser.parseName(file.getName());
        String json = JSONHandler.requestJSONVocaDB(fileName, lPanel.getLanguageSetting());
        Items items = JSONHandler.parseJSONVocaDB(json);
        return MP3Tagger.tagMP3(items, file);
    }

    //Main function
    public Main(){

        started = false;

        start = new JButton("Start");
        start.addActionListener(startListener);

        lPanel = new LanguagePanel();
        log = new TextLog();
        bPanel = new ButtonPanel();



        JLabel dragInfo = new JLabel("Drag and Drop your files/folders below. Click to remove them.");
        dragInfo.setHorizontalTextPosition(SwingConstants.CENTER);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
        panel.add(lPanel);
        panel.add(dragInfo);
        panel.add(bPanel);
        panel.add(log);
        panel.add(start);
        panel.setPreferredSize(new java.awt.Dimension(800, 600));
        getContentPane().add(panel);
        setVisible(true);
        setTitle("VocaMeta");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
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