package hu.sztibor.webshop.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

import hu.sztibor.webshop.R;
import hu.sztibor.webshop.activity.MainActivity;
import hu.sztibor.webshop.activity.RegisterActivity;
import hu.sztibor.webshop.activity.ShopActivity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserFragment extends Fragment {

    View layout;

    public UserFragment() {
        // Required empty public constructor
    }

    public static UserFragment newInstance(String param1, String param2) {
        UserFragment fragment = new UserFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    FirebaseAuth firebaseAuth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
    }

    TextView userEmailTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        layout = inflater.inflate(R.layout.fragment_user, container, false);

        FirebaseUser user = firebaseAuth.getCurrentUser();
        userEmailTextView = layout.findViewById(R.id.loggedInEmail);

        if (user != null && user.getEmail() != null && !user.getEmail().equals("")) {
            String userEmail = user.getEmail();
            userEmailTextView.setText("A bejelentkezett e-mail: " + userEmail);
        } else {
            userEmailTextView.setText("A bejelentkezett felhasználó vendég.");
        }

        Log.d("UserFragment", "User: " + user.getEmail());
        // Inflate the layout for this fragment
        return layout;



    }


}