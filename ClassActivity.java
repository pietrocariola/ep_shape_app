package com.example.epshape;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class ClassActivity extends AppCompatActivity {

    public static final String CLASS_NAME = "com.example.epshape.CLASS_NAME";
    public static final String CLASS_COUNTER = "com.example.epshape.CLASS_COUNTER";
    public static final String NUMBER_OF_CLASSES = "com.example.epshape.NUMBER_OF_CLASSES";
    public static final String NUMBER_OF_OBJS = "com.example.epshape.NUMBER_OF_OBJS";
    public static final String NUMBER_OF_BGS = "com.example.epshape.NUMBER_OF_BGS";
    public static final String NUMBER_OF_PICS = "com.example.epshape.NUMBER_OF_PICS";
    private int classCounter;
    private int numberOfClasses;
    private int numberOfObjs;
    private int numberOfBgs;
    private int numberOfPics;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class);

        // get extras from SetupActivity
        this.classCounter = getIntent().getIntExtra(
                TakePicturesActivity.CLASS_COUNTER, 0);
        this.numberOfClasses = getIntent().getIntExtra(
                SetupActivity.NUMBER_OF_CLASSES, 0);
        this.numberOfObjs = getIntent().getIntExtra(SetupActivity.NUMBER_OF_OBJS, 0);
        this.numberOfBgs = getIntent().getIntExtra(SetupActivity.NUMBER_OF_BGS, 0);
        this.numberOfPics = getIntent().getIntExtra(SetupActivity.NUMBER_OF_PICS, 0);

        // get extras from TakePictureActivity
        this.numberOfClasses = getIntent().getIntExtra(
                TakePicturesActivity.NUMBER_OF_CLASSES, this.numberOfClasses);
        this.numberOfObjs = getIntent().getIntExtra(TakePicturesActivity.NUMBER_OF_OBJS,
                this.numberOfObjs);
        this.numberOfBgs = getIntent().getIntExtra(TakePicturesActivity.NUMBER_OF_BGS,
                this.numberOfBgs);
        this.numberOfPics = getIntent().getIntExtra(TakePicturesActivity.NUMBER_OF_PICS,
                this.numberOfPics);

        TextView textView = findViewById(R.id.textView);
        textView.setText("Write the name of class "+String.valueOf(this.classCounter+1));
    }

    public void onClickNext(View view) {
        EditText editText = findViewById(R.id.className);
        String className = editText.getText().toString();
        Intent intent = new Intent(this, TakePicturesActivity.class);
        intent.putExtra(CLASS_NAME, className);
        intent.putExtra(CLASS_COUNTER, this.classCounter);
        intent.putExtra(NUMBER_OF_CLASSES, this.numberOfClasses);
        intent.putExtra(NUMBER_OF_OBJS, this.numberOfObjs);
        intent.putExtra(NUMBER_OF_BGS, this.numberOfBgs);
        intent.putExtra(NUMBER_OF_PICS, this.numberOfPics);
        startActivity(intent);
    }

}