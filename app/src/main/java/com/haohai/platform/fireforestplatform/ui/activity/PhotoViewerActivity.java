package com.haohai.platform.fireforestplatform.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.piasy.biv.view.BigImageView;
import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.base.BaseActivity;

public class PhotoViewerActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_viewer);
        blackText();
        Intent intent = getIntent();
        String url = intent.getStringExtra("url");
        if(url == null){
            Toast.makeText(this, "文件不存在", Toast.LENGTH_SHORT).show();
            finish();
        }

        BigImageView bigImageView = findViewById(R.id.mBigImage);
        ImageView leftIcon = findViewById(R.id.left_icon);
        TextView left = findViewById(R.id.left);
        bigImageView.showImage(Uri.parse(url));

        left.setOnClickListener(v -> {
            finish();
        });
        leftIcon.setOnClickListener(v -> {
            finish();
        });
    }
}