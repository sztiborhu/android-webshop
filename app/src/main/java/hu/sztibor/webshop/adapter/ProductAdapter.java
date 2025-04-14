package hu.sztibor.webshop.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import hu.sztibor.webshop.R;
import hu.sztibor.webshop.model.Product;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> implements Filterable {
    private ArrayList<Product> mProductData;
    private ArrayList<Product> mProductDataAll;
    private Context context;
    private int lastPosition = -1;

    public ProductAdapter(Context context, ArrayList<Product> productData) {
        this.context = context;
        this.mProductData = productData;
        this.mProductDataAll = new ArrayList<>(productData);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ProductAdapter.ViewHolder holder, int position) {
        Product currentProduct = mProductData.get(position);

        holder.bindTo(currentProduct);

        if (holder.getAdapterPosition() > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_in_row);
            holder.itemView.startAnimation(animation);
            lastPosition = holder.getAdapterPosition();
        }
    }

    @Override
    public int getItemCount() {
        return mProductData.size();
    }

    private Filter productFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<Product> filteredList = new ArrayList<>();
            FilterResults results = new FilterResults();

            if (constraint == null || constraint.length() == 0) {
                results.count = mProductDataAll.size();
                results.values = mProductDataAll;
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (Product product : mProductDataAll) {
                    if (product.getName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(product);
                    }
                }

                results.count = filteredList.size();
                results.values = filteredList;
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mProductData = (ArrayList<Product>) results.values;
            notifyDataSetChanged();
        }
    };

    @Override
    public Filter getFilter() {
        return productFilter;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mName;
        //private TextView mDescription;
        private TextView mPrice;
        private ImageView mImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mName = itemView.findViewById(R.id.itemName);
            //mDescription = itemView.findViewById(R.id.subTitle);
            mPrice = itemView.findViewById(R.id.price);
            mImage = itemView.findViewById(R.id.itemImage);

            itemView.findViewById(R.id.add_to_cart).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

        }

        public void bindTo(Product currentProduct) {
            mName.setText(currentProduct.getName());
            //mDescription.setText(currentProduct.getDescription());
            mPrice.setText(String.valueOf(currentProduct.getPrice()));

            Glide.with(context).load(currentProduct.getImage()).into(mImage);
        }
    }

}

