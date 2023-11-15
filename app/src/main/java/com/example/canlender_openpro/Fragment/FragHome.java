package com.example.canlender_openpro.Fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.canlender_openpro.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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
        dateTextView = view.findViewById(R.id.textView);
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

        // 날씨 정보 가져오기
        WeatherDataDownloader downloader = new WeatherDataDownloader();
        downloader.execute();

        return view;
    }

    private void updateCurrentDate() {
        String currentDate = dateFormat.format(new Date());
        dateTextView.setText("현재 날짜: " + currentDate);
    }

    private class WeatherDataDownloader extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL("https://api.openweathermap.org/data/2.5/weather?lat=36.625627&lon=127.454416&appid=545f5e5c990faad311e871f4fb035837");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                InputStream inputStream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder response = new StringBuilder();

                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                reader.close();
                inputStream.close();
                connection.disconnect();

                return response.toString();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String jsonData) {
            if (jsonData != null) {
                OpenWeather openWeather = new OpenWeather();
                final String mainWeather = openWeather.MainWeather(jsonData);

                // UI 업데이트를 메인 스레드에서 수행
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateWeatherImage(mainWeather);
                    }
                });
            } else {
                // 데이터 다운로드에 실패한 경우 처리
                Toast.makeText(getActivity(), "날씨 정보를 가져오지 못했습니다.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private class OpenWeather {
        String MainWeather(String jsonData) {
            try {
                if (jsonData == null) {
                    return "No Data available";
                }

                // JSON 문자열을 JSON 객체로 파싱
                JSONObject jsonObject = new JSONObject(jsonData);

                // "weather" 배열을 가져옴
                JSONArray weatherArray = jsonObject.getJSONArray("weather");

                // "weather" 배열의 첫 번째 객체를 가져옴
                JSONObject weatherObject = weatherArray.getJSONObject(0);

                // "main" 속성 값을 가져옴
                return weatherObject.getString("main");
            } catch (JSONException e) {
                e.printStackTrace();
                return "Error while parsing JSON data";
            }
        }
    }

    private void updateWeatherImage(String mainWeather) {
        ImageView weatherImage = view.findViewById(R.id.weatherImage);

        if ("clouds".equalsIgnoreCase(mainWeather)) {
            weatherImage.setImageResource(R.drawable.opensw_cloud);
            weatherImage.setVisibility(View.VISIBLE);
        } else if ("clear".equalsIgnoreCase(mainWeather)) {
            weatherImage.setImageResource(R.drawable.opensw_clear);
            weatherImage.setVisibility(View.VISIBLE);
        } else if ("rain".equalsIgnoreCase(mainWeather)) {
            weatherImage.setImageResource(R.drawable.opensw_rain);
            weatherImage.setVisibility(View.VISIBLE);
        } else if ("snow".equalsIgnoreCase(mainWeather)) {
            weatherImage.setImageResource(R.drawable.opensw_snow);
            weatherImage.setVisibility(View.VISIBLE);
        } else if ("mist".equalsIgnoreCase(mainWeather) || "drizzle".equalsIgnoreCase(mainWeather)) {
            weatherImage.setImageResource(R.drawable.opensw_mist);
            weatherImage.setVisibility(View.VISIBLE);
        }
    }
}
