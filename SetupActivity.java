package com.example.epshape;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class SetupActivity extends AppCompatActivity {

    public static final String NUMBER_OF_CLASSES = "com.example.epshape.NUMBER_OF_CLASSES";
    public static final String NUMBER_OF_OBJS = "com.example.epshape.NUMBER_OF_OBJS";
    public static final String NUMBER_OF_BGS = "com.example.epshape.NUMBER_OF_BGS";
    public static final String NUMBER_OF_PICS = "com.example.epshape.NUMBER_OF_PICS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
    }

    public void onClickNext(View view) {
        EditText editText = findViewById(R.id.numberOfClasses);
        int numberOfClasses = Integer.valueOf(editText.getText().toString());
        editText = findViewById(R.id.numberOfObjs);
        int numberOfObjs = Integer.valueOf(editText.getText().toString());
        editText = findViewById(R.id.numberOfBgs);
        int numberOfBgs = Integer.valueOf(editText.getText().toString());
        editText = findViewById(R.id.numberOfPics);
        int numberOfPics = Integer.valueOf(editText.getText().toString());
        if (numberOfClasses >0 &&
            numberOfObjs >0 &&
            numberOfBgs >0 &&
            numberOfPics >0) {
            Intent intent = new Intent(this, ClassActivity.class);
            intent.putExtra(NUMBER_OF_CLASSES, numberOfClasses);
            intent.putExtra(NUMBER_OF_OBJS, numberOfObjs);
            intent.putExtra(NUMBER_OF_BGS, numberOfBgs);
            intent.putExtra(NUMBER_OF_PICS, numberOfPics);
            startActivity(intent);
        }
    }
}