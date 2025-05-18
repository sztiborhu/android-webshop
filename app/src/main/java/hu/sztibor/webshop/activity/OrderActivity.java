package hu.sztibor.webshop.activity;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
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

import java.util.ArrayList;

import hu.sztibor.webshop.R;
import hu.sztibor.webshop.Utils;
import hu.sztibor.webshop.model.Cart;
import hu.sztibor.webshop.model.CartItem;
import hu.sztibor.webshop.model.Order;
import hu.sztibor.webshop.service.OrderStatusUpdateJobService;

public class OrderActivity extends AppCompatActivity {
    private static final String LOG_TAG = OrderActivity.class.getName();
    private static final String PREF_KEY = OrderActivity.class.getPackage().toString();
    private static final int SECRET_KEY = 8789;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private FirebaseFirestore firebaseFirestore;

    private CollectionReference mUsers;
    private CollectionReference mCarts;
    private CollectionReference mOrders;

    EditText mAddressEditText;
    TextView mTotalPriceTextView;

    RadioButton mTransfer;
    RadioButton mPostpaid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        int secret_key = getIntent().getIntExtra("SECRET_KEY", 0);

        if (secret_key != SECRET_KEY) {
            finish();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        firebaseFirestore = FirebaseFirestore.getInstance();

        mUsers = firebaseFirestore.collection("Users");
        mCarts = firebaseFirestore.collection("Carts");
        mOrders = firebaseFirestore.collection("Orders");

        mAddressEditText = findViewById(R.id.addressEditText);
        mTransfer = findViewById(R.id.radio_transfer);


        mTotalPriceTextView = findViewById(R.id.totalPriceText);

        mCarts.document(firebaseAuth.getCurrentUser().getUid()).get().addOnSuccessListener(documentSnapshot -> {
            Cart cart = documentSnapshot.toObject(Cart.class);

            mTotalPriceTextView.setText("Fizetendő összeg: " + cart.getTotalPrice() + " Ft");
        });
    }

    public void onOrder(View view) {
        String orderId = Utils.getRandomId();
        String userId = user.getUid();
        String deliveryAddress = mAddressEditText.getText().toString();
        String paymentMethod = mTransfer.isChecked() ? "banki átutalás" : "utánvét";

        if (deliveryAddress.isEmpty()) {
            Utils.errorDialog(this, "Adja meg a szállítási címet!").show();
            return;
        }

        mCarts.document(userId).get().addOnSuccessListener(documentSnapshot -> {
            if (!documentSnapshot.exists()) {
                Toast.makeText(OrderActivity.this, "Nincs kosár!", Toast.LENGTH_LONG).show();
                return;
            }
            Cart cart = documentSnapshot.toObject(Cart.class);
            if (cart != null) {
                ArrayList<CartItem> orderItems = cart.getItems();
                int orderTotal = cart.getTotalPrice();

                mOrders.document(orderId).set(new Order(orderId, userId, deliveryAddress, paymentMethod, orderItems, orderTotal))
                        .addOnSuccessListener(aVoid -> {
                            Log.d(LOG_TAG, "Order created successfully");

                            scheduleStatusUpdateJob(orderId);

                            mCarts.document(userId).set(new Cart(userId, new ArrayList<CartItem>(), 0));
                            Toast.makeText(OrderActivity.this, "Sikeres megrendelés!", Toast.LENGTH_LONG).show();
                            finish();
                        })
                        .addOnFailureListener(e -> Log.e(LOG_TAG, "Error creating order", e));
            }
        });
    }

    private void scheduleStatusUpdateJob(String orderId) {
        JobScheduler jobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        ComponentName componentName = new ComponentName(this, OrderStatusUpdateJobService.class);

        PersistableBundle extras = new PersistableBundle();
        extras.putString("orderId", orderId);

        JobInfo jobInfo = new JobInfo.Builder(orderId.hashCode(), componentName)
            .setMinimumLatency(10000)
            .setOverrideDeadline(15000)
            .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
            .setExtras(extras)
            .build();

        jobScheduler.schedule(jobInfo);
    }

    public void onCancel(View view) {
        finish();
    }
}