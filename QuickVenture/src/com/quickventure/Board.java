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
import java.util.Iterator;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import com.quickventure.images.Animation;
import com.quickventure.images.Sprite;
import com.quickventure.objects.Bullet;
import com.quickventure.objects.GameObject;
import com.quickventure.objects.Character;

public class Board extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private final int TARGET_FPS = 80;
	private final long OPTIMAL_TIME = 1000000000 / TARGET_FPS;
	private boolean gameRunning = false;
	private int lastFpsTime = 0;
	private int fps = 0;
	private int windowWidth = 0;
	private int windowHeight = 0;
	
	private int objectId = 0;
	private ArrayList<GameObject> objects = new ArrayList<GameObject>(); // Make into charactor and ground objects
	private ArrayList<Bullet> bullets = new ArrayList<Bullet>();
	private boolean left = false;
	private boolean right = false;
	private boolean up = false;
	private boolean down = false;
	private boolean shoot = false;
	private String direction = "right";
	
	private boolean isShooting = false;
	private boolean autoFire = false;
	private int shootTimer = 0;
	private int autoShots = 0;
	
	// Animations
	private BufferedImage[] standing_bi = {Sprite.getSprite(0,0), Sprite.getSprite(1,0), Sprite.getSprite(2,0), Sprite.getSprite(3,0), Sprite.getSprite(4,0), Sprite.getSprite(5,0), Sprite.getSprite(6,0), Sprite.getSprite(7,0)};
	private BufferedImage[] walking_bi = {Sprite.getSprite(0,1), Sprite.getSprite(1,1), Sprite.getSprite(2,1), Sprite.getSprite(3,1), Sprite.getSprite(4,1), Sprite.getSprite(5,1)};
	private BufferedImage[] jumping_bi = {Sprite.getSprite(1,3), Sprite.getSprite(2,3)};
	private BufferedImage[] shooting_bi = {Sprite.getSprite(0,2), Sprite.getSprite(1,2), Sprite.getSprite(2,2), Sprite.getSprite(3,2), Sprite.getSprite(4,2), Sprite.getSprite(5,2), Sprite.getSprite(0,3)};
	
	private Animation standing = new Animation(standing_bi, 10);
	private Animation walking = new Animation(walking_bi, 10);
	private Animation jumping = new Animation(jumping_bi, 1);
	private Animation shooting = new Animation(shooting_bi, 10);
	
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
					case KeyEvent.VK_S:
						shoot = true;
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
					case KeyEvent.VK_S:
						shoot = false;
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
			hero.setVY(hero.getVY() - 600); // Jump
			hero.setGrounded(false);
		}
		if(down && !hero.isGrounded()){
			hero.setVY(hero.getVY() + 30); // Speeds up fall
		}	
		
		// Shooting logic
		if(autoShots >= 10 && shootTimer < TARGET_FPS){ // One second delay after 10 autofire shots
			shootTimer++;
		}else{
			if(autoShots >= 10){  // Reset autofire
				autoShots = 0;
				shootTimer = 0;				
			}
			if(shoot && !isShooting){ // Simple shot by pressing 's' key
				isShooting = true;
				autoFire = false;
				shoot();
				shootTimer = 0;
			}else if(!shoot && isShooting){ // 's' key is released
				isShooting = false;
				System.out.println("Not Shooting");
			}else if(isShooting){ // 's' key is held down 
				// Auto fire handler
				if(autoFire && shootTimer < 5){ // Waits for five frames
					shootTimer++;
				}else if(autoFire){ // Shoots in autofire mode
					shoot();
					autoShots++;
					shootTimer = 0;
				}else if(shootTimer < TARGET_FPS){ // wait for 's' key to be held down for one second
					shootTimer++;
				}else{ // Begin autofire shooting
					shoot();
					shootTimer = 0;
					autoFire = true;
					autoShots = 0;
				}
			}
		}
		
		// Animation changes
		if(hero.isGrounded() && (left || right) && animation != walking){
			animation.stop();
			animation = walking;
			animation.reset();
			animation.start();
		}else if(hero.isGrounded() && !(left || right) && animation != standing){
			animation.stop();
			animation = standing;
			animation.reset();
			animation.start();
		}else if(!hero.isGrounded()){
			if(animation != jumping){
				animation.stop();
				animation = jumping;
				animation.reset();
			}
			
			if(hero.getVY() > 0 && animation.getCurrentFrame() == 0){
				animation.updateOverride();
			}
		}
		
		if(shoot && hero.isGrounded() && animation == walking && animation.getUpdate()){
			animation.setOverrideFrame(true);
			animation.setFrameOverride(shooting.getFrameAtIndex(animation.getCurrentFrame()));
		}else if(shoot && hero.isGrounded() && animation == standing && animation.getUpdate()){
			animation.setOverrideFrame(true);
			animation.setFrameOverride(shooting.getFrameAtIndex(6));
		}else if(!shoot){
			standing.setOverrideFrame(false);
			walking.setOverrideFrame(false);
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
		
		//Move objects
		Iterator<Bullet> i = bullets.iterator();
		while(i.hasNext()){
			Bullet b = i.next();
			b.getNewLocation(delta);
			b.move();
			if(b.destroy()){
				i.remove();
			}
		}
		
		hero.move();
		animation.update();
		repaint();
	}
	
	public void shoot() { 
		Character hero = (Character)objects.get(1);
		Bullet shot;
		if(direction == "right"){
			shot = new Bullet(objectId, hero.getX() + hero.getWidth(), hero.getY() + 33, 10, 10, 5, hero.getId(), 600);
			shot.setVX(700);
		}else{
			shot = new Bullet(objectId, hero.getX(), hero.getY() + 33, 10, 10, 5, hero.getId(), 600);
			shot.setVX(-700);
		}
			
		shot.setColor(Color.black);
		
		bullets.add(shot);
		objectId++;
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
		
		for(Bullet b : bullets){
			b.draw(g);
		}
			
//		for(GameObject go : objects){
//			go.draw(g);
//		}
	}
	
	private void initBoard() {
		System.out.println(windowHeight);
		System.out.println(windowWidth);
		GameObject floor = new GameObject(objectId, 0, windowHeight-50, 50, windowWidth);
		floor.setColor(Color.green);
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
