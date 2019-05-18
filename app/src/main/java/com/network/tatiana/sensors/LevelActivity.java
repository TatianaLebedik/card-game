package com.network.tatiana.sensors;

import android.content.Context;
import android.content.Intent;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import java.util.ArrayList;
import java.util.List;

public class LevelActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private long lastUpdate = 0;
    private float last_x = 0, last_y = 0, last_z = 0;
    private static final int SHAKE_THRESHOLD = 600;
    ImageView card_imageView;
    int sublevels_amount = 9;
    int current_sublevel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> listSensor = sensorManager.getSensorList(Sensor.TYPE_ALL);
        List<String> listSensorType = new ArrayList<>();

        for (int i = 0; i < listSensor.size(); i++) {
            listSensorType.add(listSensor.get(i).getName());
        }

        for (int i = 0; i < listSensor.size(); i++) {
            System.out.println( listSensorType.get(i));
        }

        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometer , SensorManager.SENSOR_DELAY_NORMAL);
        card_imageView = findViewById(R.id.card_imageView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }
    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }
    @Override
    public void onSensorChanged (SensorEvent sensorEvent){


        Sensor mySensor = sensorEvent.sensor;

        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];

            long curTime = System.currentTimeMillis();

            if ((curTime - lastUpdate) > 100) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;

                float speed = Math.abs(x + y + z - last_x - last_y - last_z)/ diffTime * 10000;

                if (speed > SHAKE_THRESHOLD) {
                    System.out.println("x_last = " + last_x);
                    System.out.println("y_last = " + last_y);
                    System.out.println("z_last = " + last_z);
                }
                last_x = x;
                last_y = y;
                last_z = z;

            }
        }



     /*
        Sensor mySensor = sensorEvent.sensor;

        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float current_x = sensorEvent.values[0];
            System.out.println("x = " + current_x);
            System.out.println("x_last = " + last_x);
            System.out.println("\n");



            if(current_x - last_x > 3){
                last_x = current_x;
                Animation animation = AnimationUtils.loadAnimation(this, R.anim.card_to_left_animation);
                card_imageView.startAnimation(animation);
                Intent intent = new Intent(LevelActivity.this, LevelActivity.class);
                intent.putExtra("sublevel", current_sublevel + 1);
                System.out.println(current_sublevel);
                startActivity(intent);
                onPause();
                this.finish();
            }

            if(current_x - last_x  < -3){
                Animation animation = AnimationUtils.loadAnimation(this, R.anim.card_to_right_animation);
                card_imageView.startAnimation(animation);
                Intent intent = new Intent(LevelActivity.this, LevelActivity.class);
                intent.putExtra("sublevel", current_sublevel + 1);
                System.out.println(current_sublevel);
                startActivity(intent);

            }

        }

        */
    }

    @Override
    public void onAccuracyChanged(final Sensor sensor, final int accuracy) {
        // empty
    }

}