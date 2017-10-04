package com.example.sara__000.placeinfo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.etEmail)
    EditText etEmail;
    @BindView(R.id.etPassword)
    EditText etPassword;

    ProgressDialog progressDialog;
    FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);

        firebaseAuth = FirebaseAuth.getInstance();
    }

    @OnClick(R.id.btnRegister)
    public void registerClick(){
        if(!isFormValid()){
            return;
        }
        showProgressDialogue();

        firebaseAuth.createUserWithEmailAndPassword(
                etEmail.getText().toString(), etPassword.getText().toString()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                hideProgresDialogue();
                if(task.isSuccessful()){
                    FirebaseUser user = task.getResult().getUser();

                    user.updateProfile(new UserProfileChangeRequest.Builder().setDisplayName(userNamefromEmail(user.getEmail())).build());

                    Toast.makeText(LoginActivity.this, "Registered",
                            Toast.LENGTH_SHORT).show();

                }else{
                    Toast.makeText(LoginActivity.this, "Not success: " +
                            task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                hideProgresDialogue();
                Toast.makeText(LoginActivity.this, "error: "+ e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



    @OnClick(R.id.btnLogin)
    public  void loginClick(){
        //for crash reporting
        // int a = 6/0;
        // Toast.makeText(this, "Math result: "+ a, Toast.LENGTH_SHORT).show();
        //then go look at the crash in firebase console
        if(!isFormValid()){
            return;
        }
        showProgressDialogue();

        firebaseAuth.signInWithEmailAndPassword(
                etEmail.getText().toString(), etPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                hideProgresDialogue();
                if(task.isSuccessful()){
                    //Create the map activity and attach it here
                    Intent mapActivity = new Intent(LoginActivity.this, MapsActivity.class);
                    mapActivity.putExtra("username", userNamefromEmail(etEmail.getText().toString()));
                    startActivity(mapActivity);
                }else{
                    Toast.makeText(LoginActivity.this,
                            "Failed: " + task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }


            }
        });
    }


    private void showProgressDialogue(){
        if (progressDialog == null){
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Wait for it.....");
        }
        progressDialog.show();
    }

    private void hideProgresDialogue(){

        if(progressDialog != null && progressDialog.isShowing()){
            progressDialog.hide();
        }
    }

    private boolean isFormValid(){
        if(TextUtils.isEmpty(etEmail.getText().toString())){
            etEmail.setError(("Should not be empty"));
            return false;
        }
        if(TextUtils.isEmpty(etPassword.getText().toString())){
            etPassword.setError(("Should not be empty"));
            return false;
        }
        return true;
    }

    private String userNamefromEmail(String email){
        if (email.contains("@")){
            return email.split("@")[0];
        }else {
            return email;
        }
    }
}

