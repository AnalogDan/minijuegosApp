package com.example.minijuegosapp.brickgame;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.media.MediaPlayer;
import android.os.Handler;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;

import com.example.minijuegosapp.R;

import java.util.Random;

public class GameView extends View {

    Context context;
    float ballX, ballY;
    Velocity velocity = new Velocity(25, 32);
    Handler handler;
    final long UPDATE_MILLIS = 30;
    Runnable runnable;
    int[] palette = {
            Color.parseColor("#001F3F"),
            Color.parseColor("#003566"),
            Color.parseColor("#00509D"),
            Color.parseColor("#C100D8"),
            Color.parseColor("#FF5DD8"),
            Color.parseColor("#C053FF")
    };
    //Gestión de colores
    int currentBallColor;
    int currentPaddleColor;
    int backgroundColor = Color.parseColor("#D3FFF7"); // azul oscuro
    Paint textPaint = new Paint();
    Paint healthPaint = new Paint();
    Paint brickPaint = new Paint();
    Paint ballPaint = new Paint();
    Paint paddlePaint = new Paint();
    float TEXT_SIZE = 120;
    float paddleX, paddleY;
    float oldX, oldPaddleX;
    int points = 0;
    int life = 3;
    Bitmap ball, paddle;
    int dWidth, dHeight;
    int ballWidth, ballHeight;
    MediaPlayer mpHit, mpMiss, mpBreak;
    Random random;
    Brick[] bricks = new Brick[32];
    int numBricks = 0;
    int brokenBricks = 0;
    boolean gameOver = false;

    public GameView(Context context) {
        super(context);
        this.context = context;
        ball = BitmapFactory.decodeResource(getResources(), R.drawable.brickgame_ball);
        paddle = BitmapFactory.decodeResource(getResources(), R.drawable.brickgame_paddle);
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                invalidate();
            }
        };
        mpHit = MediaPlayer.create(context, R.raw.brickgame_hit);
        mpMiss = MediaPlayer.create(context, R.raw.brickgame_miss);
        mpBreak = MediaPlayer.create(context, R.raw.brickgame_breaking);
        textPaint.setColor(Color.RED);
        textPaint.setTextSize(TEXT_SIZE);
        textPaint.setTextAlign(Paint.Align.LEFT);
        healthPaint.setColor(Color.GREEN);
        //brickPaint.setColor(Color.argb(255, 249, 129, 0));
        //Colores
        currentBallColor = palette[2];
        currentPaddleColor = palette[3];
        Display display = ((Activity) getContext()).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        dWidth = size.x;
        dHeight = size.y;
        random = new Random();
        ballX = random.nextInt(dWidth - 50);
        ballY = dHeight / 3;
        paddleY = (dHeight * 4) / 5;
        paddleX = dWidth / 2 - paddle.getWidth() / 2;
        ballWidth = ball.getWidth();
        ballHeight = ball.getHeight();
        createBricks();
    }

    private void createBricks() {
        int brickWidth = dWidth / 8;
        int brickHeight = dHeight / 16;
        for (int column = 0; column < 8; column++) {
            for (int row = 0; row < 4; row++) {
                bricks[numBricks] = new Brick(row, column, brickWidth, brickHeight,randomPaletteColor());
                numBricks++;
            }
        }
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        // Fondo
        canvas.drawColor(backgroundColor);

        // Movimiento pelota
        ballX += velocity.getX();
        ballY += velocity.getY();

        // Rebote paredes laterales
        if ((ballX >= dWidth - ball.getWidth()) || ballX <= 0) {
            if (mpHit != null) {
                mpHit.start();
            }
            velocity.setX(velocity.getX() * -1);
            currentBallColor = randomPaletteColor();
        }

        // Rebote parte superior
        if (ballY <= 0) {
            if (mpHit != null) {
                mpHit.start();
            }
            velocity.setY(velocity.getY() * -1);
            currentBallColor = randomPaletteColor();
        }

        // Pelota cae
        if (ballY > paddleY + paddle.getHeight()) {

            ballX = 1 + random.nextInt(dWidth - ball.getWidth() - 1);
            ballY = dHeight / 3;

            if (mpMiss != null) {
                mpMiss.start();
            }

            velocity.setX(xVelocity());
            velocity.setY(32);

            life--;

            if (life == 0) {
                gameOver = true;
                launchGameOver();
            }
        }

        // Colisión con paleta
        if (((ballX + ball.getWidth()) >= paddleX)
                && (ballX <= paddleX + paddle.getWidth())
                && (ballY + ball.getHeight() >= paddleY)
                && (ballY + ballHeight <= paddleY + paddle.getHeight())) {

            if (mpHit != null) {
                mpHit.start();
            }

            velocity.setX(velocity.getX() + 1);
            velocity.setY((velocity.getY() + 1) * -1);
            currentBallColor = randomPaletteColor();
            currentPaddleColor = randomPaletteColor();
        }

        //Colorear sprites
        ballPaint.setColorFilter(
                new PorterDuffColorFilter(currentBallColor, PorterDuff.Mode.SRC_ATOP)
        );
        paddlePaint.setColorFilter(
                new PorterDuffColorFilter(currentPaddleColor, PorterDuff.Mode.SRC_ATOP)
        );
        // Dibujar pelota
        canvas.drawBitmap(ball, ballX, ballY, ballPaint);

        // Dibujar paleta
        canvas.drawBitmap(paddle, paddleX, paddleY, paddlePaint);

        // Dibujar bricks
        for (int i = 0; i < numBricks; i++) {

            if (bricks[i].getVisibility()) {
                brickPaint.setColor(bricks[i].color);
                canvas.drawRect(
                        bricks[i].column * bricks[i].width + 1,
                        bricks[i].row * bricks[i].height + 1,
                        bricks[i].column * bricks[i].width + bricks[i].width - 1,
                        bricks[i].row * bricks[i].height + bricks[i].height - 1,
                        brickPaint
                );

                // Colisión pelota-brick
                if (ballX + ballWidth >= bricks[i].column * bricks[i].width
                        && ballX <= bricks[i].column * bricks[i].width + bricks[i].width
                        && ballY <= bricks[i].row * bricks[i].height + bricks[i].height
                        && ballY >= bricks[i].row * bricks[i].height) {

                    if (mpBreak != null) {
                        mpBreak.start();
                    }

                    velocity.setY((velocity.getY() + 1) * -1);
                    currentBallColor = randomPaletteColor();

                    bricks[i].setInvisible();

                    points += 10;

                    brokenBricks++;
                }
            }
        }

        // Mostrar puntos
        canvas.drawText("" + points, 20, TEXT_SIZE, textPaint);

        // Barra de vida
        if (life == 2) {
            healthPaint.setColor(Color.YELLOW);
        } else if (life == 1) {
            healthPaint.setColor(Color.RED);
        }

        canvas.drawRect(
                dWidth - 200,
                30,
                dWidth - 200 + 60 * life,
                80,
                healthPaint
        );

        // Ganar juego
        if (brokenBricks == numBricks) {
            gameOver = true;
            launchGameOver();
        }

        // Siguiente frame
        if (!gameOver) {
            handler.postDelayed(runnable, UPDATE_MILLIS);
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();
        if (touchY >= paddleY) {
            int action = event.getAction();
            if (action == MotionEvent.ACTION_DOWN) {
                oldX = event.getX();
                oldPaddleX = paddleX;
            }
            if (action == MotionEvent.ACTION_MOVE) {
                float shift = oldX - touchX;
                float newPaddleX = oldPaddleX - shift;
                if (newPaddleX <= 0) {
                    paddleX = 0;
                } else if (newPaddleX >= dWidth - paddle.getWidth()) {
                    paddleX = dWidth - paddle.getWidth();
                } else {
                    paddleX = newPaddleX;
                }
            }
        }
        return true;
    }

    private void launchGameOver() {
        handler.removeCallbacksAndMessages(null);
        Intent intent = new Intent(context, GameOver.class);
        intent.putExtra("points",points);
        context.startActivity(intent);
        ((Activity) context).finish();
    }

    private int xVelocity() {
        int[] values = {-35, -30, -25, 25, 30, 35};
        int index = random.nextInt(values.length);
        return values[index];
    }
    private int randomPaletteColor() {
        return palette[random.nextInt(palette.length)];
    }
}
