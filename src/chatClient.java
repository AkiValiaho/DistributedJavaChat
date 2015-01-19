import java.io.*;
import java.net.*;
import java.util.*;
/**
 * Yksinkertainen chatclienttiohjelma 
 * multithreadaavalle sokettiserverille.
 * @author Aki V‰liaho
 *
 */
public class chatClient {
	/**
	 * Clienttiohjelman konstruktori, joka
	 * pyˆr‰ytt‰‰ ohjelman k‰yntiin 
	 * @param passedHostName mihin osoitteeseen yhdistet‰‰n
	 * @param passedPortNumber mihin porttiin yhdistet‰‰n
	 * @throws IOException
	 */
	public chatClient(String passedHostName, int passedPortNumber) throws IOException {
		int portNumber = passedPortNumber;
		String hostName = passedHostName;
		Socket clientSideSocket = new Socket(hostName, portNumber);
		ObjectOutputStream output = new ObjectOutputStream(clientSideSocket.getOutputStream());
		ObjectInputStream input = new ObjectInputStream(clientSideSocket.getInputStream());
		UUID userID = UUID.randomUUID();
		chatClientReaderThread readerThread = new chatClientReaderThread(input,userID);
		readerThread.start();
		//Alustan skannerin jo t‰ss‰ ylim‰‰r‰isen threadin sijaan, koska scanneri j‰tt‰‰
		//clientin p‰‰lle jos serveri p‰‰tt‰‰ kaatua. Mielest‰ni reilumpaa, ett‰ asiakas ehtii n‰hd‰ viestit, jotka ehdittiin l‰hett‰‰
		//ennen serverin kaatumista.
		Scanner stringScanner = new Scanner(System.in);
		while (readerThread.isAlive()) {
			String string;
			if (stringScanner.hasNextLine()) {
				Viesti viestiOlio;
				string  = stringScanner.nextLine();
				if (string.equals("")) {
					continue;
				}
				if (string.equals("LOPETA")) {
					viestiOlio = new Viesti(string, InetAddress.getLocalHost().getHostAddress(),userID);
					//Tallennetaan disconnect-k‰sky olioon
					viestiOlio.setDisconnect(true);
					//Kirjoitetaan disconnect-k‰skyn sis‰lt‰v‰ olio sokettiin
					output.writeObject(viestiOlio);
					try {
						//Ja suljetaan ylim‰‰r‰iset jutut pois
						readerThread.join();
						clientSideSocket.close();
						input.close();
						output.close();
						System.exit(-1);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				viestiOlio = new Viesti(string, InetAddress.getLocalHost().getHostAddress(),userID);
				output.writeObject(viestiOlio);
			}
		}
	}
	public static void main(String[] args) throws UnknownHostException, IOException, ClassNotFoundException {
		String hostName;
		int portNumber;
		if (args.length !=2 ) {
			//K‰ytet‰‰n teht‰v‰ss‰ annettua default-porttia ja localhostia
			hostName = "localhost";
			portNumber = 12345;
		} else {
			//Muuten menn‰‰n argumenteilla
			hostName = args[0];
			portNumber = Integer.parseInt(args[1]);
		}
		//K‰ynnistet‰‰n konstruktorilla serveri
		chatClient client = new chatClient(hostName,portNumber);
	}
}