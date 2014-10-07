package com.quickventure.objects;

public class Bullet extends GameObject{
	private int damage;
	private int shooterId;

	public Bullet(int id, double x, double y, int h, int w, int d, int sId){
		super(id,x,y,h,w);
		this.damage = d;
		this.shooterId = sId;
	}
	
	public int getDamage(){
		return this.damage;
	}
	public int getShooterId(){
		return this.shooterId;
	}
}
