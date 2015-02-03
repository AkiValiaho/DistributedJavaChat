package valiaho.security;
import java.io.*;
import java.security.*;

import javax.crypto.*;
public class LocalEncryptionFactory<T extends Serializable> {
	private SealedObject sealObject;
	private SealedObject sealedObject;
	public LocalEncryptionFactory(SealedObject object) {
		this.sealedObject = object;
	}
	public LocalEncryptionFactory(T t,String algorithm,File locationToKey) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, IOException {
		  KeyGenerator kg=KeyGenerator.getInstance(algorithm);
		  Key key=readKey(locationToKey);
		  Cipher cipher=Cipher.getInstance(algorithm);
		  cipher.init(Cipher.ENCRYPT_MODE,key);
		  sealObject = new SealedObject(t, cipher);
	}
	private Key readKey(File locationToKey) {
		// TODO Auto-generated method stub
		return null;
	}
	public SealedObject getSealedObject() {
		//Return TODO
		return null;
		
	}
	public T getDecrypterSealedObject() {
		//Decryptaa sealattu objekti! TODO
		return null;
		
	}
}