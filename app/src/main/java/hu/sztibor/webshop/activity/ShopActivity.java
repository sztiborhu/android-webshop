package hu.sztibor.webshop.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import hu.sztibor.webshop.fragment.CartFragment;
import hu.sztibor.webshop.fragment.HomeFragment;
import hu.sztibor.webshop.R;
import hu.sztibor.webshop.fragment.UserFragment;

public class ShopActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    BottomNavigationView bottomNavigationView;

    Fragment[] fragments = {
            new HomeFragment(),
            new CartFragment(),
            new UserFragment()
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        firebaseAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            // User is signed in
            String userId = user.getUid();
            String userEmail = user.getEmail();
            // Use the user information as needed
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
    }

    @Override
    protected void onStop() {
        super.onStop();

        Log.d("xd", "onDestroy: ");

        firebaseAuth.signOut();

    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        //Log.d("asd", "onConfigurationChanged: " + newConfig.orientation);
        super.onConfigurationChanged(newConfig);
        /*
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            bottomNavigationView.setVisibility(BottomNavigationView.GONE);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            bottomNavigationView.setVisibility(BottomNavigationView.VISIBLE);
        }*/


    }

    public void onLogout(View view) {
        firebaseAuth.signOut();

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("SECRET_KEY", 8789);
        startActivity(intent);

        Toast.makeText(ShopActivity.this, "Kijelentkezés sikeres!", Toast.LENGTH_LONG).show();
        finish();
    }
}
