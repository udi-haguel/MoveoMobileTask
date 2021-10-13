package dev.haguel.moveotask.fragments.maps_frag;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import dev.haguel.moveotask.DatabaseManager;
import dev.haguel.moveotask.R;
import dev.haguel.moveotask.entities.NoteEntity;

public class MapsFragment extends Fragment {


    public static MapsFragment newInstance(double lat, double lng) {
        
        Bundle args = new Bundle();
        args.putDouble("lat", lat);
        args.putDouble("lng", lng);
        
        MapsFragment fragment = new MapsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private GoogleMap mGoogleMap;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            if (googleMap == null) return;

            mGoogleMap = googleMap;
            // Move Camera
            if (getArguments() != null && getArguments().containsKey("lat") && getArguments().containsKey("lng")){
                double lat = getArguments().getDouble("lat");
                double lng = getArguments().getDouble("lng");
                moveCameraToUserLocation(lat, lng);
            }
            // Add Marks On Map
            if (DatabaseManager.instance().getNoteArrayList().size() > 0) {
                for (NoteEntity note : DatabaseManager.instance().getNoteArrayList()) {
                    LatLng noteLocation = new LatLng(note.getLatitude(), note.getLongitude());
                    googleMap.addMarker(new MarkerOptions().position(noteLocation).title(note.getTitle()));
                }
            }

        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Load Map View
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }


    public void setNewPosition(LatLng latLng) {
        moveCameraToUserLocation(latLng.latitude, latLng.longitude);
    }

    @SuppressLint("MissingPermission")
    private void moveCameraToUserLocation(double latitude, double longitude) {
        if (mGoogleMap == null) return;

        mGoogleMap.setMyLocationEnabled(true);
        CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(latitude, longitude)).zoom(15).build();
        mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }
}