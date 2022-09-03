package org.PolandSignsDetection;

import static java.lang.System.out;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;

public class ViewTracesActivity extends AppCompatActivity {

    private AssetManager assetManager;
    private ListView signsView;
    private int TraceNumber = 0;
    private String[] TraceSignsOrder = null;
    private ArrayList<Item> signs = new ArrayList<>();
    private Button cleanButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.view_traces);
        cleanButton = findViewById(R.id.CleanButton);
        cleanButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                try {
                    cleanFile();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
        assetManager = getAssets();

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                TraceNumber = 0;
                TraceSignsOrder = null;
            } else {
                TraceNumber = extras.getInt(constants.traceNumberKey);
                TraceSignsOrder = extras.getStringArray(constants.traceSignOrderKey);
            }
        }

        signsView = findViewById(R.id.signs_order);
        signsView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        if(TraceSignsOrder == null){
            Toast.makeText(ViewTracesActivity.this, "TRASA JEST PUSTA!", Toast.LENGTH_SHORT).show();
        }
        else {
            ShowSigns();
        }
    }
    private void cleanFile() throws FileNotFoundException {

        String pathToFile = getFilesDir().getAbsolutePath()+ File.separator+ constants.tracesDirName+"trace"+TraceNumber+".txt";

        try(PrintWriter pw = new PrintWriter(pathToFile)){
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        TraceSignsOrder = null;
        signsView.setAdapter(null);

    }

    private void ShowSigns(){
        AssetManager assetManager = getAssets();
        InputStream istr = null;
        for(int imageIndex = 0; imageIndex<TraceSignsOrder.length;imageIndex++){
            try {
                istr = assetManager.open("Meta/"+ TraceSignsOrder[imageIndex].toString()+".png");
            } catch (IOException e) {
                istr = null;
                e.printStackTrace();
            }
            Drawable d = Drawable.createFromStream(istr, null);
            signs.add(new Item(d, "["+imageIndex+"]: "+ TraceSignsOrder[imageIndex].toString()));
        }

        ListViewAdapter adapter = new ListViewAdapter(this, signs);
        signsView.setAdapter(adapter);
    }
}

class ListViewAdapter extends ArrayAdapter<Item> {

    ListViewAdapter(@NonNull Context context, ArrayList<Item> items) {
        super(context, 0, items);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View listItem = convertView;
        if (listItem == null) {
            listItem = LayoutInflater.from(getContext()).inflate(R.layout.listview_row, parent, false);
        }

        // Get the {@link Word} object located at this position in the list
        Item currentItem = getItem(position);

        ImageView picture = listItem.findViewById(R.id.listview_row_image);
        picture.setBackground(currentItem.getPicture());

        TextView title = listItem.findViewById(R.id.listview_row_text);
        title.setText(currentItem.getTitle());

        return listItem;
    }

}

class Item {

    private Drawable mPicture;
    private String mTitle;

    Drawable getPicture() {
        return mPicture;
    }

    Item(Drawable picture, String title) {
        mPicture = picture;
        mTitle = title;
    }

    String getTitle() {
        return mTitle;
    }

}
