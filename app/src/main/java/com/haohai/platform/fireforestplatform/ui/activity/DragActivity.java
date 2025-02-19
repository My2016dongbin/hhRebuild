package com.haohai.platform.fireforestplatform.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.utils.MoveFrameLayout;

public class DragActivity extends AppCompatActivity {

    private MoveFrameLayout move;
    private ImageView filter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drag);
        move = findViewById(R.id.move);
        filter = findViewById(R.id.filter);
        move.setClick(v -> {
            Toast.makeText(this, "move", Toast.LENGTH_SHORT).show();
        });
    }
}