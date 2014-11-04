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
	private int camX = 0;
	private int camY = 0;
	
	private int objectId = 0;
	private ArrayList<GameObject> grounds = new ArrayList<GameObject>();
	private ArrayList<Character> creatures = new ArrayList<Character>();
	private Character hero = null;
	private ArrayList<Bullet> bullets = new ArrayList<Bullet>();
	private boolean left = false;
	private boolean right = false;
	private boolean up = false;
	private boolean down = false;
	private boolean shoot = false;
	private String direction = "right";
	private int score = 0;
	private int hpTimer = 0;
		
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
		camX = windowWidth / 2;
		camY = windowWidth / 2;
		initBoard();
		animation.start();
		
		while(gameRunning){
			long now = System.nanoTime();
			long updateLength = now - lastLoopTime;
			lastLoopTime = now;
			double delta = updateLength / (double)OPTIMAL_TIME;
			
//			lastFpsTime += updateLength;
//			fps++;
//			// Prints out number of game updates in a second
//			if(lastFpsTime >= 1000000000) {
//				System.out.println("FPS: " + fps + ", camX: " + camX);
//				System.out.println("Delta: " + delta);
//				lastFpsTime = 0;
//				fps = 0;
//			}
			
			gameUpdate(delta);
			
			try{
				Thread.sleep( (lastLoopTime-System.nanoTime() + OPTIMAL_TIME)/1000000 );
			}catch(Exception e){};
		}
	}
	
	private void gameUpdate(double delta) {
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
		if(hero.shoot(shoot)){
			heroShoot();
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
		
		// Restore 5 hp/sec while standing still
		if(animation == standing){
			if(hpTimer >= TARGET_FPS){
				hero.takeDamage(-5);
				hpTimer = 0;
			}else{
				hpTimer++;
			}
		}else{
			hpTimer = 0;
		}
		
		// Creature logic
		for(Character c : creatures){
			if(Math.abs(hero.getX() - c.getX()) <= windowWidth/2 + 150){ // Mob is close enough to see hero
				int dir;
				if(hero.getX() < c.getX()){ // Hero is to the left of mob
					c.setVX(c.getVX() - 10);
					dir = -1;
				}else{
					c.setVX(c.getVX() + 10);
					dir = 1;
				}
				if(c.shoot(true)){
					mobShoot(c, dir);
				}
			}else{
				c.setVX(0);
				c.resetShots();
			}
		}
		
		//Update objects		
		hero.getNewLocation(delta);
		//Ground collision detection
		for(GameObject o : grounds){
			if(!hero.isGrounded() && hero.collides(o)){
				hero.setNewLocation(-1, o.getY()-hero.getHeight());
				hero.setGrounded(true);
				hero.setVY(0);
			}
		}
		hero.move();
		
		Iterator<Character> ic = creatures.iterator();
		while(ic.hasNext()){
			Character c = ic.next();
			
			if(c.getHealth() <= 0){
				ic.remove();
				score += 5;
				hero.takeDamage(-10);
			}else{
				c.getNewLocation(delta);
				c.move();
			}
		}
		
		Iterator<Bullet> i = bullets.iterator();
		while(i.hasNext()){
			Bullet b = i.next();
			b.getNewLocation(delta);
			if(b.getShooterId() == hero.getId()){
				b.checkCollisions(creatures);
			}else{
				if(b.collides(hero)){
					hero.takeDamage(b.getDamage());
					b.setDestroy(true);
				}
			}
			if(b.destroy()){
				i.remove();
			}else{
				b.move();
			}
		}
		
		
		// Camera movement logic
		int heroX = (int)hero.getX() + hero.getWidth()/2;
		if(heroX > camX + 50){
			camX = heroX - 50;
		}else if(heroX < camX - 50){
			camX = heroX + 50;
		}
		
		// Prepare next character animation
		animation.update();
		
		repaint();
	}
	
	public void heroShoot() { 
		Bullet shot;
		if(direction == "right"){
			shot = new Bullet(objectId, hero.getX() + hero.getWidth() - 5, hero.getY() + 33, 10, 10, 5, hero.getId(), 600);
			shot.setVX(700);
		}else{
			shot = new Bullet(objectId, hero.getX(), hero.getY() + 33, 10, 10, 5, hero.getId(), 600);
			shot.setVX(-700);
		}
			
		shot.setColor(Color.black);
		
		bullets.add(shot);
		objectId++;
	}
	
	public void mobShoot(Character mob, int dir) {
		Bullet shot;
		if(dir == 1){
			shot = new Bullet(objectId, mob.getX() + mob.getWidth(), mob.getY() + 10, 10, 10, 5, mob.getId(), 400);
			shot.setVX(300);
		}else{
			shot = new Bullet(objectId, mob.getX(), mob.getY() + 10, 10, 10, 5, mob.getId(), 400);
			shot.setVX(-300);
		}
			
		shot.setColor(Color.red);
		
		bullets.add(shot);
		objectId++;
	}
	
	@Override
	public void paintComponent(Graphics g) {
		int camXOffset = camX - windowWidth/2;
		
		super.paintComponent(g);
		
		// Draws hero
		if(direction == "right"){
			g.drawImage(animation.getSprite(), (int)hero.getX() - camXOffset, (int)hero.getY(), hero.getWidth(), hero.getHeight()+11, null);
		}else{
			g.drawImage(animation.getSprite(), (int)hero.getX()+hero.getWidth() - camXOffset, (int)hero.getY(), -1*hero.getWidth(), hero.getHeight()+11, null);
		}
		
		// Draws other stuff
		for(Bullet b : bullets){
			b.draw(g, camXOffset);
		}
		for(GameObject o : grounds){
			o.draw(g, camXOffset);
		}
		for(Character c : creatures){
			c.draw(g, camXOffset);
		}
		
		g.drawString("Health: " + hero.getHealth(), windowWidth/8, windowHeight/8);
		g.drawString("Score: " + score, windowWidth*7/8-50, windowHeight/8);
	}
	
	// Creates world and the objects
	private void initBoard() {
//		System.out.println(windowHeight);
//		System.out.println(windowWidth);
		GameObject floor = new GameObject(objectId, 0-windowWidth/2, windowHeight-50, 50, windowWidth*5);
		floor.setImage("ground.png");
		grounds.add(floor);
		objectId++;
		
		hero = new Character(objectId, 50, 50, 83, 80, 20, "Hero");
		hero.setX(camX - hero.getWidth()/2);
		hero.setAY(2000);
		hero.setHero();
		hero.setAutoFireMode(false);
		objectId++;
		
		// Generates mobs every windowWidth on main ground
		for(int i = (int)hero.getX() + windowWidth; i < floor.getWidth(); i += windowWidth){
			Character c = new Character(objectId, i, floor.getY() - 62, 74, 46, 20, "Mob");
			c.setImage("troll.png");
			c.setMaxSpeed(70);
			
			creatures.add(c);
			objectId++;
		}
		
	}
}
