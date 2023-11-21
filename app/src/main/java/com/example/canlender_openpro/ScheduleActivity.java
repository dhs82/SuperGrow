package com.example.canlender_openpro;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.FileInputStream;
import java.io.FileOutputStream;

public class ScheduleActivity extends AppCompatActivity {

    private EditText editTextSchedule;
    private TextView textView2Schedule;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        editTextSchedule = findViewById(R.id.editTextSchedule);
        Button buttonAddSchedule = findViewById(R.id.buttonAddSchedule);
        Button buttonDeleteSchedule = findViewById(R.id.buttonDeleteSchedule);
        textView2Schedule = findViewById(R.id.textView2Schedule);

        // FragStar에서 전달한 데이터 받기
        String userID = getIntent().getStringExtra("UserID");
        int cYear = getIntent().getIntExtra("SelectedYear", 0);  // 기본값은 0으로 설정 (적절한 기본값으로 변경)
        int cMonth = getIntent().getIntExtra("SelectedMonth", 0)-1;
        int cDay = getIntent().getIntExtra("SelectedDay", 0);

        buttonAddSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSchedule(userID, cYear, cMonth, cDay);
            }
        });

        buttonDeleteSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteSchedule(userID, cYear, cMonth, cDay);
            }
        });

        // 저장된 일정을 표시
        updateScheduleTextView(userID, cYear, cMonth, cDay);
    }

    private String generateFileName(String userID, int cYear, int cMonth, int cDay) {
        return "" + userID + cYear + "-" + (cMonth+1)  + "" + "-"+ cDay + ".txt";
        //fname = "" + userID + cYear + "-" + (cMonth + 1) + "" + "-" + cDay + ".txt";//저장할 파일 이름 설정
    }

    private void addSchedule(String userID, int cYear, int cMonth, int cDay) {
        String fname = generateFileName(userID, cYear, cMonth, cDay);
        String schedule = editTextSchedule.getText().toString();

        try {
            FileOutputStream fos = openFileOutput(fname, MODE_APPEND);
            fos.write((schedule + "\n").getBytes());
            fos.close();
            Toast.makeText(this, "일정이 추가되었습니다.", Toast.LENGTH_SHORT).show();

            // 업데이트된 일정을 TextView에 표시
            updateScheduleTextView(userID, cYear, cMonth, cDay);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteSchedule(String userID, int cYear, int cMonth, int cDay) {
        String fname = generateFileName(userID, cYear, cMonth, cDay);
        String scheduleToDelete = editTextSchedule.getText().toString();

        try {
            FileInputStream fis = openFileInput(fname);
            byte[] fileData = new byte[fis.available()];
            fis.read(fileData);
            fis.close();

            String existingSchedules = new String(fileData);
            String[] schedulesArray = existingSchedules.split("\n");

            StringBuilder newSchedules = new StringBuilder();

            for (String schedule : schedulesArray) {
                if (!schedule.trim().equals(scheduleToDelete.trim())) {
                    newSchedules.append(schedule).append("\n");
                }
            }

            FileOutputStream fos = openFileOutput(fname, MODE_PRIVATE);
            fos.write(newSchedules.toString().getBytes());
            fos.close();

            Toast.makeText(this, "일정이 삭제되었습니다.", Toast.LENGTH_SHORT).show();

            // 업데이트된 일정을 TextView에 표시
            updateScheduleTextView(userID, cYear, cMonth, cDay);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateScheduleTextView(String userID, int cYear, int cMonth, int cDay) {
        String fname = generateFileName(userID, cYear, cMonth, cDay);

        try {
            FileInputStream fis = openFileInput(fname);
            byte[] fileData = new byte[fis.available()];
            fis.read(fileData);
            fis.close();

            String existingSchedules = new String(fileData);
            textView2Schedule.setText(existingSchedules);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
