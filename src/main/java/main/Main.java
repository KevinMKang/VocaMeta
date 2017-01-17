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

//Use GSON for jsons
//JAudioTagger for music

public class Main extends JFrame{

    private JPanel area = new JPanel();
    private ArrayList<FileButton> allFiles;
    private JButton start;
    private boolean started;

    ActionListener fileButtonListener = new ActionListener(){
        @Override
        public void actionPerformed(ActionEvent e) {
            area.remove((FileButton) e.getSource());
            allFiles.remove((FileButton) e.getSource());
            revalidate();
            repaint();
        }
    };



    ActionListener startListener = new ActionListener(){
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!started) {
                started = true;
                start.setText("Working...");
                for (FileButton files : allFiles) {
                    File current = files.getFile();
                    if (!current.isDirectory()) {
                        tagSingleFile(current);

                    } else {
                        File[] directoryContents = MP3Tagger.parseFolder(current.getPath());
                        for (File file : directoryContents) {
                            tagSingleFile(file);
                        }
                    }
                    area.remove(files);

                }
                allFiles.clear();
                revalidate();
                repaint();

                start.setText("Start");
                started = false;
            }
        }
    };


    public void tagSingleFile(File file){
        String fileName = NameParser.parseName(file.getName());
        System.out.println(fileName);
        String json = JSONHandler.requestJSONVocaDB(fileName);
        System.out.println(json);
        Items items = JSONHandler.parseJSONVocaDB(json);
        MP3Tagger.tagMP3(items, file);
    }


    public Main(){
        started = false;

        start = new JButton("Start");
        start.addActionListener(startListener);

        allFiles = new ArrayList<>();
        area.setOpaque(true);
        area.setBackground(Color.white);
        area.setBorder(BorderFactory.createLoweredBevelBorder());
        //area.setLayout(new BoxLayout(area, BoxLayout.Y_AXIS));

        new FileDrop(area, new FileDrop.Listener()
        {public void filesDropped(File[] files){
                for(File f : files){

                    if(!allFiles.contains(f)){
                        FileButton fb = new FileButton(f);
                        fb.addActionListener(fileButtonListener);
                        allFiles.add(fb);
                        area.add(fb);
                        revalidate();
                        repaint();
                    }else{
                        System.out.println(f.getPath());
                    }
                }
            }
        });

        JPanel myPanel = new JPanel();
        JLabel dragInfo = new JLabel("Drag and Drop your files/folders below. Click to remove them.");
        dragInfo.setHorizontalTextPosition(SwingConstants.CENTER);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
        panel.add(dragInfo, BorderLayout.CENTER);
        panel.add(area);
        panel.add(start);
        panel.setPreferredSize(new java.awt.Dimension(800,600));
        getContentPane().add(panel);
        setVisible(true);
        pack();
    }

    public static void main(String[] args){
        /*
        File tempDir = new File("temp");
        tempDir.mkdir();
        System.out.println("starting");
        File[] files = MP3Tagger.parseFolder("testfiles/songs/");
        for(File file : files){
            String fileName = NameParser.parseName(file.getName());
            System.out.println(fileName);
            String json = JSONHandler.requestJSONVocaDB(fileName);
            System.out.println(json);
            Items items = JSONHandler.parseJSONVocaDB(json);
            MP3Tagger.tagMP3(items, file);
        }

        try {
            FileUtils.deleteDirectory(tempDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        */

        new Main();
    }
}