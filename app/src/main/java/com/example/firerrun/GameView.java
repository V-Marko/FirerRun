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
    private Life life;  // Исправлено имя переменной
    private Animation animation;
    private Bitmap playerImage;
    private Paint textPaint;
    private BadBox badBox;
    private long lastCollisionTime = 0;
    private final long collisionCooldown = 300;
    private PlayerController playerController;

    private List<Block> blockList = new ArrayList<>();
    private List<Bullet> bullets = new ArrayList<>();  // Список пуль

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);

        getHolder().addCallback(this);

        // Инициализация игрока, жизни, фона, блоков
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
        blockList = new ArrayList<>();

        // Инициализация блоков
        for (int[] blockData : BlocksList.Blocks) {
            Block block = new Block(blockData[0], blockData[1], blockData[2], blockData[3], R.drawable.block, context);
            blockList.add(block);
        }

        addTestBlocks();  // Добавляем тестовые блоки (если нужно)
    }

    // Метод для добавления тестовых блоков (если нужно)
    public void addTestBlocks() {
        // Пример добавления блоков
//        blockList.add(new Block(200, 500, 100, 50, R.drawable.block, getContext()));
//        blockList.add(new Block(400, 500, 100, 50, R.drawable.block, getContext()));
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // Получаем ширину и высоту экрана в методе surfaceCreated()
        int backgroundWidth = getWidth();
        int backgroundHeight = getHeight();

        // Загружаем исходное изображение фона
        Bitmap originalBackground = BitmapFactory.decodeResource(getResources(), R.drawable.background);

        // Масштабируем фоновое изображение до размеров экрана
        background = Bitmap.createScaledBitmap(originalBackground, backgroundWidth, backgroundHeight, true);

        // Запускаем игровой поток
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
            bullet.update();  // Обновляем положение пули

            // Удаляем пулю, если она вышла за пределы экрана
            if (bullet.getX() > getWidth() || bullet.getX() < 0) {
                bullets.remove(i);
                continue;
            }

            // Проверка столкновения пули с badBox
            if (badBox.checkCollisionBullet(bullet)) {
                bullets.remove(i);  // Удаляем пулю после попадания
                badBox.die();  // Уничтожаем badBox
                Log.i("info", "Bullet hit bad_box and bad_box is destroyed");
            }

            // Проверка столкновений пули с блоками
            for (Block block : blockList) {
                if (bullet.checkCollision(block)) {
                    bullets.remove(i);
                    blockList.remove(block);  // Удаляем блок
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
                life.decreaseLife(20);  // Уменьшаем жизнь игрока при столкновении
                Log.i("info", "Player collided with badBox and lost life");
            }
        }

        // Проверка, если игрок касается какого-либо блока
        boolean isOnBlock = false;
        for (Block block : blockList) {
            if (block.isOnBlock(player)) {
                isOnBlock = true;

                // Обрабатываем столкновение с блоком, чтобы игрок не мог пройти сквозь него
                if (player.getY() + player.getHeight() <= block.getY()) {
                    // Если игрок не ниже блока, значит он на нем
                    player.setY(block.getY() - player.getHeight());  // Помещаем игрока сверху блока
                }
                break;
            }
        }

        // Если игрок не на блоке, ограничение земли становится 500
        if (isOnBlock) {
            // Найдем блок, на котором находится игрок, и установим ограничение земли по y этого блока
            for (Block block : blockList) {
                if (block.isOnBlock(player)) {
                    player.LandRestriction = (int) block.getY();  // Устанавливаем ограничение земли на основе блока
                    break;  // Если мы нашли блок, можно выйти из цикла
                }
            }
        } else {
            player.LandRestriction = 500;  // Если игрок не на блоке, ограничение земли становится 500
        }

        // Обновление состояния игрока и badBox
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
        animation.startWalkingAnimation();
    }

    public void stopLeft() {
        player.setMovingLeft(false);
        if (!player.isMoving()) {
            animation.stopWalkingAnimation();
        }
    }

    public void moveRight() {
        player.setMovingRight(true);
        animation.startWalkingAnimation();
    }

    public void stopRight() {
        player.setMovingRight(false);
        if (!player.isMoving()) {
            animation.stopWalkingAnimation();
        }
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
