package com.example.minijuegosapp.brickgame;

import android.content.Context;
import android.view.View;

import java.util.logging.Handler;

public class GameView extends View {

    Context context;
    float ballX, ballY;
    Velocity velocity = new Velocity(25,32);
    Handler handler;

    public GameView(Context context) {
        super(context);
    }
}
