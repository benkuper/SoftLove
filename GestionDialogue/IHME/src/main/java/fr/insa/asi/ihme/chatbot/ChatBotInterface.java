package fr.insa.asi.ihme.chatbot;

import javax.swing.*;
import java.awt.*;


public class ChatBotInterface extends JFrame{
    JPanel p=new JPanel();
    JTextArea dialog=new JTextArea(20,50);
    JTextArea input=new JTextArea(1,50);
    JScrollPane scroll=new JScrollPane(dialog, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

    public ChatBotInterface(){
        super("Chat Bot");
        setSize(600,400);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        dialog.setEditable(false);

        p.add(scroll);
        p.add(input);
        p.setBackground(new Color(0,22,33));
        add(p);

        setVisible(true);
    }


}
