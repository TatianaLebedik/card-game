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

        View overlay = findViewById(R.id.frameLayout);
        overlay.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_FULLSCREEN);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        displayWidth = size.x;

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

            //Get current imageView location
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) card_imageView.getLayoutParams();
            int x = layoutParams.leftMargin;
            card_imageView.getLayoutParams();

            //if card skipped
            if(Math.abs(orientations[2]) < 20 && (x < 0 || x > displayWidth/2) && x != 0) {

                //if it first card with task
                if(start){
                    start = false;
                    card = cardsStack.getCard();
                    card_imageView.setImageResource(card.getCardResId());
                    layoutParams.leftMargin = 0;
                    card_imageView.setLayoutParams(layoutParams);
                }else {
                    //if skipped wrong
                    if ((x < 0 && card.getTrue_way().equals("right")) || (x > displayWidth / 2 && card.getTrue_way().equals("left"))) {
                        System.out.println("Wrong " + x);
                        //Go to score activity
                        Intent intent = new Intent(LevelMainActivity.this, ScoreActivity.class);
                        intent.putExtra("img", Integer.toString(card.getAnswResId()));
                        intent.putExtra("level", Integer.toString(level));
                        startActivity(intent);
                        //if skipped right
                    } else {
                        card = cardsStack.getCard();
                        //if next card  not exist
                        if (card == null) {
                        //go to score activity
                        Intent intent = new Intent(LevelMainActivity.this, ScoreActivity.class);
                        int img = getResources().getIdentifier("score_card", "drawable", getPackageName());
                        intent.putExtra("img", Integer.toString(img));
                        intent.putExtra("level", Integer.toString(level));
                        startActivity(intent);
                        //if card exist
                        } else {
                            //set next card
                            card_imageView.setImageResource(card.getCardResId());
                            layoutParams.leftMargin = 0;
                            card_imageView.setLayoutParams(layoutParams);
                        }
                    }
                }
            }
            //skipping animation
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

}