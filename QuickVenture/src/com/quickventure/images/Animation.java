package com.quickventure.images;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Animation {

    private int frameCount;
    private int frameDelay;
    private int currentFrame;
    private int totalFrames;

    private boolean stopped;

    private ArrayList<Frame> frames = new ArrayList<Frame>();    // Arraylist of frames 
    
    private Frame frameOverride;
    private boolean overrideFrame;
    private boolean update;				// Only override frame once per frame time

    public Animation(BufferedImage[] images, int frameDelay) {
        for(int i = 0; i < images.length; i++) {
        	this.frames.add(new Frame(images[i], frameDelay));
        }
        this.frameCount = 0;
        this.frameDelay = frameDelay;
        this.currentFrame = 0;
        this.totalFrames = this.frames.size();
        this.stopped = true;        
        this.overrideFrame = false;
        this.update = true;
    }

    public void start() {
        stopped = false;
    }

    public void stop() {
        stopped = true;
    }

    public void reset() {
        this.stopped = true;
        this.frameCount = 0;
        this.currentFrame = 0;
    }

    public BufferedImage getSprite() {
    	if(this.overrideFrame){
    		return this.frameOverride.getFrame();
    	}else{
    		return frames.get(currentFrame).getFrame();
    	}
    }
    
    public Frame getFrameAtIndex(int i){
    	return frames.get(i);
    }
    
    public int getCurrentFrame() {
    	return this.currentFrame;
    }

    public void update() {
        if (!stopped) {
            frameCount++;
            if (frameCount > frameDelay) {
                frameCount = 0;
            
                currentFrame++;
                if (currentFrame > totalFrames - 1) {
                    currentFrame = 0;
                }
            }
            update = true;
        }

    }
    
    public void updateOverride() {
    	boolean temp = this.stopped;
    	this.stopped = false;
    	update();
    	this.stopped = temp;
    }
    
    public void setOverrideFrame(boolean b) {
    	this.overrideFrame = b;
    }
    
    public boolean getOverrideFrame() {
    	return this.overrideFrame;
    }
    
    public void setFrameOverride(Frame f) {
    	this.frameOverride = f;
    	this.update = false;
    }
    
    public boolean getUpdate() {
    	return this.update;
    }
}