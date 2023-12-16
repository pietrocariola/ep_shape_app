package com.example.epshape;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CAMERA_PERMISSION_CODE = 1;
    private static final int REQUEST_GET_CONTENT = 2;
    private static final int REQUEST_IMAGE_CAPTURE = 3;
    public static final String DATASET_DIR = "ep_shape_dataset";
    public static final String INTER_DIR = "dataset";
    public static final String DATA_TABLE_FILE = "data_table.csv";
    public static final String CLASS_PREDICTION = "com.example.epshape.CLASS_PREDICTION";
    public static Python py;
    private File tempFile = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        py = Python.getInstance();
        checkPermissions();
    }

    public void onClickNewDataset(View view) {
        onClickDeleteDataset(view);
        Intent intent = new Intent(this, SetupActivity.class);
        startActivity(intent);
    }

    public void onClickDeleteDataset(View view) {
        deleteFiles(new File(getFilesDir()+File.separator+DATASET_DIR));
        deleteFiles(new File(getFilesDir()+File.separator+DATASET_DIR+".zip"));
    }

    public void onClickUploadDataset(View view) {
        createDataTable(this.py);
        zipDataset(this.py);
        uploadDataset();
    }

    public void onClickDownloadModel(View view) {
        Intent getIntent = new Intent();
        getIntent.setAction(Intent.ACTION_GET_CONTENT);
        getIntent.setType("*/*");
        startActivityForResult(Intent.createChooser(getIntent, "Select File"),
                REQUEST_GET_CONTENT);
    }

    public void onClickRun(View view) {
        runModel(this.py);
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA_PERMISSION_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_GET_CONTENT && resultCode == RESULT_OK) {
            saveToFile(data.getData());
        }
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            File imageFile = new File(getFilesDir().getAbsolutePath(), "image.jpg");
            try {
                Bitmap imageBitmap = BitmapFactory.decodeFile(this.tempFile.getAbsolutePath());
                imageBitmap = rotateImage(this.tempFile.getAbsolutePath(), imageBitmap);
                FileOutputStream out = null;
                out = new FileOutputStream(imageFile);
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            PyObject module = py.getModule("run_model");
            String classPrediction = module.callAttr("run_model", imageFile.getName()).toJava(
                    String.class);
            Intent intent = new Intent(this, PredictionActivity.class);
            intent.putExtra(CLASS_PREDICTION, classPrediction);
            startActivity(intent);
            try {
                this.tempFile.delete();
                imageFile.delete();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void saveToFile(Uri uri) {
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {
            bis = new BufferedInputStream(
                    this.getContentResolver().openInputStream(uri));
            bos = new BufferedOutputStream(
                    new FileOutputStream(
                            getFilesDir().getAbsolutePath()+
                                    File.separator+"ep_shape_model.pth", false));
            byte[] buffer = new byte[1024];
            bis.read(buffer);
            do {
                bos.write(buffer);
            } while (bis.read(buffer) != -1);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (bis != null) {
                    bis.close();
                }
                if (bos != null) {
                    bos.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void deleteFiles(File parent) {
        if (parent.isDirectory()) {
            for (File child : parent.listFiles()) {
                deleteFiles(child);
            }
        }
        String path = parent.getAbsolutePath();
        boolean deleted = parent.delete();
    }

    private void zipDataset(Python py) {
        PyObject module = py.getModule("zip_dataset");
        module.callAttr("zip_dataset");
    }

    private void createDataTable(Python py) {
        PyObject module = py.getModule("create_datatable");
        module.callAttr("create_datatable");
    }

    private void runModel(Python py) {
        imageCapture();
    }

    private void uploadDataset() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("application/zip");
        File file = new File(getFilesDir(), DATASET_DIR+".zip");
        Uri fileUri = FileProvider.getUriForFile(
                MainActivity.this,
                "com.example.epshape.fileprovider",
                file);
        shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
        startActivity(shareIntent);
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

    private File createTempFile() throws IOException {
        String name = "EP_SHAPE_";
        File dir = getFilesDir();
        File file = File.createTempFile(name,".jpg", dir);
        return file;
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