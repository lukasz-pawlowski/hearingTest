package com.example.lukaszpp.hearingtest;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class nagrania extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nagrania);

        TextView textViewObject = (TextView) findViewById(R.id.listaplikow);
        textViewObject.setText("Wybierz nagranie");

        //spinner
        Spinner spinnerObject = (Spinner) findViewById(R.id.spinner);

        List<String> list = new ArrayList<String>();



        File dirFiles = Environment.getExternalStorageDirectory();
        for (String strFile : dirFiles.list()) {
            //wez nazwe pliku
            list.add(strFile);
            //textViewObject.append(strFile + "\n");

        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerObject.setAdapter(dataAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_nagrania, menu);
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

    public void playRecording(View view){

        //wez dane ze spinner
        Spinner spinnerObject = (Spinner) findViewById(R.id.spinner);
        TextView textViewObject = (TextView) findViewById(R.id.listaplikow);

        //ścieżka do pliku
        String file = String.valueOf(spinnerObject.getSelectedItem());
        String path =  Environment.getExternalStorageDirectory().
                getAbsolutePath() + "/"  + file;

        textViewObject.setText(path);

        Uri uri = Uri.parse(path);
        //graj plik



        try {
            MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(getApplicationContext(), uri);
            mediaPlayer.prepare();
            mediaPlayer.start();
        }catch(IOException e) {

        }

    }
}
