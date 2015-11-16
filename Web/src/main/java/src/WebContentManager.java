package src;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Scanner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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

	    	   while(urls.size() < nbResult) {
	    		   String adresse= "https://www.google.fr/search?q="+motCle+"&start="+urls.size();
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
	    	   }
	       }
		   catch (IOException exception)
		   {
			   System.out.println ("Erreur lors de la lecture : " + exception.getMessage());
		   }
		return urls;
	}
	
	public static ArrayList<String> amazonRequest(String motCle, int nbResult) throws IOException {
		ArrayList<String> urls = new ArrayList<String>();

	       try{

	    	   while(urls.size() < nbResult) {
	    		   String adresse= "http://www.amazon.fr/s/field-keywords="+motCle;
	    		   Document doc = Jsoup.parse(WebContentManager.getPage(adresse));
	    		   System.out.println(doc);
	    		   /**Elements test = doc.select("h3.r > a");
	    		   String href;
	    		   for(Element e : test) {

	    			   if((href = e.attr("href").toString()).startsWith("/url?q=")) {
							String url = href.substring(href.indexOf("http"), href.indexOf("&"));
							urls.add(URLDecoder.decode(url));
						}

					}**/
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
	    		   System.out.println(doc);
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
}