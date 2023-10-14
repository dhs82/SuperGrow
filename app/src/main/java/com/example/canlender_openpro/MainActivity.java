package com.example.canlender_openpro;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView dateTextView;
    private Handler handler;
    private SimpleDateFormat dateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dateTextView = findViewById(R.id.textView2);
        handler = new Handler(Looper.getMainLooper());
        dateFormat = new SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault());

        // 매 초마다 현재 날짜를 업데이트
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateCurrentDate();
                handler.postDelayed(this, 1000); // 1초마다 업데이트
            }
        }, 0);
    }

    private void updateCurrentDate() {
        String currentDate = dateFormat.format(new Date());
        dateTextView.setText("현재 날짜: " + currentDate);
    }
}