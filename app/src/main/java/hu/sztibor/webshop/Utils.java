package hu.sztibor.webshop;

import android.app.AlertDialog;
import android.content.Context;

public class Utils {
    public static AlertDialog errorDialog(Context context, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Hiba");
        builder.setMessage(message);
        builder.setPositiveButton("OK", null);
        return builder.create();
    }
}
