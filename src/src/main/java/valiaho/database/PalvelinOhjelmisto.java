package valiaho.database;

import java.io.*;
import java.net.*;

public class PalvelinOhjelmisto {
	private Integer portNumber = null;
	private ServerSocket palvelinSoketti = null;
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
			
		}
		return true;
	}
	
	/**
	 * @return Palauttaa truen jos threadi saatiin k�ynnistetty�
	 */
	private boolean startReaderThread() {
		// TODO Auto-generated method stub
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
	private class LukijaThread extends Thread {
		@Override
		public void run() {
			// TODO Auto-generated method stub
		}
	}
}