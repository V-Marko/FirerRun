package com.example.firerrun;

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
    }

    // Метод для остановки движения влево
    public void stopLeft() {
        player.setMovingLeft(false);
    }

    // Метод для движения вправо
    public void moveRight() {
        player.setMovingRight(true);
    }

    // Метод для остановки движения вправо
    public void stopRight() {
        player.setMovingRight(false);
    }

    // Метод для прыжка
    public void jump() {
        player.jump();
    }

    // Метод для стрельбы
    public void shoot() {
        gameView.shoot();
    }

    // Метод для проверки, находится ли игрок на земле
    public boolean isOnGround() {
        return !player.isJumping();
    }

    // Метод для получения текущего количества жизней игрока
    public int getPlayerLives() {
        return player.getLives();
    }

    // Метод для уменьшения жизней игрока
    public void decreasePlayerLives(int amount) {
        player.decreaseLife(amount);
    }

    // Метод для увеличения жизней игрока
    public void increasePlayerLives(int amount) {
        player.increaseLife(amount);
    }

    // Метод для сброса жизней игрока до максимального значения
    public void resetPlayerLives() {
        player.resetLives();
    }

    // Метод для получения текущей позиции игрока по X
    public float getPlayerX() {
        return player.getX();
    }

    // Метод для получения текущей позиции игрока по Y
    public float getPlayerY() {
        return player.getY();
    }

    // Метод для проверки столкновения игрока с блоком
    public boolean checkPlayerBlockCollision(Block block) {
        return player.checkCollision(block);
    }
}