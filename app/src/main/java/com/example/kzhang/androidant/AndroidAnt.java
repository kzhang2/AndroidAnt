package com.example.kzhang.androidant;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class AndroidAnt extends Activity {
    AntMoves ball;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ball = new AntMoves(this);
        setContentView(ball);
    }
}


class AntMoves extends SurfaceView implements SurfaceHolder.Callback {
    GameThread thread;
    ant[] ants = new ant[10];
    int screenW; //Device's screen width.
    int screenH; //Devices's screen height.
    int bgrW;
    int bgrH;
    int bgrScroll;
    int dBgrY; //Background scroll speed.
    Bitmap bgr, bgrReverse;
    boolean reverseBackroundFirst;
    boolean antMove;

    //Measure frames per second.
    long now;
    int framesCount=0;
    int framesCountAvg=0;
    long framesTimer=0;
    Paint fpsPaint=new Paint();

    //Frame speed
    long timeNow;
    long timePrev = 0;
    long timePrevFrame = 0;
    long timeDelta;


    public AntMoves(Context context) {
        super(context);
        bgr = BitmapFactory.decodeResource(getResources(),R.drawable.sky_bgr); //Load a background.
        //Create a flag for the onDraw method to alternate background with its mirror image.
        reverseBackroundFirst = false;
        //Initialise animation variables.
        bgrScroll = 0;  //Background scroll position
        dBgrY = 1; //Scrolling background speed
        antMove = true;
        fpsPaint.setTextSize(30);

        //Set thread
        getHolder().addCallback(this);

        setFocusable(true);
    }

    @Override
    public void onSizeChanged (int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //This event-method provides the real dimensions of this custom view.
        screenW = w;
        screenH = h;

        bgr = Bitmap.createScaledBitmap(bgr, w, h, true); //Scale background to fit the screen.
        bgrW = bgr.getWidth();
        bgrH = bgr.getHeight();

        //Create a mirror image of the background (horizontal flip) - for a more circular background.
        Matrix matrix = new Matrix();  //Like a frame or mould for an image.
        matrix.setScale(-1, 1); //Horizontal mirror effect.
        bgrReverse = Bitmap.createBitmap(bgr, 0, 0, bgrW, bgrH, matrix, true); //Create a new mirrored bitmap by applying the matrix.
        for(int i = 0; i < ants.length; i++){
            ants[i] = new ant(w, h, 200, 500, BitmapFactory.decodeResource(getResources(),R.drawable.ant));
            ants[i].randomize();
        }
    }

    //***************************************
    //*************  TOUCH  *****************
    //***************************************
    @Override
    public synchronized boolean onTouchEvent(MotionEvent ev) {
        for(int i = 0; i < ants.length; i++){
            ant tAnt = ants[i];
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    tAnt.antX = (int) (ev.getX() - (tAnt.antW / 2));
                    tAnt.antY = (int) (ev.getY() - (tAnt.antH / 2));
                    antMove = false;
                    break;
                }

                case MotionEvent.ACTION_MOVE: {
                    tAnt.antX = (int) (ev.getX() - (tAnt.antW / 2));
                    tAnt.antY = (int) (ev.getY() - (tAnt.antH / 2));
                    antMove = false;
                    break;
                }

                case MotionEvent.ACTION_UP:
                    antMove = true;
                    break;
            }
        }
        return true;
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //Draw scrolling background.
        Rect fromRect1 = new Rect(0, 0, bgrW - bgrScroll, bgrH);
        Rect toRect1 = new Rect(bgrScroll, 0, bgrW, bgrH);

        Rect fromRect2 = new Rect(bgrW - bgrScroll, 0, bgrW, bgrH);
        Rect toRect2 = new Rect(0, 0, bgrScroll, bgrH);

        if (!reverseBackroundFirst) {
            canvas.drawBitmap(bgr, fromRect1, toRect1, null);
            canvas.drawBitmap(bgrReverse, fromRect2, toRect2, null);
        }
        else{
            canvas.drawBitmap(bgr, fromRect2, toRect2, null);
            canvas.drawBitmap(bgrReverse, fromRect1, toRect1, null);
        }

        //Next value for the background's position.
        if ( (bgrScroll += dBgrY) >= bgrW) {
            bgrScroll = 0;
            reverseBackroundFirst = !reverseBackroundFirst;
        }

        //DRAW BALL
        //Rotate method one
        /*
        Matrix matrix = new Matrix();
        matrix.postRotate(angle, (ballW / 2), (ballH / 2)); //Rotate it.
        matrix.postTranslate(ballX, ballY); //Move it into x, y position.
        canvas.drawBitmap(ball, matrix, null); //Draw the ball with applied matrix.

        */// Rotate method two

/*
        canvas.save(); //Save the position of the canvas matrix.
        canvas.rotate(angle, ballX + (ballW / 2), ballY + (ballH / 2)); //Rotate the canvas matrix.
        canvas.drawBitmap(ball, ballX, ballY, null); //Draw the ball by applying the canvas rotated matrix.
        canvas.restore(); //Rotate the canvas matrix back to its saved position - only the ball bitmap was rotated not all canvas.
*/
        for(int i = 0; i < ants.length; i++){
            ants[i].updateAnt(canvas,antMove);
        }

        //*/

        //Measure frame rate (unit: frames per second).

/*        canvas.drawText(framesCountAvg+" fps", 40, 70, fpsPaint);
        framesCount++;
        if(now-framesTimer>1000) {
            framesTimer=now;
            framesCountAvg=framesCount;
            framesCount=0;
        }*/
    }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        thread = new GameThread(getHolder(), this);
        thread.setRunning(true);
        thread.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        thread.setRunning(false);
        while (retry) {
            try {
                thread.join();
                retry = false;
            } catch (InterruptedException e) {

            }
        }
    }


    class GameThread extends Thread {
        private SurfaceHolder surfaceHolder;
        private AntMoves gameView;
        private boolean run = false;

        public GameThread(SurfaceHolder surfaceHolder, AntMoves gameView) {
            this.surfaceHolder = surfaceHolder;
            this.gameView = gameView;
        }

        public void setRunning(boolean run) {
            this.run = run;
        }

        public SurfaceHolder getSurfaceHolder() {
            return surfaceHolder;
        }

        @Override
        public void run() {
            Canvas c;
            while (run) {
                c = null;

                //limit frame rate to max 60fps
                timeNow = System.currentTimeMillis();
                timeDelta = timeNow - timePrevFrame;
                if ( timeDelta < 16) {
                    try {
                        Thread.sleep(16 - timeDelta);
                    }
                    catch(InterruptedException e) {

                    }
                }
                timePrevFrame = System.currentTimeMillis();

                try {
                    c = surfaceHolder.lockCanvas(null);
                    synchronized (surfaceHolder) {
                        //call methods to draw and process next fame
                        gameView.onDraw(c);
                    }
                } finally {
                    if (c != null) {
                        surfaceHolder.unlockCanvasAndPost(c);
                    }
                }
            }
        }
    }
}