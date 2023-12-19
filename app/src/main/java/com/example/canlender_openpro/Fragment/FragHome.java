package com.example.canlender_openpro.Fragment;

import static android.content.Intent.getIntent;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
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

import com.example.canlender_openpro.BackgroundActivity;
import com.example.canlender_openpro.MemoActivity;
import com.example.canlender_openpro.NasaActivity;
import com.example.canlender_openpro.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FragHome extends Fragment {

    public int level=6;
    public int lastlevel=6;
    private TextView dateTextView;
    private ImageView characterImageView;
    private Handler handler;
    private SimpleDateFormat dateFormat;
    private View view;
    private String TAG = "프래그먼트";
    private String userID=null;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        view = inflater.inflate(R.layout.frag_home, container, false);

        Intent intent = requireActivity().getIntent();
        userID = intent.getStringExtra("UserEmail");
        // 날짜 초기화
        dateTextView = view.findViewById(R.id.textView);
        characterImageView = view.findViewById(R.id.characterImageView);
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

        readLevelFromFile(userID);
        setlevel(level);
        // 리소스 이름을 설정
        String resourceName = "lev_" + lastlevel + "char";



        // 리소스 이름을 사용하여 이미지를 설정
        int resourceId = getResources().getIdentifier(resourceName, "drawable", getActivity().getPackageName());
        characterImageView.setImageResource(resourceId);


        return view;
    }
    public void setlevel(int level){
        if (level<2){
            lastlevel=1;
        } else if (level>=2&&level<4) {
            lastlevel=2;
        }
        else if (level>=4&&level<6) {
            lastlevel=3;
        }
        else if (level>=6&&level<8) {
            lastlevel=4;
        }
        else if (level>=8&&level<10) {
            lastlevel=5;
        }
        else {
            lastlevel=6;
        }
    }
    public void readLevelFromFile(String name) {
        String fname = addexp(name);

        // 파일을 처리하는 메서드 호출
        int level = processFileForLevel(fname);

        // 읽어온 레벨 값을 사용할 수 있습니다.
        // 예를 들어, 이제 level 변수에 파일에서 읽어온 레벨 값이 들어 있습니다.
    }

    private int processFileForLevel(String fileName) {

        try {
            // 파일이 존재하지 않으면 생성
            File file = new File(getActivity().getFilesDir(), fileName);
            if (!file.exists()) {
                file.createNewFile();
            }

            // 파일을 읽고 쓰기 위해 BufferedReader와 FileWriter 대신에 FileInputStream과 FileOutputStream 사용
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader reader = new BufferedReader(isr);

            // 임시 파일 생성
            FileOutputStream fos = new FileOutputStream(new File(getActivity().getFilesDir(), fileName + "_temp"));

            // 맨 첫 줄 읽기
            String firstLine = reader.readLine();

            if (firstLine != null) {
                // 파일이 비어있지 않으면 정수로 변환 후 1을 더함
                level = Integer.parseInt(firstLine.trim());
            } else {
                // 파일이 비어있으면 0으로 초기화
                level = 0;
            }

            // 맨 첫 줄에 새로운 값 쓰기
            fos.write(String.valueOf(level).getBytes());

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
                Files.move(Paths.get(new File(getActivity().getFilesDir(), fileName + "_temp").getPath()),
                        Paths.get(file.getPath()), StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException | NumberFormatException e) {
            // 예외 발생 시 에러 로그 출력
            e.printStackTrace();


                }
        return level;
    }
    public String addexp(String userID){
        return "" +userID +"exp"+ ".txt";
    }

    private void updateCurrentDate() {
        String currentDate = dateFormat.format(new Date());
        dateTextView.setText("현재 날짜: " + currentDate+"\nlevel: " + lastlevel);

        // 버튼 클릭 이벤트 처리
        View backgroundButton = view.findViewById(R.id.background_Btn);
        View memoButton = view.findViewById(R.id.memo_Btn);
        View nasaButton = view.findViewById(R.id.nasa_Btn);
        backgroundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // BackgroundActivity로 이동하는 Intent 생성
                Intent intent = new Intent(getActivity(), BackgroundActivity.class);
                // MainActivity에서 전달받은 userEmail을 Intent에 추가
                Bundle bundle = getArguments();
                if (bundle != null) {
                    String userEmail = bundle.getString("UserEmail");
                    if (userEmail != null) {
                        intent.putExtra("UserEmail", userEmail);
                    }
                }
                startActivity(intent);
            }
        });
        memoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // BackgroundActivity로 이동하는 Intent 생성
                Intent intent = new Intent(getActivity(), MemoActivity.class);
                startActivity(intent);
            }
        });
        nasaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // BackgroundActivity로 이동하는 Intent 생성
                Intent intent = new Intent(getActivity(), NasaActivity.class);

                startActivity(intent);
            }
        });

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
                        updateWeatherString(mainWeather);
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
    private String getWeatherString(String mainWeather) {

        if ("clouds".equalsIgnoreCase(mainWeather)) {
            return "오늘 구름이 많이 꼈엉..";
        } else if ("clear".equalsIgnoreCase(mainWeather)) {
            return "날씨 완전 쭈으당 ! ㅎㅎ";
        } else if ("rain".equalsIgnoreCase(mainWeather)) {
            return "우산 챙기는거 잊지망 ! ㅎㅎ";
        } else if ("snow".equalsIgnoreCase(mainWeather)) {
            return "눈 오는거 정말 이뿌당 ! ㅎㅎ";
        } else if ("mist".equalsIgnoreCase(mainWeather) || "drizzle".equalsIgnoreCase(mainWeather)) {
            return "오늘은 조금 꿉꿉하드앙..";
        } else {
            return "알 수 없는 날씨 상태입니다.";
        }


    }
    private String updateWeatherString(String mainWeather) {
        TextView weatherStringTextView = view.findViewById(R.id.weatherString);
        String weatherString = getWeatherString(mainWeather);
        weatherStringTextView.setText(weatherString);
        return weatherString;
    }
    private void updateWeatherImage(String mainWeather) {
        ImageView weatherImage = view.findViewById(R.id.weatherImage);
        String weatherString = updateWeatherString(mainWeather);
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