package valiaho.database;

import java.util.*;

import valiaho.distributedChat.*;

public class DBUploaderThread extends TimerTask {

	private ArrayList<Viesti> kaikkiViestit;

	public DBUploaderThread(ArrayList<Viesti> kaikkiViestit) {
		this.kaikkiViestit = kaikkiViestit;
		//Grab a socket to the SQL class here
		
	}

	@Override
	public void run() {
		//Just a test task
		//Send and clear the kaikkiViestit object here
		System.out.println("Hello World");
	}
}
