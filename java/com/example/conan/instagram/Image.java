package com.example.administrator.instagram;

import com.example.instagramgallery.network.BitmapDownloaderTask;
import com.example.conan.gallery.BitmapDownload;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

public class Image extends Activity {
    ZoomView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image);

        Intent act = getIntent();
        String url = act.getExtras().getString("url");
        if (url.length() > 0) {
            imageView = (ZoomView) findViewById(R.id.image_view);

            BitmapDownload task = new BitmapDownload(imageView);
            if (!task.searchCache(url))
                task.execute(url);
        } else {

        }
    }
}


