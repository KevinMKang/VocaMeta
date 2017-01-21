package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Kevin on 1/20/2017.
 */
public class LanguagePanel extends JPanel {

    String[] lang = {"English","Japanese","Romaji"};
    private int currentOption = 0;


    public LanguagePanel(){
        JLabel desc = new JLabel("Select a language setting:");
        add(desc);

        JRadioButton eng = new JRadioButton("English");
        eng.addActionListener(listener);
        eng.setSelected(true);

        JRadioButton jp = new JRadioButton("Japanese");
        jp.addActionListener(listener);

        JRadioButton romaji = new JRadioButton("Romaji");
        romaji.addActionListener(listener);

        ButtonGroup group = new ButtonGroup();
        group.add(eng);
        group.add(jp);
        group.add(romaji);

        add(eng);
        add(jp);
        add(romaji);

        setMaximumSize(new Dimension(600,200));

    }

    ActionListener listener = new ActionListener(){
        @Override
        public void actionPerformed(ActionEvent e) {
            JRadioButton b = (JRadioButton)e.getSource();
            if(b.getText().equals("English")){
                currentOption = 0;
            }
            if(b.getText().equals("Japanese")){
                currentOption = 1;
            }
            if(b.getText().equals("Romaji")){
                currentOption = 2;
            }
        }
    };

    public String getLanguageSetting(){
        return lang[currentOption];
    }
}
