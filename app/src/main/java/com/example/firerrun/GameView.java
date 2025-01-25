package com.example.firerrun;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {
    private Bitmap background;
    private GameThread gameThread;
    private Player player;
    private Life life;
    private Animation animation;
    private Bitmap playerImage;
    private Paint textPaint;
    private BadBox badBox;
    private long lastCollisionTime = 0;
    private final long collisionCooldown = 300;
    private PlayerController playerController;

    private List<Block> blockList = new ArrayList<>();
    private List<Bullet> bullets = new ArrayList<>();

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);

        // Инициализация блоков
        for (int[] blockData : BlocksList.Blocks) {
            Block block = new Block(context, blockData[0], blockData[1], blockData[2], blockData[3], R.drawable.block);
            blockList.add(block);
        }

        // Инициализация игрока и других объектов
        gameThread = new GameThread(getHolder(), this);
        player = new Player(context);
        life = new Life();
        playerImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.stand_1);
        playerImage = Bitmap.createScaledBitmap(playerImage, 100, 100, false);
        animation = new Animation(player);
        textPaint = new Paint();
        textPaint.setColor(Color.RED);
        textPaint.setTextSize(100);
        textPaint.setTextAlign(Paint.Align.CENTER);
        badBox = new BadBox(500, 500, BitmapFactory.decodeResource(context.getResources(), R.drawable.bad_box));

        // Инициализация контроллера
        playerController = new PlayerController(player, this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        int backgroundWidth = getWidth();
        int backgroundHeight = getHeight();

        Bitmap originalBackground = BitmapFactory.decodeResource(getResources(), R.drawable.background);
        background = Bitmap.createScaledBitmap(originalBackground, backgroundWidth, backgroundHeight, true);

        gameThread.setRunning(true);
        gameThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        while (retry) {
            try {
                gameThread.setRunning(false);
                gameThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            retry = false;
        }
    }

    public void update() {
        // Обновление состояния пуль
        for (int i = bullets.size() - 1; i >= 0; i--) {
            Bullet bullet = bullets.get(i);
            bullet.update();

            if (bullet.getX() > getWidth() || bullet.getX() < 0) {
                bullets.remove(i);
                continue;
            }

            if (badBox.checkCollisionBullet(bullet)) {
                bullets.remove(i);
                badBox.die();
                Log.i("info", "Bullet hit bad_box and bad_box is destroyed");
            }

            for (Block block : blockList) {
                if (bullet.checkCollision(block)) {
                    bullets.remove(i);
                    blockList.remove(block);
                    Log.i("info", "Bullet hit block and block is destroyed");
                    break;
                }
            }
        }

        // Проверка столкновения игрока с badBox
        if (badBox.checkCollisionPlayer(player)) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastCollisionTime >= collisionCooldown) {
                lastCollisionTime = currentTime;
                life.decreaseLife(20);
                Log.i("info", "Player collided with badBox and lost life");
            }
        }

        boolean isOnBlock = false;
        for (Block block : blockList) {
            if (player.checkBlockCollision(blockList)) {
                isOnBlock = true;
                break;
            }
        }

        if (!isOnBlock) {
            player.LandRestriction = 500;
        }

        player.update();
        badBox.update();
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        if (background != null) {
            canvas.drawBitmap(background, 0, 0, null);
        }

        // Отрисовка пуль
        for (Bullet bullet : bullets) {
            bullet.draw(canvas);
        }

        player.draw(canvas);
        badBox.draw(canvas);
        life.draw(canvas);

        for (Block block : blockList) {
            block.draw(canvas);
        }
    }

    // Управление движением
    public void moveLeft() {
        player.setMovingLeft(true);
    }

    public void stopLeft() {
        player.setMovingLeft(false);
    }

    public void moveRight() {
        player.setMovingRight(true);
    }

    public void stopRight() {
        player.setMovingRight(false);
    }

    public void jump() {
        player.jump();
    }

    public void shoot() {
        Bullet newBullet = new Bullet((player.getX() + player.getWidth()),
                (player.getY() + getHeight()) / 2 - 125,
                BitmapFactory.decodeResource(getResources(), R.drawable.bullet));
        bullets.add(newBullet);
    }

    public Animation getAnnimation() {
        return animation;
    }

    public PlayerController getPlayerController() {
        return playerController;
    }

    public Player getPlayer() {
        return player;
    }

    class GameThread extends Thread {
        private SurfaceHolder surfaceHolder;
        private GameView gameView;
        private boolean running;

        public GameThread(SurfaceHolder surfaceHolder, GameView gameView) {
            this.surfaceHolder = surfaceHolder;
            this.gameView = gameView;
        }

        public void setRunning(boolean running) {
            this.running = running;
        }

        @Override
        public void run() {
            Canvas canvas;
            while (running) {
                canvas = null;
                try {
                    canvas = surfaceHolder.lockCanvas();
                    synchronized (surfaceHolder) {
                        gameView.update();
                        gameView.draw(canvas);
                    }
                } finally {
                    if (canvas != null) {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }
            }
        }
    }
}