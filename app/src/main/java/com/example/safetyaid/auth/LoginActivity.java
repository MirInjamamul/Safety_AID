package com.example.safetyaid.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.safetyaid.MainActivity;
import com.example.safetyaid.R;
import com.example.safetyaid.data.DBHelper;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    EditText email, password;
    Button login;
    TextView register;
    boolean isEmailValid, isPasswordValid;
    TextInputLayout emailError, passError;
    private  DBHelper mydb;

//    SharedPreferences sh;

    // The value will be default as empty string because for
    // the very first time when the app is opened, there is nothing to show

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        login = (Button) findViewById(R.id.login);
        register = (TextView) findViewById(R.id.register);
        emailError = (TextInputLayout) findViewById(R.id.emailError);
        passError = (TextInputLayout) findViewById(R.id.passError);

        mydb = new DBHelper(this);

//        sh = getSharedPreferences("MySharedPref", MODE_APPEND);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetValidation();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // redirect to RegisterActivity
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });


    }

    public void SetValidation() {
        // Check for a valid email address.
        if (email.getText().toString().isEmpty()) {
            emailError.setError("Please enter your email address");
            isEmailValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {
            emailError.setError("This email is Invalid");
            isEmailValid = false;
        } else  {
            isEmailValid = true;
            emailError.setErrorEnabled(false);
        }

        // Check for a valid password.
        if (password.getText().toString().isEmpty()) {
            passError.setError("Please enter a password");
            isPasswordValid = false;
        } else if (password.getText().length() < 6) {
            passError.setError("Please enter atleast 6 digit");
            isPasswordValid = false;
        } else  {
            isPasswordValid = true;
            passError.setErrorEnabled(false);
        }

        if (isEmailValid && isPasswordValid) {

            boolean getAuth = mydb.getAuthData(email.getText().toString(),password.getText().toString());

            if (getAuth) {
                Log.d(TAG,"Email and Password Correct");
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
//                SharedPreferences.Editor editor = sh.edit();
//                editor.putBoolean("logged",true);
//                editor.commit();
                Boolean x = mydb.insertStatus(true);
                System.out.println("My DB True");
                if(x) System.out.println(mydb.getStatus());
            }else {
                Toast.makeText(getApplicationContext(), "Wrong Username or Password", Toast.LENGTH_SHORT).show();
//                SharedPreferences.Editor editor = sh.edit();
//                editor.putBoolean("logged",false);
//                editor.commit();
                Boolean x = mydb.insertStatus(false);
                System.out.println("My DB false");
                if(x) System.out.println(mydb.getStatus());
            }
            }

    }

}