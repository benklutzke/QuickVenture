package com.quickventure.objects;

public class Item extends GameObject {
	private String effect;
	
	public Item(int id, double x, double y, int h, int w, String e){
		super(id,x,y,h,w);
		this.effect = e;
	}
	
	public String getEffect(){
		return this.effect;
	}
}
