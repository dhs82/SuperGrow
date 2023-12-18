package com.example.canlender_openpro.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import com.example.canlender_openpro.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FragFriend extends Fragment implements OnMapReadyCallback, LocationListener, GoogleMap.OnMapClickListener, GoogleMap.OnMarkerClickListener {

    private static final String TAG = "FragFriend";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final int NETWORK_PERMISSION_REQUEST_CODE = 2;
    private static final int LOCATION_UPDATE_INTERVAL = 60000; // 60 seconds
    private static final int LOCATION_UPDATE_FASTEST_INTERVAL = 50000; // 50 seconds
    private static final float MARKER_DISTANCE_THRESHOLD = 500;

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private List<Marker> markers = new ArrayList<>();

    private boolean isMapFixed = false; // Flag to indicate if the map is fixed
    private Location lastKnownLocation;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_friend, container, false);

        ImageButton btnMyLocation = view.findViewById(R.id.btnMyLocation);
        btnMyLocation.setOnClickListener(v -> onMyLocationButtonClick());

        ImageButton btnAddMarker = view.findViewById(R.id.btnAddMarker);
        btnAddMarker.setOnClickListener(v -> onAddMarkerButtonClick());

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        if (checkPermissions()) {
            startLocationUpdates();
        } else {
            requestPermissions();
        }

        initMap();

        return view;
    }

    private void onAddMarkerButtonClick() {
        // isMapFixed 값을 토글 (고정되어 있으면 해제, 해제되어 있으면 고정)
        isMapFixed = !isMapFixed;
    }

    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_NETWORK_STATE}, LOCATION_PERMISSION_REQUEST_CODE);
    }

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    private void addMarker(double latitude, double longitude, String title) {
        LatLng location = new LatLng(latitude, longitude);
        MarkerOptions markerOptions = new MarkerOptions().position(location).title(title);
        Marker marker = mMap.addMarker(markerOptions);
        markers.add(marker);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (checkPermissions()) {
            startLocationUpdates();
        } else {
            requestPermissions();
        }

        // mMap에 OnMapClickListener 및 OnMarkerClickListener 등록
        mMap.setOnMapClickListener(this);
        mMap.setOnMarkerClickListener(this);

        moveToCurrentLocation();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        // 이미 생성된 마커를 클릭하면 새로운 이미지의 마커로 변경
        changeMarkerIcon(marker);
        return true; // true를 반환하여 기본 동작을 막습니다.
    }

    // 마커 아이콘 변경 메서드
    private void changeMarkerIcon(Marker marker) {
        // TODO: 새로운 이미지의 마커 아이콘으로 변경하는 로직을 구현하세요.
        // 예를 들어, 다음과 같이 변경할 수 있습니다.
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.effort);
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 190, 89, false);
        marker.setIcon(BitmapDescriptorFactory.fromBitmap(resizedBitmap));
    }

    private void moveToCurrentLocation() {
        if (mMap != null && lastKnownLocation != null) {
            LatLng currentLocation = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15f));
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if (!isMapFixed) {
            // isMapFixed가 false인 경우에만 마커 추가
            addMarker(latLng.latitude, latLng.longitude, "클릭된 마커");
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        handleLocationChanged(location);
        checkAndRemoveMarkers(location);
        moveCameraFriend(new LatLng(location.getLatitude(), location.getLongitude()));
        lastKnownLocation = location;
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(LOCATION_UPDATE_INTERVAL);
        locationRequest.setFastestInterval(LOCATION_UPDATE_FASTEST_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, this, null);
    }

    private void handleLocationChanged(Location location) {
        handleLocationSuccess(location);
        checkAndRemoveMarkers(location);
        moveCameraFriend(new LatLng(location.getLatitude(), location.getLongitude()));
    }

    private void handleLocationSuccess(Location location) {
        LatLng currentLocation = (location != null) ? new LatLng(location.getLatitude(), location.getLongitude()) : new LatLng(0.0, 0.0);
        moveCameraFriend(currentLocation);
    }

    private void checkAndRemoveMarkers(Location currentLocation) {
        Iterator<Marker> iterator = markers.iterator();
        while (iterator.hasNext()) {
            Marker marker = iterator.next();
            Location markerLocation = new Location("");
            markerLocation.setLatitude(marker.getPosition().latitude);
            markerLocation.setLongitude(marker.getPosition().longitude);

            if (currentLocation.distanceTo(markerLocation) < MARKER_DISTANCE_THRESHOLD) {
                marker.remove();
                iterator.remove();
            }
        }
    }

    private void moveCameraFriend(LatLng location) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f));
    }

    @SuppressLint("MissingPermission")
    private void onMyLocationButtonClick() {
        Log.d(TAG, "MyLocationButton clicked");

        if (checkPermissions()) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(requireActivity(), location -> {
                if (location != null) {
                    handleLocationSuccess(location);
                    moveCameraFriend(new LatLng(location.getLatitude(), location.getLongitude()));
                } else {
                    Log.d(TAG, "Location is null");
                    // Handle when location is null
                }
            });
        } else {
            Log.d(TAG, "Location permission not granted");
            requestPermissions();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            initMap();
            moveToCurrentLocation();
        } else if (requestCode == NETWORK_PERMISSION_REQUEST_CODE && grantResults.length > 0) {
            handleNetworkPermissionResult(grantResults);
        } else {
            Log.d(TAG, "Location permission denied");
            // Handle when location permission is denied
        }
    }

    private void handleNetworkPermissionResult(int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            initMap();
            moveToCurrentLocation();
        } else {
            Log.d(TAG, "Network permission denied");
            // Handle when network permission is denied
        }
    }
}