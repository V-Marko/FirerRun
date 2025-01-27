package com.example.firerrun;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class Player {

    private float x;
    private static float y;
    private float speed = 10f;
    private boolean movingLeft, movingRight, jumping;
    private boolean isIdle;
    private float jumpSpeed = 100;
    private Bitmap bodyImage, headImage, gunImage, bulletImage;
    private Context context;

    public static int width = 150;
    public static int height = 150;
    public int headWidth = 150;
    public int headHeight = 150;
    public int gunWidth = 150;
    public int gunHeight = 90;

    private static final long ANIMATION_DELAY = 300;
    private List<Block> blocks;
    public float LandRestriction = 500;
    private List<Bullet> bullets;

    private boolean isFacingLeft;

    private float initialJumpSpeed = -10f;
    private float gravity = 1.2f;
    private float maxJumpHeight = 5f;

    private float currentJumpHeight = 0f;
    private Animation animation;

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
        blocks = new ArrayList<>();

        animation = new Animation(this);

    }

    public void update() {
        if (movingLeft) {
            x -= speed;
            isFacingLeft = true;
        }

        if (movingRight) {
            x += speed;
            isFacingLeft = false;
        }

        if (jumping) {
            y += jumpSpeed; // Обновляем позицию по Y
            jumpSpeed += gravity; // Увеличиваем скорость под действием гравитации
            currentJumpHeight += Math.abs(jumpSpeed); // Обновляем текущую высоту прыжка

            // Если достигнута максимальная высота прыжка или игрок приземлился
            if (currentJumpHeight >= maxJumpHeight || y >= LandRestriction) {
                jumping = false; // Завершаем прыжок
                y = LandRestriction; // Фиксируем позицию на земле
                Log.i("Jump", "JUMP ended");
            }
        }

        // Гравитация (падение, если игрок не на земле и не прыгает)
        if (!isOnGround() && !jumping) {
            y += jumpSpeed; // Обновляем позицию по Y
            jumpSpeed += gravity; // Увеличиваем скорость падения под действием гравитации
        }

        // Проверка столкновений с блоками
        boolean isOnBlock = checkBlockCollision(blocks);

        // Если игрок не на блоке и не прыгает, он падает
        if (!isOnBlock && !jumping) {
            y += jumpSpeed; // Обновляем позицию по Y
            jumpSpeed += gravity; // Увеличиваем скорость падения под действием гравитации
        }
    }
    public void jump() {
        if (isOnGround() && !jumping) { // Прыжок возможен только если игрок на земле и не в прыжке
            jumping = true;
            jumpSpeed = initialJumpSpeed; // Устанавливаем начальную скорость прыжка
            currentJumpHeight = 0f; // Сбрасываем текущую высоту прыжка
            Log.i("Jump", "JUMP initiated");
        }
    }

    public boolean isOnGround() {
        boolean groundCondition = (y >= LandRestriction);
        boolean blockCondition = checkBlockCollision(blocks);

        return groundCondition || blockCondition;
    }

    public boolean checkBlockCollision(List<Block> blocks) {
        boolean isColliding = false;
        LandRestriction = Player.y-Player.height;

        for (Block block : blocks) {
            boolean xOverlap = (x < block.getX() + block.getWidth()) &&
                    (x + width > block.getX());

            boolean yOverlap = (y + height >= block.getY()) &&
                    (y + height <= block.getY() + block.getHeight());

            Log.i("BlockCollision",
                    "Block: " + block.getX() + "," + block.getY() +
                            " | Player: " + x + "," + y +
                            " | Overlap: " + xOverlap + "," + yOverlap
            );

            if (xOverlap && yOverlap) {
                isColliding = true;

                if (y + height <= block.getY() + block.getHeight() && jumpSpeed >= 0) {
                    y = block.getY() - height; // Устанавливаем игрока на верхнюю грань блока
                    LandRestriction = (int) block.getY(); // Обновляем LandRestriction
                    jumpSpeed = 0; // Сбрасываем скорость прыжка
                }
            }
        }

        return isColliding;
    }

    public void draw(Canvas canvas) {
        Bitmap currentBodyImage = bodyImage;

        if (isFacingLeft) {
            Matrix matrix = new Matrix();
            matrix.preScale(-1, 1);
            currentBodyImage = Bitmap.createBitmap(bodyImage, 0, 0, bodyImage.getWidth(), bodyImage.getHeight(), matrix, false);
        }

        canvas.drawBitmap(currentBodyImage, x, y, null);
        canvas.drawBitmap(headImage, x + (width - headWidth) / 2 - 15, y - headHeight + 20, null);
        canvas.drawBitmap(gunImage, x, y, null);

        for (Bullet bullet : bullets) {
            bullet.draw(canvas);
        }
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
        return x;
    }

    public float getY() {
        return y;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public void setBlocks(List<Block> blockList) {
        if (blockList == null) {
            this.blocks = new ArrayList<>();
        } else {
            this.blocks = new ArrayList<>(blockList);
        }
    }
    public void setMovingLeft(boolean movingLeft) {
        this.movingLeft = movingLeft;
        if (movingLeft) {
            animation.startWalkingAnimation();
        } else if (!movingRight) {
            animation.stopWalkingAnimation();
        }
    }

    public void setMovingRight(boolean movingRight) {
        this.movingRight = movingRight;
        if (movingRight) {
            animation.startWalkingAnimation();
        } else if (!movingLeft) {
            animation.stopWalkingAnimation();
        }
    }


}