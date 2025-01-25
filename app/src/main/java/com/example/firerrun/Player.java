package com.example.firerrun;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Player {

    private float x, y;
    private float speed = 10f;
    private boolean movingLeft, movingRight, jumping;
    private boolean isIdle;
    private float jumpSpeed = 15f;
    private float gravity = 1f;
    private Bitmap bodyImage, headImage, gunImage, bulletImage;
    private Context context;

    public static int width = 150;
    public int height = 150;
    public int headWidth = 150;
    public int headHeight = 150;
    public int gunWidth = 150;
    public int gunHeight = 90;
//    public int bulletWidth = bulletImage.getWidth();

    private long lastIdleTime = 0;
    private static final long ANIMATION_DELAY = 300;
    private List<Block> blocks;
    Block block;
    public float LandRestriction =block.getY() - height;
    private List<Bullet> bullets;

    private int person_stop_int = 0;

    public Player(Context context) {
        this.context = context;
        this.x = 100;
        this.y = 100;

        bodyImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.person_walk_1);
        bodyImage = Bitmap.createScaledBitmap(bodyImage, width, height, false);

        headImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.person_head);
        headImage = Bitmap.createScaledBitmap(headImage, headWidth, headHeight, false);

        gunImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.person_gun);
        gunImage = Bitmap.createScaledBitmap(gunImage, gunWidth, gunHeight, false);

        bulletImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.bullet);
        bullets = new ArrayList<>();
        isIdle = true;



    }
    public void update() {


        if (movingLeft) {
            x -= speed;
            if(width>0){
                width = -width;
                headWidth = -headWidth;
                gunWidth = -gunWidth;
//                bulletWidth =-bulletWidth; //:TODO BulletWidth error

            }


        }
        if (movingRight) {
            x += speed;
            if(width<0){
                width = -width;
                headWidth = -headWidth;
                gunWidth = -gunWidth;
//                bulletWidth =-bulletWidth; //:TODO BulletWidth error
            }
        }


        if (jumping) {
            y -= jumpSpeed;
            jumpSpeed -= gravity;

            if (y >= LandRestriction) {
                y = LandRestriction;
                jumping = false;
                jumpSpeed = 15f;
            }
        } else {
            y += jumpSpeed;
            jumpSpeed += gravity;

            if (y >= LandRestriction) {
                y = LandRestriction;
                jumpSpeed = 15f;
            }
//            else{
//                LandRestriction = 1000;
//            }
        }

        // Проверка столкновений с блоками
        checkBlockCollision(blocks);

        if (!movingLeft && !movingRight && !jumping) {
            isIdle = true;
            onIdle();
        }
    }
    public boolean isOnBlock(Player player) {
        return player.getX() + player.getWidth() > x && player.getX() < x + width &&
                player.getY() + player.getHeight() >= y && player.getY() + player.getHeight() <= y + height;
    }



    private void onIdle() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastIdleTime >= ANIMATION_DELAY) {
            bodyImage = (person_stop_int % 2 == 0) ?
                    BitmapFactory.decodeResource(context.getResources(), R.drawable.person_stop1) :
                    BitmapFactory.decodeResource(context.getResources(), R.drawable.person_stop2);
            bodyImage = Bitmap.createScaledBitmap(bodyImage, width, height, false);
            person_stop_int++;
            lastIdleTime = currentTime;
        }
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(bodyImage, x, y, null);
        canvas.drawBitmap(headImage, x + (width - headWidth) / 2 - 15, y - headHeight + 20, null);
        canvas.drawBitmap(gunImage, x, y, null);
        for (Bullet bullet : bullets) {
            bullet.draw(canvas);
        }
    }

    public void shoot() {
        float bulletX = x + width;
        float bulletY = y + height / 2;
        bullets.add(new Bullet(bulletX, bulletY, bulletImage));
    }

    public void setMovingLeft(boolean movingLeft) {
        this.movingLeft = movingLeft;
    }

    public void setMovingRight(boolean movingRight) {
        this.movingRight = movingRight;
    }

    public void jump() {
        if (!jumping) {
            jumping = true;
        }
    }

    public boolean isMoving() {
        return movingLeft || movingRight;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setPlayerImage(Bitmap newBodyImage, Bitmap newHeadImage, Bitmap newGunImage) {
        this.bodyImage = Bitmap.createScaledBitmap(newBodyImage, width, height, false);
        this.headImage = Bitmap.createScaledBitmap(newHeadImage, headWidth, headHeight, false);
        this.gunImage = Bitmap.createScaledBitmap(newGunImage, gunWidth, gunHeight, false);
    }

    public Context getContext() {
        return context;
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    private int lives = 100;

    public void decreaseLife(int amount) {
        lives -= amount;
        if (lives < 0) {
            lives = 0;
        }
    }

    public int getLives() {
        return lives;
    }
    public boolean checkBlockCollision(List<Block> blocks) {
        boolean isColliding = false;
        LandRestriction = 1000;

        for (Block block : blocks) {
            boolean xOverlap = x + Math.abs(width) > block.getX() && x < block.getX() + block.getWidth();
            boolean yOverlap = y + height > block.getY() && y + height <= block.getY() + block.getHeight();

            if (xOverlap && yOverlap) {
                isColliding = true;

                if (y + height <= block.getY() + block.getHeight() && jumpSpeed > 0) {
                    y = block.getY() - height;
                    LandRestriction = (int) block.getY(); // Берем ограничение земли с позиции блока
                    jumpSpeed = 0; // Останавливаем падение
                    jumping = false; // Завершаем прыжок
                }

                // Если игрок сталкивается с боковой стороной блока
                if (movingRight) {
                    x = block.getX() - Math.abs(width); // Остановка вправо
                    movingRight = false;
                } else if (movingLeft) {
                    x = block.getX() + block.getWidth(); // Остановка влево
                    movingLeft = false;
                }
            }
        }

        return isColliding;
    }









    public void setBlocks(List<Block> blocks) {
        this.blocks = blocks;
    }

    public boolean checkCollision(Block block) {
        return this.getX() < block.getX() + block.getWidth() &&
                this.getX() + this.getWidth() > block.getX() &&
                this.getY() < block.getY() + block.getHeight() &&
                this.getY() + this.getHeight() > block.getY();
    }

    public void setY(float y) {
        this.y = y;
    }
}
