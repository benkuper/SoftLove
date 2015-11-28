package src;

import java.io.IOException;
import java.util.Scanner;

import org.json.simple.parser.ParseException;

public class MainWeb {

	public static String profil = "animal";
	public static String googleKey;
	
	public static void main(String[] args) throws IOException, ParseException {
		
		String recherche ="cheval blanc";
		new ZMQConnector();
		
		Scanner sc = new Scanner(System.in);
		System.out.println("Veuillez saisir la clé de google API :");
		googleKey = sc.nextLine();
		
		System.out.println("lancement thread profil");
		ProfilThread thp = new ProfilThread();
		thp.start();
		
		while(true) {

			System.out.println("begin loop");
			
			recherche = ZMQConnector.receiveKeywords();
			System.out.println("recherche : " + recherche+"\t"+"profil : "+profil);
		
			MainThread t = new MainThread(recherche, profil);
			t.start();
		}
	}
}