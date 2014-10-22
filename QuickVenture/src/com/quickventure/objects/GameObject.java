package com.quickventure.objects;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

public class GameObject {

	private final double TARGET_FPS = 80.0;
	private final double OPTIMAL_TIME = 1 / TARGET_FPS; // 16.67ms per frame
	private final double MAX_SPEED = 400;
	
	private int id;
	private double x;		// Current x coordinate
	private double y;		// Current y coordinate
	private double nx;		// Next Frame x coordinate
	private double ny;		// Next Frame y coordinate
	private Point newLoc;
	private int height;
	private int width;
	private double vx;		// Velocity x component
	private double vy;		// Velocity y component
	private double ax;		// Acceleration x component
	private double ay;		// Acceleration y component
	private boolean grounded;
	private Color color;
//	private boolean yPGround;
//	private boolean yNGround;
//	private boolean xPGround;
//	private boolean xNGround;
	
	public GameObject(int id, double x, double y, int h, int w){
		this.id = id;
		this.x = x;
		this.y = y;
		this.nx = x;
		this.ny = y;
		this.newLoc = null;
		this.height = h;
		this.width = w;
		this.vx = 0;
		this.vy = 0;
		this.ax = 0;
		this.ay = 0;
		this.color = Color.red;
	}
	
	public void setX(double x){
		this.x = x;
	}
	public void setY(double y){
		this.y = y;
	}
	public void setHeight(int h){
		this.height = h;
	}
	public void setWidth(int w){
		this.width = w;
	}
	public void setVX(double v){
		this.vx = v;
	}
	public void setVY(double v){
		this.vy = v;
	}
	public void setAX(double a){
		this.ax = a;
	}
	public void setAY(double a){
		this.ay = a;
	}
	public void setGrounded(boolean b){
		this.grounded = b;
	}
	public void setColor(Color c){
		this.color = c;
	}
	
	public int getId(){
		return this.id;
	}
	public double getX(){
		return this.x;
	}
	public double getY(){
		return this.y;
	}
	public int getHeight(){
		return this.height;
	}
	public int getWidth(){
		return this.width;
	}
	public double getVX(){
		return this.vx;
	}
	public double getVY(){
		return this.vy;
	}
	public double getAX(){
		return this.ax;
	}
	public double getAY(){
		return this.ay;
	}
	public boolean isGrounded(){
		return this.grounded;
	}
	
	public void draw(Graphics g){
		Color co = g.getColor();
		g.setColor(this.color);
		g.fillRect((int)this.x, (int)this.y, this.width, this.height);
		g.setColor(co);
	}
	public void getNewLocation(double delta){
		/*
		 * Delta is the percentage of 16.67ms that has occurred for this frame update
		 */
		double dt = delta * this.OPTIMAL_TIME;

//		System.out.println(this.y + ", " + this.vy + ", " + dt + ", " + this.ay + ", " + Math.pow(dt, 2));

		if(this.grounded){
			this.vy = 0;
			if(this.vx > 0){
				this.ax = -1000;
				if(this.vx + this.ax * dt < 0){
					this.vx = 0;
				}else{
					this.vx += this.ax * dt;
				}
			}else if(this.vx < 0){
				this.ax = 1000;
				if(this.vx + this.ax * dt > 0){
					this.vx = 0;
				}else{
					this.vx += this.ax * dt;
				}
			}else{
				this.ax = 0;
				this.vx = 0;
			}
		}else{
			this.vy += this.ay * dt;
		}
		
		if(this.vx > this.MAX_SPEED){
			this.vx = this.MAX_SPEED;
		}else if(this.vx < -1*this.MAX_SPEED){
			this.vx = -1*this.MAX_SPEED;
		}
		
		this.nx = this.x + this.vx * dt;// + 0.5 * this.ax * Math.pow(dt, 2); // xf = vo*(t*delta) + 1/2*a*(t*delta)^2
		this.ny = this.y + this.vy * dt;// + 0.5 * this.ay * Math.pow(dt, 2);
		
	}
	public void setNewLocation(double x, double y){
		if(x != -1) this.nx = x;
		if(y != -1) this.ny = y;
	}
	
	public void move(){
		this.x = this.nx;
		this.y = this.ny;
	}
	
	public boolean collides(GameObject go){
		return (go.getY()+go.getHeight() >= this.ny && go.getY() <= this.ny+this.height &&
				go.getX()+go.getWidth() >= this.nx && go.getX() <= this.nx+this.width);
	}
}
