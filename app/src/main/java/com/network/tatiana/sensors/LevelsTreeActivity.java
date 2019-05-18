package com.network.tatiana.sensors;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class LevelsTreeActivity extends AppCompatActivity {

    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_levels_tree);
        name = getIntent().getStringExtra("name");

        System.out.println("LEVELS TREE ACTIVITY START");

    }

    public void on_previous_button_click(View view){
        Intent intent = new Intent(LevelsTreeActivity.this, MainActivity.class);
        intent.putExtra("name", name);
        startActivity(intent);
    }

    public void on_level_1_button_click(View view){
        //Intent intent = new Intent(LevelsTreeActivity.this, LevelActivity.class);
        //intent.putExtra("sublevel", 0);
        //startActivity(intent);
        Intent intent = new Intent(LevelsTreeActivity.this, LevelMainActivity.class);
        //intent.putExtra("sublevel", 0);
        startActivity(intent);

    }
}
