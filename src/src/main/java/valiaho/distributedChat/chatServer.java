package valiaho.distributedChat;
import java.io.*;
import java.net.*;
import java.security.*;
import java.util.*;
import java.util.concurrent.*;

import javax.crypto.*;

import valiaho.database.*;
import valiaho.security.*;
/**
 * Yksinkertainen multithreadaava sokettiserveri.
 * 
 * Pohdiskelu: Pï¿½ï¿½tin, ettï¿½ kirjoittaisin soketteihin sarjallistettavia
 * objekteja sillï¿½ Javan versio tï¿½stï¿½ taipuu olio-ohjelmoinnin hengessï¿½ melkeimpï¿½ mihin vain. Sarjallistaminen
 * on Javassa JVM-riippumaton prosessi eli kï¿½ytï¿½nnï¿½ssï¿½ objekti voidaan sarjallistaa yhdessï¿½
 * ympï¿½ristï¿½ssï¿½ ja purkaa kokonaan toisessa. Pï¿½ï¿½dyin kirjoittamaan sarjallistettavan Viesti-luokan johon kapseloin
 * clientin IP-osoitteen, satunnaisgeneroidun UUID:n sekï¿½ viestin sisï¿½llï¿½n. Kehitin luokkaan
 * primitiivisen salauksen sen tï¿½rkeille fieldeille, jota kï¿½ytï¿½nnï¿½ssï¿½ olisi helppo lï¿½hteï¿½ laajentamaan turvallisempaan suuntaan
 * esimerkiksi jollakin julkisen avaimen salakirjoitusmenetelmï¿½llï¿½.
 * Serveripuolella pï¿½ï¿½dyin pyï¿½rittï¿½mï¿½ï¿½n mainin lisï¿½ksi kahta muuta threadia:
 * Jokaiselle Clientille omaa chatServerThread-threadia, jossa valvoin kyseiselle threadille tulevaa sokettia sekï¿½ lï¿½hetin
 * serverin pï¿½ï¿½luokasta staattisesti referoidun CopyOnWriteArrayListin threadeille soketille tulleen viestin sekï¿½ yllï¿½pitï¿½jï¿½n
 * viestinlï¿½hetys - ja lopetustoimintaan liittyvï¿½n toisen threadin.
 * Clienttipuolella kirjoitin yksinkertaisen ohjelman, joka tarkkaili tuliko LOPETA-syï¿½te kï¿½yttï¿½jï¿½ltï¿½ ja ajoi alas serverillï¿½
 * majailevan threadin Viesti-objektiin liittyvï¿½n disconnect-totuusarvon avulla. Jos syï¿½te sisï¿½lsi muuta tekstiï¿½ kirjoitettiin se sokettiin
 * serverithreadin lï¿½hetettï¿½vï¿½ksi eteenpï¿½in.
 * 
 * Tï¿½ssï¿½ tehtï¿½vï¿½ssï¿½ kï¿½ytin runsaasti java.Net.InetAddress-kirjaston metodeja. Niistï¿½ erityisesti getLocalHost() voi aiheuttaa ongelmia tï¿½mï¿½n
 * ohjelman tietojen oikeellisuudessa jos clientti elï¿½ï¿½ kompleksissa monia erilaisia verkkoadaptereja pyï¿½rittï¿½vï¿½ssï¿½ ympï¿½ristï¿½ssï¿½. Sokettiohjelmointi
 * aiheuttaa myï¿½s ongelmia jos yllï¿½pitï¿½jï¿½n palvelin sijaitsee osoitteenmuunnoksen (NAT) takana. Tï¿½llï¿½in reititintï¿½ joudutaan conffaamaan, ettï¿½ julkiseen
 * IP-osoitteeseen osuvat paketit siirtyvï¿½t sisï¿½verkon oikealle tietokoneelle. 
 * 
 * Kaikenkaikkiaan ratkaisu toimii oikein mallikkaasti vaikka objektien sijaan olisi ehkï¿½ hieman jï¿½rkevï¿½mpï¿½ï¿½ kaistan sï¿½ï¿½stï¿½misen takia lï¿½hetellï¿½
 * esimerkiksi pelkkiï¿½ stringejï¿½ bitteinï¿½. Toisaalta korkealla asteella toimittaessa objektit mahdollistavat yhï¿½ moniulotteisempien ohjelmien kirjoittamisen
 * vï¿½hemmï¿½llï¿½ vaivalla. Objektien kirjoittaminen soketteihin onnistuu kï¿½tevï¿½sti myï¿½s androidille (RMI ei ilmeisesti onnistu, koska jostain syystï¿½ java.rmi-libraryï¿½ ei ole importattu mukaan API:in)
 * 
 * Itsearviointi: 
 * Hajautettujen ohjelmien koodaaminen on hyvin mukavaa silloin kun tuntee, ettï¿½ debuggeri tottelee itseï¿½. Tï¿½llï¿½ kertaa olinkin ns. tulessa
 * ja suurinosa ongelmista ratkaistiin aika nopeasti, vaikka muutama turhauttava exceptioni hyï¿½kkï¿½sikin sokettien kautta pï¿½ï¿½lle. Hyï¿½dyllinen
 * taito mitï¿½ olen oppinut kï¿½yttï¿½mï¿½ï¿½n vasta viimeaikoina monimutkaisten projektien tekemisessï¿½ on dokumentaation lukeminen. Virhetilanne
 * on helpompi korjata jos tietï¿½ï¿½ mistï¿½ se dokumentaation mukaan syntyy. Vï¿½lillï¿½ nï¿½istï¿½ syntyy villejï¿½kin matkoja kohti rautatasoa.
 * Koodia oppii kirjoittamaan parhaiten, kun oppii ymmï¿½rtï¿½mï¿½ï¿½n sen kausaalisen luonteen; Mitï¿½ï¿½n ei synny tyhjï¿½stï¿½ ja kaikella on aina syynsï¿½
 * Metaforallisesti tï¿½mï¿½ lienee lï¿½hellï¿½ nappikuulokkeiden solmujen avaamista.
 * Sarjallistamisesta oli mukava oppia lisï¿½ï¿½. Uskonkin, ettï¿½ sain tï¿½stï¿½ tehtï¿½vï¿½stï¿½ ainakin
 * uusia ideoita omiin projekteihini kehiteltï¿½vï¿½ksi. Tyï¿½kalupakin kasvaessa ne ideat mitkï¿½ ovat olleet hautumassa kuulostavat entistï¿½ realistisemmilta
 * ja sitï¿½ kautta eivï¿½t olekaan enï¿½ï¿½ pelkkiï¿½ aloittamista vaille valmiita olevia ikuisuusprojekteja.
 * @author Aki Vï¿½liaho
 *
 */
public class chatServer{
	private Integer portNumber;
	//Kï¿½ytetï¿½ï¿½n tï¿½ssï¿½ CopyOnWriteArrayListiï¿½, joka on tehokas thread-safe implementaatio
	//silloin, kun suurinosa threadin suorittamista operaatioista on listan iterointia.
	//Tï¿½ssï¿½ tapauksessa chat-ohjelma esimerkiksi viettï¿½ï¿½ suurimman osan ajastaan maalaisjï¿½rjen mukaan
	//tutkiskellen onko tullut uusia viestejï¿½ eli iteroiden Clienttejï¿½ lï¿½pi ja ympï¿½ten uusia viestejï¿½
	//soketteihin.
	public static CopyOnWriteArrayList<chatServerThread> arrayOfClients = new CopyOnWriteArrayList<>();
	public static ServerSocket serverSocket;
	public static boolean changesToListBoolean = false;
	public static ArrayList<Viesti> kaikkiViestit = new ArrayList<>();
	private LocalEncryptionFactory encryptionFactory;
	public chatServer(Integer portNumber) {
		this.portNumber = portNumber;
		setEncryptionFactory(new LocalEncryptionFactory());
	}
	/**
	 * Toimenpiteet sï¿½ikeen avaamista varten yksittï¿½iselle clientille
	 * Lisï¿½tï¿½ï¿½n staattiseen listaan uusi muodostettu sï¿½ie.
	 * @throws IOException
	 */
	private void createThreadToDealWithClient() throws IOException {
		chatServerThread target = new chatServerThread(serverSocket.accept(),this);
		//Lisï¿½tï¿½ï¿½n synkronoituun listaan uusi threadi talteen ohjausta varten
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
			//Kï¿½ytetï¿½ï¿½n tehtï¿½vï¿½ssï¿½ annettua default-porttia
			portNumber = 12345;
		} else {
			portNumber = Integer.parseInt(args[0]);
		}
		try {
			//Yritetï¿½ï¿½n tehdï¿½ chattiserveri
			chatServer chatServer = new chatServer(portNumber);
			chatServer.acceptInitialConnection();
			//Avataan skanneri pyï¿½rimï¿½ï¿½n yllï¿½pitï¿½jï¿½lle
			Yllapitaja yllapitaja = new Yllapitaja(chatServer.getEncryptionFactory());
			//Startataan tässä ylläpitäjän oma threadi serverille
			yllapitaja.start();
			//Sitten startataan Viestejä DB:lle uppaava timertask
			Timer timer = new Timer();
			timer.scheduleAtFixedRate(new DBUploaderThread(kaikkiViestit), 10000, 50000);

			while (kuunnellaan) {
				//Palvelin ei mene tukkoon jos clienttien mï¿½ï¿½rï¿½ï¿½ hieman rajoitetaan
				if (chatServer.arrayOfClients.size() < 500) {
					chatServer.createThreadToDealWithClient();
				}
			}
		} catch (IOException e) {
			System.exit(-1);
		}
	}
	public LocalEncryptionFactory getEncryptionFactory() {
		return encryptionFactory;
	}
	public void setEncryptionFactory(LocalEncryptionFactory encryptionFactory) {
		this.encryptionFactory = encryptionFactory;
	}
}
/**
 * Serverin yllï¿½pitï¿½jï¿½n ikkunassa pyï¿½rivï¿½ threadi, joka skannailee tuleeko serverin yllï¿½pitï¿½jï¿½ltï¿½
 * LOPETA-kï¿½sky, jolloin kaikke ajetaan alas ja ilmoitetaan clienteille kaatumisesta.
 * @author Aki Vï¿½liaho
 *
 */
class Yllapitaja extends Thread {
	private Scanner scanner = new Scanner(System.in);
	//Voisi tietysti olla joku hieman pysyvï¿½mpikin tagi. 
	private UUID yllapitajanUUID = UUID.randomUUID();
	private LocalEncryptionFactory encryptionFactory; 
	public Yllapitaja(LocalEncryptionFactory encryptionFactory2) {
		this.encryptionFactory = encryptionFactory2;
	}

	@Override
	public void run() {
		while (true) {
			if (scanner.hasNextLine()) {
				String scannerLine = scanner.nextLine();
				if (scannerLine.equals("LOPETA")) {
					//Lopetetaan kaikki threadit ja serveri
					//Lykï¿½tï¿½ï¿½n jokaiseen sokettiin LOPETA-kï¿½sky
					Viesti yllapitajankatkaisu = new Viesti();
					yllapitajankatkaisu.setDisconnect(true);
					yllapitajankatkaisu.setYllapitajan(true);
					try {
						SealedObject outObject = LocalEncryptionFactory.writeSealedObjectToSocket(yllapitajankatkaisu, encryptionFactory);
						for (chatServerThread thread : chatServer.arrayOfClients) {
							try {
								//Kirjoita objekti ulkopuskurin toimimaan.
								thread.getOut().writeObject(outObject);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
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
					//Viesti ei ole LOPETA-kï¿½sky, lï¿½hetetï¿½ï¿½n yllï¿½pitï¿½jï¿½n nimissï¿½ kaikille
					Viesti yllapitajanViesti;
					try {
						yllapitajanViesti = new Viesti(scannerLine, InetAddress.getLocalHost().getHostAddress(), yllapitajanUUID);
						yllapitajanViesti.setYllapitajan(true);
						SealedObject outObject = LocalEncryptionFactory.writeSealedObjectToSocket(yllapitajanViesti, encryptionFactory);
						for (chatServerThread thread : chatServer.arrayOfClients) {
							try {
								thread.getOut().writeObject(outObject);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					} catch (UnknownHostException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		}
	}
}