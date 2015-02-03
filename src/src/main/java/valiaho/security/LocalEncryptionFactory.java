package valiaho.security;
import java.io.*;
import java.security.*;

import javax.crypto.*;
import javax.crypto.spec.*;
public class LocalEncryptionFactory<T extends Serializable> {
	private SealedObject sealedObject;
	private File locationToKeyString;
	private String algorithmString;
	private T tt;
	public LocalEncryptionFactory(SealedObject object) {
		this.setSealedObject(object);
	}
	public LocalEncryptionFactory() {
	}
	public LocalEncryptionFactory(T t,String algorithm,File locationToKey) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, IOException {
		  this.setTt(t);
		  this.setAlgorithmString(algorithm);
		  this.setLocationToKeyString(locationToKey);
		  Key key=readKey(locationToKey);
		  Cipher cipher=Cipher.getInstance(algorithm);
		  cipher.init(Cipher.ENCRYPT_MODE,key);
		  setSealedObject(new SealedObject(t, cipher));
	}
	private Key readKey(File locationToKey) throws IOException {
		// TODO Auto-generated method stub
		    FileInputStream fis = new FileInputStream(locationToKey);
		    byte[] keyBytes = new byte[fis.available()];
		    fis.read(keyBytes);
		    fis.close();
		    Key keyFromFile = new SecretKeySpec(keyBytes, "DES");
		    return keyFromFile;
	}
	public SealedObject getSealedObject() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IOException, IllegalBlockSizeException {
		if (sealedObject == null) {
			  Key key=readKey(locationToKeyString);
			  Cipher cipher=Cipher.getInstance(algorithmString);
			  cipher.init(Cipher.ENCRYPT_MODE,key);
			  setSealedObject(new SealedObject(tt, cipher));
		}
		return sealedObject;
		
	}
	public T getDecrypterSealedObject() {
		//Decryptaa sealattu objekti! TODO
		return null;
		
	}
	public void setSealedObject(SealedObject sealedObject) {
		this.sealedObject = sealedObject;
	}
	public File getLocationToKeyString() {
		return locationToKeyString;
	}
	public void setLocationToKeyString(File locationToKeyString) {
		this.locationToKeyString = locationToKeyString;
	}
	public String getAlgorithmString() {
		return algorithmString;
	}
	public void setAlgorithmString(String algorithmString) {
		this.algorithmString = algorithmString;
	}
	public T getTt() {
		return tt;
	}
	public void setTt(T tt) {
		this.tt = tt;
	}
}