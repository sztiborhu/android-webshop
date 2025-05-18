package hu.sztibor.webshop.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import hu.sztibor.webshop.R;
import hu.sztibor.webshop.model.Order;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {
    private ArrayList<Order> mOrders;
    private Context context;
    private SimpleDateFormat dateFormat;
    private FirebaseFirestore firebaseFirestore;

    public OrderAdapter(Context context, ArrayList<Order> orders) {
        this.context = context;
        this.mOrders = orders;
        this.dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm", new Locale("hu", "HU"));
        this.firebaseFirestore = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.order_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Order currentOrder = mOrders.get(position);
        holder.bindTo(currentOrder);
    }

    @Override
    public int getItemCount() {
        return mOrders.size();
    }

    public void updateOrders(ArrayList<Order> orders) {
        this.mOrders = orders;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mOrderId;
        private TextView mOrderDate;
        private TextView mOrderTotal;
        private TextView mOrderStatus;
        private TextView mOrderAddress;
        private TextView mOrderPaymentMethod;
        private ImageButton mDeleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mOrderId = itemView.findViewById(R.id.orderId);
            mOrderDate = itemView.findViewById(R.id.orderDate);
            mOrderTotal = itemView.findViewById(R.id.orderTotal);
            mOrderStatus = itemView.findViewById(R.id.orderStatus);
            mOrderAddress = itemView.findViewById(R.id.orderAddress);
            mOrderPaymentMethod = itemView.findViewById(R.id.orderPaymentMethod);
            mDeleteButton = itemView.findViewById(R.id.deleteButton);
        }

        public void bindTo(Order order) {
            mOrderId.setText("Rendelés azonosító: " + order.getId());
            mOrderDate.setText("Rendelés dátuma: " + dateFormat.format(order.getOrderDate().toDate()));
            mOrderTotal.setText("Összeg: " + order.getTotalPrice() + " Ft");
            mOrderStatus.setText("Státusz: " + order.getStatus());
            mOrderAddress.setText("Szállítási cím: " + order.getDeliveryAddress());
            mOrderPaymentMethod.setText("Fizetési mód: " + order.getPaymentMethod());

            mDeleteButton.setOnClickListener(v -> {
                firebaseFirestore.collection("Orders")
                    .document(order.getId())
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(context, "Rendelés sikeresen törölve!", Toast.LENGTH_SHORT).show();
                        mOrders.remove(order);
                        notifyDataSetChanged();
                    })
                    .addOnFailureListener(e -> 
                        Toast.makeText(context, "Hiba történt a törlés során!", Toast.LENGTH_SHORT).show()
                    );
            });
        }
    }
} 