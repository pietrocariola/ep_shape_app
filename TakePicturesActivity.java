package com.example.epshape;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class TakePicturesActivity extends AppCompatActivity {

    public static final String CLASS_COUNTER = "com.example.epshape.CLASS_COUNTER";
    public static final String NUMBER_OF_CLASSES = "com.example.epshape.NUMBER_OF_CLASSES";
    public static final String NUMBER_OF_OBJS = "com.example.epshape.NUMBER_OF_OBJS";
    public static final String NUMBER_OF_BGS = "com.example.epshape.NUMBER_OF_BGS";
    public static final String NUMBER_OF_PICS = "com.example.epshape.NUMBER_OF_PICS";
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private String className;
    private String objName = "_obj0";
    private String bgName = "_bg0";
    private int classCounter = 0;
    private int objCounter = 0;
    private int bgCounter = 0;
    private int picCounter = 0;
    private int numberOfClasses;
    private int numberOfObjs;
    private int numberOfBgs;
    private int numberOfPics;
    private File tempFile = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_pictures);
        this.className = getIntent().getStringExtra(ClassActivity.CLASS_NAME);
        this.classCounter = getIntent().getIntExtra(ClassActivity.CLASS_COUNTER, 0);
        this.numberOfClasses = getIntent().getIntExtra(
                ClassActivity.NUMBER_OF_CLASSES, 0);
        this.numberOfObjs = getIntent().getIntExtra(ClassActivity.NUMBER_OF_OBJS, 0);
        this.numberOfBgs = getIntent().getIntExtra(ClassActivity.NUMBER_OF_BGS, 0);
        this.numberOfPics = getIntent().getIntExtra(ClassActivity.NUMBER_OF_PICS, 0);
    }

    public void onClickTakePictures(View view) {
        imageCapture();
    }

    private void imageCapture() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            File tempFile = null;
            try {
                tempFile = createTempFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (tempFile != null) {
                this.tempFile = tempFile;
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.epshape.fileprovider",
                        tempFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        TextView textView = findViewById(R.id.textView);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            saveImage(this.tempFile, this.className+ this.objName + this.bgName +"_pic"+
                    String.valueOf(this.picCounter)+".jpg");
            Toast.makeText(this, "Picture: "+String.valueOf(this.picCounter+1)+"/"+
                            String.valueOf(this.numberOfPics), Toast.LENGTH_SHORT).show();
            if (this.picCounter < this.numberOfPics-1) {
                imageCapture();
                this.picCounter++;
            } else {
                this.picCounter = 0;
                if (this.bgCounter < this.numberOfBgs-1) {
                    textView.setText("Change to background "+String.valueOf(this.bgCounter+1+1));
                    this.bgName = "_bg"+String.valueOf(this.bgCounter+1);
                    this.bgCounter++;
                } else {
                    this.bgCounter = 0;
                    this.bgName = "_bg0";
                    if (this.objCounter < this.numberOfObjs-1){
                        textView.setText("Place object "+
                                String.valueOf(this.objCounter+1+1)+" in background 1");
                        this.objName = "_obj"+String.valueOf(this.objCounter+1);
                        this.objCounter++;
                    } else {
                        if (this.classCounter < this.numberOfClasses-1) {
                            this.classCounter++;
                            Intent intent = new Intent(this, ClassActivity.class);
                            intent.putExtra(CLASS_COUNTER, this.classCounter);

                            intent.putExtra(NUMBER_OF_CLASSES, this.numberOfClasses);
                            intent.putExtra(NUMBER_OF_OBJS, this.numberOfObjs);
                            intent.putExtra(NUMBER_OF_BGS, this.numberOfBgs);
                            intent.putExtra(NUMBER_OF_PICS, this.numberOfPics);

                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(this, FinishedActivity.class);
                            startActivity(intent);
                        }
                    }
                }
            }
        }
    }

    private File createTempFile() throws IOException {
        String name = "EP_SHAPE_";
        File dir = getFilesDir();
        File file = File.createTempFile(name,".jpg", dir);
        return file;
    }

    private void saveImage(File tempFile, String imageName) {
        File imageDir = new File(getFilesDir()+
                File.separator+MainActivity.DATASET_DIR+File.separator+MainActivity.INTER_DIR+
                File.separator+this.className);
        if (!imageDir.exists()) {
            boolean mkDirs = imageDir.mkdirs();
            Log.i("EP_SHAPE", imageDir.getAbsolutePath()+" created:"+
                    String.valueOf(mkDirs));
        }
        File imageFile = new File(imageDir, imageName);
        if (imageFile.exists()){
            Log.i("EP_SHAPE", imageFile.getAbsolutePath()+" already exists");
            imageFile.delete();
        }
        String tempPath = tempFile.getAbsolutePath();
        try {
            Bitmap imageBitmap = BitmapFactory.decodeFile(tempPath);
            imageBitmap = rotateImage(tempPath, imageBitmap);
            FileOutputStream out = null;
            out = new FileOutputStream(imageFile);
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.close();
            tempFile.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Bitmap rotateImage(String imagePath, Bitmap source) throws IOException {
        final ExifInterface ei = new ExifInterface(imagePath);
        final int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL);
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                source = rotateImageByAngle(source, 90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                source = rotateImageByAngle(source, 180);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                source = rotateImageByAngle(source, 270);
                break;
        }
        return source;
    }

    private Bitmap rotateImageByAngle(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

}