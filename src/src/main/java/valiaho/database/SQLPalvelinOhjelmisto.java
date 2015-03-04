package valiaho.database;
import java.io.*;
import java.net.*;
import java.security.*;
import java.util.*;

import javax.crypto.*;

import valiaho.distributedChat.*;
import valiaho.security.*;
public class SQLPalvelinOhjelmisto {
	private Integer portNumber = null;
	private ServerSocket palvelinSoketti = null;
	private LukijaThread lukijaThread = null;
	private LocalEncryptionFactory encryptionFactory = null;
	private Socket socket;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	public SQLPalvelinOhjelmisto() {
	}
	public SQLPalvelinOhjelmisto(int portNumber) {
		this.setPortNumber(portNumber);
		this.encryptionFactory = new LocalEncryptionFactory();
		try {
			this.encryptionFactory.setLocationToKeyString(new File("theKey.txt"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * K‰ynnist‰‰ palvelimen, palauttaa boolean arvon onnistuiko k‰ynnistys
	 * @return Palauttaa tiedon onnistuiko k‰ynnistys
	 * @throws IOException Jos porttiin ei jostain syyst‰ saada yhteytt‰‰ heitt‰‰ exceptionin
	 */
	public Boolean kaynnistaPalvelin() throws IOException {
		if (getPortNumber() == null || portNumber > 65535 || portNumber < 1) {
			return false;
		}

		palvelinSoketti = new ServerSocket(getPortNumber());
		startReaderThread();
		return true;
	}
	
	/**
	 * return Palauttaa falsen jos threadi ei l‰hde k‰yntiin
	 * @throws IOException 
	 */
	private void startReaderThread() throws IOException {

				//Odottaa ett‰ serverille tulee kutsu
				this.socket = palvelinSoketti.accept();
				this.in = new ObjectInputStream(socket.getInputStream());
				this.out = new ObjectOutputStream(socket.getOutputStream());
				this.lukijaThread = new LukijaThread();
				this.lukijaThread.start();
	}
	public static void main(String[] args) throws NumberFormatException, IOException {
		SQLPalvelinOhjelmisto ohjelmisto = new SQLPalvelinOhjelmisto(54321);
		ohjelmisto.kaynnistaPalvelin();
		
	}
	public Integer getPortNumber() {
		return portNumber;
	}
	public void setPortNumber(Integer portNumber) {
		this.portNumber = portNumber;
	}
	public ServerSocket getPalvelinSoketti() {
		return palvelinSoketti;
	}
	public void setPalvelinSoketti(ServerSocket palvelinSoketti) {
		this.palvelinSoketti = palvelinSoketti;

	}
	public LukijaThread getLukijaThread() {
		return lukijaThread;
	}
	public void setLukijaThread(LukijaThread lukijaThread) {
		this.lukijaThread = lukijaThread;
	}
	private class LukijaThread extends Thread {
		@Override
		public void run() {
		//K‰ynnist‰ threadi joka hoitaa saapuneen viestin tallentamisen DB:lle
		try {
			while (true) {
				lueViesti();
			}
		} catch (InvalidKeyException | ClassNotFoundException
				| NoSuchAlgorithmException | NoSuchPaddingException
				| IllegalBlockSizeException | BadPaddingException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//Terminatee lukemisen j‰lkeen
		}

		private void lueViesti() throws ClassNotFoundException, IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
			//Ota viesti vastaan ja l‰het‰ se l‰hett‰j‰ Threadille
			// + viestist‰ otetut ominaisuudet, decryptaa paketti
			ArrayList<Viesti> viestiArrayList = palautaDekryptoituViestiArrayList();
			LahettajaThread lahettajaThread = new LahettajaThread(viestiArrayList);
			lahettajaThread.start();
		}

		private ArrayList<Viesti> palautaDekryptoituViestiArrayList()
				throws IOException, ClassNotFoundException,
				InvalidKeyException, NoSuchAlgorithmException,
				NoSuchPaddingException, IllegalBlockSizeException,
				BadPaddingException {
			SealedObject readObject = (SealedObject) in.readObject();
			ArrayList<Viesti> viestiArrayList= encryptionFactory.getDecrypterSealedObjectArrayList(readObject);
			return viestiArrayList;
		}
	}
	private class LahettajaThread extends Thread {
		private ArrayList<Viesti> viestiArrayList;
		public LahettajaThread(ArrayList<Viesti> viestiArrayList) {
			this.setViestiArrayList(viestiArrayList);
		}
		//L‰hetet‰‰n puretut yksityiskohdat DB:lle t‰st‰
		@Override
		public void run() {
			lahetaTiedotDB();
		}
		private void lahetaTiedotDB() {
			//Iteroi jokainen Viesti arraylistist‰ l‰pi ja l‰het‰ tiedot DB:lle
			for (Viesti viesti : viestiArrayList) {
				System.out.println(viesti.getViesti());
			}
		}
		public ArrayList<Viesti> getViestiArrayList() {
			return viestiArrayList;
		}
		public void setViestiArrayList(ArrayList<Viesti> viestiArrayList) {
			this.viestiArrayList = viestiArrayList;
		}
	}
}