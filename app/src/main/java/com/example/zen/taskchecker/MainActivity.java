package com.example.zen.taskchecker;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private TextView signup_txt;

    private EditText email,pass;
    private Button btnLogin;

    //Firebase
    private FirebaseAuth mAuth;

    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDialog=new ProgressDialog(this);

        mAuth=FirebaseAuth.getInstance();
        signup_txt=findViewById(R.id.Signup_txt);
        email=findViewById(R.id.email);
        pass=findViewById(R.id.password_login);
        btnLogin=findViewById(R.id.login_btn);

        signup_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Registration.class));
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mEmail=email.getText().toString().trim();
                String mPass=pass.getText().toString().trim();
                if (TextUtils.isEmpty(mEmail))
                {
                    email.setError("Required Field");
                    return;
                }
                if (TextUtils.isEmpty(mPass))
                {
                    email.setError("Required Field");
                    return;
                }
                mDialog.setMessage("Process..");
                mDialog.show();

                mAuth.signInWithEmailAndPassword(mEmail,mPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            Toast.makeText(getApplicationContext(),"Login Successful",Toast.LENGTH_LONG).show();
                            Intent intent=new Intent(getApplicationContext(),Home.class);
                            startActivity(intent);
                            mDialog.dismiss();
                        }
                        if (!task.isSuccessful())
                        {
                            Toast.makeText(getApplicationContext(),task.getException().toString(),Toast.LENGTH_LONG).show();
                            mDialog.dismiss();
                        }
                    }
                });
            }
        });
    }
}
