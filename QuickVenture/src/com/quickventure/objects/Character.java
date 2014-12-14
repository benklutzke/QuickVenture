package com.quickventure.objects;

public class Character extends GameObject {
	private final int MAX_HEALTH = 40;
	
	private int health;
	private String name;
	private boolean isShooting;
	private int shootTimer;
	private int autoShots;
	private int moveDelay;
	private boolean autoFire;
	private boolean isHero;
	private double drawX;
	private double drawY;
	private int drawWidth;
	private int drawHeight;
	private boolean invincible;

	private int autoFireRate = 40; // Number of frames delayed between each shot.
	
	public Character(int id, double x, double y, int h, int w, int hp, String n){
		super(id,x,y,h,w);
		this.health = hp;
		this.name = n;
		this.isShooting = false;
		this.shootTimer = 0;
		this.autoShots = 0;
		this.autoFire = true;
		this.isHero = false;
		this.moveDelay = 0;
		this.drawX = x;
		this.drawY = y;
		this.drawWidth = w;
		this.drawHeight = h;
		this.invincible = false;
	}
	
	public void setHealth(int h){
		this.health = h;
	}
	public void setAutoFireMode(boolean b){
		this.autoFire = b;
	}
	
	public String getName(){
		return this.name;
	}
	public int getHealth(){
		return this.health;
	}
	
	public double getDrawX(){
		return this.drawX;
	}
	public double getDrawY(){
		return this.drawY;
	}
	public int getDrawWidth(){
		return this.drawWidth;
	}
	public int getDrawHeight(){
		return this.drawHeight;
	}
	
	public boolean shoot(boolean shoot) {
		// Shooting logic
		if(isHero && autoShots >= 10 && shootTimer < TARGET_FPS){ // One second delay after 10 autofire shots
			shootTimer++;
		}else{
			if(autoShots >= 10){  // Reset autofire
				autoShots = 0;
				shootTimer = 0;				
			}
			if(shoot && !isShooting){ // Simple shot by pressing 's' key
				isShooting = true;
				autoShots++;
				shootTimer = 0;
				return true;
			}else if(!shoot && isShooting){ // 's' key is released
				isShooting = false;
				autoFire = false;
				autoShots = 0;
			}else if(shoot && isShooting){ // 's' key is held down 
				// Auto fire handler
				if(autoFire && shootTimer < autoFireRate){ // Waits for n frames
					shootTimer++;
				}else if(autoFire){ // Shoots in autofire mode
					autoShots++;
					shootTimer = 0;
					return true;
				}else if(shootTimer < TARGET_FPS){ // wait for 's' key to be held down for one second
					shootTimer++;
				}else{ // Begin autofire shooting
					shootTimer = 0;
					autoFire = true;
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean chase(){
		if(moveDelay < TARGET_FPS / 2){ // Waits half a second before chasing
			moveDelay++;
			return false;
		}else{
			return shoot(true);
		}
	}
	
	public void setInvincible(boolean b){
		this.invincible = b;
	}
	
	public void resetShots(){
		this.isShooting = false;
		this.shootTimer = 0;
		this.autoShots = 0;
		this.autoFire = true;
		this.moveDelay = 0;
	}
	
	public int takeDamage(int d){
		if(this.invincible){
			return this.health;
		}
		
		this.health -= d;
		if(this.health > this.MAX_HEALTH)
			this.health = this.MAX_HEALTH;
		
		return this.health;
	}
	
	public int checkCollision(GameObject o){
		// 0 -> does not collide
		// 1 -> feet collide
		// 2 -> head collides
		// 3 -> right collides
		// 4 -> left collides
		
		if(this.ny + this.height >= o.getY() && this.ny <= o.getY() + o.getHeight() &&
		   this.nx + this.width  >= o.getX() && this.nx <= o.getX() + o.getWidth()){
			// Collides
			
			if(this.y + this.height < o.getY()){
				// Previous y was higher -> fell on object
				return 1;
			}else if(this.y > o.getY() + o.getHeight()){
				// Previous y was lower -> hit head
				return 2;
			}else if(this.x + this.width < o.getX()){
				// Previous x was lower -> right side collides
				return 3;
			}else if(this.x > o.getX() + o.getWidth()){
				// Previous x was higher -> left side collides
				return 4;
			}else{
				// Shouldn't be there... just fall through
				return 5;
			}
			
		}else{
			return 0;
		}		
	}
	
	public boolean isOn(GameObject o){
		if(this.x + this.width >= o.getX() && this.x < o.getX() + o.getHeight()){
			double v = o.getY() - (this.y + this.height);
			if(v <= 1 && v >= -1){
				return true;
			}
		}
		return false;		
	}

	public void setHero(){
		this.isHero = true;
		this.autoFireRate /= 2;
		this.x += 20;
		this.y += 20;
		this.width -= 40;
		this.height -= 20;
	}
	
	public void move(){
		if(this.isHero){
			this.drawX = this.nx - 20;
			this.drawY = this.ny - 20;
		}
		this.x = this.nx;
		this.y = this.ny;
	}
}
