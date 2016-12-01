package com.example.kzhang.androidant;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;

/**
 * Created by kzhang on 11/30/2016.
 */

public class ant {
    int antX; //ant x position.
    int antY; //ant y position.
    int dAngle; // how much the angle changes, determined randomly
    int angleCounter;
    int rRate; //rotation rate
    int rotTime; //how many times it rotates, rate
    int angle; //angle turned through by ant
    int antW;  // height
    int antH;  //width
    Bitmap antPic;
    float speed; //ant speed
    boolean isRotating;
    int screenW;
    int screenH;
    long framesTimer2 = 0;

    public ant(int screenW_, int screenH_, int antX_,  int antY_, Bitmap antPic_){
        screenW = screenW_;
        screenH = screenH_;
        antX = antX_;
        antY = antY_;
        antPic = antPic_;
    }

    public ant(int screenW_, int screenH_, int angleCounter_, int rRate_,float speed_, int angle_, Bitmap antPic_, int rotTime_){
        angleCounter = angleCounter_;
        rRate = rRate_;
        speed = speed_;
        angle = angle_;
        antPic = antPic_;
        antW = antPic.getWidth();
        antH = antPic.getHeight();
        antX = 200;
        antY = 500;
        rotTime = rotTime_;
        screenW = screenW_;
        screenH = screenH_;
    }
    public void updateAnt( Canvas canvas, boolean isMoving){
        //Compute roughly the ant's speed and location.
        //Increase rotating angle
        if(isRotating && Math.abs(angleCounter) < Math.abs(dAngle)){
            if(dAngle > 0){
                angle = (angle + rRate) % 360;
                angleCounter+=rRate;
            } else if (dAngle < 0){
                angle = (angle - rRate + 360) % 360;
                angleCounter-=rRate;
            }
            framesTimer2 = System.currentTimeMillis();
        } else if (isRotating && Math.abs(angleCounter) > Math.abs(dAngle)){
            isRotating = false;
        }

        long now=System.currentTimeMillis();
        if(now - framesTimer2 >  rotTime){
            framesTimer2 = now;
            isRotating = true;
            dAngle = (int) (144 * Math.random() - 72);
            angleCounter = 0;
        }
        if(isMoving){
            antY += (int) (speed * Math.sin(Math.PI * angle / 180.0));
            antX += (int) (speed * Math.cos(Math.PI * angle / 180.0));
        }

/*            dY+= acc; //Increase or decrease speed.*/
        canvas.save(); //Save the position of the canvas matrix.
        canvas.rotate(angle, antX + (antW / 2),   antY+(antH / 2)); //Rotate the canvas matrix.
        canvas.drawBitmap(antPic, antX, antY, null); //Draw the ball by applying the canvas rotated matrix.
        canvas.restore(); //Rotate the canvas matrix back to its saved position - only the ball bitmap was rotated not all canvas.
    }

    public void randomize(){
        speed = (float) (3 * Math.random() + 1);
        rotTime = (int) (2000 * Math.random() + 1000);
        rRate = (int) (1 * Math.random() + 2);
    }
}
