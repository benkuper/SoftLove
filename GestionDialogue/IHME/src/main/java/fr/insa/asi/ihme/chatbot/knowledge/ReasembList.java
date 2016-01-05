package fr.insa.asi.ihme.chatbot.knowledge;

import java.util.Vector;

/**
 * Created by clement on 07/12/2015.
 */
public class ReasembList extends Vector{
    /**
     *  Add an element to the reassembly list.
     */
    public void add(String reasmb) {
        addElement(reasmb);
    }

    /**
     *  Print the reassembly list.
     */
    public void print(int indent) {
        for (int i = 0; i < size(); i++) {
            for (int j = 0; j < indent; j++) System.out.print(" ");
            String s = (String)elementAt(i);
            System.out.println("reasemb: " + s);
        }
    }
}
