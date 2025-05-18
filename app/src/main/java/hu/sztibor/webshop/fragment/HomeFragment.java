package hu.sztibor.webshop.fragment;

import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import hu.sztibor.webshop.R;
import hu.sztibor.webshop.Utils;
import hu.sztibor.webshop.adapter.ProductAdapter;
import hu.sztibor.webshop.model.Product;

public class HomeFragment extends Fragment {

    private View layout;
    private int gridNumber = 1;

    private RecyclerView mRecyclerView;
    private ArrayList<Product> mItemList;
    private ProductAdapter mAdapter;

    private FirebaseFirestore mFirestore;
    private CollectionReference mItems;

    public HomeFragment() {

    }
    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.fragment_home, container, false);

        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            gridNumber = 2;
        } else {
            gridNumber = 1;
        }

        mRecyclerView = layout.findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this.getContext(), gridNumber));
        mItemList = new ArrayList<>();

        mAdapter = new ProductAdapter(mRecyclerView.getContext(), mItemList);
        mRecyclerView.setAdapter(mAdapter);


        mFirestore = FirebaseFirestore.getInstance();
        mItems = mFirestore.collection("Products");


        queryData();
        //initializeData();



        return layout;
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            gridNumber = 2;
            mRecyclerView.setLayoutManager(new GridLayoutManager(this.getContext(), gridNumber));
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            gridNumber = 1;
            mRecyclerView.setLayoutManager(new GridLayoutManager(this.getContext(), gridNumber));
        }
    }

    private void queryData() {
        mItemList.clear();

        mItems.orderBy("name").limit(10).get().addOnSuccessListener(queryDocumentSnapshots -> {
           for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
               Product product = snapshot.toObject(Product.class);
               mItemList.add(product);
           }

           if (mItemList.isEmpty()) {
               initializeData();
               queryData();
           }


           Log.d("asd", "queryData: " + mItemList.size());
            mAdapter.notifyDataSetChanged();
        });
    }

    private void initializeData() {
        String[] itemsList = getResources().getStringArray(R.array.shopping_item_names);
        String[] itemsInfo = getResources().getStringArray(R.array.shopping_item_desc);
        int[] itemsPrice = getResources().getIntArray(R.array.shopping_item_price);
        TypedArray itemsImageResource = getResources().obtainTypedArray(R.array.shopping_item_images);

        //mItemList.clear();
        for (int i = 0; i < itemsList.length; i++) {
            Product product = new Product(Utils.getRandomId(), itemsList[i], itemsInfo[i], itemsPrice[i], "");
            mItems.add(product);
        }
        itemsImageResource.recycle();

        //mAdapter.notifyDataSetChanged();

    }
}