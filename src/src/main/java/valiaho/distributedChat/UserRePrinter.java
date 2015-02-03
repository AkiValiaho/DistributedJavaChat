package valiaho.distributedChat;

import java.io.*;

public class UserRePrinter extends Thread {
	public Integer portNumber;
	public UserRePrinter() {
	
	}
		@Override
		public void run() {
			while (true) {
				if (chatServer.changesToListBoolean) {
					//TODO
					//Lähetä päivitetyt tiedot jokaiseen GUI:hin
					Viesti kayttajaListaViesti = new Viesti();
					for (chatServerThread chatServerThread : chatServer.arrayOfClients) {
						//Lähetä controllerille
						try {
							chatServerThread.getOut().writeObject(kayttajaListaViesti);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		}
}