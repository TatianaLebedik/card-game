package com.network.tatiana.sensors;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    Button start_button;
    EditText name_textEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        start_button = findViewById(R.id.start_button);
        name_textEdit = findViewById(R.id.name_editText);

        if(getIntent().getStringExtra("name") != null) {
            name_textEdit.setText(getIntent().getStringExtra("name"));
        }
        System.out.println("MAIN ACTIVITY START");
    }

    public void on_start_button_click(View view){

        String name = name_textEdit.getText().toString();
        System.out.println("button clicked");
        System.out.println(name);

        //TODO: connect to DB and check name

        Intent intent = new Intent(MainActivity.this, LevelsTreeActivity.class);
        intent.putExtra("name", name);
        startActivity(intent);


    }
}
