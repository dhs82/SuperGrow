package com.example.canlender_openpro;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class BackgroundActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_background);
    }

    // 백 버튼 처리
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish(); // 현재 액티비티를 종료
    }
}
