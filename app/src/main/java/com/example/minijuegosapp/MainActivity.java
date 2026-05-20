package com.example.minijuegosapp;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.minijuegosapp.tic_tac_toe.AddPlayers;
import com.google.android.material.card.MaterialCardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Intent;
import android.view.MotionEvent;
import android.widget.Button;

import com.example.minijuegosapp.brickgame.Brickgame;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        //Obtener las tarjetas/botones a utilizar
        MaterialCardView cardSnake = findViewById(R.id.cardSnake);
        MaterialCardView cardBrickGame = findViewById(R.id.cardBrickGame);
        MaterialCardView cardTicTacToe = findViewById(R.id.cardTicTacToe);

        setupCardAnimation(cardSnake);
        setupCardAnimation(cardBrickGame);
        setupCardAnimation(cardTicTacToe);

        cardSnake.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, Snake.class);
            startActivity(intent);
        });

        cardBrickGame.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, Brickgame.class);
            startActivity(intent);
        });

        cardTicTacToe.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddPlayers.class);
            startActivity(intent);
        });
    }
    //Animaciones de las tarjetas/botones
    @SuppressLint("ClickableViewAccessibility")
    private void setupCardAnimation(MaterialCardView card) {

        card.setOnTouchListener((v, event) -> {

            switch (event.getAction()) {

                case MotionEvent.ACTION_DOWN:
                    v.animate().scaleX(0.96f).scaleY(0.96f).setDuration(100).start();
                    break;

                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    v.animate().scaleX(1f).scaleY(1f).setDuration(100).start();
                    break;
            }

            return false;
        });
    }
}