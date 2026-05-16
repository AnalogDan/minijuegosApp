package com.example.minijuegosapp;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Intent;
import android.widget.Button;

import com.example.minijuegosapp.brickgame.Brickgame;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        //Snake button
        Button btn = findViewById(R.id.snakeButton);
        btn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, Snake.class);
            startActivity(intent);
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //BrickGame
        Button btn_brickgame = findViewById(R.id.brickgameButton);
        btn_brickgame.setOnClickListener(v ->{
            Intent intent = new Intent(MainActivity.this, Brickgame.class);
            startActivity(intent);
        });
    }
}