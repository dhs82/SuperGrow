package com.example.canlender_openpro;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

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
                try {
                    addSchedule(userID, cYear, cMonth, cDay);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        buttonDeleteSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String lineString = editTextSchedule.getText().toString();
                try {
                    int line = Integer.parseInt(lineString);
                    int tmp;
                    tmp=(line-1)*2+2;
                    deleteSchedule(userID, cYear, cMonth, cDay,tmp+1);
                    // 여기서 'line' 변수를 사용할 수 있습니다.
                } catch (NumberFormatException e) {
                    // 문자열이 유효한 정수로 변환되지 않는 경우 예외 처리
                    e.printStackTrace();
                }
            }
        });

        // 저장된 일정을 표시
        updateScheduleTextView(userID, cYear, cMonth, cDay);
    }

    private String generateFileName(String userID, int cYear, int cMonth, int cDay) {
        return "" + userID + cYear + "-" + (cMonth+1)  + "" + "-"+ cDay + ".txt";
    }

    private void addSchedule(String userID, int cYear, int cMonth, int cDay) throws IOException {
        // 파일명 생성
        String fname = generateFileName(userID, cYear, cMonth, cDay);

        // 해당 날짜의 일정 개수를 1 증가시키는 메서드 호출
        addScheduleplus(userID, cYear, cMonth, cDay);

        // 파일 객체 생성
        File file = new File(getFilesDir(), fname);

        // 일정 내용을 에딧텍스트에서 가져오기
        String schedule = editTextSchedule.getText().toString();

        try {
            // 파일을 append 모드로 열기 (true는 파일을 append 모드로 열게 합니다.)
            FileOutputStream fos = new FileOutputStream(file, true);

            // 일정 내용을 파일에 쓰기
            fos.write((schedule + "\n").getBytes());
            fos.write(("NO\n").getBytes());

            // 파일 닫기
            fos.close();

            // 사용자에게 일정 추가 완료 메시지 표시
            Toast.makeText(this, "일정이 추가되었습니다.", Toast.LENGTH_SHORT).show();

            // 업데이트된 일정을 TextView에 표시
            updateScheduleTextView(userID, cYear, cMonth, cDay);
        } catch (Exception e) {
            // 예외 발생 시 에러 로그 출력
            e.printStackTrace();
        }
    }


    public void addScheduleplus(String userID, int cYear, int cMonth, int cDay) {
        // 파일명 생성
        String fname = generateFileName(userID, cYear, cMonth, cDay);

        try {
            // 파일이 존재하지 않으면 생성
            File file = new File(getFilesDir(), fname);
            if (!file.exists()) {
                file.createNewFile();
            }

            // 파일을 읽고 쓰기 위해 BufferedReader와 FileWriter 대신에 FileInputStream과 FileOutputStream 사용
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader reader = new BufferedReader(isr);

            // 임시 파일 생성
            FileOutputStream fos = new FileOutputStream(new File(getFilesDir(), fname + "_temp"));

            // 맨 첫 줄 읽기
            String firstLine = reader.readLine();

            int value;
            if (firstLine != null) {
                // 파일이 비어있지 않으면 정수로 변환 후 1을 더함
                value = Integer.parseInt(firstLine.trim()) + 1;
            } else {
                // 파일이 비어있으면 0으로 초기화
                value = 0;
            }

            // 맨 첫 줄에 새로운 값 쓰기
            fos.write(String.valueOf(value).getBytes());

            // 파일이 비어있을 때만 한 번 줄바꿈 추가
            if (firstLine == null) {
                fos.write('\n');
            }

            // 나머지 파일 내용 복사
            if (firstLine != null) {
                fos.write('\n'); // 첫 줄을 이미 처리했으므로 다시 쓰지 않음
            }
            String line;
            while ((line = reader.readLine()) != null) {
                fos.write((line + "\n").getBytes());
            }

            // 사용한 리소스 닫기
            fis.close();
            fos.close();

            // 임시 파일을 원래 파일로 복사
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Files.move(Paths.get(new File(getFilesDir(), fname + "_temp").getPath()),
                        Paths.get(file.getPath()), StandardCopyOption.REPLACE_EXISTING);
            }

            // 사용자에게 일정 추가 완료 메시지 표시
            Toast.makeText(this, "일정이 추가되었습니다.", Toast.LENGTH_SHORT).show();

            // 업데이트된 일정을 TextView에 표시
            updateScheduleTextView(userID, cYear, cMonth, cDay);
        } catch (IOException | NumberFormatException e) {
            // 예외 발생 시 에러 로그 출력
            e.printStackTrace();

            // 사용자에게 파일 열기 오류 메시지 표시
            Toast.makeText(this, "파일을 열 수 없습니다. 오류 메시지: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteSchedule(String userID, int cYear, int cMonth, int cDay, int lineNumber) {
        String fname = generateFileName(userID, cYear, cMonth, cDay);
        int decrementValue = deleteScheduleplus(userID, cYear, cMonth, cDay);

        try {
            FileInputStream fis = openFileInput(fname);
            byte[] fileData = new byte[fis.available()];
            fis.read(fileData);
            fis.close();

            String existingSchedules = new String(fileData);
            String[] schedulesArray = existingSchedules.split("\n");

            StringBuilder newSchedules = new StringBuilder();

            for (int i = 0; i < schedulesArray.length; i++) {
                // 지정된 라인과 해당 라인의 다음 라인을 건너뛰기
                if (i + 1 == lineNumber || i + 2 == lineNumber) {
                    continue;
                }

                // 나머지 라인을 newSchedules StringBuilder에 추가
                newSchedules.append(schedulesArray[i]).append("\n");
            }

            FileOutputStream fos = openFileOutput(fname, MODE_PRIVATE);
            fos.write(newSchedules.toString().getBytes());
            fos.close();

            if (decrementValue > 0) {
                Toast.makeText(this, "일정이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "삭제할 일정이 없습니다.", Toast.LENGTH_SHORT).show();
            }

            // 업데이트된 일정을 TextView에 표시
            updateScheduleTextView(userID, cYear, cMonth, cDay);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int deleteScheduleplus(String userID, int cYear, int cMonth, int cDay) {
        // 파일명 생성
        String fname = generateFileName(userID, cYear, cMonth, cDay);

        int decrementValue = 0;

        try {
            // 파일이 존재하지 않으면 생성
            File file = new File(getFilesDir(), fname);
            if (!file.exists()) {
                file.createNewFile();
            }

            // 파일을 읽고 쓰기 위해 BufferedReader와 FileWriter 대신에 FileInputStream과 FileOutputStream 사용
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader reader = new BufferedReader(isr);

            // 임시 파일 생성
            FileOutputStream fos = new FileOutputStream(new File(getFilesDir(), fname + "_temp"));

            // 맨 첫 줄 읽기
            String firstLine = reader.readLine();

            if (firstLine != null) {
                // 파일이 비어있지 않으면 정수로 변환 후 1을 빼기
                decrementValue = Integer.parseInt(firstLine.trim()) - 1;

                // 맨 첫 줄에 새로운 값 쓰기
                fos.write(String.valueOf(decrementValue).getBytes());

                // 파일이 비어있을 때만 한 번 줄바꿈 추가
                if (firstLine == null) {
                    fos.write('\n');
                }

                // 나머지 파일 내용 복사
                if (firstLine != null) {
                    fos.write('\n'); // 첫 줄을 이미 처리했으므로 다시 쓰지 않음
                }
            }

            String line;
            while ((line = reader.readLine()) != null) {
                fos.write((line + "\n").getBytes());
            }

            // 사용한 리소스 닫기
            fis.close();
            fos.close();

            // 임시 파일을 원래 파일로 복사
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Files.move(Paths.get(new File(getFilesDir(), fname + "_temp").getPath()),
                        Paths.get(file.getPath()), StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException | NumberFormatException e) {
            // 예외 발생 시 에러 로그 출력
            e.printStackTrace();

            // 사용자에게 파일 열기 오류 메시지 표시
            Toast.makeText(this, "파일을 열 수 없습니다. 오류 메시지: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        return decrementValue;
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
