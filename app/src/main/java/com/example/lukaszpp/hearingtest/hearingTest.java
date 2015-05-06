package com.example.lukaszpp.hearingtest;

import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Chronometer;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;


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

    //media recorder
    private MediaRecorder myAudioRecorder;

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

            }

            public void onStopTrackingTouch(SeekBar seekBar) {

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

            }

            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_hearing_test, menu);
        return true;
    }



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

    //rozpoczęcie nagrywania
    public void startRecord(View view){
        try {

            //rekorder audio
            myAudioRecorder = new MediaRecorder();
            myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
            myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
            //myAudioRecorder.setOutputFile(outputFile);

            //plik
            Long tsLong = System.currentTimeMillis()/1000;
            String filename = Environment.getExternalStorageDirectory().
                    getAbsolutePath() + "/" + tsLong.toString() + ".3gp";

            //start
            myAudioRecorder.setOutputFile(filename);
            myAudioRecorder.prepare();
            myAudioRecorder.start();

            //start chronometer
            TextView textViewObject = (TextView) findViewById(R.id.textView3);
            textViewObject.setText("Nagrywanie");

        } catch (IllegalStateException e) {

            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        }


    }

    //zatrzymanie nagrywania i zapisanie pliku
    public void stopRecord(View view){

        //zatrzymaj audio
        myAudioRecorder.stop();
        myAudioRecorder.reset();
        myAudioRecorder.release();
        myAudioRecorder  = null;

        //zatrzymaj chronometr
        TextView textViewObject = (TextView) findViewById(R.id.textView3);
        textViewObject.setText("Nagrywanie Stop");
    }

    //otwórz nagrania
    public void otworzNagrania(View view){
        //create intent
        Intent intent = new Intent(this, nagrania.class);

        //wystartowanie aktywnosci
        startActivity(intent);
    }
}
