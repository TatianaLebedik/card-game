package com.network.tatiana.sensors;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class LevelMainActivity extends AppCompatActivity implements SensorEventListener {

    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar
            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
           mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;

    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };

    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
           hide();
        }
    };

    private int displayWidth;
    private SensorManager sensorManager;
    private Sensor gyroscope;
    ImageView card_imageView;
    private int level = 1;
    private boolean start = true;
    private CardsStack cardsStack;
    private Card card = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_main);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        displayWidth = size.x;

        mVisible = true;
        mContentView = findViewById(R.id.frameLayoutFull);
        card_imageView = findViewById(R.id.card_imageView);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        cardsStack  = new CardsStack(this,level);
        try {

            gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        }catch(NullPointerException e){
            System.out.println(e);
        }

        onResume();


    }


    @Override
    public void onSensorChanged (SensorEvent sensorEvent) {

        Sensor mySensor = sensorEvent.sensor;

        if (mySensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {

            float[] rotationMatrix = new float[16];
            SensorManager.getRotationMatrixFromVector(rotationMatrix, sensorEvent.values);
            // Remap coordinate system
            float[] remappedRotationMatrix = new float[16];
            SensorManager.remapCoordinateSystem(rotationMatrix,
                    SensorManager.AXIS_Z,
                    SensorManager.AXIS_X,
                    remappedRotationMatrix);

            // Convert to orientations
            float[] orientations = new float[3];
            SensorManager.getOrientation(remappedRotationMatrix, orientations);
            for(int i = 0; i < 3; i++) {
                orientations[i] = (float)(Math.toDegrees(orientations[i]));
            }


            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) card_imageView.getLayoutParams();
            int x = layoutParams.leftMargin;
            card_imageView.getLayoutParams();


            if(Math.abs(orientations[2]) < 20 && (x < 0 || x > displayWidth/2) && x != 0) {

                if(start){
                    start = false;
                    card = cardsStack.getCard();
                    card_imageView.setImageResource(card.getCardResId());
                    System.out.println("ID:" + card.getCardResId());
                    layoutParams.leftMargin = 0;
                    card_imageView.setLayoutParams(layoutParams);
                }else {

                    //if (card == null) {

                    if ((x < 0 && card.getTrue_way().equals("right")) || (x > displayWidth / 2 && card.getTrue_way().equals("left"))) {
                        System.out.println("Wrong " + x);
                        Intent intent = new Intent(LevelMainActivity.this, ScoreActivity.class);
                        intent.putExtra("img", Integer.toString(card.getAnswResId()));
                        intent.putExtra("level", Integer.toString(level));
                        startActivity(intent);
                    } else {
                        card = cardsStack.getCard();
                        if (card == null) {
                            System.out.println("Huston, we have a problem...");

                        Intent intent = new Intent(LevelMainActivity.this, ScoreActivity.class);
                        int img = getResources().getIdentifier("card", "drawable", getPackageName());
                        System.out.println("EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE:" + img);
                        intent.putExtra("img", Integer.toString(img));
                        intent.putExtra("level", Integer.toString(level));
                        startActivity(intent);

                        } else {
                            System.out.println("ID:" + card.getCardResId());
                            card_imageView.setImageResource(card.getCardResId());
                            System.out.println("New card");
                            layoutParams.leftMargin = 0;
                            card_imageView.setLayoutParams(layoutParams);
                        }
                    }

                    //} else {
                    //   System.out.println("Huston, we have a problem...");
//
//                        Intent intent = new Intent(LevelMainActivity.this, ScoreActivity.class);
//                        int img = getResources().getIdentifier("card", "drawable", getPackageName());
//                        System.out.println("EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE:" + img);
//                        intent.putExtra("img", Integer.toString(img));
//                        intent.putExtra("level", level);
//                        startActivity(intent);
                    // }
                }
            }
            else if(Math.abs(orientations[2]) > 20){
                layoutParams.leftMargin = x - 5*(int)orientations[2];
                card_imageView.setLayoutParams(layoutParams);

            }

        }
    }

    @Override
    public void onAccuracyChanged(final Sensor sensor, final int accuracy) {
        // empty
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }
    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mVisible = false;

      //   Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */

    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }


}