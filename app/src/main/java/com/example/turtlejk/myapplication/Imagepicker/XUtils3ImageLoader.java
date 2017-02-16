package com.example.turtlejk.myapplication.Imagepicker;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.widget.ImageView;

import com.example.turtlejk.myapplication.R;
import com.lzy.imagepicker.loader.ImageLoader;

import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.io.File;

public class XUtils3ImageLoader implements ImageLoader {
    @Override
    public void displayImage(Activity activity, String path, ImageView imageView, int width, int height) {
        ImageOptions options = new ImageOptions.Builder()//
                .setLoadingDrawableId(R.mipmap.default_image)//
                .setFailureDrawableId(R.mipmap.default_image)//
                .setConfig(Bitmap.Config.RGB_565)//
                .setSize(width, height)//
                .setCrop(false)//
                .setUseMemCache(true)//
                .build();
        x.image().bind(imageView, Uri.fromFile(new File(path)).toString(), options);
    }

    @Override
    public void clearMemoryCache() {
    }
}
