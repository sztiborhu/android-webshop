package hu.sztibor.webshop;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.nio.charset.Charset;
import java.util.Random;

import hu.sztibor.webshop.model.Product;

public class Utils {
    public static AlertDialog errorDialog(Context context, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Hiba");
        builder.setMessage(message);
        builder.setPositiveButton("OK", null);
        return builder.create();
    }

    public static String getRandomId() {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;
        Random random = new Random();

        String generatedString = random.ints(leftLimit, rightLimit + 1)
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        return generatedString;
    }

    public static Bitmap returnBitmap(Product currentProduct) {
        byte[] decodedString = Base64.decode(currentProduct.getImage().replace("data:image/png;base64,", ""), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }
}
