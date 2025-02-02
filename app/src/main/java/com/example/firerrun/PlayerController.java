package com.example.firerrun;

import android.util.Log;

public class PlayerController {
    private Player player;
    private GameView gameView;

    public PlayerController(Player player, GameView gameView) {
        this.player = player;
        this.gameView = gameView;
    }

    public void onShootButtonPressed() {
        gameView.shoot();
    }

    public void moveLeft() {
        player.setMovingLeft(true);
        player.setMovingRight(false);
        Log.i("PlayerController", "Moving left");
    }

    public void stopLeft() {
        player.setMovingLeft(false);
        Log.i("PlayerController", "Stopped moving left");
    }

    public void moveRight() {
        player.setMovingRight(true);
        player.setMovingLeft(false);
        Log.i("PlayerController", "Moving right");
    }

    public void stopRight() {
        player.setMovingRight(false);
        Log.i("PlayerController", "Stopped moving right");
    }

    public void jump() {

        player.jump();
        Log.i("PlayerController", "Jumping");
    }
}