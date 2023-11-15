package com.example.canlender_openpro.Fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.canlender_openpro.R;

import android.os.AsyncTask;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


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
        dateTextView = view.findViewById(R.id.textView2);
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

        return view;
    }

    private void updateCurrentDate() {
        String currentDate = dateFormat.format(new Date());
        dateTextView.setText("현재 날짜: " + currentDate);
    }
}
class MainActivity extends AppCompatActivity {
    /*private TextView weatherTextView;*/ // 날씨 정보를 표시할 TextView
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // XML에서 TextView에 대한 참조 얻기
        /*weatherTextView = findViewById(R.id.weathernow);*/

        WeatherDataDownloader downloader = new WeatherDataDownloader();
        downloader.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public class WeatherDataDownloader extends AsyncTask<Void, Void, String> {
        /*private TextView weatherTextView; // 날씨 정보를 표시할 TextView
        public WeatherDataDownloader(TextView weatherTextView) {
            this.weatherTextView = weatherTextView;
        }*/
        public String doInBackground(Void... voids) {
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
                String mainWeather = openWeather.MainWeather(jsonData);
                ImageView weatherImage = findViewById(R.id.weatherImage);

                // 여기에서 mainWeather를 사용하여 UI를 업데이트하거나 필요한 작업을 수행합니다.
                /*if (weatherTextView != null) {
                    ImageView weatherImage = findViewById(R.id.weatherImage);*/

                if("clouds".equalsIgnoreCase(mainWeather)){
                    weatherImage.setImageResource(R.drawable.opensw_cloud);
                    weatherImage.setVisibility(View.VISIBLE);
                }
                else if("clear".equalsIgnoreCase(mainWeather)){
                    weatherImage.setImageResource(R.drawable.opensw_clear);
                    weatherImage.setVisibility(View.VISIBLE);
                }
                else if("rain".equalsIgnoreCase(mainWeather)){
                    weatherImage.setImageResource(R.drawable.opensw_rain);
                    weatherImage.setVisibility(View.VISIBLE);
                }
                else if("snow".equalsIgnoreCase(mainWeather)){
                    weatherImage.setImageResource(R.drawable.opensw_snow);
                    weatherImage.setVisibility(View.VISIBLE);
                }
                else if("mist".equalsIgnoreCase(mainWeather)||"drizzle".equalsIgnoreCase(mainWeather)){
                    weatherImage.setImageResource(R.drawable.opensw_mist);
                    weatherImage.setVisibility(View.VISIBLE);
                }

                /*weatherTextView.setText("현재 날씨: " + mainWeather);*/

            } else {
                // 데이터 다운로드에 실패한 경우 처리할 내용을 추가합니다.
                Toast.makeText(MainActivity.this, "날씨 정보를 가져오지 못했습니다.", Toast.LENGTH_LONG).show();
                /*if (weatherTextView != null) {
                    weatherTextView.setText("날씨 정보를 가져오지 못했습니다.");*/
            }
        }
    }



    class OpenWeather {

        String MainWeather(String jsonData) {
            try {
                if(jsonData==null){
                    return "No Data available";
                }

                // JSON 문자열을 JSON 객체로 파싱
                JSONObject jsonObject = new JSONObject(jsonData);

                // "weather" 배열을 가져옴
                JSONArray weatherArray = jsonObject.getJSONArray("weather");

                // "weather" 배열의 첫 번째 객체를 가져옴
                JSONObject weatherObject = weatherArray.getJSONObject(0);

                // "main" 속성 값을 가져옴
                String mainWeather = weatherObject.getString("main");

                return mainWeather;
            } catch (JSONException e) {
                e.printStackTrace();
                return "Error while parsing JSON data";
            }
        }


    }


}