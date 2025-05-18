package hu.sztibor.webshop.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import hu.sztibor.webshop.fragment.CartFragment;
import hu.sztibor.webshop.fragment.HomeFragment;
import hu.sztibor.webshop.R;
import hu.sztibor.webshop.fragment.UserFragment;

public class ShopActivity extends AppCompatActivity {
    private static final String LOG_TAG = ShopActivity.class.getName();
    private static final String PREF_KEY = ShopActivity.class.getPackage().toString();
    private static final int SECRET_KEY = 8789;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;

    BottomNavigationView bottomNavigationView;

    Fragment[] fragments = {
            new HomeFragment(),
            new CartFragment(),
            new UserFragment()
    };

    private ActivityResultLauncher<String> requestPermissionLauncher;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        int secret_key = getIntent().getIntExtra("SECRET_KEY", 0);

        if (secret_key != SECRET_KEY) {
            finish();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);
        firebaseAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            String userId = user.getUid();
            String userEmail = user.getEmail();
        } else {
            Log.d("ShopActivity", "No user is signed in");
            finish();
        }

        bottomNavigationView = findViewById(R.id.bottomNavigation);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Log.d("ShopActivity", "Item selected: " + item.getItemId());
            int itemId = item.getItemId();
            if (itemId == R.id.page_1) {
                Log.d("ShopActivity", "Page 1 selected");
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, fragments[0])
                        .commit();
                return true;
            }
            if (itemId == R.id.page_2) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, fragments[1])
                        .commit();
                return true;

            }

            if (itemId == R.id.page_3) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, fragments[2])
                            .commit();
                    return true;
            }


            return false;
        });

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new HomeFragment())
                .commit();

        requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    Log.d(LOG_TAG, "Notification permission granted");
                } else {
                    Log.d(LOG_TAG, "Notification permission denied");
                }
            }
        );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) 
                != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.d("xd", "onDestroy: ");

        firebaseAuth.signOut();

    }




    public void onLogout(View view) {
        firebaseAuth.signOut();

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("SECRET_KEY", 8789);
        startActivity(intent);

        Toast.makeText(ShopActivity.this, "Kijelentkezés sikeres!", Toast.LENGTH_LONG).show();
        finish();
    }

    public void goToProfileEdit(View view) {
        Intent intent = new Intent(this, ProfileEditActivity.class);

        intent.putExtra("SECRET_KEY", 8789);
        startActivity(intent);
    }

    public void goToOrders(View view) {
        Intent intent = new Intent(this, OrdersActivity.class);

        intent.putExtra("SECRET_KEY", 8789);
        startActivity(intent);
    }

    public void addToCart(View view) {
        Log.d("ShopActivity", "addToCart: ");
    }

    public void goToOrder(View view) {
        Intent intent = new Intent(this, OrderActivity.class);
        intent.putExtra("SECRET_KEY", SECRET_KEY);
        startActivity(intent);
    }
}
