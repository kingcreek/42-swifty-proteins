package es.kingcreek.swifty_proteins.activities;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;

import es.kingcreek.swifty_proteins.MyApplication;
import es.kingcreek.swifty_proteins.R;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText emailEditText, passwordEditText;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        MyApplication.getInstance().addActivity(this);

        mAuth = FirebaseAuth.getInstance();
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);

        Button loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(view -> loginUser());

        Button registerButton = findViewById(R.id.registerButton);
        registerButton.setOnClickListener(view -> startRegisterActivity());

        // Setup biometric authentication
        biometricPrompt = new BiometricPrompt(this,
                ContextCompat.getMainExecutor(this), new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(LoginActivity.this, "Authentication error: " + errString, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                // Handle successful authentication
                if (validateToken()) {
                    startMainActivity();
                } else {
                    Toast.makeText(LoginActivity.this, "Token validation failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(LoginActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric login")
                .setSubtitle("Log in using your biometric credential")
                .setNegativeButtonText("Cancel")
                .build();

        Button biometricLoginButton = findViewById(R.id.biometricLoginButton);
        biometricLoginButton.setOnClickListener(view -> biometricPrompt.authenticate(promptInfo));

        // Hidde biometric button if not supported on the device or if user dont have loged account
        if (BiometricManager.from(this).canAuthenticate() != BiometricManager.BIOMETRIC_SUCCESS || !validateToken()) {
            biometricLoginButton.setVisibility(View.GONE);
        }

    }

    private void loginUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        // Request login to firebase
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    // Login ok
                    if (task.isSuccessful()) {
                        startMainActivity();
                    } else {
                        // Ops, login error, show provided message
                        Toast.makeText(LoginActivity.this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private boolean validateToken() {
        // Since we are using Firebase Authentication, we do not need to manually validate a token here
        return mAuth.getCurrentUser() != null;
    }

    private void startMainActivity() {
        // Show a simple message to demostrate biometric are linked to account
        Toast.makeText(getApplicationContext(), "Welcome " + mAuth.getCurrentUser().getEmail(), Toast.LENGTH_LONG).show();
        // Start list activity
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void startRegisterActivity() {
        // Show register activity
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        MyApplication.getInstance().removeActivity(this);
        super.onDestroy();
    }
}
