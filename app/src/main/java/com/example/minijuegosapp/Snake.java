package com.example.minijuegosapp;

import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import android.media.MediaPlayer;

public class Snake extends AppCompatActivity implements SurfaceHolder.Callback{
    private final List<SnakePoints> snakePointsList = new ArrayList<>();
    private SurfaceView surfaceView;
    private TextView scoreTV;
    private SurfaceHolder surfaceHolder;

    //Direcciones de la serpiente, deben ser right, left, top, bottom
    private String movingPosition = "right";
    private int score = 0;
    private static final int pointSize = 28;
    private static final int defaultTailPoints = 3;
    private static final int snakeColor = Color.GREEN;
    private static final int foodColor = Color.RED;
    private static final int snakeMovingSpeed = 800; //Debe ser entre 1 y 1000
    //Food coordinates
    private int positionX, positionY;
    private Timer timer;
    private Canvas canvas = null;
    private Paint pointColor = null;
    private Paint pointFoodColor = null;
    private MediaPlayer deadSound;
    private MediaPlayer moveSound;
    private MediaPlayer foodSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snake);

        surfaceView = findViewById(R.id.surfaceView);
        scoreTV = findViewById(R.id.scoreTV);

        moveSound = MediaPlayer.create(this, R.raw.move);
        deadSound = MediaPlayer.create(this, R.raw.dead);
        foodSound = MediaPlayer.create(this, R.raw.food);

        final AppCompatImageButton topBtn = findViewById(R.id.topBtn);
        final AppCompatImageButton leftBtn = findViewById(R.id.leftBtn);
        final AppCompatImageButton rightBtn = findViewById(R.id.rightBtn);
        final AppCompatImageButton bottomBtn = findViewById(R.id.bottomBtn);

        surfaceView.getHolder().addCallback(this);

        topBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveSound.start();
                if (!movingPosition.equals("bottom")){
                    movingPosition = "top";
                };
            }
        });
        leftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveSound.start();
                if (!movingPosition.equals("right")){
                    movingPosition = "left";
                };
            }
        });
        rightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveSound.start();
                if (!movingPosition.equals("left")){
                    movingPosition = "right";
                };
            }
        });
        bottomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveSound.start();
                if (!movingPosition.equals("top")){
                    movingPosition = "bottom";
                };
            }
        });
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        this.surfaceHolder = holder;
        init();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {

    }

    private void init(){
       //Reiniciar la longitud y pintaje
        snakePointsList.clear();
        scoreTV.setText("0");
        score = 0;
        movingPosition  = "right";
        int  startPositionX = (pointSize) * defaultTailPoints;
        for(int i=0; i<defaultTailPoints; i++){ //Build default snake lenght
            SnakePoints snakePoints = new SnakePoints(startPositionX, pointSize);
            snakePointsList.add(snakePoints);
            startPositionX = startPositionX - (pointSize * 2);
        }

        //Crear comida
        addPoint();

        moveSnake();

    }

    private void addPoint(){
        int surfaceWidth = surfaceView.getWidth() - (pointSize * 2);
        int surfaceHeight = surfaceView.getHeight() - (pointSize * 2);

        int randomXPosition = new Random().nextInt(surfaceWidth/pointSize);
        int randomYPosition = new Random().nextInt(surfaceHeight/pointSize);

        if ((randomXPosition % 2) != 0){
            randomXPosition = randomXPosition + 1;
        }
        if ((randomYPosition % 2) != 0){
            randomYPosition = randomYPosition + 1;
        }
        positionX = (pointSize * randomXPosition) + pointSize;
        positionY = (pointSize * randomYPosition) + pointSize;

    }

    private void moveSnake(){
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run(){
                //Get head position
                int headPositionX = snakePointsList.get(0).getPositionX();
                int headPositionY = snakePointsList.get(0).getPositionY();
                //Check for snake eating point
                if(headPositionX == positionX && positionY == headPositionY){
                    growSnake();
                    addPoint();
                }

                //Move depending on direction
                switch(movingPosition){
                    case "right":
                        snakePointsList.get(0).setPositionX(headPositionX+(pointSize*2));
                        snakePointsList.get(0).setPositionY(headPositionY);
                        break;
                    case "left":
                        snakePointsList.get(0).setPositionX(headPositionX-(pointSize*2));
                        snakePointsList.get(0).setPositionY(headPositionY);
                        break;
                    case "top":
                        snakePointsList.get(0).setPositionX(headPositionX);
                        snakePointsList.get(0).setPositionY(headPositionY-(pointSize*2));
                        break;
                    case "bottom":
                        snakePointsList.get(0).setPositionX(headPositionX);
                        snakePointsList.get(0).setPositionY(headPositionY+(pointSize*2));
                        break;
                }

                //Check if snake collides with walls or itself
                if(checkGameOver(headPositionX, headPositionY)){
                    timer.purge(); //Stop timer
                    timer.cancel();

                    AlertDialog.Builder builder = new AlertDialog.Builder(Snake.this); //Show game over message
                    builder.setMessage("Tu puntaje: "+score);
                    builder.setTitle("Perdiste >:D");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Jugar de nuevo", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            init();
                        }
                    });

                    runOnUiThread(new Runnable(){
                        @Override
                        public void run(){
                            builder.show();
                        }
                    });
                }else{
                    canvas = surfaceHolder.lockCanvas();
                    canvas.drawColor(Color.WHITE, PorterDuff.Mode.CLEAR);
                    canvas.drawCircle(snakePointsList.get(0).getPositionX(), snakePointsList.get(0).getPositionY(), pointSize, createPaintColor());//snake head
                    canvas.drawCircle(positionX, positionY, pointSize, createFoodPaintColor());//draw food

                    //Draw whole snake
                    for(int i=1; i<snakePointsList.size(); i++){
                        int getTempPositionX = snakePointsList.get(i).getPositionX();
                        int getTempPositionY = snakePointsList.get(i).getPositionY();
                        snakePointsList.get(i).setPositionX(headPositionX);
                        snakePointsList.get(i).setPositionY(headPositionY);
                        canvas.drawCircle(snakePointsList.get(i).getPositionX(), snakePointsList.get(i).getPositionY(), pointSize, createPaintColor());
                        headPositionX = getTempPositionX;
                        headPositionY = getTempPositionY;
                    }
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }, 1000- snakeMovingSpeed, 1000- snakeMovingSpeed);
    }

    private void growSnake(){
        SnakePoints snakePoints = new SnakePoints(0, 0);
        snakePointsList.add(snakePoints);
        foodSound.start();
        score++;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                scoreTV.setText(String.valueOf(score));
            }
        });
    }

    private boolean checkGameOver(int headPositionX, int headPositionY){
        boolean gameOver = false;

        //Check walls collision
        if(snakePointsList.get(0).getPositionX() < 0 ||
                snakePointsList.get(0).getPositionY() < 0 ||
                snakePointsList.get(0).getPositionX() >= surfaceView.getWidth() ||
                snakePointsList.get(0).getPositionX() >= surfaceView.getWidth())
        {
            deadSound.start();
            gameOver = true;
        //Check tail collision
        }else{
            for(int i = 1; i<snakePointsList.size(); i++){
                if(headPositionX == snakePointsList.get(i).getPositionX() && headPositionY == snakePointsList.get(i).getPositionY()){
                    gameOver = true;
                    deadSound.start();
                    break;
                }
            }
        }

        return gameOver;
    }

    private Paint createPaintColor(){
        if(pointColor == null){
            pointColor = new Paint();
            pointColor.setColor(snakeColor);
            pointColor.setStyle(Paint.Style.FILL);
            pointColor.setAntiAlias(true);
        }
        return pointColor;
    }
    private Paint createFoodPaintColor(){
        if(pointFoodColor == null){
            pointFoodColor = new Paint();
            pointFoodColor.setColor(foodColor);
            pointFoodColor.setStyle(Paint.Style.FILL);
            pointFoodColor.setAntiAlias(true);
        }
        return pointFoodColor;
    }
}