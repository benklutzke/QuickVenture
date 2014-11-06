package com.quickventure.objects;

import java.util.ArrayList;

public class Bullet extends GameObject{
	private int damage;
	private int shooterId;
	private int duration;
	private double distanceTraveled;
	private boolean destroy;

	public Bullet(int id, double x, double y, int h, int w, int d, int sId, int dur){
		super(id,x,y,h,w);
		this.damage = d;
		this.shooterId = sId;
		this.duration = dur;
		this.distanceTraveled = 0;
		this.destroy = false;
		setLimited(false);
	}
	
	public int getDamage(){
		return this.damage;
	}
	public int getShooterId(){
		return this.shooterId;
	}
	
	public void move(){
		double dx = super.getNX() - super.getX();
		double dy = super.getNY() - super.getY();
		
		this.distanceTraveled += Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
		if(this.distanceTraveled > this.duration){
			this.destroy = true;
		}
		super.move();
	}
	
	public boolean destroy(){
		return this.destroy;
	}
	public void setDestroy(boolean b){
		this.destroy = b;
	}
	
	public boolean checkCollisions(ArrayList<Character> creatures){
		for(Character c : creatures){
			if(collides(c)){
				c.takeDamage(this.damage);
				this.destroy = true;
				return true;
			}
		}
		return false;
	}
}
