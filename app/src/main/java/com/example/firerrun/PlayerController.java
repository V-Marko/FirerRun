package com.example.firerrun;

import android.util.Log;

public class PlayerController {
    private Player player;
    private GameView gameView;

    public PlayerController(Player player, GameView gameView) {
        this.player = player;
        this.gameView = gameView;
    }

    // Метод для движения влево
    public void moveLeft() {
        player.setMovingLeft(true);
        player.setMovingRight(false); // Останавливаем движение вправо
        Log.i("PlayerController", "Moving left");
    }

    // Метод для остановки движения влево
    public void stopLeft() {
        player.setMovingLeft(false);
        Log.i("PlayerController", "Stopped moving left");
    }

    // Метод для движения вправо
    public void moveRight() {
        player.setMovingRight(true);
        player.setMovingLeft(false); // Останавливаем движение влево
        Log.i("PlayerController", "Moving right");
    }

    // Метод для остановки движения вправо
    public void stopRight() {
        player.setMovingRight(false);
        Log.i("PlayerController", "Stopped moving right");
    }

    public void jump() {
        player.jump();
        Log.i("PlayerController", "Jumping");
    }

    // Метод для стрельбы
    public void shoot() {
        if (gameView != null) {
            gameView.shoot();
            Log.i("PlayerController", "Shooting");
        }
    }
}