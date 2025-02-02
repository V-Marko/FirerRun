
package com.example.firerrun;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

public class BadBox {
    public float x;
    private float y;
    private float width;
    private float height;
    private Bitmap image;
    private boolean isAlive;

    public BadBox(float x, float y, float width, float height, Bitmap image) {
        this.x = x;
        this.y = y;
        this.image = image;
        this.width = width;
        this.height = height;
        this.isAlive = true;

        this.image = Bitmap.createScaledBitmap(image, (int) width, (int) height, false);
    }

    public void draw(Canvas canvas) {
        if (isAlive) {
            canvas.drawBitmap(image, x, y, null);
        }
    }

    public boolean checkCollisionPlayer(Player player) {
        if (!isAlive) return false;
        Rect playerRect = new Rect((int)player.getX(), (int)player.getY(),
                (int)(player.getX() + player.getWidth()), (int)(player.getY() + player.getHeight()));

        Rect badBoxRect = new Rect((int)x, (int)y, (int)(x + width), (int)(y + height));

        return Rect.intersects(playerRect, badBoxRect);
    }

    public boolean checkCollisionBullet(Bullet bullet) {
        if (!isAlive) return false;

        Rect bulletRect = new Rect((int)bullet.getX(), (int)bullet.getY(),
                (int)(bullet.getX() + bullet.getWidth()), (int)(bullet.getY() + bullet.getHeight()));

        Rect badBoxRect = new Rect((int)x, (int)y, (int)(x + width), (int)(y + height));

        return Rect.intersects(bulletRect, badBoxRect);
    }

    public void die() {
        isAlive = false;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void update() {
    }
}
