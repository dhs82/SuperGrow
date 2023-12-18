package com.example.canlender_openpro;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;

import com.example.canlender_openpro.Fragment.FragFriend;
import com.example.canlender_openpro.Fragment.FragHome;
import com.example.canlender_openpro.Fragment.FragHotel;
import com.example.canlender_openpro.Fragment.FragStar;
import com.google.android.material.bottomnavigation.BottomNavigationView;



public class MainActivity extends AppCompatActivity {

    //바텀 네비게이션
    BottomNavigationView bottomNavigationView;

    private String TAG = "메인";

    // 프래그먼트 변수
    Fragment fragment_home;
    Fragment fragment_star;
    Fragment fragment_group;
    Fragment fragment_hotel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //프래그먼트 생성
        fragment_home=new FragHome();
        fragment_star=new FragStar();
        fragment_group=new FragFriend();
        fragment_hotel=new FragHotel();

        //초기 플래그먼트 설정
        getSupportFragmentManager().beginTransaction().replace(R.id.main_layout, fragment_home).commitAllowingStateLoss();

        //바텀 네비게이션
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        //리스터 등록
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Log.i(TAG, "바텀 네비게이션 클릭");

                int itemId = item.getItemId();
                if (itemId == R.id.home) {
                    Log.i(TAG, "home 들어옴");
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_layout, fragment_home).commitAllowingStateLoss();
                    return true;
                } else if (itemId == R.id.star) {
                    Log.i(TAG, "star 들어옴");
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_layout, fragment_star).commitAllowingStateLoss();
                    return true;
                } else if (itemId == R.id.group) {
                    Log.i(TAG, "group 들어옴");
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_layout, fragment_group).commitAllowingStateLoss();
                    return true;
                } else if (itemId == R.id.hotel) {
                    Log.i(TAG, "hotel 들어옴");
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_layout, fragment_hotel).commitAllowingStateLoss();
                    return true;
                }
                return true;
            }
        });
        Intent intent = getIntent();
        if (intent != null) {
            String userEmail = intent.getStringExtra("UserEmail");
            if (userEmail != null) {
                // Bundle을 사용하여 데이터를 Fragment에 전달
                Bundle bundle = new Bundle();
                bundle.putString("UserEmail", userEmail);
                fragment_home.setArguments(bundle);
            }
        }
    }
}