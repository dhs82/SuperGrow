package com.example.canlender_openpro;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MemoActivity extends AppCompatActivity {

    private EditText memoEditText;
    private Button saveButton;
    private String memoKey; // 각 메모를 식별하기 위한 키

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo);

        memoEditText = findViewById(R.id.memoEditText);
        saveButton = findViewById(R.id.saveButton);

        // 메모장1 클릭 시
        memoKey = "memo1";
        loadMemo(); // 저장된 메모 불러오기

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveMemo(); // 메모 저장
            }
        });
    }

    private void loadMemo() {
        // 저장된 메모 불러오기
        String savedMemo = getSavedMemo();
        memoEditText.setText(savedMemo);
    }

    private String getSavedMemo() {
        // SharedPreferences를 사용하여 저장된 메모를 불러옵니다.
        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        return sharedPreferences.getString(memoKey, "");
    }

    private void saveMemo() {
        // 메모 저장
        String memoText = memoEditText.getText().toString();

        // SharedPreferences를 사용하여 메모를 저장합니다.
        SharedPreferences.Editor editor = getPreferences(Context.MODE_PRIVATE).edit();
        editor.putString(memoKey, memoText);
        editor.apply();

        Toast.makeText(this, "메모가 저장되었습니다.", Toast.LENGTH_SHORT).show();
    }
}