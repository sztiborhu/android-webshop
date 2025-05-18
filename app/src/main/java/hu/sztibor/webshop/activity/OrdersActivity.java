package hu.sztibor.webshop.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

import hu.sztibor.webshop.R;
import hu.sztibor.webshop.adapter.OrderAdapter;
import hu.sztibor.webshop.model.Order;

public class OrdersActivity extends AppCompatActivity {
    private static final String LOG_TAG = OrdersActivity.class.getName();
    private static final String PREF_KEY = OrdersActivity.class.getPackage().toString();
    private static final int SECRET_KEY = 8789;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private FirebaseFirestore firebaseFirestore;
    private CollectionReference mOrders;

    private RecyclerView mRecyclerView;
    private ArrayList<Order> mOrderList;
    private OrderAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        int secret_key = getIntent().getIntExtra("SECRET_KEY", 0);
        if (secret_key != SECRET_KEY) {
            finish();
        }

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();
        mOrders = firebaseFirestore.collection("Orders");

        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mOrderList = new ArrayList<>();
        mAdapter = new OrderAdapter(this, mOrderList);
        mRecyclerView.setAdapter(mAdapter);

        loadOrders();
    }

    private void loadOrders() {
        if (user != null) {
            String userId = user.getUid();
            
            mOrders.whereEqualTo("userId", userId)
                    .orderBy("orderDate", Query.Direction.DESCENDING)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        mOrderList.clear();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            Order order = document.toObject(Order.class);
                            mOrderList.add(order);
                        }
                        mAdapter.updateOrders(mOrderList);
                    })
                    .addOnFailureListener(e -> Log.e(LOG_TAG, "Error loading orders", e));
        }
    }

    public void onBack(View view) {
        finish();
    }
}