package com.quickventure.objects;

import java.util.Timer;
import java.util.TimerTask;

public class Item extends GameObject {
	private String effect;
	
	public Item(int id, double x, double y, int h, int w, String e){
		super(id,x,y,h,w);
		this.effect = e;
	}
	
	public String getEffect(){
		return this.effect;
	}
	
	public void effect(final Character c){
		if(this.effect == "heal"){
			c.takeDamage(-5);
		}else if(this.effect == "star"){
			c.setInvincible(true);
			new Timer().schedule(new TimerTask() {          
			    @Override
			    public void run() {
			        c.setInvincible(false);    
			    }
			}, 1000);
		}
	}
}
