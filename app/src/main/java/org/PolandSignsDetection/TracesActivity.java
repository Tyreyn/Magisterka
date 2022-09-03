package org.PolandSignsDetection;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_trace);
        assetManager = getAssets();
        readTracesFile(assetManager);
        traceView = (LinearLayout) findViewById(R.id.available_trace);
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
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            Button btn = new Button(this);
            btn.setId(i);
            final int id_ = btn.getId();
            btn.setText("Trasa " + id_);
            btn.setBackgroundColor(Color.rgb(255,255, 255));
            traceView.addView(btn, params);
            startDetect = ((Button) findViewById(id_));
            startDetect.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    Intent intent = new Intent(TracesActivity.this, ViewTracesActivity.class);
                    intent.putExtra("Numer_Trasy", view.getId());
                    intent.putExtra("Trasa",parseTraceFile("trace"+view.getId()+".txt"));
                    startActivity(intent);
                    return false;
                }
            });
            startDetect.setOnClickListener((View.OnClickListener) (view) -> {
                Intent intent = new Intent(TracesActivity.this, DetectorActivity.class);
                intent.putExtra("Numer_Trasy", view.getId());
                intent.putExtra("Trasa",parseTraceFile("trace"+view.getId()+".txt"));
                startActivity(intent);
            });
        }
    }
    private String[] parseTraceFile(String fileName){
        BufferedReader reader;
        String[] buffor = new String[200];
        String pathToFile = getFilesDir().getAbsolutePath()+ File.separator+ constants.tracesDirName+fileName;
        int i = 0;
        try{
            final InputStream file = new FileInputStream(pathToFile);
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

        if (i == 0){
            return null;
        }
        return Arrays.copyOfRange(buffor,0,i-1);
    }
    private void readTracesFile(AssetManager mgr) {
        detectedTraces = new ArrayList<String>();
        String dirPath = getFilesDir().getAbsolutePath() + File.separator + constants.tracesDirName;

        try {
            File directory = new File(dirPath);
                File[] files = directory.listFiles();
                for (File file : files) {
                    LOGGER.d("[TRACES_FILE] znaleziono:" + file);
                    detectedTraces.add(file.getName());
                }
            } catch(Exception e){
                e.printStackTrace();
            }

    }
}
