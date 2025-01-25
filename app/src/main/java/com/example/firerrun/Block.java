package com.example.firerrun;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.content.Context;
import android.graphics.Rect;

public class Block {
    private float x, y;
    private int width, height;
    private Bitmap image;
    private Context context;

    // Конструктор теперь принимает параметры для позиции и размеров блока
    public Block(Context context, float x, float y, int width, int height, int imageResourceId) {
        this.context = context;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        // Загружаем изображение для блока
        this.image = BitmapFactory.decodeResource(context.getResources(), imageResourceId);
        this.image = Bitmap.createScaledBitmap(image, width, height, false);
    }

    // Метод отрисовки блока на холсте
    public void draw(Canvas canvas) {
        if (image != null) {
            canvas.drawBitmap(image, x, y, null);
        } else {
            canvas.drawRect(x, y, x + width, y + height, null);
        }
    }

    // Проверка столкновения блока с игроком
    public boolean checkCollisionBlockPlayer(Player player) {
        Rect playerRect = new Rect((int) player.getX(), (int) player.getY(),
                (int) (player.getX() + player.getWidth()), (int) (player.getY() + player.getHeight()));

        Rect blockRect = new Rect((int) x, (int) y, (int) (x + width), (int) (y + height));

        return Rect.intersects(playerRect, blockRect);
    }

    // Проверка, стоит ли игрок на блоке
    public boolean isOnBlock(Player player) {
        return player.getX() + player.getWidth() > x && player.getX() < x + width &&
                player.getY() + player.getHeight() >= y && player.getY() + player.getHeight() <= y + height;
    }

    // Геттеры
    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }
}
