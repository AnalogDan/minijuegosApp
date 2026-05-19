package com.example.minijuegosapp.brickgame;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.example.minijuegosapp.R;

public class GameOver extends AppCompatActivity {

    TextView tvPoints;
    ImageView ivNewHighest;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over_brickgame);
        ivNewHighest = findViewById(R.id.ivNewHeighest);
        tvPoints = findViewById(R.id.tvPoints);
        int points = getIntent().getIntExtra("points", 0);
        if (points == 320){
            ivNewHighest.setVisibility(View.VISIBLE);
        }
        tvPoints.setText(""+points);

        //Ocultar las barras superiores e inferiores
        WindowInsetsControllerCompat controller =
                ViewCompat.getWindowInsetsController(getWindow().getDecorView());

        if (controller != null) {
            controller.hide(WindowInsetsCompat.Type.systemBars());
            controller.setSystemBarsBehavior(
                    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            );
        }
    }

    public void restart(View view){
        Intent intent = new Intent(GameOver.this, Brickgame.class);
        startActivity(intent);
        finish();
    }

    public void exit(View view){
        finish();
    }
}
