package com.buzz.vpn.widge;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.TextView;

import com.buzz.vpn.R;
import com.buzz.vpn.utils.ScreenUtils;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

@SuppressLint("AppCompatCustomView")
public class WJTextView extends TextView {

    private int mSolidColor = Color.TRANSPARENT;//填充颜色
    private int mBorderColor = Color.TRANSPARENT;//边框颜色
    private float mBorderWidth = 0;//边框宽度
    private float mCornerRadius = 0;//圆角大小

    private float mTopLeftRadius = 0;
    private float mTopRightRadius = 0;
    private float mBottomLeftRadius = 0;
    private float mBottomRightRadius = 0;

    private int mGradientStartColor, mGradientEndColor,mGradientCenterColor;
    private Integer mTextGradientStartColor, mTextGradientEndColor;

    private GradientDrawable.Orientation mGradientOrientation = GradientDrawable.Orientation.LEFT_RIGHT;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public WJTextView(Context context) {
        this(context, null);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public WJTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public WJTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, 0,0);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public WJTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs, R.styleable.WJTextView, defStyleAttr, 0);
        if (attributes != null) {
            final int N = attributes.getIndexCount();
            for (int i = 0; i < N; i++) {
                initAttr(attributes.getIndex(i), attributes);
            }
            attributes.recycle();

            GradientDrawable gd = new GradientDrawable();//创建drawable
            if (mCornerRadius != 0) {
                gd.setCornerRadius(mCornerRadius);
            }else {
                //1、2两个参数表示左上角，3、4表示右上角，5、6表示右下角，7、8表示左下角
                gd.setCornerRadii(new float[] { mTopLeftRadius,
                        mTopLeftRadius, mTopRightRadius, mTopRightRadius, mBottomRightRadius,
                        mBottomRightRadius , mBottomLeftRadius, mBottomLeftRadius});
            }
            if (mGradientStartColor != Color.TRANSPARENT) {
                if (mGradientCenterColor != Color.TRANSPARENT) {
                    gd.setColors(new int[] { mGradientStartColor,
                            mGradientCenterColor, mGradientEndColor});
                }else {
                    gd.setColors(new int[] { mGradientStartColor, mGradientEndColor});
                }
                gd.setOrientation(mGradientOrientation);
            }else {
                if (mSolidColor != Color.TRANSPARENT) {
                    gd.setColor(mSolidColor);
                }
            }
            if (mBorderWidth > 0) {
                gd.setStroke((int) mBorderWidth, mBorderColor);
            }

            this.setBackground(gd);
        }
    }

    protected void initAttr(int attr, TypedArray attributes) {
        switch (attr) {
            case R.styleable.WJTextView_wj_solid_color:{
                mSolidColor = attributes.getColor(R.styleable.WJTextView_wj_solid_color, Color.TRANSPARENT);
            }
            break;
            case R.styleable.WJTextView_wj_border_color:{
                mBorderColor = attributes.getColor(R.styleable.WJTextView_wj_border_color, Color.TRANSPARENT);
            }
            break;
            case R.styleable.WJTextView_wj_border_width:{
                mBorderWidth = attributes.getDimension(R.styleable.WJTextView_wj_border_width, 0);
            }
            break;
            case R.styleable.WJTextView_wj_corner_radius:{
                mCornerRadius = attributes.getDimension(R.styleable.WJTextView_wj_corner_radius, 0);
            }
            break;
            case R.styleable.WJTextView_wj_top_left_radius:{
                mTopLeftRadius = attributes.getDimension(R.styleable.WJTextView_wj_top_left_radius, 0);
            }
            break;
            case R.styleable.WJTextView_wj_top_right_radius:{
                mTopRightRadius = attributes.getDimension(R.styleable.WJTextView_wj_top_right_radius, 0);
            }
            break;
            case R.styleable.WJTextView_wj_bottom_left_radius:{
                mBottomLeftRadius = attributes.getDimension(R.styleable.WJTextView_wj_bottom_left_radius, 0);
            }
            break;
            case R.styleable.WJTextView_wj_bottom_right_radius:{
                mBottomRightRadius = attributes.getDimension(R.styleable.WJTextView_wj_bottom_right_radius, 0);
            }
            break;
            case R.styleable.WJTextView_wj_gradient_start_color:{
                mGradientStartColor = attributes.getColor(R.styleable.WJTextView_wj_gradient_start_color, Color.TRANSPARENT);
            }
            break;
            case R.styleable.WJTextView_wj_gradient_center_color:{
                mGradientCenterColor = attributes.getColor(R.styleable.WJTextView_wj_gradient_center_color, Color.TRANSPARENT);
            }
            break;
            case R.styleable.WJTextView_wj_gradient_end_color:{
                mGradientEndColor = attributes.getColor(R.styleable.WJTextView_wj_gradient_end_color, Color.TRANSPARENT);
            }
            break;
            case R.styleable.WJTextView_wj_gradient_orientation:{
                int ordinal = attributes.getInt(R.styleable.WJTextView_wj_gradient_orientation, GradientDrawable.Orientation.LEFT_RIGHT.ordinal());
                mGradientOrientation = GradientDrawable.Orientation.values()[ordinal];
            }
            break;
        }
    }

    /**
     * 设置渐变颜色
     * @param startColor 开始颜色
     * @param endColor   结束颜色
     */
    public void setGradientColor(int startColor, int endColor){
        this.mGradientStartColor = startColor;
        this.mGradientEndColor = endColor;
        GradientDrawable myGrad = (GradientDrawable) getBackground();
        myGrad.setColors(new int[] { mGradientStartColor, mGradientEndColor});
        myGrad.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);
        this.setBackground(myGrad);
    }

    /**
     * 设置渐变颜色
     * @param startColor 开始颜色
     * @param endColor   结束颜色
     * @param orientation 方向
     */
    public void setGradientColor(int startColor, int endColor, GradientDrawable.Orientation orientation){
        this.mGradientStartColor = startColor;
        this.mGradientEndColor = endColor;
        GradientDrawable myGrad = (GradientDrawable) getBackground();
        myGrad.setColors(new int[] { mGradientStartColor, mGradientEndColor});
        myGrad.setOrientation(orientation);
        this.setBackground(myGrad);
    }

    /**
     * 设置渐变颜色
     * @param startColor 开始颜色
     * @param centerColor 中间颜色
     * @param endColor   结束颜色
     */
    public void setGradientColor(int startColor, int centerColor, int endColor){
        this.mGradientStartColor = startColor;
        this.mGradientCenterColor = centerColor;
        this.mGradientEndColor = endColor;
        GradientDrawable myGrad = (GradientDrawable) getBackground();
        myGrad.setColors(new int[] { mGradientStartColor,
                mGradientCenterColor, mGradientEndColor});
        myGrad.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);
        this.setBackground(myGrad);
    }

    public void setCornerRadius(float radius) {
        GradientDrawable myGrad = (GradientDrawable) getBackground();
        myGrad.setCornerRadius(ScreenUtils.dpToPxInt(getContext(),radius));
        this.setBackground(myGrad);

    }

    public void setCornerRadii(float topLeftRadius, float topRightRadius, float btmRightRadius, float btmLeftRadius){

        mTopLeftRadius = ScreenUtils.dpToPxInt(getContext(),topLeftRadius);
        mTopRightRadius = ScreenUtils.dpToPxInt(getContext(),topRightRadius);
        mBottomRightRadius = ScreenUtils.dpToPxInt(getContext(),btmRightRadius);
        mBottomLeftRadius = ScreenUtils.dpToPxInt(getContext(),btmLeftRadius);

        GradientDrawable myGrad = (GradientDrawable) getBackground();
        //1、2两个参数表示左上角，3、4表示右上角，5、6表示右下角，7、8表示左下角
        myGrad.setCornerRadii(new float[] { mTopLeftRadius,
                mTopLeftRadius, mTopRightRadius, mTopRightRadius, mBottomRightRadius,
                mBottomRightRadius , mBottomLeftRadius, mBottomLeftRadius});
        this.setBackground(myGrad);
    }

    public void setSolidColor(int color){
        this.mSolidColor = color;
        GradientDrawable myGrad = (GradientDrawable) getBackground();
        myGrad.setColor(color);

    }

    public void setBorderColor(int color){
        this.mBorderColor = color;
        GradientDrawable myGrad = (GradientDrawable) getBackground();
        myGrad.setStroke((int) mBorderWidth, mBorderColor);
    }

    public void setTextGradientColor(Integer startColor, Integer endColor) {
        mTextGradientStartColor = startColor;
        mTextGradientEndColor = endColor;
    }
}
