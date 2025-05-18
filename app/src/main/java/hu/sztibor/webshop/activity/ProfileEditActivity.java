package hu.sztibor.webshop.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import hu.sztibor.webshop.R;
import hu.sztibor.webshop.Utils;
import hu.sztibor.webshop.model.Product;
import hu.sztibor.webshop.model.User;

public class ProfileEditActivity extends AppCompatActivity {
    private static final String LOG_TAG = ProfileEditActivity.class.getName();
    private static final String PREF_KEY = ProfileEditActivity.class.getPackage().toString();
    private static final int SECRET_KEY = 8789;


    EditText nameEditText;
    EditText emailEditText;
    EditText phoneEditText;
    EditText passwordEditText;
    EditText passwordAgainEditText;
    SharedPreferences preferences;
    private FirebaseAuth firebaseAuth;

    private FirebaseFirestore mFirestore;
    private CollectionReference mUsers;

    User user;

    String originalEmail;
    String originalPhone;
    String originalName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        int secret_key = getIntent().getIntExtra("SECRET_KEY", 0);

        if (secret_key != SECRET_KEY) {
            finish();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);

        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        passwordAgainEditText = findViewById(R.id.passwordAgainEditText);

        preferences = getSharedPreferences(PREF_KEY, MODE_PRIVATE);

        firebaseAuth = FirebaseAuth.getInstance();

        mFirestore = FirebaseFirestore.getInstance();
        mUsers = mFirestore.collection("Users");

        String userId = firebaseAuth.getUid();
        Log.d(LOG_TAG, "User ID: " + firebaseAuth.getCurrentUser());

        if (userId != null) {
            mUsers.whereEqualTo("id", userId).get().addOnSuccessListener(queryDocumentSnapshots -> {
                for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                    user = snapshot.toObject(User.class);
                }

                originalEmail = user.getEmail();
                originalName = user.getName();
                originalPhone = user.getPhone();


                emailEditText.setText(originalEmail);
                nameEditText.setText(originalName);
                phoneEditText.setText(originalPhone);
            });
        }





    }

    public void onCancel(View view) {
        finish();
    }

    public void onEdit(View view) {
        String name = nameEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String phone = phoneEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String passwordAgain = passwordAgainEditText.getText().toString();

        String errorMessage = "";

        if (name.isEmpty()) {
            errorMessage += "Név megadása kötelező!\n";
        }

        if (email.isEmpty()) {
            errorMessage += "Email megadása kötelező!\n";
        }

        if (phone.isEmpty()) {
            errorMessage += "Telefonszám megadása kötelező!\n";
        }

        if (!password.isEmpty() && !password.equals(passwordAgain)) {
            errorMessage += "A két jelszó nem egyezik!";
        }

        if (!errorMessage.isEmpty()) {
            Utils.errorDialog(ProfileEditActivity.this, errorMessage).show();
            return;
        }

        FirebaseUser mUser = firebaseAuth.getCurrentUser();

        Log.d(LOG_TAG, String.valueOf(firebaseAuth));
        Log.d(LOG_TAG, String.valueOf(mUser));

        if (mUser != null) {
            /*
            if (!email.equals(originalEmail)) {
                mUser.verifyBeforeUpdateEmail(email).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        mUsers.document(user.getId()).update("email", email);
                        Log.d(LOG_TAG, "Email updated");
                    } else {
                        Log.d(LOG_TAG, "Email update failed");
                        Log.d(LOG_TAG, task.getException().toString());
                    }
                });

            }*/

            if (!phone.equals(originalPhone)) {
                mUsers.document(user.getId()).update("phone", phone);
            }

            if (!name.equals(originalName)) {
                mUsers.document(user.getId()).update("name", name);
            }

            mUser.updatePassword(password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d(LOG_TAG, "Password updated");
                } else {
                    Log.d(LOG_TAG, "Password update failed");
                    Log.d(LOG_TAG, task.getException().toString());
                }
            });


            Toast.makeText(ProfileEditActivity.this, "Szerkesztés sikeres!", Toast.LENGTH_LONG).show();

            finish();
        } else {
            Utils.errorDialog(this, "Hiba történt a felhasználó frissítésekor!").show();
        }






    }
}