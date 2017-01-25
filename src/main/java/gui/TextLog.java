package gui;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by KMFK on 2017-01-23.
 */
public class TextLog extends JPanel{

    private JTextArea log;
    private JButton clear;


    public TextLog(){
        setLayout(new BoxLayout(this,BoxLayout.LINE_AXIS));

        log = new JTextArea(0,1);
        log.setAutoscrolls(true);
        log.setText("VocaMeta started.\n");
        log.setEditable(false);
        //log.setBackground(Color.white);
        //log.setOpaque(true);

        clear = new JButton("Clear Log");
        clear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.setText("Log cleared.\n");
            }
        });



        JScrollPane scroll = new JScrollPane(log);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setAutoscrolls(true);
        add(scroll);
        add(clear);


        setBackground(Color.white);
        setOpaque(true);
        setBorder(BorderFactory.createLoweredBevelBorder());

        setVisible(true);

        //setMaximumSize(new Dimension(800, 200));
        scroll.setMinimumSize(new Dimension(0, 150));
        scroll.setPreferredSize(new Dimension(600, 150));
        DefaultCaret caret = (DefaultCaret)log.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
    }

    public void addText(String text){
        log.append(text);
        revalidate();
        repaint();
    }

    public void updateArea(){
        log.update(log.getGraphics());
    }
}
