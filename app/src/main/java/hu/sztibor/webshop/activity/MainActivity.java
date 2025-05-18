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
import com.google.firebase.auth.FirebaseUser;

import hu.sztibor.webshop.R;
import hu.sztibor.webshop.Utils;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getName();
    private static final String PREF_KEY = MainActivity.class.getPackage().toString();
    private static final int SECRET_KEY = 8789;

    EditText emailEditText;
    EditText passwordEditText;

    private SharedPreferences preferences;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);

        preferences = getSharedPreferences(PREF_KEY, MODE_PRIVATE);
        mAuth = FirebaseAuth.getInstance();

        mAuth.signOut();
    }

    public void onLogin(View view) {
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            Utils.errorDialog(this, "Kérjük, adja meg az e-mail címét és a jelszavát!").show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                Log.d(LOG_TAG, "User logged in successfully");
                Toast.makeText(MainActivity.this, "Sikeres bejelentkezés!", Toast.LENGTH_LONG).show();
                openShopActivity();
                finish();
            } else {
                Log.d("LOG_TAG", "Login failed: " + task.getException().getMessage());
                Utils.errorDialog(MainActivity.this, "Nem sikerült a bejelentkezés.\n" + task.getException().getMessage()).show();
            }
        });
    }

    public void onGuestLogin(View view) {
        mAuth.signInAnonymously().addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                Log.d(LOG_TAG, "Guest user logged in successfully");
                Toast.makeText(MainActivity.this, "Vendég bejelentkezés sikeres!", Toast.LENGTH_LONG).show();
                openShopActivity();
                finish();
            } else {
                Log.d("LOG_TAG", "Guest login failed: " + task.getException().getMessage());
                Utils.errorDialog(MainActivity.this, "Nem sikerült a vendég bejelentkezés.\n" + task.getException().getMessage()).show();
            }
        });
    }

    public void onRegister(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        intent.putExtra("SECRET_KEY", SECRET_KEY);
        startActivity(intent);
        Log.i(LOG_TAG, "Register button clicked");
    }

    @Override
    protected void onPause() {
        super.onPause();

        Log.d("asd", "onPause called");

        SharedPreferences.Editor editor = preferences.edit();

        editor.putString("email", emailEditText.getText().toString());
        editor.putString("password", passwordEditText.getText().toString());

        editor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();

        String email = preferences.getString("email", "");
        String password = preferences.getString("password", "");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        Log.d(LOG_TAG, "User: " + user);

        if (user != null) {
            openShopActivity();
            finish();
        }
    }

    private void openShopActivity() {
        Intent intent = new Intent(this, ShopActivity.class);
        intent.putExtra("SECRET_KEY", SECRET_KEY);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

}