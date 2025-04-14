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

import java.util.ArrayList;

import hu.sztibor.webshop.R;
import hu.sztibor.webshop.adapter.ProductAdapter;
import hu.sztibor.webshop.model.Product;

public class HomeFragment extends Fragment {

    private View layout;
    private int gridNumber = 1;

    private RecyclerView mRecyclerView;
    private ArrayList<Product> mItemList;
    private ProductAdapter mAdapter;

    public HomeFragment() {
        // Required empty public constructor
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
        // Inflate the layout for this fragment
        layout = inflater.inflate(R.layout.fragment_home, container, false);

        mRecyclerView = layout.findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this.getContext(), gridNumber));
        mItemList = new ArrayList<>();

        mAdapter = new ProductAdapter(mRecyclerView.getContext(), mItemList);
        mRecyclerView.setAdapter(mAdapter);

        initializeData();

        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            gridNumber = 2;
        } else {
            gridNumber = 1;
        }

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

    private void initializeData() {
        String[] itemsList = getResources().getStringArray(R.array.shopping_item_names);
        String[] itemsInfo = getResources().getStringArray(R.array.shopping_item_desc);
        int[] itemsPrice = getResources().getIntArray(R.array.shopping_item_price);
        TypedArray itemsImageResource = getResources().obtainTypedArray(R.array.shopping_item_images);

        mItemList.clear();
        for (int i = 0; i < itemsList.length; i++) {
            Product product = new Product(itemsList[i], itemsInfo[i], itemsPrice[i], itemsImageResource.getResourceId(i, 0));
            mItemList.add(product);
        }
        itemsImageResource.recycle();

        mAdapter.notifyDataSetChanged();

    }
}