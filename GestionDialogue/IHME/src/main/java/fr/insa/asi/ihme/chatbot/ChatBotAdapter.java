package fr.insa.asi.ihme.chatbot;

import fr.insa.asi.ihme.chatbot.pubsub.Publisher;
import fr.insa.asi.ihme.chatbot.pubsub.VoiceRecognitionSubscriber;
import fr.insa.asi.ihme.chatbot.pubsub.tools.Response;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class ChatBotAdapter implements KeyListener{
    private VoiceRecognitionSubscriber entree;
    private Publisher publisher;
    private ChatBotInterface chatBotInterface;
    private ChatBot chatBot;
    private Response paroleResponse;
    private static String topicParole = "Parole";
    private static String topicChatBot = "Chatbot";

    public ChatBotAdapter(){
        this.paroleResponse = new Response("");
        this.entree = new VoiceRecognitionSubscriber(topicParole, "tcp://localhost", 8090, this.paroleResponse);
        this.publisher = new Publisher(1, "tcp://*:8090");
        ParoleSubscriberHandler entryResponse = new ParoleSubscriberHandler(this.paroleResponse);
        entryResponse.start();
        entree.start();
        this.chatBot = new ChatBot();
        this.chatBot.readScript("data/mati.script");
        this.chatBotInterface = new ChatBotInterface();
        this.chatBotInterface.input.addKeyListener(this);

    }

    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            chatBotInterface.input.setEditable(false);
            //-----grab quote------
            String quote = chatBotInterface.input.getText();
            chatBotInterface.input.setText("");
            addText("-->You:\t" + quote + "\n");
            publisher.publish(topicParole, quote);
        }
    }

    public void keyReleased(KeyEvent e){
        if(e.getKeyCode()==KeyEvent.VK_ENTER){
            chatBotInterface.input.setEditable(true);
        }
    }

    public void keyTyped(KeyEvent e){}

    public void addText(String str){
        chatBotInterface.dialog.setText(chatBotInterface.dialog.getText() + str);
    }

    private void processResponse(Response r){
        System.out.println("Processing response ...");
        String chatBotResponse = this.chatBot.processInput(r.getResponseString());
        addText("-->MATI:\t" + chatBotResponse);
        addText("\n");
        this.publisher.publish(topicChatBot, chatBotResponse);
    }

    private class ParoleSubscriberHandler extends Thread{
        Response response;
        public ParoleSubscriberHandler(Response r){ this.response = r;}

        @Override
        public void run(){
            while (!Thread.currentThread().isInterrupted()) {
                synchronized (response) {
                    try {
                        response.wait();
                        System.out.println("Handled : " + response.getResponseString());
                        ChatBotAdapter.this.processResponse(response);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }



}

