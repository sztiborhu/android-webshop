package hu.sztibor.webshop.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import hu.sztibor.webshop.R;
import hu.sztibor.webshop.Utils;
import hu.sztibor.webshop.model.Cart;
import hu.sztibor.webshop.model.CartItem;
import hu.sztibor.webshop.model.Product;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {
    private ArrayList<CartItem> mCartItems;
    private Context context;
    private int lastPosition = -1;
    private ArrayList<Product> mProducts;
    private OnCartUpdateListener mListener;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    FirebaseUser firebaseUser;
    private CollectionReference mCarts;

    public interface OnCartUpdateListener {
        void onCartUpdated(Cart cart);
    }

    public CartAdapter(Context context, ArrayList<CartItem> cartItems, ArrayList<Product> products) {
        this.context = context;
        this.mCartItems = cartItems;
        this.mProducts = products;

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        mCarts = firebaseFirestore.collection("Carts");
    }

    public void setOnCartUpdateListener(OnCartUpdateListener listener) {
        this.mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.cart_item, parent, false));
    }

    @Override
    public void onBindViewHolder(CartAdapter.ViewHolder holder, int position) {
        CartItem currentItem = mCartItems.get(position);
        Product product = findProductById(currentItem.getProductId());

        if (product != null) {
            holder.bindTo(currentItem, product);
        }

        if (holder.getAdapterPosition() > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_in_from_top);
            holder.itemView.startAnimation(animation);
            lastPosition = holder.getAdapterPosition();
        }

        holder.itemView.findViewById(R.id.add_to_quantity).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateCartItemQuantity(currentItem, 1);
            }
        });

        holder.itemView.findViewById(R.id.remove_from_quantity).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentItem.getQuantity() > 1) {
                    updateCartItemQuantity(currentItem, -1);
                } else {
                    removeCartItem(currentItem);
                }
            }
        });

        holder.itemView.findViewById(R.id.remove_from_cart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeCartItem(currentItem);
            }
        });
    }

    private void updateCartItemQuantity(CartItem item, int change) {
        String userId = firebaseUser.getUid();
        mCarts.document(userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Cart cart = documentSnapshot.toObject(Cart.class);
                if (cart != null) {
                    ArrayList<CartItem> items = cart.getItems();
                    for (CartItem cartItem : items) {
                        if (cartItem.getProductId().equals(item.getProductId())) {
                            cartItem.setQuantity(cartItem.getQuantity() + change);
                            cart.setTotalPrice(cart.getTotalPrice() + (item.getPrice() * change));
                            break;
                        }
                    }
                    mCarts.document(userId).set(cart).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            if (mListener != null) {
                                mListener.onCartUpdated(cart);
                            }
                        }
                    });
                }
            }
        });
    }

    private void removeCartItem(CartItem item) {
        String userId = firebaseUser.getUid();
        mCarts.document(userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Cart cart = documentSnapshot.toObject(Cart.class);
                if (cart != null) {
                    ArrayList<CartItem> items = cart.getItems();
                    items.removeIf(cartItem -> cartItem.getProductId().equals(item.getProductId()));
                    cart.setTotalPrice(cart.getTotalPrice() - (item.getPrice() * item.getQuantity()));
                    mCarts.document(userId).set(cart).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            if (mListener != null) {
                                mListener.onCartUpdated(cart);
                            }
                        }
                    });
                }
            }
        });
    }

    private Product findProductById(String productId) {
        for (Product product : mProducts) {
            if (product.getId().equals(productId)) {
                return product;
            }
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return mCartItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mName;
        private TextView mPrice;
        private TextView mQuantity;
        private ImageView mImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mName = itemView.findViewById(R.id.itemName);
            mPrice = itemView.findViewById(R.id.price);
            mQuantity = itemView.findViewById(R.id.quantity);
            mImage = itemView.findViewById(R.id.itemImage);
        }

        public void bindTo(CartItem cartItem, Product product) {
            mName.setText(product.getName());
            mPrice.setText(cartItem.getPrice() + " Ft");
            mQuantity.setText(String.valueOf(cartItem.getQuantity()));
            mImage.setImageBitmap(Utils.returnBitmap(product));
        }
    }
}
