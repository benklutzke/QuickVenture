package com.quickventure.images;

import java.awt.image.BufferedImage;

public class Frame {

    private BufferedImage frame;
    private int duration;

    public Frame(BufferedImage frame, int duration) {
        this.frame = frame;
        this.duration = duration;
    }

    public BufferedImage getFrame() {
        return frame;
    }
    public int getDuration() {
        return duration;
    }
    
    public void setFrame(BufferedImage frame) {
        this.frame = frame;
    }
    public void setDuration(int duration) {
        this.duration = duration;
    }

}