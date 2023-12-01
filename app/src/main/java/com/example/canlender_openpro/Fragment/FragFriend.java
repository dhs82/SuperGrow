package com.example.canlender_openpro.Fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.canlender_openpro.R;

public class FragFriend extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_friend, container, false);

        // 지도 초기화
        initMap();

        // 현재 위치로 이동하는 버튼 설정
        ImageButton btnMyLocation = view.findViewById(R.id.btnMyLocation);
        btnMyLocation.setOnClickListener(v -> onMyLocationButtonClick());

        return view;
    }

    private void initMap() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(requireActivity(), this::handleLocationSuccess);
        } else {
            // 위치 권한이 없는 경우 권한 요청
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }

        // 마커 추가
        addMarkers();
    }

    private void addMarkers() {
        // 첫 번째 마커
        LatLng marker1 = new LatLng(36.6258446, 127.4542446);
        mMap.





                addMarker(new MarkerOptions().position(marker1).title("Marker 1").snippet("Description for Marker 1"));

        // 두 번째 마커
        LatLng marker2 = new LatLng(36.6239046, 127.4592405);
        mMap.addMarker(new MarkerOptions().position(marker2).title("Marker 2").snippet("Description for Marker 2"));

        // 세 번째 마커
        LatLng marker3 = new LatLng(36.6283269, 127.4581964);
        mMap.addMarker(new MarkerOptions().position(marker3).title("Marker 3").snippet("Description for Marker 3"));

        // 네 번째 마커
        LatLng marker4 = new LatLng(36.631407, 127.4571562);
        mMap.addMarker(new MarkerOptions().position(marker4).title("Marker 4").snippet("Description for Marker 5"));

        //다섯 번째 마커
        LatLng marker5 = new LatLng(36.6273709, 127.460599);
        mMap.addMarker(new MarkerOptions().position(marker5).title("Marker 4").snippet("Description for Marker 5"));
    }

    private void handleLocationSuccess(Location location) {
        if (location != null) {
            // 현재 위치가 있는 경우 현재 위치로 이동
            LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
            moveCamera(currentLocation);
        } else {
            // 현재 위치가 없는 경우 (36, 127) 위치로 이동
            LatLng desiredLocation = new LatLng(36.632473380701, 127.45314301376);
            moveCamera(desiredLocation);
        }
    }

    private void moveCamera(LatLng location) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 15f));
    }

    private void onMyLocationButtonClick() {
        Log.d("MyLocationButton", "Button clicked");

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(requireActivity(), this::handleLocationSuccess);
        } else {
            Log.d("MyLocationButton", "Location permission not granted");
            // 위치 권한이 없는 경우 권한 요청
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // 위치 권한이 허용된 경우 지도 초기화
            initMap();
        }
    }
}
