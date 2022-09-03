package org.PolandSignsDetection;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ConfigurationInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.PolandSignsDetection.customview.OverlayView;
import org.PolandSignsDetection.env.Logger;
import org.PolandSignsDetection.tflite.Classifier;
import org.PolandSignsDetection.tracking.MultiBoxTracker;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static final float MINIMUM_CONFIDENCE_TF_OD_API = 0.85f;
    private static final Logger LOGGER = new Logger();

    public static final int TF_OD_API_INPUT_SIZE = 640;

    private static final boolean TF_OD_API_IS_QUANTIZED = false;

    private static final String TF_OD_API_MODEL_FILE = "yolov5.tflite";

    private static final String TF_OD_API_LABELS_FILE = "file:///android_asset/customclasses.txt";

    // Minimum detection confidence to track a detection.
    private static final boolean MAINTAIN_ASPECT = true;
    private static final int PERMISSIONS_READ_STORAGE = 98;
    private static final int PERMISSIONS_WRITE_STORAGE = 99;
    private Integer sensorOrientation = 90;

    private Classifier detector;

    private Matrix frameToCropTransform;
    private Matrix cropToFrameTransform;
    private MultiBoxTracker tracker;
    private OverlayView trackingOverlay;

    protected int previewWidth = 0;
    protected int previewHeight = 0;

    private Bitmap sourceBitmap;
    private Bitmap cropBitmap;

    private Button cameraButton;
    private ImageView imageView;
    private ArrayList<String> res = new ArrayList<String>();

    //TODO zmienić na false
    private boolean DEBUG = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cameraButton = findViewById(R.id.cameraButton);

        cameraButton.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, TracesActivity.class)));

        if(DEBUG){
            UpdateTraceFile();
        }

        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();

        System.err.println(Double.parseDouble(configurationInfo.getGlEsVersion()));
        System.err.println(configurationInfo.reqGlEsVersion >= 0x30000);
        System.err.println(String.format("%X", configurationInfo.reqGlEsVersion));

    }

    private void UpdateTraceFile() {
        if(ActivityCompat.checkSelfPermission(MainActivity.this ,Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        && ActivityCompat.checkSelfPermission(MainActivity.this ,Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
        {
            readFile();
        }
        else
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                int[] permInt = new int[]{PERMISSIONS_READ_STORAGE,PERMISSIONS_WRITE_STORAGE};
                String[] permString = new String[] {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                ActivityCompat.requestPermissions(this, permString, 98);
            }
        }

    }
    private void readFile(){
        String dirPath = getFilesDir().getAbsolutePath() + File.separator + constants.tracesDirName;
        File projDir = new File(dirPath);
        if(!projDir.exists()){
            projDir.mkdirs();
        }

        copyAssets();
    }
    private void copyAssets() {
        AssetManager assetManager = getAssets();
        String[] files = null;
        try {
            files = assetManager.list(constants.tracesDirName);
        } catch (IOException e) {
            LOGGER.e("tag", "Failed to get asset file list.", e);
        }
        for(String filename : files) {
            InputStream in = null;
            OutputStream out = null;
            try {
                in = assetManager.open(constants.tracesDirName +filename);

                String outDir = getFilesDir().getAbsolutePath() + File.separator + constants.tracesDirName;

                File outFile = new File(outDir, filename);

                out = new FileOutputStream(outFile);
                copyFile(in, out);
                in.close();
                in = null;
                out.flush();
                out.close();
                out = null;
            } catch(IOException e) {
                LOGGER.e("tag", "Failed to copy asset file: " + filename, e);
            }
        }
    }
    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode){
            case PERMISSIONS_READ_STORAGE:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    readFile();
                }
                else
                {
                    Toast.makeText(MainActivity.this, "Aplikacja potrzebuje dostępu do plików", Toast.LENGTH_SHORT).show();
                    finish();
                }
            case PERMISSIONS_WRITE_STORAGE:
                if(grantResults[1] == PackageManager.PERMISSION_GRANTED){
                    readFile();
                }
                else
                {
                    Toast.makeText(MainActivity.this, "Aplikacja potrzebuje dostępu do plików", Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }
}
