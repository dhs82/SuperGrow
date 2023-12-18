package com.example.canlender_openpro;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.material.textview.MaterialTextView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class NasaActivity extends AppCompatActivity {

    private RequestQueue queue;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nasa);

        // Volley 라이브러리를 사용하기 위해 RequestQueue 초기화
        queue = Volley.newRequestQueue(this);

        // 첫 번째 NASA API 호출
        String firstApiURL = "https://api.nasa.gov/planetary/apod?api_key=akvsSgu4fV1m0wXD036PJiymJp0dHvbQJ4eRWQfv";
        JsonObjectRequest firstJsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, firstApiURL, null,
                response -> {
                    try {
                        // API 응답에서 필요한 데이터 추출
                        String astronomyTitle = response.getString("title");
                        String astronomyDate = response.getString("date");
                        String astronomyExplanation = response.getString("explanation");
                        String astronomyImageUrl = response.getString("hdurl");

                        // UI 업데이트
                        updateUI(astronomyTitle, astronomyDate, astronomyExplanation, astronomyImageUrl);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(NasaActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show()
        );

        // 두 번째 NASA API 호출
        String secondApiURL = "https://api.nasa.gov/planetary/apod?api_key=akvsSgu4fV1m0wXD036PJiymJp0dHvbQJ4eRWQfv";
        JsonObjectRequest secondJsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, secondApiURL, null,
                response -> {
                    try {
                        // API 응답에서 필요한 데이터 추출
                        String astronomyTitle = response.getString("title");
                        String astronomyDate = response.getString("date");
                        String astronomyExplanation = response.getString("explanation");
                        String astronomyImageUrl = response.getString("hdurl");

                        // UI 업데이트
                        updateUI(astronomyTitle, astronomyDate, astronomyExplanation, astronomyImageUrl);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    // Volley 에러 로그 추가
                    Log.e("VolleyError", "Volley Error: " + error.toString());

                    Toast.makeText(NasaActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                });

        // 요청을 RequestQueue에 추가
        queue.add(firstJsonObjectRequest);
        queue.add(secondJsonObjectRequest);
    }

    // UI 업데이트 메서드
    private void updateUI(String title, String date, String explanation, String imageUrl) {
        // UI를 업데이트하는 코드 추가

        // 예시: 텍스트뷰에 데이터 설정
        MaterialTextView titleTextView = findViewById(R.id.astronomyImageTitleMark);
        titleTextView.setText(title);

        // 예시: 이미지뷰에 이미지 로드 (Glide 사용)
        ImageView imageView = findViewById(R.id.astronomyImageView);
        Glide.with(this).load(imageUrl).into(imageView);
    }

    // 백 버튼 처리
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish(); // 현재 액티비티를 종료
    }
}
