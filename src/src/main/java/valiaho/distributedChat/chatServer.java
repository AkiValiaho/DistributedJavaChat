package valiaho.distributedChat;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
/**
 * Yksinkertainen multithreadaava sokettiserveri.
 * 
 * Pohdiskelu: P��tin, ett� kirjoittaisin soketteihin sarjallistettavia
 * objekteja sill� Javan versio t�st� taipuu olio-ohjelmoinnin hengess� melkeimp� mihin vain. Sarjallistaminen
 * on Javassa JVM-riippumaton prosessi eli k�yt�nn�ss� objekti voidaan sarjallistaa yhdess�
 * ymp�rist�ss� ja purkaa kokonaan toisessa. P��dyin kirjoittamaan sarjallistettavan Viesti-luokan johon kapseloin
 * clientin IP-osoitteen, satunnaisgeneroidun UUID:n sek� viestin sis�ll�n. Kehitin luokkaan
 * primitiivisen salauksen sen t�rkeille fieldeille, jota k�yt�nn�ss� olisi helppo l�hte� laajentamaan turvallisempaan suuntaan
 * esimerkiksi jollakin julkisen avaimen salakirjoitusmenetelm�ll�.
 * Serveripuolella p��dyin py�ritt�m��n mainin lis�ksi kahta muuta threadia:
 * Jokaiselle Clientille omaa chatServerThread-threadia, jossa valvoin kyseiselle threadille tulevaa sokettia sek� l�hetin
 * serverin p��luokasta staattisesti referoidun CopyOnWriteArrayListin threadeille soketille tulleen viestin sek� yll�pit�j�n
 * viestinl�hetys - ja lopetustoimintaan liittyv�n toisen threadin.
 * Clienttipuolella kirjoitin yksinkertaisen ohjelman, joka tarkkaili tuliko LOPETA-sy�te k�ytt�j�lt� ja ajoi alas serverill�
 * majailevan threadin Viesti-objektiin liittyv�n disconnect-totuusarvon avulla. Jos sy�te sis�lsi muuta teksti� kirjoitettiin se sokettiin
 * serverithreadin l�hetett�v�ksi eteenp�in.
 * 
 * T�ss� teht�v�ss� k�ytin runsaasti java.Net.InetAddress-kirjaston metodeja. Niist� erityisesti getLocalHost() voi aiheuttaa ongelmia t�m�n
 * ohjelman tietojen oikeellisuudessa jos clientti el�� kompleksissa monia erilaisia verkkoadaptereja py�ritt�v�ss� ymp�rist�ss�. Sokettiohjelmointi
 * aiheuttaa my�s ongelmia jos yll�pit�j�n palvelin sijaitsee osoitteenmuunnoksen (NAT) takana. T�ll�in reititint� joudutaan conffaamaan, ett� julkiseen
 * IP-osoitteeseen osuvat paketit siirtyv�t sis�verkon oikealle tietokoneelle. 
 * 
 * Kaikenkaikkiaan ratkaisu toimii oikein mallikkaasti vaikka objektien sijaan olisi ehk� hieman j�rkev�mp�� kaistan s��st�misen takia l�hetell�
 * esimerkiksi pelkki� stringej� bittein�. Toisaalta korkealla asteella toimittaessa objektit mahdollistavat yh� moniulotteisempien ohjelmien kirjoittamisen
 * v�hemm�ll� vaivalla. Objektien kirjoittaminen soketteihin onnistuu k�tev�sti my�s androidille (RMI ei ilmeisesti onnistu, koska jostain syyst� java.rmi-library� ei ole importattu mukaan API:in)
 * 
 * Itsearviointi: 
 * Hajautettujen ohjelmien koodaaminen on hyvin mukavaa silloin kun tuntee, ett� debuggeri tottelee itse�. T�ll� kertaa olinkin ns. tulessa
 * ja suurinosa ongelmista ratkaistiin aika nopeasti, vaikka muutama turhauttava exceptioni hy�kk�sikin sokettien kautta p��lle. Hy�dyllinen
 * taito mit� olen oppinut k�ytt�m��n vasta viimeaikoina monimutkaisten projektien tekemisess� on dokumentaation lukeminen. Virhetilanne
 * on helpompi korjata jos tiet�� mist� se dokumentaation mukaan syntyy. V�lill� n�ist� syntyy villej�kin matkoja kohti rautatasoa.
 * Koodia oppii kirjoittamaan parhaiten, kun oppii ymm�rt�m��n sen kausaalisen luonteen; Mit��n ei synny tyhj�st� ja kaikella on aina syyns�
 * Metaforallisesti t�m� lienee l�hell� nappikuulokkeiden solmujen avaamista.
 * Sarjallistamisesta oli mukava oppia lis��. Uskonkin, ett� sain t�st� teht�v�st� ainakin
 * uusia ideoita omiin projekteihini kehitelt�v�ksi. Ty�kalupakin kasvaessa ne ideat mitk� ovat olleet hautumassa kuulostavat entist� realistisemmilta
 * ja sit� kautta eiv�t olekaan en�� pelkki� aloittamista vaille valmiita olevia ikuisuusprojekteja.
 * @author Aki V�liaho
 *
 */
public class chatServer{
	private Integer portNumber;
	//K�ytet��n t�ss� CopyOnWriteArrayListi�, joka on tehokas thread-safe implementaatio
	//silloin, kun suurinosa threadin suorittamista operaatioista on listan iterointia.
	//T�ss� tapauksessa chat-ohjelma esimerkiksi viett�� suurimman osan ajastaan maalaisj�rjen mukaan
	//tutkiskellen onko tullut uusia viestej� eli iteroiden Clienttej� l�pi ja ymp�ten uusia viestej�
	//soketteihin.
	public static CopyOnWriteArrayList<chatServerThread> arrayOfClients = new CopyOnWriteArrayList<>();
	public static ServerSocket serverSocket;
	public chatServer(Integer portNumber) {
		this.portNumber = portNumber;
	}
	/**
	 * Toimenpiteet s�ikeen avaamista varten yksitt�iselle clientille
	 * Lis�t��n staattiseen listaan uusi muodostettu s�ie.
	 * @throws IOException
	 */
	private void createThreadToDealWithClient() throws IOException {
		chatServerThread target = new chatServerThread(serverSocket.accept(),this);
		//Lis�t��n synkronoituun listaan uusi threadi talteen ohjausta varten
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
			//K�ytet��n teht�v�ss� annettua default-porttia
			portNumber = 12345;
		} else {
			portNumber = Integer.parseInt(args[0]);
		}
		try {
			//Yritet��n tehd� chattiserveri
			chatServer chatServer = new chatServer(portNumber);
			chatServer.acceptInitialConnection();
			//Avataan skanneri py�rim��n yll�pit�j�lle
			Yllapitaja yllapitaja = new Yllapitaja();
			yllapitaja.start();
			while (kuunnellaan) {
				//Palvelin ei mene tukkoon jos clienttien m��r�� hieman rajoitetaan
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
 * Serverin yll�pit�j�n ikkunassa py�riv� threadi, joka skannailee tuleeko serverin yll�pit�j�lt�
 * LOPETA-k�sky, jolloin kaikke ajetaan alas ja ilmoitetaan clienteille kaatumisesta.
 * @author Aki V�liaho
 *
 */
class Yllapitaja extends Thread {
	private Scanner scanner = new Scanner(System.in);
	//Voisi tietysti olla joku hieman pysyv�mpikin tagi. 
	private UUID yllapitajanUUID = UUID.randomUUID();
	@Override
	public void run() {
		while (true) {
			if (scanner.hasNextLine()) {
				String scannerLine = scanner.nextLine();
				if (scannerLine.equals("LOPETA")) {
					//Lopetetaan kaikki threadit ja serveri
					//Lyk�t��n jokaiseen sokettiin LOPETA-k�sky
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
					//Viesti ei ole LOPETA-k�sky, l�hetet��n yll�pit�j�n nimiss� kaikille
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