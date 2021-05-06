package com.example.spots_tour;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity  {
    //Initialize variable
    private NavigationView view_start;
    private DrawerLayout drawerLayout;
    boolean logon=false;
    private static final int REQUEST_LOGIN = 100;
    private FirebaseAnalytics mFirebaseAnalytics;
    private static final String TAG = MainActivity.class.getSimpleName();
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private GoogleMap mMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mAuth = FirebaseAuth.getInstance();
        logon = getIntent().getBooleanExtra("logon",false);
        if(!logon && (mAuth.getCurrentUser()==null)){
            updateUserState();
        }
//        mAuth.addAuthStateListener的前面不要加入這個updateUserState(); 不然登入畫面會多跳轉一次
        if(mAuth.getCurrentUser() != null){
            mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    Log.d(TAG, "onAuthStateChanged: 狀態改變");
                    updateUserState();
                }
            });
        }

        setUpActionBar();
        initDrawer();
        initBody();




    }

    private void updateUserState() {
        user= mAuth.getCurrentUser();
        if(user == null ){
            Intent intent = new Intent(this,LoginActivity.class);
            startActivityForResult(intent,REQUEST_LOGIN);
            Log.d(TAG, "updateUserState: 準備登入");
        }else {
            Log.d(TAG, "updateUserState: 已經登入");
            Toast.makeText(this,"歡迎登入 "+ user.getEmail()+"是否Email驗證 : "+user.isEmailVerified(),Toast.LENGTH_LONG).show();
            if(!(user.isEmailVerified())){
                user.sendEmailVerification()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                new AlertDialog.Builder(MainActivity.this)
                                        .setTitle("驗證信箱")
                                        .setMessage("已成功送出驗證郵件，請檢查您的電子信箱")
                                        .setPositiveButton("OK",null)
                                        .show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle("驗證信箱")
                                .setMessage("無法送出驗證郵件"+e.getLocalizedMessage())
                                .setPositiveButton("OK",null)
                                .show();
                    }
                });
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_LOGIN){
            if(resultCode !=RESULT_OK){
                finish();
            }
        }
    }

    private void setUpActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // 設定ActionBar標題列的左上角可以加上按鈕
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    // 初始化抽屜選單功能
    private void initDrawer() {
        drawerLayout = findViewById(R.id.drawer_layout);
        // 建立ActionBarDrawerToggle監聽器，監聽抽屜開關的狀態
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.textOpen, R.string.textClose);
        // 檢查裝置是否為M(Marshmallow, Android 6.0)以決定要採用新/舊的方法來加上抽屜監聽器
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            drawerLayout.addDrawerListener(actionBarDrawerToggle);
        } else {
            drawerLayout.setDrawerListener(actionBarDrawerToggle);
        }
        // 將左上角按鈕動畫與抽屜選單開關同步化
        actionBarDrawerToggle.syncState();

        view_start = (NavigationView) findViewById(R.id.navigation_start);
        view_start.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                item.setChecked(true);
                drawerLayout.closeDrawers();
                Fragment fragment;
                switch (item.getItemId()){
                    case R.id.item_home:
                        initBody();
                        break;
                    case R.id.item_showVideo:
                        Intent intent1 = new Intent(MainActivity.this,Showvideo.class);
                        startActivity(intent1);
                        break;
                    case R.id.item_createVideo:
                    Intent intent = new Intent(MainActivity.this,CreateVideoActivity.class);
                    startActivity(intent);
                        break;
                    case R.id.item_aboutUs:
                        fragment = new AboutUsFragment();
                        switchFragment(fragment);
                        setTitle("About Us");
                        break;
                    case R.id.item_logout:
                        logout();
                        break;
                     default:
                        initBody();
                        break;

                }
                return true;
            }
        });

    }

    private void logout() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        //Initialize alert dialog
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mAuth.signOut();
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Dismiss dialog
                dialog.dismiss();
            }
        }).show();
    }

    private void initBody() {
        Fragment fragment = new MapsFragment();
        switchFragment(fragment);
        setTitle("Home");
    }

    // 點擊標題列上的按鈕會呼叫此方法
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // 如果抽屜開啟，將之關閉；如果關閉，將之開啟
            case android.R.id.home:
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }


                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void switchFragment(Fragment fragment) {
        Fragment mapsFragment = new MapsFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        if(fragment == mapsFragment){
            fragmentManager.beginTransaction().add(R.id.body,fragment).commit();
        }else {

        FragmentTransaction fragmentTransaction =
                fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.body, fragment);
        fragmentTransaction.commit();
        }

    }



}