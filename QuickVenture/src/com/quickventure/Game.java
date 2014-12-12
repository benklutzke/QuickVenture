package com.quickventure;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.io.File;

import javax.swing.JFrame;

public class Game extends JFrame {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static String mapFile = null;

	public Game(String file) {
		initUI(file);
	}
	
	private void initUI(String file) {
		Board gameBoard = new Board();
		add(gameBoard, BorderLayout.CENTER);
		
		setSize(800, 600);
		
//		pack();
		
		setTitle("QuickVenture");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		gameBoard.setMapFile(file);
		gameBoard.setSize(getWidth()-14, getHeight()-37); // Not universal, not sure how else to do it.
		gameBoard.setBackground(new Color(204,250,250));
		gameBoard.runGameLoop();
	}
	
	public static void main(String [] args) {
		if(args.length > 0){
			File f = new File(args[0]);
			if(f.exists() && !f.isDirectory()){
				mapFile = args[0];
			}
		}
		
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				Game game = new Game(mapFile);
				game.setVisible(true);
			}
		});
	}
}
