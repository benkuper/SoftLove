package src;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import org.json.simple.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class KeywordCounter {
	
	public static ArrayList<String> getSynonymes(String requete) throws IOException {
		ArrayList<String> mots = new ArrayList<String>();
		String[] motCles = requete.split(" ");
		try {
				for(String motCle : motCles)
				{
					if(motCle.length() > 3) {
						String adresse = "http://www.synonymo.fr/synonyme/"+motCle;
						Document doc = Jsoup.parse(WebContentManager.getPage(adresse));
						Elements liens = doc.select(".fiche .synos a");
						String liensTexte = liens.text();
						String[] t = liensTexte.split(" ");
						mots.add(motCle.replace("+", " "));
						for (String mot : t) {
							if(!mots.contains(mot) && !mot.equals("")) {
								mots.add(mot);
							}
						}
					}
				}
		} catch (IOException exception) {
			System.out.println("Erreur lors de la lecture : " + exception.getMessage());
		}
		return mots;
	}

	public static ArrayList<String> extractKeywords(String pageContent) {
		pageContent = processWord(pageContent);
		
		ArrayList<String> res = new ArrayList<String>();
		String[] termes = pageContent.split(" ");

		for(String terme : termes) {
			if(terme.length() > 3 && terme.length() < 27) {
				res.add(terme);
			}
		}
		
		return res;
	}
	
	private static String processWord(String x) {
	    String tmp;

	    tmp = x.toLowerCase();
	    tmp = tmp.replace(",", "");
	    tmp = tmp.replace(".", "");
	    tmp = tmp.replace(";", "");
	    tmp = tmp.replace("!", "");
	    tmp = tmp.replace("?", "");
	    tmp = tmp.replace("(", "");
	    tmp = tmp.replace(")", "");
	    tmp = tmp.replace("{", "");
	    tmp = tmp.replace("}", "");
	    tmp = tmp.replace("[", "");
	    tmp = tmp.replace("]", "");
	    tmp = tmp.replace("<", "");
	    tmp = tmp.replace(">", "");
	    tmp = tmp.replace("%", "");

	    return tmp;
	}
	
	public static void extractAnswer(String recherche, ArrayList<String> pageContent) {
		    // O(n)
		    Map<String, Integer> map = new HashMap<String, Integer>();
		    for (String str : pageContent) {
		        map.put(str, map.containsKey(str)? map.get(str)+1 : 1);
		    }

		    // O(n)
		    TreeMap<Integer, TreeSet<String>> treemap = new TreeMap<Integer, TreeSet<String>>();
		    for (String key : map.keySet()) {
		        if (treemap.containsKey(map.get(key))) {
		            treemap.get(map.get(key)).add(key);
		        }
		        else {
		            TreeSet<String> set = new TreeSet<String>();
		            set.add(key);
		            treemap.put(map.get(key), set);
		        }
		    }

		    int cpt = 0;

    		JSONObject obj=new JSONObject();
    		
		    // O(n)
		    Map<Integer, TreeSet<String>> result = treemap.descendingMap();
		    for (int count : result.keySet()) {
		        TreeSet<String> set = result.get(count);
		        for (String word : set) {
		        	if(cpt< 5) {
		        		cpt++;
		        		obj=new JSONObject();
		        		obj.put("terme",word);
		  			  	obj.put("poids",count);
		  			  	obj.put("recherche",recherche);

		  				ZMQConnector.sendKeywords(obj.toJSONString());
		            System.out.println(obj.toJSONString());
		        	}
		        }
		}   
	}
}