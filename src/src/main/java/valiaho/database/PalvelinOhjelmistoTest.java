package valiaho.database;

import static org.junit.Assert.assertFalse;

import java.io.*;

import org.junit.*;

public class PalvelinOhjelmistoTest {

	@Test
	public void testIfPortNumberNullReturnsFalse() {
		SQLPalvelinOhjelmisto palvelinOhjelmisto = new SQLPalvelinOhjelmisto();
		try {
			assertFalse(palvelinOhjelmisto.kaynnistaPalvelin());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testIfPortNumberOutOfScopeReturnsFalse() {
		SQLPalvelinOhjelmisto palvelinOhjelmisto = new SQLPalvelinOhjelmisto(65536);
		try {
			assertFalse(palvelinOhjelmisto.kaynnistaPalvelin());
			palvelinOhjelmisto = new SQLPalvelinOhjelmisto(0);
			assertFalse(palvelinOhjelmisto.kaynnistaPalvelin());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
