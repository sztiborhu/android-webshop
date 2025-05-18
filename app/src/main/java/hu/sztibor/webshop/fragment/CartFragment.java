package hu.sztibor.webshop.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import hu.sztibor.webshop.R;
import hu.sztibor.webshop.adapter.CartAdapter;
import hu.sztibor.webshop.model.Cart;
import hu.sztibor.webshop.model.CartItem;
import hu.sztibor.webshop.model.Product;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CartFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CartFragment extends Fragment implements CartAdapter.OnCartUpdateListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private static final String TAG = "CartFragment";
    
    private RecyclerView mRecyclerView;
    private CartAdapter mAdapter;
    private ArrayList<CartItem> mCartItems;
    private ArrayList<Product> mProducts;
    private TextView mTotalPrice;
    
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser firebaseUser;
    private CollectionReference mCarts;
    private CollectionReference mProductsCollection;

    public CartFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CartFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CartFragment newInstance(String param1, String param2) {
        CartFragment fragment = new CartFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        mCarts = firebaseFirestore.collection("Carts");
        mProductsCollection = firebaseFirestore.collection("Products");
        
        mCartItems = new ArrayList<>();
        mProducts = new ArrayList<>();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);
        
        mRecyclerView = view.findViewById(R.id.recyclerView);
        mTotalPrice = view.findViewById(R.id.totalPrice);
        
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new CartAdapter(getContext(), mCartItems, mProducts);
        mAdapter.setOnCartUpdateListener(this);
        mRecyclerView.setAdapter(mAdapter);
        
        loadCart();

        FloatingActionButton fab = view.findViewById(R.id.orderButton);
        if (mCartItems.isEmpty()) {
            fab.setVisibility(View.GONE);
        } else {
            fab.setVisibility(View.VISIBLE);
        }

        return view;
    }
    
    private void loadCart() {
        if (firebaseUser != null) {
            String userId = firebaseUser.getUid();
            
            mProductsCollection.get().addOnSuccessListener(queryDocumentSnapshots -> {
                mProducts.clear();
                for (DocumentSnapshot document : queryDocumentSnapshots) {
                    Product product = document.toObject(Product.class);
                    mProducts.add(product);
                }
                
                mCarts.document(userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Cart cart = documentSnapshot.toObject(Cart.class);
                        if (cart != null) {
                            mCartItems.clear();
                            mCartItems.addAll(cart.getItems());
                            mAdapter.notifyDataSetChanged();
                            updateTotalPrice(cart.getTotalPrice());


                            if (!mCartItems.isEmpty()) {
                                FloatingActionButton fab = getView().findViewById(R.id.orderButton);
                                fab.setVisibility(View.VISIBLE);
                            } else {
                            }
                        }
                    }
                });
            });
        }
    }
    
    private void updateTotalPrice(int totalPrice) {
        mTotalPrice.setText("Összesen: " + totalPrice + " Ft");
    }

    @Override
    public void onCartUpdated(Cart cart) {
        mCartItems.clear();
        mCartItems.addAll(cart.getItems());
        mAdapter.notifyDataSetChanged();
        updateTotalPrice(cart.getTotalPrice());

        FloatingActionButton fab = getView().findViewById(R.id.orderButton);
        if (mCartItems.isEmpty()) {
            fab.setVisibility(View.GONE);
        } else {
            fab.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadCart();
    }

    @Override
    public void onConfigurationChanged(android.content.res.Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        loadCart();
    }
}