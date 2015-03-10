package valiaho.database;

import java.io.*;
import java.net.*;
import java.security.*;
import java.util.*;

import javax.crypto.*;

import valiaho.distributedChat.*;
import valiaho.security.*;

public class DBUploaderThread extends TimerTask {
	private ArrayList<Viesti> kaikkiViestit;
	private LocalEncryptionFactory encryptionFactory;
	private ObjectOutputStream out;
	private Socket sqlSocketServer;

	public DBUploaderThread(ArrayList<Viesti> kaikkiViestit) {
		this.kaikkiViestit = kaikkiViestit;
		encryptionFactory = new LocalEncryptionFactory();
		//Grab a socket to the SQL class here
		try {
			this.sqlSocketServer = new Socket("localhost", 54321);
			this.out = new ObjectOutputStream(sqlSocketServer.getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public void run() {
		//Just a test task
		//Send and clear the kaikkiViestit object here
		try {
			out.writeObject(LocalEncryptionFactory.writeSealedObjectToSocket(kaikkiViestit, encryptionFactory));
			//Handle concurrency errors with LOCK!
			//TODO
			kaikkiViestit.clear();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}