package valiaho.distributedChat;
import java.io.*;
import java.util.*;
/**
 * Viesti-luokka kapsuloi lähettäjän ip-osoitteen ja viestin sarjallistettavaan olioon.
 * Luokan olioita voi lähettää soketteihin objectoutputsteamilla
 * @author Aki Vï¿½liaho, SoftICE Oy
 *
 */
/**
 * @author Aki
 *
 */
/**
 * @author Aki
 *
 */
/**
 * @author Aki
 *
 */
/**
 * @author Aki
 *
 */
public class Viesti implements Serializable{
	private static final long serialVersionUID = 1L;
	private String ip = "0",viesti ="0";
	private Boolean disconnect = false;
	private UUID userID;
	private Boolean kryptattu = false;
	private Boolean yllapitajan = false;
	private Boolean informationObjectBoolean = false;
	/**
	 * Konstuktori luokalle
	 * @param viesti Viestin sisï¿½ltï¿½
	 * @param ip Lï¿½hettï¿½jï¿½n IP-osoite
	 * @param userID2 Lï¿½hettï¿½jï¿½n uniikki UIID
	 */
	public Viesti(String viesti, String ip, UUID userID2) {
		this.setIp(ip);
		this.setViesti(viesti);
		this.setUserID(userID2);
	}
	public Viesti() {
	}
	/**
	 * Korvaa oletusarvoisen writeObjectin lisï¿½ï¿½mï¿½llï¿½ pï¿½ï¿½lle yksinkertaisen salakirjoituslayerin
	 * Tarvittaessa tätä salakirjoituslayeriä voi muokata turvallismpaan suuntaan muokkaamalla sen toimijoita toimimaan tietyllä tavalla.
	 * @param out Mihin kirjoitetaan
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		//"Encryptataan" tiedot Caesarilla, ettï¿½ ei aivan suoraan saa napsittua serializable-esitystï¿½ verkosta
		//Tï¿½ssï¿½ voisi kï¿½yttï¿½ï¿½ jotakin hienompaakin suhteellisen helposti. Lï¿½hettï¿½isi vaikka privaattikeyt jonkun
		//turvallisemman verkkoprotokollan kautta serveriltï¿½ clientille ja purkaisi sitten tï¿½ssï¿½ oliossa kï¿½yttï¿½en tuota
		//keytï¿½ (tuhoten avaimen tietysti oliosta ennen eteenpï¿½in lï¿½hettï¿½mistï¿½).
		//Helpompi tapa olisi kï¿½yttï¿½ï¿½ tietysti jotakin valmislibraryï¿½ objektin signaamiseen ja sealaamiseen. API:sta lï¿½ytyy itseasiassa
		//kaksi hyï¿½dyllistï¿½ luokkaa: https://docs.oracle.com/javase/7/docs/api/java/security/SignedObject.html ja
		//https://docs.oracle.com/javase/7/docs/api/javax/crypto/SealedObject.html joilla voisi tï¿½llaisen wrapperin helposti kehitellï¿½
		//ja tï¿½ten vï¿½lttyisi nï¿½iden writeObjektin ja readObjektin kirjoittamiselta.
		if (getKryptattu()) {
			//On jo kryptattu, sarjallistetaan
			out.defaultWriteObject();
		} else {
			encrypt(out);
		}
		}
	//Tästä metodista tulee nyt aivan turha jos saadaan sealedobject toimimaan oikein
	private void encrypt(ObjectOutputStream out) throws IOException {
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < getIp().length(); i++){
			char c = getIp().charAt(i);
			stringBuilder.append((char)c+1);
		    stringBuilder.append(',');
		}
		stringBuilder.append("");
		setIp(stringBuilder.toString());
		stringBuilder = new StringBuilder();
		for (int i = 0; i < getViesti().length(); i++){
		    char c = getViesti().charAt(i);
		    stringBuilder.append((char)c+1);
		    stringBuilder.append(',');
		}
		setViesti(stringBuilder.toString());
		//Asetetaan tieto, ettï¿½ objektin fieldit on nyt 'salakirjoitettu'
		setKryptattu(true);
		out.defaultWriteObject();
		//Kirjoitetaan ulos defaultilla
	}
	/**
	 * Puretaan sarjallistus
	 * @param in Mitï¿½ kuunnellaan
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException {
	//Ja kï¿½ï¿½nnetï¿½ï¿½n koko homma takaisin ykkï¿½s-Caesarista
		decrypt(in);
	}
	private void decrypt(ObjectInputStream in) throws IOException,
			ClassNotFoundException {
		in.defaultReadObject();
		StringBuilder stringBuilder = new StringBuilder();
		List<String> ipArray =Arrays.asList(getIp().split(","));
		for (String string : ipArray) {
			stringBuilder.append((char)(Integer.parseInt(string)-1));
		}
		setIp(stringBuilder.toString());
		stringBuilder = new StringBuilder();
		ipArray = Arrays.asList(getViesti().split(","));
		for (String string : ipArray) {
			  stringBuilder.append((char)(Integer.parseInt(string)-1));
		}
		setViesti(stringBuilder.toString());
		//Asetetaan tieto, ettï¿½ objektin fieldit on purettu
		setKryptattu(false);
	}
	/**
	 * @return Palauttaa olioon kapseloidun IP-osoitteen public metodin kautta.
	 */
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getViesti() {
		return viesti;
	}
	public void setViesti(String viesti) {
		this.viesti = viesti;
	}
	public UUID getUserID() {
		return userID;
	}
	public void setUserID(UUID userID) {
		this.userID = userID;
	}
	public Boolean getDisconnect() {
		return disconnect;
	}
	public void setDisconnect(Boolean disconnect) {
		this.disconnect = disconnect;
	}
	public Boolean getYllapitajan() {
		return yllapitajan;
	}
	public void setYllapitajan(Boolean yllapitajan) {
		this.yllapitajan = yllapitajan;
	}
	public Boolean getKryptattu() {
		return kryptattu;
	}
	public void setKryptattu(Boolean kryptattu) {
		this.kryptattu = kryptattu;
	}
	public Boolean getInformationObjectBoolean() {
		return informationObjectBoolean;
	}
	public void setInformationObjectBoolean(Boolean informationObjectBoolean) {
		this.informationObjectBoolean = informationObjectBoolean;
	}
}