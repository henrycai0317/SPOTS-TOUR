package com.example.spots_tour;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.util.Arrays;
import java.util.List;

public class CreateVideoActivity extends AppCompatActivity {

    private static final int PICK_VIDEO=1;
    private static final int ATOCOMPLETE_CODE = 100;
    VideoView videoView;
    Button button;
    ProgressBar progressBar;
    EditText editText;
    private Uri videoUri;
    MediaController mediaController;
    StorageReference storageReference;
    DatabaseReference databaseReference;
    Member member;
    UploadTask uploadTask;
    // autocomplete initialize
    EditText editAddress ;
    TextView textLat,textLng;
//    TextView textLatLng;
     LatLng latLng;
     TextInputEditText comment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_video);

        member = new Member();
        storageReference= FirebaseStorage.getInstance().getReference("Video");
        databaseReference = FirebaseDatabase.getInstance().getReference("video");

        videoView =findViewById(R.id.videoview_main);
        button = findViewById(R.id.button_upload_main);
        progressBar =findViewById(R.id.progress_bar_main);
        editText =findViewById(R.id.et_video_name);

        // autocomplete initialize
        editAddress = findViewById(R.id.edit_text);
//        textLatLng = findViewById(R.id.text_latlng);
        textLat = findViewById(R.id.text_latitude);
        textLng = findViewById(R.id.text_longtitude);
        comment=findViewById(R.id.videoComment);
        //Initialize places
        Places.initialize(getApplicationContext(),"AIzaSyBMF_YC0z2QPRcm5lh0CJbbA5cf7B9bFbw");
        //Set EditText non focusable
        editAddress.setFocusable(false);
        editAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Initialize place field list
                List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS
                        ,Place.Field.LAT_LNG,Place.Field.NAME);
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY
                        ,fieldList).build(CreateVideoActivity.this);
                //Start Activity Result
                startActivityForResult(intent,ATOCOMPLETE_CODE);

            }
        });


        mediaController = new MediaController(this);
        videoView.setMediaController(mediaController);
        videoView.start();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                UploadVideo();
            }
        });


    }




    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_VIDEO || resultCode == RESULT_OK ||
        data != null || data.getData() != null){
            videoUri = data.getData();
            videoView.setVideoURI(videoUri);

        }

        if(requestCode ==ATOCOMPLETE_CODE && resultCode == RESULT_OK){
            //When Sucess
            //Initialize place
            Place place = Autocomplete.getPlaceFromIntent(data);
            //Set address on editAddress
            editAddress.setText(place.getAddress());
            //Set Latitude & Longtitude
            latLng = place.getLatLng();
            textLat.setText(String.valueOf(place.getLatLng().latitude));
            textLng.setText(String.valueOf(place.getLatLng().longitude));
//            textLatLng.setText(String.valueOf(place.getLatLng()));
        }else if(resultCode == AutocompleteActivity.RESULT_ERROR){
            //When Error
            //Initialize status
            Status status = Autocomplete.getStatusFromIntent(data);
            //Display Toast
            Toast.makeText(getApplicationContext(),status.getStatusMessage(),
                    Toast.LENGTH_LONG).show();
        }

    }


    public void ChooseVideo(View view) {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_VIDEO);
    }


    private String getExt(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    public void ShowVideo(View view) {
        Intent intent = new Intent(CreateVideoActivity.this,Showvideo.class);
        startActivity(intent);
    }

    private void UploadVideo() {
        String videoName = editText.getText().toString();
        String search = editText.getText().toString().toLowerCase();
        String desc = comment.getText().toString();
        double latitude = Double.parseDouble(textLat.getText().toString());
        double longtitude = Double.parseDouble(textLng.getText().toString());
//        latLng = new LatLng(latitude,longtitude);


        if(videoUri != null || !TextUtils.isEmpty(videoName)){
            progressBar.setVisibility(View.VISIBLE);
            final StorageReference reference = storageReference.child(System.currentTimeMillis()+"."+getExt(videoUri));
            uploadTask = reference.putFile(videoUri);

            Task<Uri>  urltask= uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if(!task.isSuccessful()){
                        throw task.getException();
                    }
                    return reference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {

                    if(task.isSuccessful()){
                        Uri downloadUri = task.getResult();
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(CreateVideoActivity.this,"Data Saved",Toast.LENGTH_LONG).show();

                        member.setName(videoName);
                        member.setVideouri(downloadUri.toString());
                        member.setSearch(search);
                        member.setComment(desc);
                        member.setLatitude(latitude);
                        member.setLongitude(longtitude);
//                        member.setLatLng(latLng);
                        String i = databaseReference.push().getKey();
                        databaseReference.child(i).setValue(member);
                        Intent intent = new Intent(CreateVideoActivity.this,MapsFragment.class);

                    }else {
                        Toast.makeText(CreateVideoActivity.this,"Failed ",Toast.LENGTH_LONG).show();
                    }

                }
            });
        }else{
            Toast.makeText(CreateVideoActivity.this,"All Fields are required",Toast.LENGTH_LONG).show();
        }
    }

}