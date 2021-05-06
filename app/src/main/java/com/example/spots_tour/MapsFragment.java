package com.example.spots_tour;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class MapsFragment extends Fragment implements OnMapReadyCallback,LocationListener,GoogleMap.OnMarkerClickListener {
    private static final String TAG = MapsFragment.class.getSimpleName();
    private GoogleMap mMap;
        private ChildEventListener mChildEventListener;
        private DatabaseReference mVideo;
        Marker marker;
    private Activity activity;
    private BottomSheetBehavior bottomSheetBehavior;




    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        LinearLayout linearLayout = getView().findViewById(R.id.design_bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(linearLayout);
        ChildEventListener mChildEventListener;
        mVideo = FirebaseDatabase.getInstance().getReference("video");

        activity=getActivity();




    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_maps, container, false);
    }



    @Override
        public void onMapReady(GoogleMap googleMap) {

         Map<Double , Member> memMarker = new HashMap();
        MediaController mediaController = new MediaController(activity);


            mMap = googleMap;
            setupMap();
            googleMap.setOnMarkerClickListener(this);
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mVideo.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot s : snapshot.getChildren()){
                        Member member = s.getValue(Member.class);
                        if(member.getLatitude() != 0 && member.getLongitude() != 0){
                        LatLng location = new LatLng(member.getLatitude(),member.getLongitude());
                        mMap.addMarker(new MarkerOptions().position(location).title(member.getName()))
                                .setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                             memMarker.put(member.getLatitude(),member);
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            // add on Click Lisener for marker on maps
            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {

                    Member member = memMarker.get(marker.getPosition().latitude);
                    VideoForMapFragment fragment = new VideoForMapFragment();

                    Log.d(TAG, "onMarkerClick: "+marker.getPosition());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(),10));
                    bottomSheetBehavior.setPeekHeight(700,true);
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    TextView title =getView().findViewById(R.id.video_title);
                    TextView desc = getView().findViewById(R.id.descride);
                    VideoView videoView=getView().findViewById(R.id.videoview_forMap);
                    title.setText(member.getName());
                    desc.setText(member.getComment());
                    mediaController.setAnchorView(videoView);
                    videoView.setMediaController(mediaController);
                    Uri uri = Uri.parse(member.getVideouri());
                    videoView.setVideoURI(uri);
                    videoView.start();


                    return false;
                }
            });

        }
        private void setupMap() {
//        25.0327717,121.5616929
            LatLng  taipei101 = new LatLng(  25.0327717,121.5616929);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(taipei101,2));

   }

    @Override
    public void onLocationChanged(@NonNull Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

}