package hu.sztibor.webshop.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import hu.sztibor.webshop.R;
import hu.sztibor.webshop.Utils;

public class RegisterActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getName();
    private static final String PREF_KEY = MainActivity.class.getPackage().toString();
    private static final int SECRET_KEY = 8789;


    EditText nameEditText;
    EditText emailEditText;
    EditText passwordEditText;
    EditText passwordAgainEditText;
    SharedPreferences preferences;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        int secret_key = getIntent().getIntExtra("SECRET_KEY", 0);

        if (secret_key != 8789) {
            finish();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        passwordAgainEditText = findViewById(R.id.passwordAgainEditText);

        preferences = getSharedPreferences(PREF_KEY, MODE_PRIVATE);

        String email = preferences.getString("email", "");
        String password = preferences.getString("password", "");

        emailEditText.setText(email);
        passwordEditText.setText(password);

        mAuth = FirebaseAuth.getInstance();

    }

    public void onRegister(View view) {
        String name = nameEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String passwordAgain = passwordAgainEditText.getText().toString();

        String errorMessage = "";

        if (name.isEmpty()) {
            errorMessage += "Név megadása kötelező!\n";
        }

        if (email.isEmpty()) {
            errorMessage += "Email megadása kötelező!\n";
        }

        if (password.isEmpty()) {
            errorMessage += "Jelszó megadása kötelező!\n";
        }

        if (passwordAgain.isEmpty()) {
            errorMessage += "Jelszó megerősítése kötelező!\n";
        }

        if (!password.isEmpty() && !password.equals(passwordAgain)) {
            errorMessage += "A két jelszó nem egyezik!";
        }

        if (!errorMessage.isEmpty()) {
            Utils.errorDialog(RegisterActivity.this, errorMessage).show();
            return;
        }



        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d("LOG_TAG", "User registered successfully");
                        Toast.makeText(RegisterActivity.this, "Regisztráció sikeres!", Toast.LENGTH_LONG).show();
                        openShopActivity();
                        finish();
                    } else {
                        Log.d("LOG_TAG", "Registration failed: " + task.getException().getMessage());
                        Utils.errorDialog(RegisterActivity.this, "Sikertelen regisztráció!\n" + task.getException().getMessage()).show();
                    }
                });
    }

    public void onCancel(View view) {
        finish();
    }

    private void openShopActivity() {
        Intent intent = new Intent(this, ShopActivity.class);
        // intent.putExtra("SECRET_KEY", SECRET_KEY);
        startActivity(intent);
    }


}
