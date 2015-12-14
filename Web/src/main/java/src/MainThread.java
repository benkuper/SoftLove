package src;

import java.io.IOException;
import java.util.ArrayList;

public class MainThread extends Thread {

	private String recherche;
	private String profil;

	MainThread(String recherche, String profil) {
		this.recherche = recherche;
		this.profil = profil;
	}

	public void run() {
		ArrayList<String> res;
		try {
			/* RECHERCHE WEB */
			res = WebContentManager.googleAPIRequest(recherche, 2);
			
			/**res = new ArrayList<String>();
			res.add("https://fr.wikipedia.org/wiki/Cheval");
			res.add("https://www.youtube.com/watch?v=9pMGG0CgAD0");
			res.add("http://jeu.info/chevaux.htm");
			res.add("http://www.cheval-shop.com/");
			res.add("http://www.cheval-francais.eu/");
			res.add("http://www.cheval-francais.eu/fr/les-courses-par-date.html");
			res.add("https://fr.vikidia.org/wiki/Cheval");
			res.add("http://www.salon-cheval.com/");**/
			    	
			ArrayList<String> synonymes = new ArrayList<String>();
		
			synonymes = KeywordCounter.getSynonymes(recherche+" "+profil);
		    System.out.println("synonymes : "+synonymes);
		    	
		    ArrayList<ArrayList<String>> docs = new ArrayList<ArrayList<String>>();
		    ArrayList<String> doc;

		    for(String r : res) {
				doc = KeywordCounter.extractKeywords(WebContentManager.getTextPage(r));
			    docs.add(doc);
		    }

		    TFIDFCalculator calculator = new TFIDFCalculator();
		    
		    /*TF-IDF WEB & IMAGES*/
		    double tfidf;
		    double valeurMax = -1;
		    String wikiImg = new String();
		    String tfidfImg = new String();
		    for(ArrayList<String> cur : docs) {
		    	tfidf = 0.0;
		    		
		    	for(String terme : synonymes) {
		    		tfidf += calculator.tfIdf(cur, docs, terme);
		    	}
		    	
		    	if(res.get(docs.indexOf(cur)).contains("wikipedia.org/wiki")) {
		    		wikiImg = res.get(docs.indexOf(cur));
		    	}
			    if(tfidf > valeurMax) {
			    	valeurMax = tfidf;
			    	tfidfImg = res.get(docs.indexOf(cur));
		    	}

				System.out.println("TF-IDF ( " + recherche + " ) pour " + res.get(docs.indexOf(cur)) + " = " + tfidf);
		    }
		    ArrayList<String> imgDownload = new ArrayList<String>();
		    if(!wikiImg.isEmpty())
		    	imgDownload.add(wikiImg);
		    imgDownload.add(tfidfImg);
		    
		    // envoi des keywords
		    if(!tfidfImg.isEmpty()) {
		    	KeywordCounter.extractAnswer(recherche,KeywordCounter.extractKeywords(WebContentManager.getTextPage(tfidfImg)));
		    }
		    
		    System.out.println("sites retenu pour extraction d'images : "+imgDownload);
			ImageManager.imageDownloader(imgDownload, recherche);
			
			/* RECHERCHE AMAZON */
			ArrayList<String> products = new ArrayList<String>();
			res = WebContentManager.amazonRequest(recherche);
			
			if(products.size()>3) {
				products.subList(0, 3);
			}
			
			for(String s:res) {
				products.add(WebContentManager.getPage(s));
			}
			int count = 0;
			for(String page : products) {
				WebContentManager.extractProduct(recherche, page, res.get(count));
				count++;
			}
			
			System.out.println("end of request");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}