package com.example.firerrun;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.Button;

public class MainActivity extends Activity {

    private GameView gameView;
    private Button btnLeft, btnRight, btnJump, btnShoot;
    private PlayerController playerController;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gameView = findViewById(R.id.gameView);
        btnLeft = findViewById(R.id.btnLeft);
        btnRight = findViewById(R.id.btnRight);
        btnJump = findViewById(R.id.btnJump);
        btnShoot = findViewById(R.id.btnShoot);

        // Инициализация контроллера
        playerController = new PlayerController(gameView.getPlayer(), gameView);

        // Обработка нажатий кнопок
        btnLeft.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    playerController.moveLeft();
                    break;
                case MotionEvent.ACTION_UP:
                    playerController.stopLeft();
                    break;
            }
            return true;
        });

        btnRight.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    playerController.moveRight();
                    break;
                case MotionEvent.ACTION_UP:
                    playerController.stopRight();
                    break;
            }
            return true;
        });

        btnJump.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                playerController.jump();
            }
            return true;
        });

        btnShoot.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                playerController.shoot();
            }
            return true;
        });
    }
}