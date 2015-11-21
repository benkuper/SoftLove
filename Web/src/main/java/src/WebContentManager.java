package src;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Scanner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import org.json.simple.JSONObject;

public class WebContentManager {
	public static String getPage(String adresse) throws IOException {
		final URL url = new URL(adresse);
		final URLConnection connection = url.openConnection();
		connection.setConnectTimeout(60000);
		connection.setReadTimeout(60000);
		connection.addRequestProperty("User-Agent", "Mozilla/5.0");
		StringBuilder page = new StringBuilder();
		try {
			final Scanner reader = new Scanner(connection.getInputStream(), "UTF-8");
		while(reader.hasNextLine()){
			final String line = reader.nextLine();
			page.append(line);
		}
		reader.close();
		} catch(Exception e ){

		}
		return page.toString();
	}
	
	public static String getTextPage(String adresse) throws IOException {
		Document doc = Jsoup.parse(WebContentManager.getPage(adresse));
		
		return doc.body().text();
	}

	public static String URLsanitize(String s) {

		if(s.startsWith("http")) {
			return s;
		}

		return "http:" + s;
	}

	public static ArrayList<String> googleRequest(String motCle, int nbResult) throws IOException {
		ArrayList<String> urls = new ArrayList<String>();

	       try{

	    	  // while(urls.size() < nbResult) {
	    		   String adresse= "https://www.google.fr/search?q="+motCle+"&num="+nbResult;
	    		   Document doc = Jsoup.parse(WebContentManager.getPage(adresse));
	    		   System.out.println(doc);
	    		   Elements test = doc.select("h3.r > a");
	    		   String href;
	    		   for(Element e : test) {

	    			   if((href = e.attr("href").toString()).startsWith("/url?q=")) {
							String url = href.substring(href.indexOf("http"), href.indexOf("&"));
							urls.add(URLDecoder.decode(url));
						}

					}
	    	  // }
	       }
		   catch (IOException exception)
		   {
			   System.out.println ("Erreur lors de la lecture : " + exception.getMessage());
		   }
		return urls;
	}
	
	public static ArrayList<String> googleAPIRequest(String motCle, int nbResult) throws IOException {
		ArrayList<String> urls = new ArrayList<String>();
		
		motCle=motCle.replace(' ', '+');
		System.out.println("debug " + motCle);
		
		String searchEngineLanguage = "fr";
		String documentLanguage = "lang_fr";
		HttpURLConnection conn = null;
		while(urls.size() < nbResult) {
		    URL url = new URL("https://www.googleapis.com/customsearch/v1?key=AIzaSyBzgCg7v-tfVZ4-iEx7ZjDALsnKr06wGzU&cx=013036536707430787589:_pqjad5hr1a&q="+motCle+"&alt=json&hl="+searchEngineLanguage+"&lr="+documentLanguage+"&start="+(urls.size()+1));
	        conn = (HttpURLConnection) url.openConnection();
	        conn.setRequestMethod("GET");
	        conn.setRequestProperty("Accept", "application/json");
	        BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
	
	        String output;
	        while ((output = br.readLine()) != null) {
	
	            if(output.contains("\"link\": \"")){                
	                String link=output.substring(output.indexOf("\"link\": \"")+("\"link\": \"").length(), output.indexOf("\","));
	                urls.add(link);
	            }     
	        }
		}
        conn.disconnect(); 
        System.out.println(urls.toString());
		return urls;
	}
	
	public static ArrayList<String> amazonRequest(String req) throws IOException {
		ArrayList<String> urls = new ArrayList<String>();
		//req.replace(' ', '+');

	       try{
	    		   String adresse= "http://www.amazon.fr/s/field-keywords="+req;
	    		   System.out.println("amazon request : " +adresse);
	    		   Document doc = Jsoup.parse(WebContentManager.getPage(adresse));
	    		   //System.out.println(doc);
	    		   
	    		   
	    		   Elements elems = doc.select(".s-access-detail-page");
	    		   String href;
	    		   for(Element e : elems) {

	    			   href = e.attr("href").toString();
	    			   urls.add(URLDecoder.decode(href));
						
					}
	       }
		   catch (IOException exception)
		   {
			   System.out.println ("Erreur lors de la lecture : " + exception.getMessage());
		   }
		return urls;
	}
	
	public static ArrayList<String> duckduckRequest(String motCle, int nbResult) throws IOException {
		ArrayList<String> urls = new ArrayList<String>();

	       try{

	    	   while(urls.size() < nbResult) {
	    		   String adresse= "https://duckduckgo.com/?q="+motCle;
	    		   Document doc = Jsoup.parse(WebContentManager.getPage(adresse));
	    		   Elements test = doc.select("h2 > .result__a");
	    		   String href;
	    		   for(Element e : test) {

	    			   if((href = e.attr("href").toString()).startsWith("/url?q=")) {
							String url = href.substring(href.indexOf("http"), href.indexOf("&"));
							urls.add(URLDecoder.decode(url));
						}

					}
	    	   }
	       }
		   catch (IOException exception)
		   {
			   System.out.println ("Erreur lors de la lecture : " + exception.getMessage());
		   }
		return urls;
	}

	public static void extractProduct(String recherche, String page) throws FileNotFoundException, UnsupportedEncodingException {
		String image = "";
		String titre = "";
		String description = "";
		String prix = "";
		
		Document doc = Jsoup.parse(page);
		Elements prixs = doc.select("#priceblock_ourprice");
		Elements titres = doc.select("#productTitle");
		Elements descriptions = doc.select("#feature-bullets");
		Elements images = doc.select("#landingImage");
		
		if(!images.isEmpty()) {image = images.get(0).attr("src");}
		if(!prixs.isEmpty()) {prix = prixs.get(0).text();}
		if(!descriptions.isEmpty()) {description = descriptions.get(0).text();}
		if(!titres.isEmpty()) {titre = titres.get(0).text();}
		
		if(!titre.equals("") && !prix.equals("")) {
		
			JSONObject obj=new JSONObject();
			  obj.put("prix",prix);
			  obj.put("titre",titre);
			  obj.put("image",image);
			  obj.put("description",description);
			  obj.put("recherche",recherche);
			
			  System.out.println(obj.toJSONString());
			ZMQConnector.sendAmazon(obj.toJSONString());
		}
	}
}