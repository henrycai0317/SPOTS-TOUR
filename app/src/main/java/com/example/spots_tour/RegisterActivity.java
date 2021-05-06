package com.example.spots_tour;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {


    private EditText newAccount;
    private EditText newNickName;
    private EditText pw1;
    private EditText pw2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        newAccount = findViewById(R.id.newAccount);
        newNickName = findViewById(R.id.newNickName);
        pw1 = findViewById(R.id.newPW1);
        pw2 = findViewById(R.id.newPW2);
    }


    public  void createNewAccount(View view){
        String account = newAccount.getText().toString();
        String nickname = newNickName.getText().toString();
        String password = pw1.getText().toString();
        String cPassword = pw2.getText().toString();

        if(account.isEmpty()){
            Toast.makeText(this,"Please Enter Account",Toast.LENGTH_LONG).show();
            return;
        }

        if(nickname.isEmpty()){
            Toast.makeText(this,"Please Enter NickName",Toast.LENGTH_LONG).show();
            return;
        }

        if(!(password.equals(cPassword))){
            Toast.makeText(this,"Confirm Password",Toast.LENGTH_LONG).show();
            return;
        }
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(account,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    new AlertDialog.Builder(RegisterActivity.this)
                            .setTitle("註冊結果")
                            .setMessage("註冊成功 歡迎使用 SPOTS-TOUR ")
                             .setPositiveButton("OK",null).show();
                             setResult(RESULT_OK);
                             finish();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RegisterActivity.this,"Fail : "+e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
            }
        });

    }
}