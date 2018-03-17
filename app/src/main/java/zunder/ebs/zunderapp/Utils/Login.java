package zunder.ebs.zunderapp.Utils;
import android.app.ProgressDialog;
import android.content.Intent;
import zunder.ebs.zunderapp.Profile.Profile;
import zunder.ebs.zunderapp.R;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity implements View.OnClickListener {
    private EditText editTextEmail, editTextPassword;
    private Button login;
    private TextView signup;
    private FirebaseAuth mAuth;
    private ProgressDialog dialog;

    /**
     * Main method. Create variables and
     * call functions when the screen is created.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        dialog = new ProgressDialog(Login.this);
        dialog.setTitle("Please wait");
        dialog.setMessage("Logging in...");
        //link elements in the XML layout to the Java class
        linkElements();

        //instantiate authentification with FIrebase
        mAuth = FirebaseAuth.getInstance();
    }

    /**
     * link elements in the XML layout to the Java class
     */
    private void linkElements() {
        editTextEmail = (EditText) findViewById(R.id.email);
        editTextPassword = (EditText) findViewById(R.id.pass);
        //Set button listener
        login = (Button) findViewById(R.id.login);
        signup = (TextView) findViewById(R.id.signup);
        login.setOnClickListener(this);
        signup.setOnClickListener(this);
    }


    private void progress(){
        ProgressDialog dialog = new ProgressDialog(Login.this);
        dialog.setTitle("Please wait");
        dialog.setMessage("Logging in...");
        dialog.show();
    }

    /**
     * Method to sign in an existing user.
     * There are some conditions in the form where
     * the user types its email or password. Such
     * as when the user types the email the char
     * @ must be typed
     */
    private void loginUser(){
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (email.isEmpty()) {
            editTextEmail.setError("Email is required");
            editTextEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Please enter a valid email");
            editTextEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            editTextPassword.setError("Password is required");
            editTextPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            editTextPassword.setError("Minimum lenght of password should be 6");
            editTextPassword.requestFocus();
            return;
        }


        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Intent intent = new Intent(Login.this, Profile.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    overridePendingTransition(R.anim.quick_fade_in, R.anim.quick_fade_out);
                }else {
                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     * Method to execute events when tapping
     * certain buttons. In this case there are
     * 2 buttons. Sign in + Sign up
     * @param view
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.signup:
                Intent intent = new Intent(Login.this, Register.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.quick_fade_in, R.anim.quick_fade_out);
                break;

            case R.id.login:
                new loginUser().execute("");
                break;
        }
    }

    /**
     * Inner class that logins the user.
     * This inner class provides 3 key elements
     * - Run a task in background
     * - Run a task beforehand
     * - Run a task afterwards
     */
    private class loginUser extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                Thread.sleep(700);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            loginUser();
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            dialog.dismiss();

        }
        @Override
        protected void onPreExecute() {
            dialog.show();
        }

    }
}