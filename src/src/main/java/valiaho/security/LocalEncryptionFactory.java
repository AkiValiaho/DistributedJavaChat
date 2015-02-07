package valiaho.security;
import java.io.*;
import java.security.*;
import java.util.*;

import javax.crypto.*;
import javax.crypto.spec.*;

import valiaho.distributedChat.*;
/**
 * @author Aki Väliaho, SoftICE Oy
 */
public class LocalEncryptionFactory {
	private SealedObject sealedObject;
	private File locationToKeyString;
	private String algorithmString;
	private Viesti tt;
	private static Key key; 
	public LocalEncryptionFactory(SealedObject object) {
		this.setSealedObject(object);
	}
	public LocalEncryptionFactory() {
		try {
			this.key = readKey(new File(("theKey.txt")));
			this.algorithmString = "DES";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * Yleiskäyttöinen factory DES-tyylisen kryptauksen ja olion paketoinnin suorittamiseen
	 * @param t
	 * @param algorithm
	 * @param locationToKey	File objekti avaimeen
	 * @throws NoSuchAlgorithmException Tätä kyseistä algoritmia ei löydy määritellystä listasta
	 * @throws NoSuchPaddingException Hashtablesta ei löydy 
	 * @throws InvalidKeyException
	 * @throws IllegalBlockSizeException
	 * @throws IOException Ei pysty avaamaan handlea tiedostoon!
	 */
	public LocalEncryptionFactory(Viesti t,String algorithm,File locationToKey) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, IOException {
		  this.setTt(t);
		  this.setAlgorithmString(algorithm);
		  this.setLocationToKeyString(locationToKey);
		  Cipher cipher=Cipher.getInstance(algorithm);
		  cipher.init(Cipher.ENCRYPT_MODE,getKey());
		  setSealedObject(new SealedObject((Serializable) t, cipher));
	}
	private Key readKey(File locationToKey) throws IOException {
		// TODO Auto-generated method stub
		    FileInputStream fis = new FileInputStream(locationToKey);
		    byte[] keyBytes = new byte[fis.available()];
		    fis.read(keyBytes);
		    fis.close();
		    byte[] encodedKey = Base64.getDecoder().decode(keyBytes);
		    Key keyFromFile = new SecretKeySpec(encodedKey, "DES");
		    return keyFromFile;
	}
	/**
	 * @return Palauttaa valmiin sealedObjektin
	 * @throws InvalidKeyException avain on virheellinens
	 * @throws NoSuchAlgorithmException Kyseistä algoritmia ei löydy määritellyistä tapauksista
	 * @throws NoSuchPaddingException
	 * @throws IOException Ei pysty avaamaan kahvaa tiedoston
	 * @throws IllegalBlockSizeException Blokin koko ei vastaa annettua avainta
	 */
	public SealedObject getSealedObject() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IOException, IllegalBlockSizeException {
			  Cipher cipher=Cipher.getInstance(algorithmString);
			  cipher.init(Cipher.ENCRYPT_MODE,getKey());
			  setSealedObject(new SealedObject((Serializable) tt, cipher));
			  return this.sealedObject;
	}
	public Viesti getDecrypterSealedObject(SealedObject object) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, ClassNotFoundException, IllegalBlockSizeException, BadPaddingException, IOException {
		if (object == null) {
			return null;
		}
		String algorithmName = object.getAlgorithm();
	    Cipher cipher = Cipher.getInstance(algorithmName);
	    cipher.init(Cipher.DECRYPT_MODE, key);
	    Viesti viesti = (Viesti) object.getObject(cipher);
	    return viesti;
		}
	public void setSealedObject(SealedObject sealedObject) {
		this.sealedObject = sealedObject;
	}
	public File getLocationToKeyString() {
		return locationToKeyString;
	}
	public void setLocationToKeyString(File locationToKeyString) throws IOException {
		LocalEncryptionFactory.setKey(readKey(locationToKeyString));
		this.locationToKeyString = locationToKeyString;
	}
	public String getAlgorithmString() {
		return algorithmString;
	}
	public void setAlgorithmString(String algorithmString) {
		this.algorithmString = algorithmString;
	}
	public Viesti getTt() {
		return tt;
	}
	public void setTt(Viesti tt) {
		this.tt = tt;
	}
	public static Key getKey() {
		return key;
	}
	public static void setKey(Key key) {
		LocalEncryptionFactory.key = key;
	}
}