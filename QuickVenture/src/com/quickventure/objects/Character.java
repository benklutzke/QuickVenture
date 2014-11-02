package com.quickventure.objects;

public class Character extends GameObject {
	private int health;
	private String name;
	private boolean isShooting;
	private int shootTimer;
	private int shootDelay;
	
	public Character(int id, double x, double y, int h, int w, int hp, String n){
		super(id,x,y,h,w);
		this.health = hp;
		this.name = n;
		this.isShooting = false;
		this.shootTimer = 0;
		this.shootDelay = 10;
	}
	
	public void setHealth(int h){
		this.health = h;
	}
	public void setIsShooting(boolean b){
		this.isShooting = b;
	}
	public void setShootDelay(int i){
		this.shootDelay = i;
	}
	
	public String getName(){
		return this.name;
	}
	public int getHealth(){
		return this.health;
	}
	public boolean getIsShooting(){
		return this.isShooting;
	}
	public int getShootTimer(){
		return this.shootTimer;
	}
	public void resetShootTimer(){
		this.shootTimer = 0;
	}
	public void incShootTimer(){
		this.shootTimer++;
	}
	public boolean shouldShoot(){
		if(this.shootTimer >= this.shootDelay){
			resetShootTimer();
			return true;
		}
		return false;
	}
	
	public int takeDamage(int d){
		this.health -= d;
		return this.health;
	}
}
