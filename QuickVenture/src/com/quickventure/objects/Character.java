package com.quickventure.objects;

public class Character extends GameObject {
	private int health;
	private String name;
	
	public Character(int id, double x, double y, int h, int w, int hp, String n){
		super(id,x,y,h,w);
		this.health = hp;
		this.name = n;
	}
	
	public void setHealth(int h){
		this.health = h;
	}
	
	public String getName(){
		return this.name;
	}
	public int getHealth(){
		return this.health;
	}
	
	public int takeDamage(int d){
		this.health -= d;
		return this.health;
	}
}
