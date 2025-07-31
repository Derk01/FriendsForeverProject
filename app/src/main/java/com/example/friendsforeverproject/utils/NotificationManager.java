package com.example.friendsforeverproject.utils;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

public class NotificationManager {

    // Show a toast message
    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    // Show a snackbar message (attached to a view)
    public static void showSnackbar(View anchorView, String message) {
        Snackbar.make(anchorView, message, Snackbar.LENGTH_SHORT).show();
    }

    // Show a long snackbar (optional utility)
    public static void showLongSnackbar(View anchorView, String message) {
        Snackbar.make(anchorView, message, Snackbar.LENGTH_LONG).show();
    }
}
