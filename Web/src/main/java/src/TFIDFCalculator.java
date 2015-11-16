package src;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TFIDFCalculator {

    public static double termFrequency(ArrayList<String> doc, String term) {
        double result = 0;
        for (String word : doc) {
            if (term.equalsIgnoreCase(word))
                result++;
        }
        
        if(doc.size() == 0) return 0;
        return result / doc.size();
    }

    public static double inverseTermFrequency(ArrayList<ArrayList<String>> docs, String term) {
        double n = 0;
        for (List<String> doc : docs) {
            for (String word : doc) {
                if (term.equalsIgnoreCase(word)) {
                    n++;
                    break;
                }
            }
        }
        if(n==0) return 0;
        return Math.log(docs.size() / n);
    }


    public  double tfIdf(ArrayList<String> doc, ArrayList<ArrayList<String>> docs, String term) {
        return termFrequency(doc, term) * inverseTermFrequency(docs, term);

    }

    public static void main(String[] args) throws IOException {

        /*List<String> doc1 = Arrays.asList("Lorem", "ipsum", "dolor", "ipsum", "sit", "ipsum");
        List<String> doc2 = Arrays.asList("Vituperata", "incorrupte", "at", "ipsum", "pro", "quo");
        List<String> doc3 = Arrays.asList("Has", "persius", "disputationi", "id", "simul");
        List<List<String>> documents = Arrays.asList(doc1, doc2, doc3);*/

    	String recherche = "cheval";

		new ZMQConnector();
    	
    	//ArrayList<String> res = WebContentManager.googleRequest(recherche, 2);
    	ArrayList<String> res = new ArrayList<String>();
    	res.add("https://fr.wikipedia.org/wiki/Cheval");
    	res.add("https://www.youtube.com/watch?v=9pMGG0CgAD0");
    	res.add("http://jeu.info/chevaux.htm");
    	res.add("http://www.cheval-shop.com/");
    	res.add("http://www.cheval-francais.eu/");
    	res.add("http://www.cheval-francais.eu/fr/les-courses-par-date.html");
    	res.add("https://fr.vikidia.org/wiki/Cheval");
    	res.add("http://www.salon-cheval.com/");
    	System.out.println(res);
    	
    	ArrayList<String> synonymes = KeywordCounter.getSynonymes(recherche);
    	System.out.println("synonymes : "+synonymes);
    	
    	ArrayList<ArrayList<String>> docs = new ArrayList<ArrayList<String>>();
    	ArrayList<String> doc;

    	for(String r : res) {
    		doc = KeywordCounter.extractKeywords(WebContentManager.getTextPage(r));
    		docs.add(doc);
    	}

        TFIDFCalculator calculator = new TFIDFCalculator();
        double tfidf;

    	for(ArrayList<String> cur : docs) {
    		tfidf = 0.0;
    		
    		for(String terme : synonymes) {
    			tfidf += calculator.tfIdf(cur, docs, terme);
    		}

			System.out.println("TF-IDF ( " + recherche + " ) pour " + res.get(docs.indexOf(cur)) + " = " + tfidf);
    	}
    }


}