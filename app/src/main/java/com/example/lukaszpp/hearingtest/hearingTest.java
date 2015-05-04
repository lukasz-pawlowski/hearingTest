package com.example.lukaszpp.hearingtest;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;


public class hearingTest extends ActionBarActivity {

    private int probkowanie = 8000;
    private int iloscProbek = 20 * probkowanie; //ilosc sekund * ilosc probkowania na sekunde
    private int probekNaHz = 20;
    private double sample[] = new double[iloscProbek]; //próbka

    //częstotliwość
   // private final double freqOfTone = 0; // hz
    private double minFreq = 1;
    private double maxFreq = 44000;

    private byte generatedSnd[] = new byte[2 * iloscProbek]; //dźwięk

    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hearing_test);

        //pierwszy seek bar
        SeekBar seekBarObject = (SeekBar) findViewById(R.id.seekBar);
        seekBarObject.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChanged = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
                progressChanged = progress;
                TextView textViewObject = (TextView) findViewById(R.id.textView);
                SeekBar seekBarObject = (SeekBar) findViewById(R.id.seekBar);
                int intprogress = seekBarObject.getProgress();
                textViewObject.setText((String.valueOf(intprogress)));

            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO niepotzebne?
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
               // TODO niepotrzebne?
            }
        });

        //drugi seek bar
        SeekBar seekBarObject2 = (SeekBar) findViewById(R.id.seekBar2);
        seekBarObject2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChanged = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
                progressChanged = progress;
                TextView textViewObject = (TextView) findViewById(R.id.textView2);
                SeekBar seekBarObject2 = (SeekBar) findViewById(R.id.seekBar2);
                int intprogress = seekBarObject2.getProgress();
                textViewObject.setText((String.valueOf(intprogress)));
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO niepotzebne?
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO niepotrzebne?
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_hearing_test, menu);
        return true;
    }

    /*
    @Override
    protected void onResume() {
        super.onResume();

        // Use a new tread as this can take a while
        final Thread thread = new Thread(new Runnable() {
            public void run() {
                genTone();
                handler.post(new Runnable() {

                    public void run() {
                        playSound();
                    }
                });
            }
        });
        thread.start();
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    //graj, muzyko
    public void startTest(View view){

        //pobranie wartosci min i max
        SeekBar seekBarObject = (SeekBar) findViewById(R.id.seekBar);
        this.minFreq = seekBarObject.getProgress();
        seekBarObject = (SeekBar) findViewById(R.id.seekBar2);
        this.maxFreq = seekBarObject.getProgress();


        //dystans między min i max freq
        if(this.minFreq > this.maxFreq)
            this.maxFreq = this.maxFreq + 1;
        double freqDist = this.maxFreq - this.minFreq;

        double currentFreq = this.minFreq;

        // wypelnij tablice
        for (int i = 0; i < this.iloscProbek; ++i) {
            sample[i] = Math.sin(2 * Math.PI * i / (this.probkowanie/currentFreq));
            if(i % this.probekNaHz == 0){
                currentFreq = currentFreq + 1;
            }
        }
/*
        //obliczanie skoku - co ile probek skok frequency
        //this.iloscProbek
        double jump = this.iloscProbek / freqDist;

        //ustalenie wartosci min i max dla danego herca
        double currentMin = this.minFreq;
        double currentMax = this.minFreq + jump;
        //petla dla kazdego Hz
        while(currentMin < this.maxFreq) {

            //petla dla kazdej probki danego Hz
            double i = 0;
            while(i < jump){
                int j = ((int) currentMin) + ((int) i);
                sample[j] = Math.sin(2 * Math.PI * i /(this.probkowanie/currentFreq));
                i = i +1;
            }

            //podniesienie countera probki do nastepnego herza
            currentFreq = currentFreq + 1;
            currentMin = currentMax;
            currentMax = currentMax + jump;
        }
*/
        //wariacki bufor
        int idx = 0;
        for (final double dVal : sample) {
            // scale to maximum amplitude
            final short val = (short) ((dVal * 32767));
            // in 16 bit wav PCM, first byte is the low order byte
            generatedSnd[idx++] = (byte) (val & 0x00ff);
            generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);

        }

        //graj, muzyko
        final AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                this.probkowanie, AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT, generatedSnd.length,
                AudioTrack.MODE_STATIC);
        audioTrack.write(generatedSnd, 0, generatedSnd.length);
        audioTrack.play();

    }
/*
    void genTone(int no){
        // fill out the array
        for (int i = 0; i < numSamples; ++i) {
            sample[i] = Math.sin(2 * Math.PI * i / (sampleRate/freqOfTone));
        }

        // convert to 16 bit pcm sound array
        // assumes the sample buffer is normalised.
        int idx = 0;
        for (final double dVal : sample) {
            // scale to maximum amplitude
            final short val = (short) ((dVal * 32767));
            // in 16 bit wav PCM, first byte is the low order byte
            generatedSnd[idx++] = (byte) (val & 0x00ff);
            generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);

        }
    }

    void playSound(){
        final AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                sampleRate, AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT, generatedSnd.length,
                AudioTrack.MODE_STATIC);
        audioTrack.write(generatedSnd, 0, generatedSnd.length);
        audioTrack.play();
    }
*/
}
