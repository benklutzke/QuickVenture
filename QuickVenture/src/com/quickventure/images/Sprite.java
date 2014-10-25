package com.quickventure.images;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Sprite {

    private static BufferedImage spriteSheet;
    private static final int TILE_SIZE_X = 80;
    private static final int TILE_SIZE_Y = 94;

    public static BufferedImage loadSprite(String file) {

        BufferedImage sprite = null;

        try {
            sprite = ImageIO.read(new File("images/" + file + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return sprite;
    }

    public static BufferedImage getSprite(int xGrid, int yGrid) {

        if (spriteSheet == null) {
            spriteSheet = loadSprite("hero_spritesheet");
        }
        
        // Things aren't even in the sprite sheet so offsets are needed based on sprite location
        int xOffset = 0;
        int yOffset = 0;
        
        if(yGrid == 1){
        	yOffset = 3;
        }else if(xGrid == 0 && yGrid == 2){
        	yOffset = 2;
        }else if(xGrid == 0 && yGrid == 3){
        	yOffset = -2;
        }

        return spriteSheet.getSubimage(xGrid * TILE_SIZE_X + xOffset, yGrid * TILE_SIZE_Y + yOffset, TILE_SIZE_X, TILE_SIZE_Y);
    }

}