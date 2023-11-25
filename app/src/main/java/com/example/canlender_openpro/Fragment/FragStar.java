package com.example.canlender_openpro.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.canlender_openpro.R;
import com.example.canlender_openpro.ScheduleActivity;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class FragStar extends Fragment {

    private View view;
    private String TAG = "프래그먼트";
    private String fname = null;
    private String str = null;
    private CalendarView calendarView;
    private Button schedule_Btn;
    private TextView diaryTextView, textView2, textView3;
    private String userID; // 추가된 부분

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        view = inflater.inflate(R.layout.frag_star, container, false);

        calendarView = view.findViewById(R.id.calendarView);
        diaryTextView = view.findViewById(R.id.diaryTextView);
        textView2 = view.findViewById(R.id.textView2);
        textView3 = view.findViewById(R.id.textView3);
        schedule_Btn = view.findViewById(R.id.schedule_Btn);

        //로그인 및 회원가입 엑티비티에서 이름을 받아옴
        Intent intent = requireActivity().getIntent();
        String name = intent.getStringExtra("UserName");
        userID = intent.getStringExtra("UserEmail");
        textView3.setText(name + "님의 달력 일기장");

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                diaryTextView.setVisibility(View.VISIBLE);
                schedule_Btn.setVisibility(View.VISIBLE);
                diaryTextView.setText(String.format("%d / %d / %d", year, month + 1, dayOfMonth));
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

        return view;
    }

    @SuppressLint("ResourceType")
    public void checkDay(int cYear, int cMonth, int cDay, String userID) {
        fname = "" + userID + cYear + "-" + (cMonth + 1) + "" + "-" + cDay + ".txt"; // 저장할 파일 이름 설정
        FileInputStream fis = null; // FileStream fis 변수

        RelativeLayout showLayout = view.findViewById(R.id.showlayout);

        // 기존에 생성된 View들을 모두 제거
        showLayout.removeAllViews();


        try {
            fis = requireContext().openFileInput(fname);
            byte[] fileData = new byte[fis.available()];
            fis.read(fileData);
            fis.close();

            str = new String(fileData);


            // 일정이 있을 경우에만 처리
            if (!str.isEmpty()) {
                // 개행 문자를 기준으로 일정을 나누어 배열로 저장
                String[] schedulesArray = str.split("\n");

                // 각 라인에 채크박스 추가
                for (int i = 1; i < schedulesArray.length; i += 2) {
                    String lineWithA = String.valueOf((i + 1) / 2) + ". " + schedulesArray[i];

                    // CheckBox 생성
                    CheckBox checkBox = new CheckBox(requireContext());
                    // 채크박스에 고유한 ID 부여
                    checkBox.setId(View.generateViewId());
                    // 채크박스에 일정 텍스트 설정
                    checkBox.setText(lineWithA);

                    // 생성한 채크박스를 레이아웃에 추가하기 위한 레이아웃 파라미터 생성
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.WRAP_CONTENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT
                    );

                    if (i > 1) {
                        // 이전 채크박스 아래에 배치
                        params.addRule(RelativeLayout.BELOW, checkBox.getId() - 1);
                    } else {
                        // 첫 번째 채크박스는 일정의 번호를 표시하는 TextView 아래에 배치
                        params.addRule(RelativeLayout.BELOW, R.id.textView2);
                    }

                    // 채크박스에 레이아웃 파라미터 설정
                    checkBox.setLayoutParams(params);
                    // 채크박스를 레이아웃에 추가
                    showLayout.addView(checkBox);

                    // CheckBox에 OnCheckedChangeListener 추가
                    int finalI = i;
                    checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            // 체크박스가 체크되었을 때 해당 다음 라인의 일정을 "Yes"로 수정
                            if (isChecked) {
                                schedulesArray[finalI + 1] = "Yes";
                                //Toast.makeText(ScheduleActivity.this, lineWithA + " 일정이 Yes로 변경되었습니다.", Toast.LENGTH_SHORT).show();
                            } else {
                                // 체크가 해제되었을 때 "No"로 변경
                                schedulesArray[finalI + 1] = "No";
                                //Toast.makeText(ScheduleActivity.this, lineWithA + " 일정이 No로 변경되었습니다.", Toast.LENGTH_SHORT).show();
                            }
                            // 수정된 일정을 파일에 저장
                            saveSchedulesToFile(schedulesArray, fname);
                        }
                    });

                    // "Yes"인 경우에만 체크 상태 설정
                    if (i + 1 < schedulesArray.length && schedulesArray[i + 1].equals("Yes")) {
                        checkBox.setChecked(true);
                    }
                }
            } else {
                // 일정이 없을 경우 처리
                TextView noScheduleTextView = new TextView(requireContext());
                noScheduleTextView.setText("일정이 없습니다.");
            }
        } catch (FileNotFoundException e) {
            // 파일이 없는 경우 처리
            e.printStackTrace();
            // 일정이 없을 경우 처리
            TextView noScheduleTextView = new TextView(requireContext());
            noScheduleTextView.setText("일정이 없습니다.");
        } catch (Exception e) {
            // 예외 발생 시 에러 메시지 출력
            e.printStackTrace();
        }
    }


    // 일정을 파일에 저장하는 메서드
    private void saveSchedulesToFile(String[] schedulesArray, String filename) {
        try {
            // StringBuilder를 사용하여 수정된 일정을 다시 문자열로 구성
            StringBuilder modifiedSchedules = new StringBuilder();
            for (String schedule : schedulesArray) {
                modifiedSchedules.append(schedule).append("\n");
            }

            // 파일 쓰기를 위한 FileOutputStream 생성
            FileOutputStream fos = requireContext().openFileOutput(filename, Context.MODE_PRIVATE);
            fos.write(modifiedSchedules.toString().getBytes());
            fos.close();
        } catch (Exception e) {
            // 예외 발생 시 에러 메시지 출력
            e.printStackTrace();
        }
    }
}
