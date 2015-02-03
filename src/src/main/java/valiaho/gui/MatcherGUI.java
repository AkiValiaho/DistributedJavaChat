package valiaho.gui;

import java.awt.*;

import javax.swing.*;

public class MatcherGUI {

	JFrame frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {

	}

	/**
	 * Create the application.
	 * @wbp.parser.entryPoint
	 */
	public MatcherGUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 842, 529);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

}

