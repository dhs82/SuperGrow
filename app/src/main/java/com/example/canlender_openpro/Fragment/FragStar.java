package com.example.canlender_openpro.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.canlender_openpro.R;
import com.example.canlender_openpro.ScheduleActivity;

import java.io.FileInputStream;
import java.io.FileOutputStream;

public class FragStar extends Fragment {

    private View view;
    private String TAG = "프래그먼트";
    private String fname = null;
    private String str = null;
    private CalendarView calendarView;
    private Button cha_Btn, del_Btn, save_Btn, schedule_Btn;
    private TextView diaryTextView, textView2, textView3;
    private EditText contextEditText;
    private String userID; // 추가된 부분

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        view = inflater.inflate(R.layout.frag_star, container, false);

        calendarView = view.findViewById(R.id.calendarView);
        diaryTextView = view.findViewById(R.id.diaryTextView);
        save_Btn = view.findViewById(R.id.save_Btn);
        del_Btn = view.findViewById(R.id.del_Btn);
        cha_Btn = view.findViewById(R.id.cha_Btn);
        textView2 = view.findViewById(R.id.textView2);
        textView3 = view.findViewById(R.id.textView3);
        contextEditText = view.findViewById(R.id.contextEditText);
        schedule_Btn = view.findViewById(R.id.schedule_Btn);

        //로그인 및 회원가입 엑티비티에서 이름을 받아옴
        Intent intent = requireActivity().getIntent();
        String name = intent.getStringExtra("UserName");
        userID = intent.getStringExtra("UserID");
        textView3.setText(name + "님의 달력 일기장");

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                diaryTextView.setVisibility(View.VISIBLE);
                save_Btn.setVisibility(View.VISIBLE);
                contextEditText.setVisibility(View.VISIBLE);
                textView2.setVisibility(View.INVISIBLE);
                cha_Btn.setVisibility(View.INVISIBLE);
                del_Btn.setVisibility(View.INVISIBLE);
                schedule_Btn.setVisibility(View.VISIBLE);
                diaryTextView.setText(String.format("%d / %d / %d", year, month + 1, dayOfMonth));
                contextEditText.setText("");
                checkDay(year, month, dayOfMonth, userID);

                // 선택된 날짜의 년도, 월, 일을 변수에 저장
                int selectedYear = year;
                int selectedMonth = month + 1; // 월은 0부터 시작하므로 1을 더해줍니다.
                int selectedDay = dayOfMonth;

                // ScheduleActivity에 전달할 데이터
                schedule_Btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent scheduleIntent = new Intent(requireContext(), ScheduleActivity.class);
                        scheduleIntent.putExtra("UserID", userID);
                        scheduleIntent.putExtra("SelectedYear", selectedYear);
                        scheduleIntent.putExtra("SelectedMonth", selectedMonth);
                        scheduleIntent.putExtra("SelectedDay", selectedDay);
                        startActivity(scheduleIntent);
                    }
                });
            }
        });

        save_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveDiary(fname);
                str = contextEditText.getText().toString();
                textView2.setText(str);
                save_Btn.setVisibility(View.INVISIBLE);
                cha_Btn.setVisibility(View.VISIBLE);
                del_Btn.setVisibility(View.VISIBLE);
                schedule_Btn.setVisibility(View.VISIBLE);
                contextEditText.setVisibility(View.INVISIBLE);
                textView2.setVisibility(View.VISIBLE);
            }
        });

        return view;
    }

    public void checkDay(int cYear, int cMonth, int cDay, String userID) {
        fname = "" + userID + cYear + "-" + (cMonth + 1) + "" + "-" + cDay + ".txt";//저장할 파일 이름 설정
        FileInputStream fis = null;//FileStream fis 변수

        try {
            fis = requireContext().openFileInput(fname);

            byte[] fileData = new byte[fis.available()];
            fis.read(fileData);
            fis.close();

            str = new String(fileData);

            contextEditText.setVisibility(View.INVISIBLE);
            textView2.setVisibility(View.VISIBLE);
            textView2.setText(str);

            save_Btn.setVisibility(View.INVISIBLE);
            cha_Btn.setVisibility(View.VISIBLE);
            del_Btn.setVisibility(View.VISIBLE);
            schedule_Btn.setVisibility(View.VISIBLE);

            cha_Btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    contextEditText.setVisibility(View.VISIBLE);
                    textView2.setVisibility(View.INVISIBLE);
                    contextEditText.setText(str);

                    save_Btn.setVisibility(View.VISIBLE);
                    cha_Btn.setVisibility(View.INVISIBLE);
                    del_Btn.setVisibility(View.INVISIBLE);
                    schedule_Btn.setVisibility(View.VISIBLE);
                    textView2.setText(contextEditText.getText());
                }
            });

            del_Btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    textView2.setVisibility(View.INVISIBLE);
                    contextEditText.setText("");
                    contextEditText.setVisibility(View.VISIBLE);
                    save_Btn.setVisibility(View.VISIBLE);
                    cha_Btn.setVisibility(View.INVISIBLE);
                    del_Btn.setVisibility(View.INVISIBLE);
                    schedule_Btn.setVisibility(View.VISIBLE);
                    removeDiary(fname);
                }
            });

            if (textView2.getText() == null) {
                textView2.setVisibility(View.INVISIBLE);
                diaryTextView.setVisibility(View.VISIBLE);
                save_Btn.setVisibility(View.VISIBLE);
                cha_Btn.setVisibility(View.INVISIBLE);
                del_Btn.setVisibility(View.INVISIBLE);
                schedule_Btn.setVisibility(View.VISIBLE);
                contextEditText.setVisibility(View.VISIBLE);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("WrongConstant")
    public void removeDiary(String readDay) {
        FileOutputStream fos = null;

        try {
            fos = requireContext().openFileOutput(readDay, getActivity().MODE_NO_LOCALIZED_COLLATORS);
            String content = "";
            fos.write((content).getBytes());
            fos.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("WrongConstant")
    public void saveDiary(String readDay) {
        FileOutputStream fos = null;

        try {
            fos = requireContext().openFileOutput(readDay, getActivity().MODE_NO_LOCALIZED_COLLATORS);
            String content = contextEditText.getText().toString();
            fos.write((content).getBytes());
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
