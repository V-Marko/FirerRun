package com.example.firerrun;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;

public class Animation {
    private Player player;
    private int walkIndex = 0; // Индекс текущего кадра анимации
    private Bitmap[] walkFrames; // Массив кадров анимации
    private Bitmap headImage; // Изображение головы
    private Bitmap gunImage; // Изображение оружия
    private Handler handler = new Handler(); // Handler для управления анимацией
    private int frameDuration = 100; // Задержка между кадрами (в миллисекундах)
    private boolean isAnimating = false; // Флаг, указывающий, активна ли анимация

    public Animation(Player player) {
        this.player = player;
        loadWalkFrames(); // Загружаем кадры анимации
        loadHeadImage(); // Загружаем изображение головы
        loadGunImage(); // Загружаем изображение оружия
    }

    // Загрузка кадров анимации
    private void loadWalkFrames() {
        int[] frameResources = {
                R.drawable.person_walk_1,
                R.drawable.person_walk_2,
                R.drawable.person_walk_3,
                R.drawable.person_walk_4,
                R.drawable.person_walk_5,
                R.drawable.person_walk_6,
                R.drawable.person_walk_7,
                R.drawable.person_walk_8,
        };
        walkFrames = new Bitmap[frameResources.length];
        for (int i = 0; i < frameResources.length; i++) {
            walkFrames[i] = BitmapFactory.decodeResource(player.getContext().getResources(), frameResources[i]);
        }
    }

    // Загрузка изображения головы
    private void loadHeadImage() {
        headImage = BitmapFactory.decodeResource(player.getContext().getResources(), R.drawable.person_head);
    }

    // Загрузка изображения оружия
    private void loadGunImage() {
        gunImage = BitmapFactory.decodeResource(player.getContext().getResources(), R.drawable.person_gun);
    }

    // Метод для запуска анимации ходьбы
    public void startWalkingAnimation() {
        if (!isAnimating) {
            isAnimating = true;
            handler.post(animationRunnable); // Запускаем анимацию
        }
    }

    // Метод для остановки анимации ходьбы
    public void stopWalkingAnimation() {
        isAnimating = false;
        handler.removeCallbacks(animationRunnable); // Останавливаем анимацию
    }

    private Runnable animationRunnable = new Runnable() {
        @Override
        public void run() {
            if (isAnimating) {
                // Устанавливаем текущий кадр анимации
                player.setPlayerImage(walkFrames[walkIndex], headImage, gunImage);
                // Переходим к следующему кадру
                walkIndex = (walkIndex + 1) % walkFrames.length;
                // Повторяем через frameDuration миллисекунд
                handler.postDelayed(this, frameDuration);
            }
        }
    };
}