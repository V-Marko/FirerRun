//package com.example.firerrun;
//
//import android.animation.Animator;
//import android.animation.AnimatorListenerAdapter;
//import android.animation.ValueAnimator;
//import android.util.Log;
//import android.view.animation.AccelerateDecelerateInterpolator;
//
//public class SwitchCader {
//    private Player player;
//    private GameView gameView;
//    private float screenSize;
//    private int index = 1;
//    private ValueAnimator animator;
//    private boolean isAnimating = false;
//
//    public SwitchCader(Player player, GameView gameView) {
//        this.player = player;
//        this.gameView = gameView;
//        this.screenSize = gameView.getScreenWidth(gameView.getContext());
//    }
//
//    public void updateCader() {
//        float threshold = (screenSize / 2) + (screenSize / 3);
//
//        if (isAnimating) {
//            return;
//        }
//
//        if (player.getX() > threshold && player.getVelocityX() > 0) {
//            gameView.post(() -> startAnimationLeft());
//            index++;
//        } else if (player.getX() < threshold && player.getVelocityX() < 0) {
//            if (index > 1) {
//                gameView.post(() -> startAnimationRight());
//                index--;
//            }
//        }
//    }
//
//    private void startAnimationRight() {
//        animateCaderTransition(screenSize / 20, false);
//    }
//
//    private void startAnimationLeft() {
//        animateCaderTransition(-screenSize / 20, true);
//    }
//
//    private void animateCaderTransition(float offsetValue, boolean isLeft) {
//        isAnimating = true;
//
//        animator = ValueAnimator.ofFloat(0, offsetValue);
//        animator.setDuration(500);
//        animator.setInterpolator(new AccelerateDecelerateInterpolator());
//        animator.addUpdateListener(animation -> {
//            float offset = (float) animation.getAnimatedValue();
//
//            for (Block block : gameView.getBlockList()) {
//                block.x += offset;
//            }
//
//            for (Bullet bullet : gameView.getBullets()) {
//                bullet.x += offset;
//            }
//
//            player.setX(player.getX() + offset);
//            gameView.getBadBox().x += offset;
//
//            gameView.invalidate();
//        });
//
//        animator.addListener(new AnimatorListenerAdapter() {
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                super.onAnimationEnd(animation);
//                isAnimating = false;
//            }
//        });
//
//        animator.start();
//    }
//}
package com.example.firerrun;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.util.Log;
import android.view.animation.AccelerateDecelerateInterpolator;

public class SwitchCader {
    private Player player;
    private GameView gameView;
    private static float screenSize;
    private int index = 1;
    private ValueAnimator animator;
    private boolean isAnimating = false;
    private static final float CAMERA_OFFSET_SPEED = 20;  // Скорость смещения камеры
    private static final float CAMERA_BORDER = 100; // Пределы, на которых камера должна остановиться (можно настроить)

    public SwitchCader(Player player, GameView gameView) {
        this.player = player;
        this.gameView = gameView;
        this.screenSize = gameView.getScreenWidth(gameView.getContext());
    }

    public void updateCader() {
        float threshold = (screenSize / 4);

        if (isAnimating) {
            return;
        }

        if (player.getX() > threshold && player.getVelocityX() > 0) {
            gameView.post(() -> startAnimationLeft());
            index++;
        } else if (player.getX() < threshold && player.getVelocityX() < 0) {
            if (index > 1) {
                gameView.post(() -> startAnimationRight());
                index--;
            }
        }
    }

    private void startAnimationRight() {
        animateCaderTransition(CAMERA_OFFSET_SPEED, false);
    }

    private void startAnimationLeft() {
        animateCaderTransition(-CAMERA_OFFSET_SPEED, true);
    }

    private void animateCaderTransition(float offsetValue, boolean isLeft) {
        isAnimating = true;

        animator = ValueAnimator.ofFloat(0, offsetValue);
        animator.setDuration(500);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addUpdateListener(animation -> {
            float offset = (float) animation.getAnimatedValue();

            // Получаем текущую позицию игрока и ограничиваем движение камеры
            float newPlayerX = player.getX() + offset;
            float minX = CAMERA_BORDER; // Предел для левого края
            float maxX = gameView.getWidth() - CAMERA_BORDER; // Предел для правого края

            // Ограничиваем положение камеры, чтобы игрок не выходил за пределы экрана
            if (newPlayerX < minX) {
                offset = minX - player.getX();  // Камера не будет двигаться влево, если достигнут левый предел
            } else if (newPlayerX > maxX) {
                offset = maxX - player.getX();  // Камера не будет двигаться вправо, если достигнут правый предел
            }

            // Применяем смещение
            for (Block block : gameView.getBlockList()) {
                block.x += offset;
            }

            for (Bullet bullet : gameView.getBullets()) {
                bullet.x += offset;
            }

            player.setX(player.getX() + offset);
            gameView.getBadBox().x += offset;

            gameView.invalidate();
        });

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                isAnimating = false;
            }
        });

        animator.start();
    }
}