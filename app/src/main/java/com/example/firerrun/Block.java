package com.example.firerrun;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.content.Context;
import android.graphics.Rect;

public class Block {
    private float x;
    private float y;  // Changed from static to instance variable
    private float width;
    private float height;
    private Bitmap image;

    // Constructor with image
    public Block(float x, float y, float width, float height, int imageResource, Context context) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.image = BitmapFactory.decodeResource(context.getResources(), imageResource);
        this.image = Bitmap.createScaledBitmap(this.image, (int) width, (int) height, false);
    }

    // Constructor without image
    public Block(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.image = null; // No image provided, don't render image
    }

    // Draw the block
    public void draw(Canvas canvas) {
        if (image != null) {
            canvas.drawBitmap(image, x, y, null);
        } else {
            // Draw a rectangle if there's no image
            canvas.drawRect(x, y, x + width, y + height, null);
        }
    }

    // Collision detection with player
    public boolean checkCollisionBlockPlayer(Player player) {
        Rect playerRect = new Rect((int) player.getX(), (int) player.getY(),
                (int) (player.getX() + player.getWidth()), (int) (player.getY() + player.getHeight()));

        Rect blockRect = new Rect((int) x, (int) y, (int) (x + width), (int) (y + height));

        return Rect.intersects(playerRect, blockRect);
    }

    // Getters
    public float getX() {
        return x;
    }

    public float getY() {  // Made non-static
        return y;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    // Check if the player is standing on the block
    public boolean isOnBlock(Player player) {
        // Check if player is within the block's x and y range and is standing on top
        return player.getX() + player.getWidth() > x && player.getX() < x + width &&
                player.getY() + player.getHeight() >= y && player.getY() + player.getHeight() <= y + height;
    }
}
