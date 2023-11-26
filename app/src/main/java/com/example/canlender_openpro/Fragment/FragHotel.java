package com.example.canlender_openpro.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.canlender_openpro.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class FragHotel extends Fragment {
    private View view;
    private String userID;
    private TextView tvFileResults; // TextView 추가

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_hotel, container, false);

        // TextView 초기화
        tvFileResults = view.findViewById(R.id.tvFileResults);

        // Button 클릭 이벤트
        Button btnSearchFiles = view.findViewById(R.id.btnSearchFiles);
        btnSearchFiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSearchFilesButtonClick(v);
            }
        });

        return view;
    }

    // Button 클릭 메서드
    private void onSearchFilesButtonClick(View view) {
        // 사용자 아이디와 현재 년, 월, 일을 가져오기
        Intent intent = requireActivity().getIntent();
        userID = intent.getStringExtra("UserEmail");
        Calendar calendar = Calendar.getInstance();
        int cYear = calendar.get(Calendar.YEAR);
        int cMonth = calendar.get(Calendar.MONTH);
        int cDay = calendar.get(Calendar.DAY_OF_MONTH);

        // 내부 저장소 디렉터리 가져오기
        File internalStorageDirectory = getActivity().getFilesDir();

        // 파일 결과를 저장할 StringBuilder
        StringBuilder fileResults = new StringBuilder();

        // 파일 검색 및 결과 추가
        for (int day = 1; day <= 31; day++) {
            String fnameToSearch = userID + cYear + "-" + (cMonth + 1) + "-" + day + ".txt";
            File fileToSearch = new File(internalStorageDirectory, fnameToSearch);

            if (fileToSearch.exists()) {
                String formattedDate = formatDate(cYear, cMonth + 1, day);
                fileResults.append("날짜: ").append(formattedDate).append("\n");
                String fileContent = readEvenLines(fileToSearch);
                fileResults.append("일정 내용:\n").append(fileContent).append("\n");
            } else {
                // fileResults.append("파일 찾지 못함: ").append(fileToSearch.getAbsolutePath()).append("\n");
            }
        }

        // 결과를 TextView에 설정
        tvFileResults.setText(fileResults.toString());
    }

    private String formatDate(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day); // Month is 0-based
        Date date = calendar.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 MM월 dd일");
        return sdf.format(date);
    }

    private String readEvenLines(File file) {
        StringBuilder contents = new StringBuilder();

        try {
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);

            int lineNumber = 1;
            String line;
            while ((line = br.readLine()) != null) {
                // 짝수번째 라인만 추가
                if (lineNumber % 2 == 0) {
                    contents.append(lineNumber/2).append(": ").append(line).append("\n");
                }
                lineNumber++;
            }

            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return contents.toString();
    }
}
