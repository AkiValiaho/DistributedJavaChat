package valiaho.distributedChat;
import java.io.*;
import java.util.*;
/**
 * Viesti-luokka kapsuloi l�hett�j�n ip-osoitteen ja viestin sarjallistettavaan olioon.
 * Luokan olioita voi l�hett�� soketteihin objectoutputsteamilla
 * @author Aki V�liaho, SoftICE Oy
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
	 * @param viesti Viestin sis�lt�
	 * @param ip L�hett�j�n IP-osoite
	 * @param userID2 L�hett�j�n uniikki UIID
	 */
	public Viesti(String viesti, String ip, UUID userID2) {
		this.setIp(ip);
		this.setViesti(viesti);
		this.setUserID(userID2);
	}
	public Viesti() {
	}
	/**
	 * Korvaa oletusarvoisen writeObjectin lis��m�ll� p��lle yksinkertaisen salakirjoituslayerin
	 * Tarvittaessa t�t� salakirjoituslayeri� voi muokata turvallismpaan suuntaan muokkaamalla sen toimijoita toimimaan tietyll� tavalla.
	 * @param out Mihin kirjoitetaan
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		//"Encryptataan" tiedot Caesarilla, ett� ei aivan suoraan saa napsittua serializable-esityst� verkosta
		//T�ss� voisi k�ytt�� jotakin hienompaakin suhteellisen helposti. L�hett�isi vaikka privaattikeyt jonkun
		//turvallisemman verkkoprotokollan kautta serverilt� clientille ja purkaisi sitten t�ss� oliossa k�ytt�en tuota
		//keyt� (tuhoten avaimen tietysti oliosta ennen eteenp�in l�hett�mist�).
		//Helpompi tapa olisi k�ytt�� tietysti jotakin valmislibrary� objektin signaamiseen ja sealaamiseen. API:sta l�ytyy itseasiassa
		//kaksi hy�dyllist� luokkaa: https://docs.oracle.com/javase/7/docs/api/java/security/SignedObject.html ja
		//https://docs.oracle.com/javase/7/docs/api/javax/crypto/SealedObject.html joilla voisi t�llaisen wrapperin helposti kehitell�
		//ja t�ten v�lttyisi n�iden writeObjektin ja readObjektin kirjoittamiselta.
		if (getKryptattu()) {
			//On jo kryptattu, sarjallistetaan
			out.defaultWriteObject();
		} else {
			encrypt(out);
		}
		}
	//T�st� metodista tulee nyt aivan turha jos saadaan sealedobject toimimaan oikein
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
		//Asetetaan tieto, ett� objektin fieldit on nyt 'salakirjoitettu'
		setKryptattu(true);
		out.defaultWriteObject();
		//Kirjoitetaan ulos defaultilla
	}
	/**
	 * Puretaan sarjallistus
	 * @param in Mit� kuunnellaan
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException {
	//Ja k��nnet��n koko homma takaisin ykk�s-Caesarista
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
		//Asetetaan tieto, ett� objektin fieldit on purettu
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