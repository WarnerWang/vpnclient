package com.hxjg.vpn.api.bean.commonBean.app;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;

public class SeriDrawable implements Serializable {

    private static final long serialVersionUID = 1L;

    private byte[] mBitmapBytes = null;

    public SeriDrawable(byte[] bitmapBytes) {

// TODO Auto-generated constructor stub

        this.mBitmapBytes = bitmapBytes;

    }

    public SeriDrawable(Drawable drawable){

            mBitmapBytes= drawableToBytes(drawable);

    }

    public Drawable getDrawable(){

        return bytesToDrawable(mBitmapBytes);

    }

    public byte[] getBitmapBytes() {

        return this.mBitmapBytes;

    }

    /**

     * Drawable转换成byte[]

     * @param d

     * @return

     */

    public byte[] drawableToBytes(Drawable d) {

        Bitmap bitmap = this.drawableToBitmap(d);

        return this.bitmapToBytes(bitmap);

    }

    /**

     *  Drawable转换成Bitmap

     * @param drawable

     * @return

     */

    public Bitmap drawableToBitmap(Drawable drawable) {

        Bitmap bitmap = Bitmap

                .createBitmap(

                        drawable.getIntrinsicWidth(),

                        drawable.getIntrinsicHeight(),

                        drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888

                                : Bitmap.Config.RGB_565);

        Canvas canvas = new Canvas(bitmap);

        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),

                drawable.getIntrinsicHeight());

        drawable.draw(canvas);

        return bitmap;

    }

    /**

     * Bitmap转换成byte[]

     * @param bm

     * @return

     */

    public byte[] bitmapToBytes(Bitmap bm) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);

        return baos.toByteArray();

    }

    /**

     * byte[]转换成Bitmap

     * @param b

     * @return

     */

    public Bitmap bytesToBitmap(byte[] b) {

        if (b.length != 0) {

            return BitmapFactory.decodeByteArray(b, 0, b.length);

        }

        return null;

    }

    /**

     * byte[]转换成Drawable

     * @param b

     * @return

     */

    public Drawable bytesToDrawable(byte[] b) {

        Bitmap bitmap = this.bytesToBitmap(b);

        return this.bitmapToDrawable(bitmap);

    }

    /**

     * Bitmap转换成Drawable

     * @param bitmap

     * @return

     */

    public Drawable bitmapToDrawable(Bitmap bitmap) {

        BitmapDrawable bd = new BitmapDrawable(bitmap);

        Drawable d = (Drawable) bd;

        return d;

    }


}
