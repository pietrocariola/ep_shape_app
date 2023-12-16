package com.example.epshape;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import java.io.File;

public class FinishedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finished);
    }

    public void onClickUploadDataset(View view) {
        createDataTable(MainActivity.py);
        zipDataset(MainActivity.py);
        uploadDataset();
    }

    public void onClickBack(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void zipDataset(Python py) {
        PyObject module = py.getModule("zip_dataset");
        module.callAttr("zip_dataset");
    }

    private void createDataTable(Python py) {
        PyObject module = py.getModule("create_datatable");
        module.callAttr("create_datatable");
    }

    private void uploadDataset() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("application/zip");
        File file = new File(getFilesDir(), MainActivity.DATASET_DIR+".zip");
        Uri fileUri = FileProvider.getUriForFile(
                FinishedActivity.this,
                "com.example.epshape.fileprovider",
                file);
        shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
        startActivity(shareIntent);
    }
}

