 package com.example.insan.timerdemo;

import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

 public class MainActivity extends AppCompatActivity {

     SeekBar timerSeekBar;
     final TextView timerTextView;

     public void updateTimer(int secondsLeft){

         int minutes = (int) secondsLeft /60;
         int seconds = secondsLeft - minutes * 60;



         String secondString = Integer.toString(seconds);

         if(secondString == "0") {
             secondString = "00";
         }
         timerTextView.setText(Integer.toString(minutes) + ":" + Integer.toString(seconds));

     }

     public void controllerTimer(View view) {
         new CountDownTimer(timerSeekBar.getProgress() * 1000, 1000) {
             @Override
             public void onTick(long millisUntilFinished) {
                    updateTimer((int) milisUntilFinished/1000);
             }

             @Override
             public void onFinish() {

             }
         };
     }

     @Override
     protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_main);

         timerSeekBar = (SeekBar) findViewById(R.id.timerseekbar);
         timerTextView = (TextView)findViewById(R.id.timerTextView);
         Button controllerButton = (Button)findViewById(R.id.controllerButton);

         timerSeekBar.setMax(600);
         timerSeekBar.setProgress(30);
         timerSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
             @Override
             public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                    updateTimer(progress);

             }

             @Override
             public void onStartTrackingTouch(SeekBar seekBar) {

             }

             @Override
             public void onStopTrackingTouch(SeekBar seekBar) {

             }
         });


     }
 }

