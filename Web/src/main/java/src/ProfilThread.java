package src;

public class ProfilThread extends Thread {

	ProfilThread() {	}

	public void run() {
		while(true) {
			MainWeb.profil = ZMQConnector.receiveSocialNetwork();
		}
	}
	
}