package com.quickventure;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;

import javax.swing.JFrame;

public class Game extends JFrame {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Game() {
		initUI();
	}
	
	private void initUI() {
		Board gameBoard = new Board();
		add(gameBoard, BorderLayout.CENTER);
		
		setSize(800, 600);
		
//		pack();
		
		setTitle("QuickVenture");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		gameBoard.setSize(getWidth()-14, getHeight()-37); // Not universal, not sure how else to do it.
		gameBoard.setBackground(new Color(204,250,250));
		gameBoard.runGameLoop();
	}
	
	public static void main(String [] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				Game game = new Game();
				game.setVisible(true);
			}
		});
	}
}
