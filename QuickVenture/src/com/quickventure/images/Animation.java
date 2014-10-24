package com.quickventure.images;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Animation {

    private int frameCount;
    private int frameDelay;
    private int currentFrame;
    private int animationDirection;
    private int totalFrames;

    private boolean stopped;

    private List<Frame> frames = new ArrayList<Frame>();    // Arraylist of frames 
    
    private Frame frameOverride;
    private boolean overrideFrame;
    private boolean update;				// Only override frame once per frame time

    public Animation(BufferedImage[] frames, int frameDelay) {
        this.frameDelay = frameDelay;
        this.stopped = true;

        for (int i = 0; i < frames.length; i++) {
            addFrame(frames[i], frameDelay);
        }

        this.frameCount = 0;
        this.frameDelay = frameDelay;
        this.currentFrame = 0;
        this.animationDirection = 1;
        this.totalFrames = this.frames.size();
        
        this.overrideFrame = false;
        this.update = true;
    }

    public void start() {
        if (!stopped) {
            return;
        }

        if (frames.size() == 0) {
            return;
        }

        stopped = false;
    }

    public void stop() {
        if (frames.size() == 0) {
            return;
        }

        stopped = true;
    }

    public void restart() {
        if (frames.size() == 0) {
            return;
        }

        stopped = false;
        currentFrame = 0;
    }

    public void reset() {
        this.stopped = true;
        this.frameCount = 0;
        this.currentFrame = 0;
    }

    private void addFrame(BufferedImage frame, int duration) {
        if (duration <= 0) {
            System.err.println("Invalid duration: " + duration);
            throw new RuntimeException("Invalid duration: " + duration);
        }

        frames.add(new Frame(frame, duration));
        currentFrame = 0;
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
                currentFrame += animationDirection;

                if (currentFrame > totalFrames - 1) {
                    currentFrame = 0;
                }
                else if (currentFrame < 0) {
                    currentFrame = totalFrames - 1;
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