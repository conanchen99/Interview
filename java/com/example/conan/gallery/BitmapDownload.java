package com.example.conan.gallery;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;
import java.lang.ref.WeakReference;
import java.util.HashMap;

/**
 * Created by conan on 3/09/2015.
 */
public class BitmapDownload extends AsyncTask <String, Void, Bitmap> {
    private static HashMap<String, Bitmap> cache;
    private final WeakReference<ImageView> imageViewReference;
    private String url;

    public BitmapDownload(ImageView imageView) {
        imageViewReference = new WeakReference<ImageView>(imageView);

        if (cache == null) {
            cache = new HashMap<String, Bitmap>();
        }
    }

    protected Bitmap doInBackground(String... params) {
        url = params[0];

        if (cache.containsKey(url)) {
            return cache.get(url);
        }

        return WebInterface.downloadBitmap(url);
    }
    @Override

    protected void onPostExecute(Bitmap bitmap) {

        cache.put(url, bitmap);
        if (isCancelled()) {
            return;
        }

        if (imageViewReference != null) {
            ImageView imageView = imageViewReference.get();
            if (imageView != null) {

                imageView.setImageBitmap(bitmap);
            }
        }
    }

    public boolean searchCache(String url) {
        if (imageViewReference != null) {
            ImageView imageView = imageViewReference.get();
            if (imageView != null) {
// check for cached copy
                if (cache.containsKey(url)) {
// set bitmap on image view
                    imageView.setImageBitmap(cache.get(url));
                    return true;
                }
            }
        }
        return false;
    }
}

