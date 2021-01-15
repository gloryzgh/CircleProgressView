package com.ghzhang.circleprogressbar;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.ghzhang.view.CircleProgressBar;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button bt = findViewById(R.id.button5);
        CircleProgressBar circleProgressBar = findViewById(R.id.circleProgressBar);
        bt.setOnClickListener(v -> {
           int  currentProgress = new Random().nextInt(100);
            Log.d("like", "onCreate: " + currentProgress);
            circleProgressBar.setProgress(currentProgress);
        });
    }
}