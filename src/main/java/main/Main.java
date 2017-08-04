package main;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URL;
import classes.Items;
import gui.*;
import parsers.NameParser;
import tools.JSONHandler;
import tools.MP3Tagger;
import javax.swing.*;

public class Main extends JFrame{

    private JButton start;
    private LanguagePanel lPanel;
    private TextLog log;
    private FilePanel bPanel;

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
            log.addText("Starting tagging process.");
            log.updateArea();
            if (!started) {
                started = true;

                for (FileButton fileButton : bPanel.getFileButtons()) {

                    if(tagSingleFile(fileButton.getFile())==0) {
                        fileButton.markDone();
                        log.addText(fileButton.getFileName()+ " tagged succesfully!");


                    }else{
                        log.addText("Failed to tag " + fileButton.getFileName() +". Try renaming the file?");

                    }
                    log.updateArea();
                    revalidate();
                    repaint();
                }

                for(int i = 0; i < bPanel.getFileButtons().size(); i++){
                    if(bPanel.getFileButtons().get(i).isDone()){
                        bPanel.removeFileButton(bPanel.getFileButtons().get(i));
                        i-=1;
                    }
                }
                log.addText("Finished Tagging.");
                log.updateArea();
                revalidate();
                repaint();
                started = false;
            }
        }
    };

    // Tags a single File, starting from an API call to VocaDB.
    public int tagSingleFile(File file){
        String fileName = NameParser.parseName(file.getName());
        Items items = JSONHandler.requestJSONVocaDB(fileName, lPanel.getLanguageSetting());
        return MP3Tagger.tagMP3(items, file);
    }

    //Main function
    public Main(){

        try{
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()){
                if("Nimbus".equals(info.getName())){
                    UIManager.setLookAndFeel(info.getClassName());
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        started = false;

        start = new JButton("Start");
        start.addActionListener(startListener);

        lPanel = new LanguagePanel();
        log = new TextLog();
        bPanel = new FilePanel();

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.add(lPanel);
        mainPanel.add(bPanel);
        mainPanel.add(log);

        JPanel startPanel = new JPanel();
        startPanel.add(start, BorderLayout.SOUTH);
        mainPanel.add(startPanel);

        mainPanel.setPreferredSize(new java.awt.Dimension(800, 600));


        URL iconURL = getClass().getResource("/icon.png");
        ImageIcon icon = new ImageIcon(iconURL);
        setIconImage(icon.getImage());

        getContentPane().add(mainPanel);
        setVisible(true);
        setTitle("VocaMeta");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        pack();
    }

    public static void main(String[] args){
        new Main();
    }
}