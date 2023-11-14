package com.example.canlender_openpro.Fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.canlender_openpro.R;

public class FragHome extends Fragment {

    private TextView dateTextView;
    private Handler handler;
    private SimpleDateFormat dateFormat;
    private View view;

    private String TAG = "프래그먼트";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        view = inflater.inflate(R.layout.frag_home, container, false);

        // 날짜 초기화
        dateTextView = view.findViewById(R.id.textView2);
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

        return view;
    }

    private void updateCurrentDate() {
        String currentDate = dateFormat.format(new Date());
        dateTextView.setText("현재 날짜: " + currentDate);
    }
}
