package gui;

import tools.MP3Tagger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

/**
 * Created by KMFK on 2017-01-23.
 */
public class ButtonPanel extends JPanel {

    private final JFileChooser fChooser = new JFileChooser();
    private JPanel mainPanel;

    private ArrayList<File> fileListing;
    private ArrayList<FileButton> buttonListing;

    private JButton fCButton;

    ActionListener fileChooseListener = new ActionListener(){
        @Override
        public void actionPerformed(ActionEvent e){
            int returnVal = fChooser.showOpenDialog(ButtonPanel.this);

            if(returnVal == JFileChooser.APPROVE_OPTION){
                File file = fChooser.getSelectedFile();
                System.out.println(file.getName());
                File[] temp = {file};
                processFile(temp);
            }
        }
    };

    //ActionListener made specifically for fileButtons
    ActionListener fileButtonListener = new ActionListener(){
        @Override
        public void actionPerformed(ActionEvent e) {
            removeFileButton((FileButton) e.getSource());
            revalidate();
            repaint();
        }
    };

    public void processFile(File[] folder){
        for(File f : folder){
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

    public ButtonPanel(){
        setLayout(new BorderLayout());

        JLabel dragInfo = new JLabel("Drag and Drop your files/folders below. Click to remove them.");
        dragInfo.setHorizontalTextPosition(SwingConstants.CENTER);
        add(dragInfo, BorderLayout.NORTH);

        mainPanel = new JPanel();


        fCButton = new JButton("Open File/Folder");
        fCButton.addActionListener(fileChooseListener);
        add(fCButton,BorderLayout.SOUTH);

        fChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

        fileListing = new ArrayList<>();
        buttonListing = new ArrayList<>();

        //Filedrop Object to let area support dragging and dropping files.
        new FileDrop(mainPanel, new FileDrop.Listener()
        {public void filesDropped(File[] files){
                processFile(files);
            }
        });
        //setMinimumSize(new Dimension(800,300));
        //setPreferredSize(new Dimension(800,300));

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        //scrollPane.setPreferredSize(new Dimension(800,500));
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);

        mainPanel.setOpaque(true);
        mainPanel.setBackground(Color.white);
        setBorder(BorderFactory.createLoweredBevelBorder());
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));

        add(scrollPane, BorderLayout.CENTER);
    }

    private void addFileButton(FileButton fb){
        mainPanel.add(fb);
        fileListing.add(fb.getFile());
        buttonListing.add(fb);
    }

    public void removeFileButton(FileButton fb){
        mainPanel.remove(fb);
        fileListing.remove(fb.getFile());
        buttonListing.remove(fb);
    }

    public ArrayList<File> getFiles(){
        return fileListing;
    }

    public ArrayList<FileButton> getFileButtons(){ return buttonListing; }
}