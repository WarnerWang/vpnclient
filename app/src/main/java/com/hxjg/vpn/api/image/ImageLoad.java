package com.hxjg.vpn.api.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.hxjg.vpn.R;
import com.hxjg.vpn.utils.Logger;
import com.hxjg.vpn.utils.StringUtils;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * glide doc link: https://muyangmin.github.io/glide-docs-cn/doc/transformations.html
 */
public class ImageLoad {

    public interface LoadResult{
        void loadSuccess(Drawable drawable);
    }

    public static LoadResult loadResult;

    /**
     * @param context
     * @param url
     * @param imageView
     * @param circle
     * @param placeHolder
     * @param radius      0-25
     */
    public static void loadImage(Context context, String url, ImageView imageView, boolean circle, @DrawableRes int placeHolder, int radius) {
        if (context == null) {
            Logger.e("context is null");
            return;
        }

        if (imageView == null) {
            Logger.e("imageView is null");
            return;
        }

        if (TextUtils.isEmpty(url)) {
            Logger.e("url is empty");
            imageView.setImageResource(placeHolder);
            return;
        }

        RequestBuilder<Drawable> requestBuilder = Glide
                .with(context)
                .load(url)
                .apply(RequestOptions.placeholderOf(placeHolder)
                        .diskCacheStrategy(DiskCacheStrategy.ALL));
        if (circle) {
            requestBuilder = requestBuilder.apply(RequestOptions.circleCropTransform());
        }
        requestBuilder.into(new SimpleTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                imageView.setImageDrawable(resource);

                imageView.setDrawingCacheEnabled(true);
                Bitmap drawingCache = imageView.getDrawingCache();
                if (drawingCache != null) {
                    Bitmap bitmap = Bitmap.createBitmap(drawingCache);
                    bitmap = RGB565toARGB888(bitmap);
                    Bitmap rsBlur = rsBlur(context, bitmap, radius, 0.5F);
                    imageView.setImageBitmap(rsBlur);
                }
                imageView.setDrawingCacheEnabled(false);

            }
        });
    }

    public static void loadImage(Context context, String url, @DrawableRes int placeHolder, LoadResult loadResult){
        if (TextUtils.isEmpty(url)) {
            Logger.e("url is empty");
            if (loadResult != null) {
                loadResult.loadSuccess(context.getResources().getDrawable(placeHolder));
            }
            return;
        }
        RequestBuilder<Drawable> requestBuilder = Glide
                .with(context)
                .load(url)
                .apply(RequestOptions.placeholderOf(placeHolder)
                        .diskCacheStrategy(DiskCacheStrategy.ALL))
                .apply(RequestOptions.circleCropTransform());

        requestBuilder.into(new SimpleTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {

                    if (loadResult != null && resource != null) {
                        loadResult.loadSuccess(resource);
                    }
            }
        });
    }

    public static void loadImageResult(Context context, String url, LoadResult loadResult){
        if (TextUtils.isEmpty(url)) {
            Logger.e("url is empty");
            return;
        }
        RequestBuilder<Drawable> requestBuilder = Glide
                .with(context)
                .load(url)
                .apply(RequestOptions.placeholderOf(R.mipmap.placeholder)
                        .diskCacheStrategy(DiskCacheStrategy.ALL));

        requestBuilder.into(new SimpleTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {

                if (loadResult != null && resource != null) {
                    loadResult.loadSuccess(resource);
                }
            }
        });
    }

    /**
     * 高斯模糊
     *
     * @param context
     * @param radius  0-25
     */
    private static Bitmap rsBlur(Context context, Bitmap source, int radius, float scale) {

        int width = Math.round(source.getWidth() * scale);
        int height = Math.round(source.getHeight() * scale);

        Bitmap inputBmp = Bitmap.createScaledBitmap(source, width, height, false);

        RenderScript renderScript = RenderScript.create(context);


        // Allocate memory for Renderscript to work with

        final Allocation input = Allocation.createFromBitmap(renderScript, inputBmp);
        final Allocation output = Allocation.createTyped(renderScript, input.getType());

        // Load up an instance of the specific script that we want to use.
        ScriptIntrinsicBlur scriptIntrinsicBlur = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
        scriptIntrinsicBlur.setInput(input);

        // Set the blur radius
        scriptIntrinsicBlur.setRadius(radius);

        // Start the ScriptIntrinisicBlur
        scriptIntrinsicBlur.forEach(output);

        // Copy the output to the blurred bitmap
        output.copyTo(inputBmp);


        renderScript.destroy();
        return inputBmp;
    }

    public static void loadImage(Context context, String url, ImageView imageView, boolean circle, @DrawableRes int placeHolder) {
        if (context == null) {
            Logger.e("context is null");
            return;
        }

        if (imageView == null) {
            Logger.e("imageView is null");
            return;
        }

        if (TextUtils.isEmpty(url)) {
            Logger.e("url is empty");
            imageView.setImageResource(placeHolder);
            return;
        }
        imageView.setImageResource(placeHolder);

        RequestBuilder<Drawable> requestBuilder = Glide
                .with(context)
                .load(url)
                .apply(RequestOptions.placeholderOf(placeHolder)
                        .diskCacheStrategy(DiskCacheStrategy.ALL));


        if (circle) {
            requestBuilder = requestBuilder.apply(RequestOptions.circleCropTransform());
        }
        requestBuilder.into(new SimpleTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                if (StringUtils.isEmpty(resource)) {
                    imageView.setImageResource(placeHolder);
                } else {
                    imageView.setImageDrawable(resource);
                }
            }
        });
    }

    private static Bitmap RGB565toARGB888(Bitmap img) {
        int numPixels = img.getWidth() * img.getHeight();
        int[] pixels = new int[numPixels];

        //Get JPEG pixels.  Each int is the color values for one pixel.
        img.getPixels(pixels, 0, img.getWidth(), 0, 0, img.getWidth(), img.getHeight());

        //Create a Bitmap of the appropriate format.
        Bitmap result = Bitmap.createBitmap(img.getWidth(), img.getHeight(), Bitmap.Config.ARGB_8888);

        //Set RGB pixels.
        result.setPixels(pixels, 0, result.getWidth(), 0, 0, result.getWidth(), result.getHeight());
        return result;

    }

    public static void loadUserAvatar(Context context, String url, ImageView imageView, boolean circle) {
        loadImage(context, url, imageView, circle, R.mipmap.place_hold_user_avatar);
    }

    /**
     * @param context
     * @param url
     * @param imageView
     * @param radius    模糊半径 0-25
     */
    public static void loadImage(Context context, String url, ImageView imageView, int radius) {
        loadImage(context, url, imageView, false, R.mipmap.placeholder, radius);
    }

    public static void loadImage(Context context, String url, ImageView imageView) {
        loadImage(context, url, imageView, false, R.mipmap.placeholder);
    }

}
