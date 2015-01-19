import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
/**
 * Yksinkertainen multithreadaava sokettiserveri.
 * 
 * Pohdiskelu: Päätin, että kirjoittaisin soketteihin sarjallistettavia
 * objekteja sillä Javan versio tästä taipuu olio-ohjelmoinnin hengessä melkeimpä mihin vain. Sarjallistaminen
 * on Javassa JVM-riippumaton prosessi eli käytännössä objekti voidaan sarjallistaa yhdessä
 * ympäristössä ja purkaa kokonaan toisessa. Päädyin kirjoittamaan sarjallistettavan Viesti-luokan johon kapseloin
 * clientin IP-osoitteen, satunnaisgeneroidun UUID:n sekä viestin sisällön. Kehitin luokkaan
 * primitiivisen salauksen sen tärkeille fieldeille, jota käytännössä olisi helppo lähteä laajentamaan turvallisempaan suuntaan
 * esimerkiksi jollakin julkisen avaimen salakirjoitusmenetelmällä.
 * Serveripuolella päädyin pyörittämään mainin lisäksi kahta muuta threadia:
 * Jokaiselle Clientille omaa chatServerThread-threadia, jossa valvoin kyseiselle threadille tulevaa sokettia sekä lähetin
 * serverin pääluokasta staattisesti referoidun CopyOnWriteArrayListin threadeille soketille tulleen viestin sekä ylläpitäjän
 * viestinlähetys - ja lopetustoimintaan liittyvän toisen threadin.
 * Clienttipuolella kirjoitin yksinkertaisen ohjelman, joka tarkkaili tuliko LOPETA-syöte käyttäjältä ja ajoi alas serverillä
 * majailevan threadin Viesti-objektiin liittyvän disconnect-totuusarvon avulla. Jos syöte sisälsi muuta tekstiä kirjoitettiin se sokettiin
 * serverithreadin lähetettäväksi eteenpäin.
 * 
 * Tässä tehtävässä käytin runsaasti java.Net.InetAddress-kirjaston metodeja. Niistä erityisesti getLocalHost() voi aiheuttaa ongelmia tämän
 * ohjelman tietojen oikeellisuudessa jos clientti elää kompleksissa monia erilaisia verkkoadaptereja pyörittävässä ympäristössä. Sokettiohjelmointi
 * aiheuttaa myös ongelmia jos ylläpitäjän palvelin sijaitsee osoitteenmuunnoksen (NAT) takana. Tällöin reititintä joudutaan conffaamaan, että julkiseen
 * IP-osoitteeseen osuvat paketit siirtyvät sisäverkon oikealle tietokoneelle. 
 * 
 * Kaikenkaikkiaan ratkaisu toimii oikein mallikkaasti vaikka objektien sijaan olisi ehkä hieman järkevämpää kaistan säästämisen takia lähetellä
 * esimerkiksi pelkkiä stringejä bitteinä. Toisaalta korkealla asteella toimittaessa objektit mahdollistavat yhä moniulotteisempien ohjelmien kirjoittamisen
 * vähemmällä vaivalla. Objektien kirjoittaminen soketteihin onnistuu kätevästi myös androidille (RMI ei ilmeisesti onnistu, koska jostain syystä java.rmi-libraryä ei ole importattu mukaan API:in)
 * 
 * Itsearviointi: 
 * Hajautettujen ohjelmien koodaaminen on hyvin mukavaa silloin kun tuntee, että debuggeri tottelee itseä. Tällä kertaa olinkin ns. tulessa
 * ja suurinosa ongelmista ratkaistiin aika nopeasti, vaikka muutama turhauttava exceptioni hyökkäsikin sokettien kautta päälle. Hyödyllinen
 * taito mitä olen oppinut käyttämään vasta viimeaikoina monimutkaisten projektien tekemisessä on dokumentaation lukeminen. Virhetilanne
 * on helpompi korjata jos tietää mistä se dokumentaation mukaan syntyy. Välillä näistä syntyy villejäkin matkoja kohti rautatasoa.
 * Koodia oppii kirjoittamaan parhaiten, kun oppii ymmärtämään sen kausaalisen luonteen; Mitään ei synny tyhjästä ja kaikella on aina syynsä
 * Metaforallisesti tämä lienee lähellä nappikuulokkeiden solmujen avaamista.
 * Sarjallistamisesta oli mukava oppia lisää. Uskonkin, että sain tästä tehtävästä ainakin
 * uusia ideoita omiin projekteihini kehiteltäväksi. Työkalupakin kasvaessa ne ideat mitkä ovat olleet hautumassa kuulostavat entistä realistisemmilta
 * ja sitä kautta eivät olekaan enää pelkkiä aloittamista vaille valmiita olevia ikuisuusprojekteja.
 * @author Aki Väliaho
 *
 */
public class chatServer{
	private Integer portNumber;
	//Käytetään tässä CopyOnWriteArrayListiä, joka on tehokas thread-safe implementaatio
	//silloin, kun suurinosa threadin suorittamista operaatioista on listan iterointia.
	//Tässä tapauksessa chat-ohjelma esimerkiksi viettää suurimman osan ajastaan maalaisjärjen mukaan
	//tutkiskellen onko tullut uusia viestejä eli iteroiden Clienttejä läpi ja ympäten uusia viestejä
	//soketteihin.
	public static CopyOnWriteArrayList<chatServerThread> arrayOfClients = new CopyOnWriteArrayList<>();
	public static ServerSocket serverSocket;
	public chatServer(Integer portNumber) {
		this.portNumber = portNumber;
	}
	/**
	 * Toimenpiteet säikeen avaamista varten yksittäiselle clientille
	 * Lisätään staattiseen listaan uusi muodostettu säie.
	 * @throws IOException
	 */
	private void createThreadToDealWithClient() throws IOException {
		chatServerThread target = new chatServerThread(serverSocket.accept(),this);
		//Lisätään synkronoituun listaan uusi threadi talteen ohjausta varten
		arrayOfClients.add(target);
		target.start();
	}
	/**
	 * Muodostetaan serverisoketti
	 * @throws IOException
	 */
	private void acceptInitialConnection() throws IOException {
		chatServer.serverSocket = new ServerSocket(portNumber);
	}
	public static void main(String[] args) {
		int portNumber;
		boolean kuunnellaan = true;
		if (args.length !=1 ) {
			//Käytetään tehtävässä annettua default-porttia
			portNumber = 12345;
		} else {
			portNumber = Integer.parseInt(args[0]);
		}
		try {
			//Yritetään tehdä chattiserveri
			chatServer chatServer = new chatServer(portNumber);
			chatServer.acceptInitialConnection();
			//Avataan skanneri pyörimään ylläpitäjälle
			Yllapitaja yllapitaja = new Yllapitaja();
			yllapitaja.start();
			while (kuunnellaan) {
				//Palvelin ei mene tukkoon jos clienttien määrää hieman rajoitetaan
				if (chatServer.arrayOfClients.size() < 500) {
					chatServer.createThreadToDealWithClient();
				}
			}
		} catch (IOException e) {
			System.exit(-1);
		}
	}
}
/**
 * Serverin ylläpitäjän ikkunassa pyörivä threadi, joka skannailee tuleeko serverin ylläpitäjältä
 * LOPETA-käsky, jolloin kaikke ajetaan alas ja ilmoitetaan clienteille kaatumisesta.
 * @author Aki Väliaho
 *
 */
class Yllapitaja extends Thread {
	private Scanner scanner = new Scanner(System.in);
	//Voisi tietysti olla joku hieman pysyvämpikin tagi. 
	private UUID yllapitajanUUID = UUID.randomUUID();
	@Override
	public void run() {
		while (true) {
			if (scanner.hasNextLine()) {
				String scannerLine = scanner.nextLine();
				if (scannerLine.equals("LOPETA")) {
					//Lopetetaan kaikki threadit ja serveri
					//Lykätään jokaiseen sokettiin LOPETA-käsky
					Viesti yllapitajankatkaisu = new Viesti();
					yllapitajankatkaisu.setDisconnect(true);
					yllapitajankatkaisu.setYllapitajan(true);
					for (chatServerThread thread : chatServer.arrayOfClients) {
						try {
							thread.getOut().writeObject(yllapitajankatkaisu);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					try {
						//Ei sovi unohtaa katkaista sokettia!
						chatServer.serverSocket.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					//Serveri kuolee
					System.exit(-1);
				} else {
					//Viesti ei ole LOPETA-käsky, lähetetään ylläpitäjän nimissä kaikille
					Viesti yllapitajanViesti;
					try {
						yllapitajanViesti = new Viesti(scannerLine, InetAddress.getLocalHost().getHostAddress(), yllapitajanUUID);
						yllapitajanViesti.setYllapitajan(true);
						for (chatServerThread thread : chatServer.arrayOfClients) {
							try {
								thread.getOut().writeObject(yllapitajanViesti);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					} catch (UnknownHostException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}
}