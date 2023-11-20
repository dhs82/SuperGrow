package com.example.canlender_openpro.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;

import com.example.canlender_openpro.R;

public class FragStar extends Fragment {

    private View view;
    private String TAG = "프래그먼트";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        view = inflater.inflate(R.layout.frag_star, container, false);

        CalendarView calendarView = view.findViewById(R.id.calendarView);

        // 달력에서 날짜를 선택했을 때 이벤트 처리
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                // 선택된 날짜에 대한 처리를 여기에 추가
                String selectedDate = year + "-" + (month + 1) + "-" + dayOfMonth;
                Toast.makeText(requireContext(), "Selected Date: " + selectedDate, Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}
