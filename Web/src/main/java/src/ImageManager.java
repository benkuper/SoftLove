package src;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import org.json.simple.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ImageManager {

	public static void imageDownloader(ArrayList<String> res, String recherche) throws IOException {

		for(String s : res) {
			Document doc = Jsoup.parse(WebContentManager.getPage(s));
			Elements imgs = doc.select("img");

			try
			{
				for(Element e : imgs) {
					URL url = new URL(WebContentManager.URLsanitize(e.attr("src")));

					if(url.toString().startsWith("http://") || url.toString().startsWith("https://")) {
						if(url.toString().startsWith("http://upload.wikimedia.org/wikipedia/")) {
							url = new URL(url.toString().substring(0, url.toString().indexOf("://upload"))+"s"+url.toString().substring(url.toString().indexOf("://upload"), url.toString().indexOf("/thumb"))+url.toString().substring(url.toString().indexOf("/thumb")+6, url.toString().lastIndexOf("/")));
						}
						
						JSONObject obj=new JSONObject();
						  obj.put("recherche", recherche);
						  obj.put("urlimage",url.toString());
						  
						ZMQConnector.sendImage(obj.toJSONString());

					}
				}
			}
			catch (IOException exception)
			{
			    System.out.println ("Erreur lors de la lecture : " + exception.getMessage());
			}
		}
	}
}