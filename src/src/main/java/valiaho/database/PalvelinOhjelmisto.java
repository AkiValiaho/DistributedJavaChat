package valiaho.database;
import java.io.*;
import java.net.*;
public class PalvelinOhjelmisto {
	private Integer portNumber = null;
	private ServerSocket palvelinSoketti = null;
	private LukijaThread lukijaThread = null;
	public PalvelinOhjelmisto() {
	}
	public PalvelinOhjelmisto(int portNumber) {
		this.setPortNumber(portNumber);
		
	}

	/**
	 * K�ynnist�� palvelimen, palauttaa boolean arvon onnistuiko k�ynnistys
	 * @return Palauttaa tiedon onnistuiko k�ynnistys
	 * @throws IOException Jos porttiin ei jostain syyst� saada yhteytt�� heitt�� exceptionin
	 */
	public Boolean kaynnistaPalvelin() throws IOException {
		if (getPortNumber() == null || portNumber > 65535 || portNumber < 1) {
			return false;
		}

		palvelinSoketti = new ServerSocket(getPortNumber());
		if (!startReaderThread()) {
			return false;
		}
		return true;
	}
	
	/**
	 * return Palauttaa falsen jos threadi ei l�hde k�yntiin
	 */
	private boolean startReaderThread() {
		try {
			while (true) {
				//Odottaa ett� serverille tulee kutsu
				palvelinSoketti.accept();
				this.lukijaThread = new LukijaThread();
				this.lukijaThread.start();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;

	}
	public static void main(String[] args) throws NumberFormatException, IOException {
		int portNumber = Integer.parseInt(args[0]);
		PalvelinOhjelmisto ohjelmisto = new PalvelinOhjelmisto(portNumber);
		
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
		//K�ynnist� threadi joka hoitaa saapuneen viestin tallentamisen DB:lle
		lueViesti();
		//Terminatee lukemisen j�lkeen
		}

		private void lueViesti() {
			//Ota viesti vastaan ja l�het� se l�hett�j� Threadille
			// + viestist� otetut ominaisuudet, decryptaa paketti
			LahettajaThread lahettajaThread = new LahettajaThread();
			lahettajaThread.start();
		}
	}
	private class LahettajaThread extends Thread {
		@Override
		public void run() {
			lahetaTiedotDB();
		}
		private void lahetaTiedotDB() {
			// TODO Auto-generated method stub
		}
	}
}