package fr.insa.asi.ihme.chatbot.knowledge;

public class KnowledgeBase {
    String prevResponse;

    String[][] language={
            //standard greetings
            {"salut","yop","bonjour","comment va","hey"},
            {"bonjour, comment ça va ?","salut, comment ça va ?"},
            //question greetings
            {"comment ca va","comment vas-tu","bien","bien et toi"},
            {"Bien.","Ca va.", "Tres bien, merci."},
            //yes
            {"oui", "si"},
            {"non.","NON !","Non merci."},
            //non
            {"non"},
            {"Et pourtant !", "Si.", "J'y peux rien !"},
            {"test"},
            {"cheval"},
            //default
            {"Je n'ai pas compris, peux-tu reformuler ?"}
    };

    public String[][] getLanguage(){ return this.language; }

    public KnowledgeBase(){}

    public String findMatch(String in){
        String res = "";
        int r = 0; // research index
        byte response = 0;
			/*
			0:we're searching through chatBot[][] for matches
			1:we didn't find anything
			2:we did find something
			*/
        //-----check for matches----
        int j = 0;//which group we're checking
        while (response == 0) {
            if (inArray(in.toLowerCase(), this.getLanguage()[j * 2])) {
                response = 2;
                do{
                    r = (int) Math.floor(Math.random() * this.getLanguage()[(j * 2) + 1].length);
                    res = this.getLanguage()[(j * 2) + 1][r];
                }while(res == prevResponse);
            }
            j++;
            if (((j * 2) == (this.getLanguage().length - 1)) && (response == 0)) {
                response = 1;
            }
        }

        //-----default--------------
        if (response == 1) {
            r = (int) Math.floor(Math.random() * this.getLanguage()[this.getLanguage().length - 1].length);
            res = this.getLanguage()[this.getLanguage().length - 1][r];
        }
        prevResponse = res;
        return res;
    }


    public boolean inArray(String in,String[] str){
        boolean match=false;
        for(int i=0;i<str.length;i++){
            if(str[i].equals(in)){
                match=true;
            }
        }
        return match;
    }
}
