package es.kingcreek.swifty_proteins.share;

import static es.kingcreek.swifty_proteins.activities.ProteinView.REQUEST_CODE_SHARE;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.view.PixelCopy;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.google.ar.sceneform.SceneView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import es.kingcreek.swifty_proteins.observers.AppLifecycleObserver;

public class ShareHelper {

    private static final String PROVIDER_AUTHORITY = "es.kingcreek.swifty_proteins.share.bitmapprovider";

    // Método para capturar y compartir la imagen del SceneView
    public static void captureAndShareSceneView(Activity activity, SceneView sceneView, AppLifecycleObserver appLifecycleObserver) {
        // Capturar la imagen del SceneView
        Bitmap bitmap = Bitmap.createBitmap(sceneView.getWidth(), sceneView.getHeight(), Bitmap.Config.ARGB_8888);
        PixelCopy.request(sceneView, bitmap, (copyResult) -> {
            if (copyResult == PixelCopy.SUCCESS) {
                constructShareableContent(activity, bitmap, appLifecycleObserver);
            } else {
                Toast.makeText(activity, "Error taking screnshoot", Toast.LENGTH_LONG).show();
            }
        }, new Handler());
    }

    public static Uri getBitmapUriFromBitmap(Context context, Bitmap bitmap) {
        Uri bmpUri = null;
        try {
            File file = new File(
                    context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                    "share_image_" + System.currentTimeMillis() + ".png"
            );
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = FileProvider.getUriForFile(context, PROVIDER_AUTHORITY, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }

    public static void constructShareableContent(Activity activity, Bitmap image, AppLifecycleObserver appLifecycleObserver) {
        Uri imageUri = getBitmapUriFromBitmap(activity, image);
        if (imageUri != null) {
            // Set sharing true to prevent exit from app
            appLifecycleObserver.setSharing(true);
            // Create intent with image uri to share
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
            shareIntent.setType("image/*");
            // Start share activity, and catch result in ProteinView
            activity.startActivityForResult(Intent.createChooser(shareIntent, "Share via"), REQUEST_CODE_SHARE);
        }
    }
}