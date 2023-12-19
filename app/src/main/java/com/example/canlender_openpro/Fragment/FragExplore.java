package com.example.canlender_openpro.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.canlender_openpro.R;
import com.google.android.material.textview.MaterialTextView;

import org.json.JSONException;
import org.json.JSONObject;

public class FragExplore extends Fragment {

    private RequestQueue queue;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 프래그먼트 레이아웃 설정
        View view = inflater.inflate(R.layout.frag_explore, container, false);

        // Volley 라이브러리를 사용하기 위해 RequestQueue 초기화
        queue = Volley.newRequestQueue(requireContext()); // 'this' 대신 'requireContext()' 사용

        // NASA API 호출
        String apiURL = "https://api.nasa.gov/planetary/apod?api_key=akvsSgu4fV1m0wXD036PJiymJp0dHvbQJ4eRWQfv";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, apiURL, null,
                response -> {
                    try {
                        // API 응답에서 필요한 데이터 추출
                        String astronomyTitle = response.getString("title");
                        String astronomyDate = response.getString("date");
                        String astronomyExplanation = response.getString("explanation");
                        String astronomyImageUrl = response.getString("hdurl");

                        // UI 업데이트
                        updateUI(view, astronomyTitle, astronomyDate, astronomyExplanation, astronomyImageUrl);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    // Volley 에러 로그 추가
                    Log.e("VolleyError", "Volley Error: " + error.toString());

                    Toast.makeText(requireContext(), "Something Went Wrong", Toast.LENGTH_SHORT).show();
                });

        // 요청을 RequestQueue에 추가
        queue.add(jsonObjectRequest);

        return view;
    }

    // UI 업데이트 메서드
    private void updateUI(View view, String title, String date, String explanation, String imageUrl) {
        // UI를 업데이트하는 코드 추가

        // 예시: 텍스트뷰에 데이터 설정
        MaterialTextView titleTextView = view.findViewById(R.id.astronomyImageTitleMark);
        titleTextView.setText(title);

        // 예시: 이미지뷰에 이미지 로드 (Glide 사용)
        ImageView imageView = view.findViewById(R.id.astronomyImageView);
        Glide.with(this).load(imageUrl).into(imageView);
    }
}
