package com.example.spots_tour;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private static final int REQUEST_REGISTER = 100;
    private FirebaseAnalytics mFirebaseAnalytics;
    private FirebaseAuth mAuth;
    private static final String TAG = LoginActivity.class.getSimpleName();
    private TextView edid;
    private TextView edpassword;
    private CheckBox cbRemember;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mAuth = FirebaseAuth.getInstance();
        edid = findViewById(R.id.ed_id);
        edpassword = findViewById(R.id.ed_password);
        cbRemember = findViewById(R.id.cb_rem_userid);
        cbRemember.setChecked(
                         getSharedPreferences("spots_tour",MODE_PRIVATE)
                        .getBoolean("REMEMBER_USERID",false)
                );
        cbRemember.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean b) {
                getSharedPreferences("spots_tour",MODE_PRIVATE)
                        .edit()
                        .putBoolean("REMEMBER_USERID",b)
                        .apply();
            }
        });
        String userid = getSharedPreferences("spots_tour",MODE_PRIVATE)
                .getString("USERID","");
        edid.setText(userid);

    }
    public void forgetPassword(View view){
        String userid = edid.getText().toString();
        if(userid.isEmpty()){
            Toast.makeText(this,"userid is Empty",Toast.LENGTH_LONG).show();
            return;
        }
        mAuth.sendPasswordResetEmail(userid).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    new AlertDialog.Builder(LoginActivity.this)
                            .setTitle("忘記密碼")
                            .setMessage("已成功送出變更密碼郵件，請檢查您的電子信箱")
                            .setPositiveButton("OK",null)
                            .show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                new AlertDialog.Builder(LoginActivity.this)
                        .setTitle("錯誤訊息")
                        .setMessage("變更密碼郵件送出失敗 使用者不存在，請檢查您的帳號是否正確")
                        .setPositiveButton("OK",null)
                        .show();
            }
        });
    }

    public void login(View view){
        user = mAuth.getCurrentUser();
        String userid = edid.getText().toString();
        String password = edpassword.getText().toString();

            if (userid.isEmpty()) {
                Toast.makeText(this, "userid is Empty", Toast.LENGTH_LONG).show();
                return;
            }
            if (password.isEmpty()) {
                Toast.makeText(this, "password is Empty", Toast.LENGTH_LONG).show();
                return;
            }
            mAuth.signInWithEmailAndPassword(userid, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        boolean remember=getSharedPreferences("atm",MODE_PRIVATE)
                                .getBoolean("REMEMBER_USERID",false);
                        if(remember){
                            getSharedPreferences("spots_tour",MODE_PRIVATE)
                                    .edit()
                                    .putString("USERID",userid)
                                    .apply();
                        }
                        Toast.makeText(LoginActivity.this, "Login OK", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                        intent.putExtra("logon",true);
                        startActivity(intent);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(LoginActivity.this, "Fail : " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
            });



    }





    public void register(View view){
        Intent intent = new Intent(this,RegisterActivity.class);
       startActivityForResult(intent,REQUEST_REGISTER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_REGISTER){
            if(resultCode ==RESULT_OK){
                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                intent.putExtra("logon",true);
                startActivity(intent);
            }
        }
    }



}