package com.example.conan.instagram;

import android.widget.BaseAdapter;
import java.util.HashMap;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.conan.gallery.BitmapDownload;

import org.json.JSONObject;


public class Adapter extends BaseAdapter {
    private HashMap<ImageView, BitmapDownload> competitors;
    JSONObject imageData;
    Context context1;
    int size = 0;
    public Adapter(Context context1, JSONObject imageData, int number) {
        Log.i("crb", "image data downloaded");
        this.context1 = ccontext1;
        this.imageData = imageData;
        competitors = new HashMap<ImageView, BitmapDownload>();
        size = number;
    }
    @Override
    public int getCount() {
        try {
            return imageData.getJSONArray("data").length();
        } catch (JSONException e) {

            return 0;
        } catch (NullPointerException e) {
            return 0;
        }
    }
    @Override
    public Object getItem(int position) {
        return null;
    }
    @Override
    public long getItemId(int position) {
        return 0;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            if (size == 0) {
                size++;
                imageView = new ImageView(context1);
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                imageView.setLayoutParams(new GridView.LayoutParams(100, 100));
            } else if (size == 1) {
                size++;
                imageView = new ImageView(context1);
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                imageView.setLayoutParams(new GridView.LayoutParams(100, 100));
            } else {
                size = 0;
                imageView = new ImageView(context1);
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                imageView.setLayoutParams(new GridView.LayoutParams(220, 220));
            }
        } else {
            imageView = (ImageView) convertView;
        }
        imageView.setImageBitmap(null);
        try {
            BitmapDownload task = new BitmapDownload(imageView);
            BitmapDownload other = competitors.put(imageView, task);
            if (other != null)
                other.cancel(false);
            String url = imageData.getJSONArray("data").getJSONObject(position)
                    .getJSONObject("images").getJSONObject("thumbnail")
                    .getString("url");
            if (!task.searchCache(url))
                task.execute(url);
        } catch (JSONException e) {
        }
        return imageView;
    }
}
