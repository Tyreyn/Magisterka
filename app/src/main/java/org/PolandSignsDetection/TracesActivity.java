package org.PolandSignsDetection;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.PolandSignsDetection.env.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

public class TracesActivity extends AppCompatActivity {
    public static final String TRACES = "Traces/";
    private static final Logger LOGGER = new Logger();
    private LinearLayout traceView;
    protected ArrayList<String> detectedTraces;
    AssetManager assetManager;
    private Button startDetect;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_trace);
        assetManager = getAssets();
        readTracesFile(assetManager, TRACES);
        traceView = (LinearLayout) findViewById(R.id.choose_trace);
        /**traceView = findViewById(R.id.trace_list);
        ArrayAdapter<String> deviceAdapter =
                new ArrayAdapter<>(
                        TracesActivity.this , R.layout.listview_row, R.id.listview_row_text, detectedTraces);
        traceView.setAdapter(deviceAdapter);**/
        setUpTraces();
    }
    private void setUpTraces(){
        for (int i = 0; i < detectedTraces.size(); i++) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            Button btn = new Button(this);
            btn.setId(i);
            final int id_ = btn.getId();
            btn.setText("Trasa " + id_);
            btn.setBackgroundColor(Color.rgb(255,255, 255));
            traceView.addView(btn, params);
            startDetect = ((Button) findViewById(id_));
            startDetect.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    Toast.makeText(TracesActivity.this,"Wczytano mapę: " + view.getId(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(TracesActivity.this, DetectorActivity.class);
                    intent.putExtra("Trasa",parseTraceFile(assetManager,TRACES+"trace"+view.getId()+".txt"));
                    startActivity(intent);
                    return false;
                }
            });
        }
    }
    private String[] parseTraceFile(AssetManager assetManager, String pathToFile){
        BufferedReader reader;
        String[] buffor = new String[200];
        int i = 0;
        try{
            final InputStream file = assetManager.open(pathToFile);
            reader = new BufferedReader(new InputStreamReader(file));
            String line;
            while((line=reader.readLine()) != null){
                LOGGER.d("[TRACE] Kolejność znaków na trasie["+i+"]: "+ line);
                buffor[i] =line;
                i++;
            }
        } catch(IOException ioe){
            ioe.printStackTrace();
        }
        return Arrays.copyOfRange(buffor,0,i-1);
    }
    private void readTracesFile(AssetManager mgr, String path) {
        detectedTraces = new ArrayList<String>();
        try {
            String[] files = mgr.list(path);
            for (String file : files) {
                String[] splits = file.split("\\.");
                if (splits[splits.length - 1].equals("txt")) {
                    LOGGER.d("[TRACES_FILE] znaleziono:" +file);
                    detectedTraces.add(file);
                }
            }
        }
        catch (IOException e)
        {
            LOGGER.d("[TRACES_FILE] Błąd przy szukaniu pliku: " + e.getMessage());
        }
    }
}
