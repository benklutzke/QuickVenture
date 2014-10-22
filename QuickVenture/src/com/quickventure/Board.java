package com.quickventure;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import com.quickventure.images.Animation;
import com.quickventure.images.Sprite;
import com.quickventure.objects.GameObject;
import com.quickventure.objects.Character;

public class Board extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private final int TARGET_FPS = 80;
	private final long OPTIMAL_TIME = 1000000000 / TARGET_FPS; // 16.67ms per frame
	private boolean gameRunning = false;
	private int lastFpsTime = 0;
	private int fps = 0;
	private int windowWidth = 0;
	private int windowHeight = 0;
	
	private int objectId = 0;
	private ArrayList<GameObject> objects = new ArrayList<GameObject>();
	private boolean left = false;
	private boolean right = false;
	private boolean up = false;
	private boolean down = false;
	private boolean space = false;
	private String direction = "right";
	
	// Animations
	private BufferedImage[] standing_bi = {Sprite.getSprite(0,0), Sprite.getSprite(1,0), Sprite.getSprite(2,0), Sprite.getSprite(3,0), Sprite.getSprite(4,0), Sprite.getSprite(5,0), Sprite.getSprite(6,0), Sprite.getSprite(7,0)};
	private BufferedImage[] walking_bi = {Sprite.getSprite(0,1), Sprite.getSprite(1,1), Sprite.getSprite(2,1), Sprite.getSprite(3,1), Sprite.getSprite(4,1), Sprite.getSprite(5,1)};
	
	private Animation standing = new Animation(standing_bi, 10);
	private Animation walking = new Animation(walking_bi, 10);
	
	private Animation animation = standing;
	
	public Board() {
		KeyListener listener = new KeyListener() {

			@Override
			public void keyPressed(KeyEvent k) {
				switch (k.getKeyCode()){ // I guess just use booleans
					case KeyEvent.VK_LEFT:
						left = true;
						break;
					case KeyEvent.VK_RIGHT:
						right = true;
						break;
					case KeyEvent.VK_UP:
						up = true;
						break;
					case KeyEvent.VK_DOWN:
						down = true;
						break;
					case KeyEvent.VK_SPACE:
						space = true;
						break;
				}
			}

			@Override
			public void keyReleased(KeyEvent k) {
				switch (k.getKeyCode()){
					case KeyEvent.VK_LEFT:
						left = false;
						break;
					case KeyEvent.VK_RIGHT:
						right = false;
						break;
					case KeyEvent.VK_UP:
						up = false;
						break;
					case KeyEvent.VK_DOWN:
						down = false;
						break;
					case KeyEvent.VK_SPACE:
						space = false;
						break;
				}
			}

			@Override
			public void keyTyped(KeyEvent k) {
//				System.out.println("KeyTyped:" + k.getKeyChar());		
			}
		};
		addKeyListener(listener);
		setFocusable(true);
	}
	
	public void runGameLoop() {
		if(!gameRunning){
			Thread loop = new Thread() {
				public void run(){
					gameLoop();
				}
			};
			gameRunning = true;
			loop.start();
		}
	}
	
	private void gameLoop() {
		long lastLoopTime = System.nanoTime();
		windowWidth = getWidth();
		windowHeight = getHeight();
		initBoard();
		animation.start();
		
		while(gameRunning){
			long now = System.nanoTime();
			long updateLength = now - lastLoopTime;
			lastLoopTime = now;
			double delta = updateLength / (double)OPTIMAL_TIME;
			
			lastFpsTime += updateLength;
			fps++;
			
			// Prints out number of game updates in a second
			if(lastFpsTime >= 1000000000) {
				System.out.println("FPS: " + fps);
//				System.out.println("Delta: " + delta);
				lastFpsTime = 0;
				fps = 0;
			}
			
			gameUpdate(delta);
			
			try{
				Thread.sleep( (lastLoopTime-System.nanoTime() + OPTIMAL_TIME)/1000000 );
			}catch(Exception e){};
		}
	}
	
	private void gameUpdate(double delta) {		
		//Check for new objects
		
		// Need to determine the new coordinates of each object, determine if they collide with something
		// and then have them move appropriately. So the move logic should be put into a new method that
		// just determines the newX and newY without actually setting them so they can be changed to prevent
		// overlapping objects (Hero lands on the floor).
		GameObject floor = objects.get(0);
		Character hero = (Character)objects.get(1);

		//Check for user inputs
		if(left){
			direction = "left";
			if(hero.isGrounded()){
				hero.setVX(hero.getVX() - 30);
			}else{
				hero.setVX(hero.getVX() - 10);
			}
		}
		if(right){
			direction = "right";
			if(hero.isGrounded()){
				hero.setVX(hero.getVX() + 30);
			}else{
				hero.setVX(hero.getVX() + 10);
			}
		}
		if(up && hero.isGrounded()){
			hero.setVY(hero.getVY() - 1000);
			hero.setGrounded(false);
		}
		if(down && !hero.isGrounded()){
			hero.setVY(hero.getVY() + 30); // Needs to go back to default on landing though.
		}
		
		if(hero.isGrounded() && (left || right) && animation == standing){
//			if(animation == standing){
				animation.stop();
				animation = walking;
				animation.reset();
				animation.start();
//			}
		}else if(hero.isGrounded() && !(left || right) && animation == walking){
			animation.stop();
			animation = standing;
			animation.reset();
			animation.start();
		}
		
		
//		for(GameObject go : objects){
		hero.getNewLocation(delta);
		//Collision detection
		if(!hero.isGrounded() && hero.collides(floor)){
			hero.setNewLocation(-1, floor.getY()-hero.getHeight());
			hero.setGrounded(true);
			hero.setVY(0);
		}
			//Remove obsolete objects
			
//		}
			//Move objects
		hero.move();
		animation.update();
		repaint();
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Character hero = (Character)objects.get(1);
		objects.get(0).draw(g);
		
		if(direction == "right"){
			g.drawImage(animation.getSprite(), (int)hero.getX(), (int)hero.getY(), hero.getWidth(), hero.getHeight()+11, null);
		}else{
			g.drawImage(animation.getSprite(), (int)hero.getX()+hero.getWidth(), (int)hero.getY(), -1*hero.getWidth(), hero.getHeight()+11, null);
		}
			
//		for(GameObject go : objects){
//			go.draw(g);
//		}
	}
	
	private void initBoard() {
		System.out.println(windowHeight);
		System.out.println(windowWidth);
		GameObject floor = new GameObject(objectId, 0, windowHeight-50, 50, windowWidth);
		objects.add(floor);
		objectId++;
		
		Character hero = new Character(objectId, 50, 50, 83, 80, 40, "Hero");
		objects.add(hero);
		objectId++;
		
		hero.setAY(2000);
	}

	
//	private void loadImage() {
//		ImageIcon ii = new ImageIcon("images/ball.png");
//		image = ii.getImage();
//	}
	
//	private void drawDonut(Graphics g) {
//		Graphics2D g2d = (Graphics2D) g;
//		
//		RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//		
//		rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
//		
//		g2d.setRenderingHints(rh);
//		
//		Dimension size = getSize();
//		double w = size.getWidth();
//		double h = size.getHeight();
//		
//		Ellipse2D e = new Ellipse2D.Double(0, 0, 80, 130);
//		g2d.setStroke(new BasicStroke(1));
//		g2d.setColor(Color.red);
//		
//		for(double  d = 0; d < 360; d += 5){
//			AffineTransform at = AffineTransform.getTranslateInstance(w/2, h/2);
//			at.rotate(Math.toRadians(d));
//			g2d.draw(at.createTransformedShape(e));
//		}
//	}
}
