package hu.sztibor.webshop.service;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.firestore.FirebaseFirestore;

import hu.sztibor.webshop.R;
import hu.sztibor.webshop.model.Order;

public class OrderStatusUpdateJobService extends JobService {
    private static final String TAG = "OrderStatusUpdateJob";
    private static final String CHANNEL_ID = "order_status_channel";
    private static final int NOTIFICATION_ID = 1;
    private FirebaseFirestore db;
    private boolean jobCancelled = false;

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(TAG, "Job started");
        db = FirebaseFirestore.getInstance();
        createNotificationChannel();
        
        String orderId = params.getExtras().getString("orderId");
        if (orderId != null) {
            updateOrderStatus(orderId, params);
        }
        
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(TAG, "Job cancelled before completion");
        jobCancelled = true;
        return true;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Rendelés státusz értesítések",
                NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("Értesítések a rendelések státuszáról");
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void updateOrderStatus(String orderId, JobParameters params) {
        if (jobCancelled) {
            return;
        }

        db.collection("Orders")
            .document(orderId)
            .update("status", "Szállítás alatt")
            .addOnSuccessListener(aVoid -> {
                Log.d(TAG, "Order status updated successfully: " + orderId);
                showNotification(orderId);
                jobFinished(params, false);
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error updating order status: " + orderId, e);
                jobFinished(params, true); // Retry the job
            });
    }

    private void showNotification(String orderId) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Rendelés státusz frissítés")
            .setContentText("A " + orderId + " számú rendelésed szállítás alatt van!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        } else {
            notificationManager.notify(orderId.hashCode(), builder.build());
        }
    }
} 