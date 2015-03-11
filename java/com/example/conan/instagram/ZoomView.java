package com.example.conan.instagram;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.ImageView;

public class ZoomView extends ImageView {
    private ScaleGestureDetector detector;
    private float scale = 1f;
    private static float minZoom = 0.5f, maxZoom = 3f;
    private Matrix transform;
    private int activePointerID = -1;
    private float lastX, lastY;
    private int offsetX = 0, offsetY = 0;

    private int width, height;
    private int imageWidth, imageHeight;
    public ZoomView(Context context, AttributeSet set, int defStyle) {
        super(context, set, defStyle);
    }
    public ZoomView(Context context, AttributeSet set) {
        super(context, set);
    }
    public ZoomView(Context context) {
        super(context);
    }
    @Override
    public void setImageBitmap(Bitmap bitmap) {
        super.setImageBitmap(bitmap);
        imageWidth = bitmap.getWidth();
        imageHeight = bitmap.getHeight();
        setupPinchZoom();
    }

    public void setupPinchZoom() {
        DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
        width = metrics.widthPixels;
        height = metrics.heightPixels;

// set the size of the picture to fit the smallest orientation of the screen
        if (width > height) {
            scale = (float) height / imageHeight;
        } else {
            scale = (float) width / imageWidth;
        }

// adjust scaling limits if needed
        if (scale > maxZoom)
            maxZoom = scale;
        if (scale < minZoom)
            minZoom = scale;

// create a transformation matrix and set the scale and translation based on the initial scale
        transform = new Matrix();
        transform.setScale(scale, scale);
        transform.postTranslate(-306 * scale, -306 * scale);

// set up a listener for pinch and zoom
        detector = new ScaleGestureDetector(getContext(), new ScaleListener());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
// exit event without handling if image is not yet loaded
//if (this.getDrawable() == null)
//return true;
        if (detector != null)
            detector.onTouchEvent(event);
// get the type of action associated with the event and switch on it
        final int action = MotionEventCompat.getActionMasked(event);
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                final int pointerIndex = MotionEventCompat.getActionIndex(event);
                final float x = MotionEventCompat.getX(event, pointerIndex);
                final float y = MotionEventCompat.getY(event, pointerIndex);
// save our last position and the pointer id
                lastX = x;
                lastY = y;
                activePointerID = MotionEventCompat.getPointerId(event, pointerIndex);
                break; }
            case MotionEvent.ACTION_MOVE: {
// fetch the active pointer index
                final int pointerIndex = MotionEventCompat.findPointerIndex(event, activePointerID);
                final float x = MotionEventCompat.getX(event, pointerIndex);
                final float y = MotionEventCompat.getY(event, pointerIndex);
// only move if not scaling?
                if (detector != null && !detector.isInProgress()) {
                    final float dx = x - lastX;
                    final float dy = y - lastY;
// adjust canvas offset
                    offsetX += dx;
                    offsetY += dy;
// remember these new coordinates
                    lastX = x;
                    lastY = y;
                    invalidate();
                }
                break; }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {
                activePointerID = -1;
                break; }
            case MotionEvent.ACTION_POINTER_UP: {
// get pointer information
                final int pointerIndex = MotionEventCompat.getActionIndex(event);
                final int pointerID = MotionEventCompat.getPointerId(event, pointerIndex);
// if the pointer we were tracking was lifted, choose a new pointer
                if (pointerID == activePointerID) {
                    final int newPointerIndex = (pointerIndex == 0 ? 1 : 0);
                    lastX = MotionEventCompat.getX(event, newPointerIndex);
                    lastY = MotionEventCompat.getY(event, newPointerIndex);
                    activePointerID = MotionEventCompat.getPointerId(event, newPointerIndex);
                }
                break; }
        }
        fitToScreen();
        return true;
    }
    public void fitToScreen() {
//Log.i("crb", "limit = " + ((scale * imageWidth - width) / 2));
//Log.i("crb", "left edge at = " + (offsetX));
        int edgeX;
        if (scale * imageWidth >= width)
            edgeX = (int) ((scale * imageWidth - width) / 2);
        else
            edgeX = (int) ((width - scale * imageWidth) / 2);
        if (offsetX > edgeX)
            offsetX = edgeX;
        else if (offsetX < -edgeX)
            offsetX = -edgeX;
        int edgeY;
        if (scale * imageHeight <= height)
            edgeY = (int) ((scale * imageHeight - height) / 2);
        else
            edgeY = (int) ((height - scale * imageHeight) / 2);
        if (offsetY < edgeY)
            offsetY = edgeY;
        else if (offsetY > -edgeY)
            offsetY = -edgeY;
    }
    @Override
    public void onDraw(Canvas canvas) {
        BitmapDrawable draw = (BitmapDrawable) this.getDrawable();
        if (draw != null) {

            canvas.translate(width / 2 + offsetX, height / 2 + offsetY);
            canvas.drawBitmap(draw.getBitmap(), transform, null);
        }
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {

            scale *= detector.getScaleFactor();
            scale = Math.max(minZoom, Math.min(scale, maxZoom));

            transform.setScale(scale, scale);
            transform.postTranslate(-306 * scale, -306 * scale);

            invalidate();
            return true;
        }
    }
}

