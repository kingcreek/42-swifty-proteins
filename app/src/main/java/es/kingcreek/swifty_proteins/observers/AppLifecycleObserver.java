package es.kingcreek.swifty_proteins.observers;

import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import android.app.Activity;
import android.util.Log;

import es.kingcreek.swifty_proteins.MyApplication;

public class AppLifecycleObserver implements DefaultLifecycleObserver {

    // Use static for have single instance in all activity
    private static boolean isSharing = false;

    public void setSharing(boolean sharing) {
        isSharing = sharing;
    }

    @Override
    public void onStart(LifecycleOwner owner) {
        // Do nothing
    }

    @Override
    public void onStop(LifecycleOwner owner) {
        // Prevent close app if user share image
        if (!isSharing) {
            MyApplication.getInstance().finishAllActivities();
        }
    }
}