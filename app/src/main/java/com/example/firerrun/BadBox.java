package com.example.firerrun;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

public class BadBox {
    private float x, y;
    private int width, height;
    private Bitmap image;

    public BadBox(float x, float y, Bitmap image) {
        this.x = x;
        this.y = y;
        this.image = image;
        this.width = 150;
        this.height = 150;

        this.image = Bitmap.createScaledBitmap(image, width, height, false);
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(image, x, y, null);
    }

    public boolean checkCollision(Player player) {
        Rect playerRect = new Rect((int)player.getX(), (int)player.getY(),
                (int)(player.getX() + player.getWidth()), (int)(player.getY() + player.getHeight()));

        Rect badBoxRect = new Rect((int)x, (int)y, (int)(x + width), (int)(y + height));

        return Rect.intersects(playerRect, badBoxRect);
    }


    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void update() {
    }
}
