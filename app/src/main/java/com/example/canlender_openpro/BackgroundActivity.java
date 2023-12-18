package com.example.canlender_openpro;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import android.graphics.Color;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
public class BackgroundActivity extends AppCompatActivity {
    private TextView tvFileResults;
    private String userID;
    private int[] YesNumArray = new int[7];
    private int[] totalEventsArray= new int[7];
    private LineChart lineChart;
    private float[] dailyCompletionRate = new float[7];
    private int DayTh=0;
    // 몇번째 날인지 체크
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_background);

        // 레이아웃에서 LineChart 찾기
        lineChart = findViewById(R.id.lineChart);
        // 사용자 아이디 가져오기
        userID = getIntent().getStringExtra("UserEmail");

        // 배열 초기화
        YesNumArray = new int[7];
        totalEventsArray = new int[7];

        onSearchFilesButtonClick();
        setupLineChart();
    }

    private void setupLineChart() {
        // 꺾은선 그래프 설정
        lineChart.setTouchEnabled(true);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);

        // X축 설정
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new XAxisValueFormatter());

        // 왼쪽 Y축 설정
        YAxis leftYAxis = lineChart.getAxisLeft();
        leftYAxis.setAxisMinimum(0f);
        leftYAxis.setAxisMaximum(1f);
        leftYAxis.setGranularity(0.2f);
        // 오른쪽 Y축 설정
        YAxis rightYAxis = lineChart.getAxisRight();
        rightYAxis.setAxisMinimum(0f);
        rightYAxis.setAxisMaximum(1f);
        rightYAxis.setGranularity(0.2f);
        // 세로 그리드 라인 제거
        xAxis.setDrawGridLines(false);
        // 가로 그리드 라인 제거 (왼쪽 Y축)
        leftYAxis.setDrawGridLines(false);
        // 가로 그리드 라인 제거 (오른쪽 Y축)
        rightYAxis.setDrawGridLines(false);

       /* Description description = new Description();
        description.setEnabled(false);
        lineChart.setDescription(description);*/
        // 데이터 추가
        ArrayList<Entry> entries = new ArrayList<>();
        // 각 날짜의 (yesnum / totalEvents) 값을 추가
        for(int d=0;d<7;d++){
            dailyCompletionRate[d]=(float)YesNumArray[d]/(float)totalEventsArray[d];
            entries.add(new Entry(d, dailyCompletionRate[d]));
        }
        // 차트에 담길 데이터
        LineDataSet dataSet = new LineDataSet(entries, "일정 달성률");
        dataSet.setColor(Color.GREEN);
        dataSet.setCircleColor(Color.GREEN);

        LineData lineData = new LineData(dataSet);

        lineChart.setData(lineData);
        lineChart.invalidate();
    }
    private class XAxisValueFormatter extends ValueFormatter {
        @Override
        public String getAxisLabel(float value, AxisBase axis) {
            // 여기에서 날짜 포맷을 반환 (value가 날짜를 나타냄)
            int intValue = (int) value; // value가 float이므로 int로 변환

            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, intValue - 6); // 1을 빼서 현재 날짜로부터 몇 일 전인지 계산

            SimpleDateFormat sdf = new SimpleDateFormat("dd일");
            return sdf.format(calendar.getTime());
        }
    }
    // Button 클릭 메서드
    private void onSearchFilesButtonClick() {
        // 사용자 아이디와 현재 년, 월, 일을 가져오기
        Calendar calendar = Calendar.getInstance();
        int cYear = calendar.get(Calendar.YEAR);
        int cMonth = calendar.get(Calendar.MONTH);
        int cDay = calendar.get(Calendar.DAY_OF_MONTH);
        //오늘날짜

        // 내부 저장소 디렉터리 가져오기
        File internalStorageDirectory = getFilesDir();

        // 파일 결과를 저장할 StringBuilder
        StringBuilder fileResults = new StringBuilder();


        //날짜 조정 부분
        // 파일 검색 및 결과 추가
        for (int day=cDay-6; day <=cDay; day++) {

            String fnameToSearch = userID + cYear + "-" + (cMonth + 1) + "-" + day + ".txt";
            File fileToSearch = new File(internalStorageDirectory, fnameToSearch);
            if (fileToSearch.exists()) {
                readEvenLines(fileToSearch,DayTh);
                DayTh++;
            }
        }

    }

    private void readEvenLines(File file,int day) {
        /*StringBuilder contents = new StringBuilder();*/

        try {
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            int lineNum=0;
            String line;
            while ((line = br.readLine()) != null) {
                // 파일의 첫 번째 줄인 경우
                if (lineNum==0) {
                    // 등록된 일정의 갯수-1을 읽어와서 1을 더한 값을 totalEventsArray[day]에 저장
                    totalEventsArray[day] = (Integer.parseInt(line) + 1);
                    lineNum++;
                }
                else {
                    // 홀수 번째 라인인 경우
                    if (lineNum % 2 == 1) {
                        if ("Yes".equals(line.trim())) {
                            YesNumArray[day]++;
                        }
                    }
                    else {
                        lineNum++;
                        // 짝수 번째 라인인 경우 다음 줄을 읽음
                        line = br.readLine();
                    }
                }

            }
            Log.d("BackgroundActivity", "totalEventsArray[" + day + "]: " + totalEventsArray[day]);
            Log.d("BackgroundActivity", "YesNumArray[" + day + "]: " + YesNumArray[day]);
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        /*return contents.toString();*/
    }
}