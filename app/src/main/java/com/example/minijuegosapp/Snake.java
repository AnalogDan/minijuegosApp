package com.example.minijuegosapp;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowInsetsController;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import android.media.MediaPlayer;
import android.content.Intent;
import android.os.Handler;
import android.view.MotionEvent;
import android.widget.ProgressBar;

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
    private boolean canMove = true;
    private int playableWidth;
    private int playableHeight;
    private boolean gameOver = false;
    private boolean isPaused = false;

    //To protect timer/game when blocking the phone or going to other app
    @Override
    protected void onPause() {
        super.onPause();

        if(timer != null){
            timer.cancel();
            timer.purge();
            timer = null;
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        if(isPaused || gameOver){
            drawGame();
        }
        else if(timer == null){
            moveSnake();
        }
    }
    private void drawGame(){ //Draws one frame, still to protect when switching app
        canvas = surfaceHolder.lockCanvas();
        if(canvas == null){
            return;
        }
        //Clear screen
        canvas.drawColor(Color.BLACK);
        //Create border paint
        Paint borderPaint = new Paint();
        borderPaint.setColor(Color.RED);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(8);
        //Draw playable area border
        canvas.drawRect(
                0,
                0,
                playableWidth,
                playableHeight,
                borderPaint
        );
        //Draw food
        canvas.drawCircle(
                positionX,
                positionY,
                pointSize,
                createFoodPaintColor()
        );
        //Draw snake head
        canvas.drawCircle(
                snakePointsList.get(0).getPositionX(),
                snakePointsList.get(0).getPositionY(),
                pointSize,
                createPaintColor()
        );
        //Draw snake body
        for(int i = 1; i < snakePointsList.size(); i++){

            canvas.drawCircle(
                    snakePointsList.get(i).getPositionX(),
                    snakePointsList.get(i).getPositionY(),
                    pointSize,
                    createPaintColor()
            );
        }
        surfaceHolder.unlockCanvasAndPost(canvas);
    }
    private void hideSystemUI(){
        WindowInsetsControllerCompat controller =
                ViewCompat.getWindowInsetsController(getWindow().getDecorView());
        if(controller != null){
            controller.hide(WindowInsetsCompat.Type.systemBars());
            controller.setSystemBarsBehavior(
                    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            );
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snake);

        //Ocultar barras del android que obstrullen botones
        hideSystemUI();

        //Set variables with elements
        surfaceView = findViewById(R.id.surfaceView);
        scoreTV = findViewById(R.id.scoreTV);
        final AppCompatImageButton pauseBtn = findViewById(R.id.pauseBtn);

        moveSound = MediaPlayer.create(this, R.raw.move);
        deadSound = MediaPlayer.create(this, R.raw.dead);
        foodSound = MediaPlayer.create(this, R.raw.food);

        final AppCompatImageButton topBtn = findViewById(R.id.topBtn);
        final AppCompatImageButton leftBtn = findViewById(R.id.leftBtn);
        final AppCompatImageButton rightBtn = findViewById(R.id.rightBtn);
        final AppCompatImageButton bottomBtn = findViewById(R.id.bottomBtn);

        surfaceView.getHolder().addCallback(this);

        //Movement buttons behavior
        topBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveSound.start();
                if (!movingPosition.equals("bottom") && canMove){
                    canMove = false;
                    movingPosition = "top";
                };
            }
        });
        leftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveSound.start();
                if (!movingPosition.equals("right") && canMove){
                    canMove = false;
                    movingPosition = "left";
                };
            }
        });
        rightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveSound.start();
                if (!movingPosition.equals("left") && canMove){
                    canMove = false;
                    movingPosition = "right";
                };
            }
        });
        bottomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveSound.start();
                if (!movingPosition.equals("top") && canMove){
                    canMove = false;
                    movingPosition = "bottom";
                };
            }
        });

        getOnBackPressedDispatcher().addCallback(this,
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        Intent intent = new Intent(Snake.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });

        //Back button and progress bar
        final AppCompatImageButton backBtn = findViewById(R.id.backBtn);
        final ProgressBar backHoldProgress = findViewById(R.id.backHoldProgress);
        final boolean[] holdingBackButton = {false};
        Handler backHandler = new Handler();
        Runnable backRunnable = new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(Snake.this, MainActivity.class);
                startActivity(intent);

                finish();
            }
        };
        backBtn.setOnTouchListener(new View.OnTouchListener() {
            Thread progressThread;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        holdingBackButton[0] = true;
                        backHoldProgress.setProgress(0);
                        progressThread = new Thread(() -> {
                            for(int i = 0; i <= 100; i++){
                                if(!holdingBackButton[0]){
                                    return;
                                }
                                if(!holdingBackButton[0]){
                                    return;
                                }
                                int progress = i;
                                runOnUiThread(() -> {
                                    if(holdingBackButton[0]){
                                        backHoldProgress.setProgress(progress);
                                    }
                                });
                                try{
                                    Thread.sleep(7);
                                }catch(Exception e){
                                    return;
                                }
                            }
                        });
                        progressThread.start();
                        backHandler.postDelayed(backRunnable, 700);
                        return true;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        holdingBackButton[0] = false;
                        backHandler.removeCallbacks(backRunnable);
                        if(progressThread != null){
                            progressThread.interrupt();
                        }
                        runOnUiThread(() -> backHoldProgress.setProgress(0));
                        return true;
                }
                return false;
            }
        });

        //Pause behavior
        pauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isPaused){
                    moveSound.start();
                    isPaused = true;
                    if(timer != null){
                        timer.cancel();
                        timer.purge();
                        timer = null;
                    }
                    backBtn.setVisibility(View.VISIBLE);
                    backHoldProgress.setVisibility(View.VISIBLE);
                    pauseBtn.setImageResource(android.R.drawable.ic_media_play);
                }else{
                    isPaused = false;
                    if(timer == null){
                        moveSnake();
                    }
                    backBtn.setVisibility(View.INVISIBLE);
                    backHoldProgress.setVisibility(View.INVISIBLE);
                    pauseBtn.setImageResource(android.R.drawable.ic_media_pause);
                }
            }
        });
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        this.surfaceHolder = holder;
        if(snakePointsList.isEmpty()){
            init();
        }else{
            drawGame();
            if(!isPaused && !gameOver && timer == null){
                moveSnake();
            }
        }
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {

    }

    private void init(){
       //Reiniciar la longitud y pintaje
        gameOver = false;
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

        //Definir zona jugable basándose en el grid de movimiento
        int gridSize = pointSize * 2;
        playableWidth = surfaceView.getWidth() - (surfaceView.getWidth() % gridSize);
        playableHeight = surfaceView.getHeight() - (surfaceView.getHeight() % gridSize);

        //Crear comida y mover snake
        addPoint();
        moveSnake();

    }


    private void addPoint(){
        int surfaceWidth = playableWidth - (pointSize * 2);
        int surfaceHeight = playableHeight - (pointSize * 2);
        boolean validPosition = false;
        while(!validPosition){
            int randomXPosition = new Random().nextInt(surfaceWidth / pointSize);
            int randomYPosition = new Random().nextInt(surfaceHeight / pointSize);
            if((randomXPosition % 2) != 0){
                randomXPosition++;
            }
            if((randomYPosition % 2) != 0){
                randomYPosition++;
            }
            int newPositionX = (pointSize * randomXPosition) + pointSize;
            int newPositionY = (pointSize * randomYPosition) + pointSize;
            validPosition = true;

            //Check collision with snake body
            for(int i = 0; i < snakePointsList.size(); i++){
                if(snakePointsList.get(i).getPositionX() == newPositionX &&
                        snakePointsList.get(i).getPositionY() == newPositionY)
                {
                    validPosition = false;
                    break;
                }
            }

            //If valid, assign final position
            if(validPosition){
                positionX = newPositionX;
                positionY = newPositionY;
            }
        }
    }

    private void moveSnake(){
        if(timer != null){
            timer.cancel();
            timer.purge();
        }
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run(){
                canMove = true;
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
                            AlertDialog dialog = builder.show();

                            WindowInsetsControllerCompat dialogController =
                                    ViewCompat.getWindowInsetsController(dialog.getWindow().getDecorView());

                            if(dialogController != null){

                                dialogController.hide(WindowInsetsCompat.Type.systemBars());

                                dialogController.setSystemBarsBehavior(
                                        WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                                );
                            }
                        }
                    });
                }else{
                    canvas = surfaceHolder.lockCanvas();
                    //Clear screen
                    canvas.drawColor(Color.BLACK);
                    //Create border paint
                    Paint borderPaint = new Paint();
                    borderPaint.setColor(Color.RED);
                    borderPaint.setStyle(Paint.Style.STROKE);
                    borderPaint.setStrokeWidth(8);
                    //Draw playable area border
                    canvas.drawRect(
                            0,
                            0,
                            playableWidth,
                            playableHeight,
                            borderPaint
                    );
                    //Draw snake head
                    canvas.drawCircle(
                            snakePointsList.get(0).getPositionX(),
                            snakePointsList.get(0).getPositionY(),
                            pointSize,
                            createPaintColor()
                    );
                    //Draw food
                    canvas.drawCircle(
                            positionX,
                            positionY,
                            pointSize,
                            createFoodPaintColor()
                    );

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
        boolean isGameOver = false;

        //To make collisions align nicely with walls
        int gridSize = pointSize * 2;
        int playableWidth = surfaceView.getWidth() - (surfaceView.getWidth() % gridSize);
        int playableHeight = surfaceView.getHeight() - (surfaceView.getHeight() % gridSize);

        //Check walls collision
        if(snakePointsList.get(0).getPositionX() - pointSize < 0 ||
                snakePointsList.get(0).getPositionY() - pointSize < 0 ||
                snakePointsList.get(0).getPositionX() + pointSize > playableWidth ||
                snakePointsList.get(0).getPositionY() + pointSize > playableHeight)
        {
            deadSound.start();

            isGameOver = true;
            gameOver = true;

            timer.purge();
            timer.cancel();
        //Check tail collision
        }else{
            for(int i = 1; i < snakePointsList.size(); i++){

                if(snakePointsList.get(0).getPositionX() == snakePointsList.get(i).getPositionX() &&
                        snakePointsList.get(0).getPositionY() == snakePointsList.get(i).getPositionY())
                {
                    deadSound.start();
                    isGameOver = true;
                    gameOver = true;
                    timer.purge();
                    timer.cancel();
                    break;
                }
            }
        }

        return isGameOver;
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