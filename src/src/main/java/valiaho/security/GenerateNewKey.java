package valiaho.security;

import java.io.*;
import java.security.*;
import java.util.*;

import javax.crypto.*;

public class GenerateNewKey {
	public GenerateNewKey() throws NoSuchAlgorithmException, IOException {
	KeyGenerator generator; 
	 generator = KeyGenerator.getInstance("DES"); 
	 generator.init(new SecureRandom()); 
	 Key randomDESKey = generator.generateKey(); 
	 File toWritTo = new File("theKey.txt");
	 FileWriter writer = new FileWriter(toWritTo);
	 BufferedWriter newWriter = new BufferedWriter(writer);
	 newWriter.write(Base64.getEncoder().encodeToString(randomDESKey.getEncoded()));
	 newWriter.close();
	}
public static void main(String[] args) throws NoSuchAlgorithmException, IOException {
	GenerateNewKey newKey = new GenerateNewKey();
}
}
