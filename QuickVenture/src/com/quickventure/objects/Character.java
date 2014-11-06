package com.quickventure.objects;

public class Character extends GameObject {
	private final int MAX_HEALTH = 40;
	
	private int health;
	private String name;
	private boolean isShooting;
	private int shootTimer;
	private int shootDelay;
	private int autoShots;
	private int moveDelay;
	private boolean autoFire;
	private boolean isHero;

	private int autoFireRate = 40; // Number of frames delayed between each shot.
	
	public Character(int id, double x, double y, int h, int w, int hp, String n){
		super(id,x,y,h,w);
		this.health = hp;
		this.name = n;
		this.isShooting = false;
		this.shootTimer = 0;
		this.shootDelay = 0;
		this.autoShots = 0;
		this.autoFire = true;
		this.isHero = false;
		this.moveDelay = 0;
	}
	
	public void setHealth(int h){
		this.health = h;
	}
	public void setAutoFireMode(boolean b){
		this.autoFire = b;
	}
	public void setHero(){
		this.isHero = true;
		this.autoFireRate /= 2;
	}
	
	public String getName(){
		return this.name;
	}
	public int getHealth(){
		return this.health;
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
	
	public void resetShots(){
		this.isShooting = false;
		this.shootTimer = 0;
		this.shootDelay = 0;
		this.autoShots = 0;
		this.autoFire = true;
		this.moveDelay = 0;
	}
	
	public int takeDamage(int d){
		this.health -= d;
		if(this.health > this.MAX_HEALTH)
			this.health = this.MAX_HEALTH;
		
		return this.health;
	}
}
