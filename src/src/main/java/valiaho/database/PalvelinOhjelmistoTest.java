package valiaho.database;

import static org.junit.Assert.assertFalse;

import java.io.*;

import org.junit.*;

public class PalvelinOhjelmistoTest {

	@Test
	public void testIfPortNumberNullReturnsFalse() {
		PalvelinOhjelmisto palvelinOhjelmisto = new PalvelinOhjelmisto();
		try {
			assertFalse(palvelinOhjelmisto.kaynnistaPalvelin());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testIfPortNumberOutOfScopeReturnsFalse() {
		PalvelinOhjelmisto palvelinOhjelmisto = new PalvelinOhjelmisto(65536);
		try {
			assertFalse(palvelinOhjelmisto.kaynnistaPalvelin());
			palvelinOhjelmisto = new PalvelinOhjelmisto(0);
			assertFalse(palvelinOhjelmisto.kaynnistaPalvelin());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
