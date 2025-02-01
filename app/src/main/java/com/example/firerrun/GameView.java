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

        for (int[] blockData : BlocksList.Blocks) {
            Block block;
            switch (blockData[4]) {
                case 0:
                    block = new Block(context, blockData[0], blockData[1], blockData[2], blockData[3], R.drawable.block);
                    blockList.add(block);
                    break;
                case 1:
                    block = new Block(context, blockData[0], blockData[1], blockData[2], blockData[3], R.drawable.block2);
                    blockList.add(block);
            }
        }
        player = new Player(context);
        player.setBlocks(blockList);

        gameThread = new GameThread(getHolder(), this);
        life = new Life();
        playerImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.stand_1);
        playerImage = Bitmap.createScaledBitmap(playerImage, 100, 100, false);
        animation = new Animation(player);
        textPaint = new Paint();
        textPaint.setColor(Color.RED);
        textPaint.setTextSize(100);
        textPaint.setTextAlign(Paint.Align.CENTER);
        badBox = new BadBox(500, 500, BitmapFactory.decodeResource(context.getResources(), R.drawable.bad_box));

        playerController = new PlayerController(player, this);
    }

    public void shoot() {
        boolean isFacingLeft = Player.isFacingLeft;
        float bulletX = isFacingLeft ? (player.getX() - Bullet.width) : (player.getX() + player.getWidth());
        float bulletY = (player.getY() + player.getHeight()) / 2 + 217;

        Bullet newBullet = new Bullet(bulletX, bulletY, BitmapFactory.decodeResource(getResources(), R.drawable.bullet), isFacingLeft);
        bullets.add(newBullet);

        Log.i("GameView", "Bullet shot in direction: " + (isFacingLeft ? "Left" : "Right"));
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
            }

            for (Block block : blockList) {
                if (bullet.getX() < block.getX() + block.getWidth() &&
                        bullet.getX() + Bullet.width > block.getX() &&
                        bullet.getY() < block.getY() + block.getHeight() &&
                        bullet.getY() + Bullet.height > block.getY()) {
                    bullets.remove(i);
                    blockList.remove(block);
                    Log.i("info", "Bullet hit block and block is destroyed");
                    break;
                }
            }
        }

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
        for (Bullet bullet : bullets) {
            try{
                bullet.draw(canvas);

            }catch (Exception е){}
        }


        player.draw(canvas);
        badBox.draw(canvas);
        life.draw(canvas);

        for (Block block : blockList) {
            block.draw(canvas);
        }
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
            long lastTime = System.nanoTime();
            double nsPerUpdate = 1000000000.0 / 120.0; // 120 FPS
            double delta = 0;

            while (running) {
                long now = System.nanoTime();
                delta += (now - lastTime) / nsPerUpdate;
                lastTime = now;

                while (delta >= 1) {
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
                    delta--;
                }
            }
        }
    }
}