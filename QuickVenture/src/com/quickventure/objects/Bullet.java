package com.quickventure.objects;

public class Bullet extends GameObject{
	private int damage;
	private int shooterId;
	private int duration;
	private double distanceTraveled;

	public Bullet(int id, double x, double y, int h, int w, int d, int sId, int dur){
		super(id,x,y,h,w);
		this.damage = d;
		this.shooterId = sId;
		this.duration = dur;
		this.distanceTraveled = 0;
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
		super.move();
	}
	
	public boolean destroy(){
		return (this.distanceTraveled > this.duration);
	}
}
