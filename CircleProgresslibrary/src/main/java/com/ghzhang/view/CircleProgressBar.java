package com.ghzhang.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;

public class CircleProgressBar extends View {
    private static final String TAG = "CircleProgressBar";
    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int mRadius;
    private int mBgStrokeWidth, mStrokeWidth, mIconSrcWidth;
    private int mTextSize;
    private int mAngle;
    private int mCurrentProgress,mTargetProgress;
    private String  mPercentage;
    private int mMaxProgress = 100;
    private int mMaxAngle = 360;
    private int mWidth, mHeight;
    private int mBgColor = 0xFF888888;
    private int mProgressColor = 0xFF6C7587;
    private int mType;
    private int mIconDrawable;
    private Paint.Cap mStrokeCap;
    private Bitmap mBitmap;
    private ValueAnimator mValueAnimator;
    Paint.FontMetrics fontMetrics = new Paint.FontMetrics();

    public CircleProgressBar(Context context) {
        this(context, null);
    }

    public CircleProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircleProgressBar,
                defStyleAttr, 0);

        mBgColor = a.getColor(R.styleable.CircleProgressBar_bgColor, mBgColor);
        mProgressColor = a.getColor(R.styleable.CircleProgressBar_progressColor, mProgressColor);
        mStrokeCap = getStrokeCap(a.getInt(R.styleable.CircleProgressBar_strokeCap, CAP_SQUARE));
        mType = a.getInt(R.styleable.CircleProgressBar_type, TYPE_NONE);
        mCurrentProgress = a.getInt(R.styleable.CircleProgressBar_progress, 0);
        mAngle = getAngle(mCurrentProgress);
        mPercentage = getPercentage(mCurrentProgress);
        Log.d(TAG, "mCurrentProgress: "+mCurrentProgress +"   mAngle :  "+  mAngle);
        Log.d(TAG, "mPercentage: "+mPercentage);
        mIconDrawable = a.getResourceId(R.styleable.CircleProgressBar_iconSrc,R.mipmap.battery );
        mIconSrcWidth = a.getDimensionPixelSize(R.styleable.CircleProgressBar_iconSrcWidth, 0);
        mBgStrokeWidth = a.getDimensionPixelSize(R.styleable.CircleProgressBar_bgStrokeWidth, 5);
        mStrokeWidth = a.getDimensionPixelSize(R.styleable.CircleProgressBar_strokeWidth, 20);
        mTextSize = mBgStrokeWidth * 8;
        mTextSize = a.getDimensionPixelSize(R.styleable.CircleProgressBar_textSize, mTextSize);
        a.recycle();
        init();
    }

    private void init() {
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.getFontMetrics(fontMetrics);
        mTextPaint.setStyle(Paint.Style.FILL);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(mStrokeCap);
        if (mIconSrcWidth > 0) {
            mBitmap = getBitmap(getResources(), mIconDrawable, mIconSrcWidth);

        } else {
            mBitmap = BitmapFactory.decodeResource(getResources(), mIconDrawable);
        }
        initAnim();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int defaultValue = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,200,getDisplayMetrics());
        int width = measureHandler(widthMeasureSpec,defaultValue);
        int height = measureHandler(heightMeasureSpec,defaultValue);
        int value = Math.min(width, height);
        setMeasuredDimension(value,value);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = getWidth();
        mHeight = getHeight();
        int paddingStart = getPaddingStart();
        int paddingEnd = getPaddingEnd();
        int maxPadding = Math.max(paddingStart, paddingEnd);
        mRadius = (mWidth-maxPadding-mStrokeWidth) / 2 ;
    }



    private static final int TYPE_TEXT = 1;
    private static final int TYPE_ICON = 2;
    private static final int TYPE_NONE = 3;
    private RectF mRect = new RectF();

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setColor(mBgColor);
        mPaint.setStrokeWidth(mBgStrokeWidth);
        canvas.drawCircle(mWidth >> 1, mHeight >> 1, mRadius, mPaint);

        mPaint.setColor(mProgressColor);
        mPaint.setStrokeWidth(mStrokeWidth);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            canvas.drawArc((mWidth >> 1) - mRadius, (mHeight >> 1) - mRadius,
                    (mWidth >> 1) + mRadius, (mHeight >> 1) + mRadius, -90, mAngle, false, mPaint);
        } else {
            mRect.set((mWidth >> 1) - mRadius, (mHeight >> 1) - mRadius,
                    (mWidth >> 1) + mRadius, (mHeight >> 1) + mRadius);
            canvas.drawArc(mRect,-90,mAngle,false,mPaint);
        }


        switch (mType) {
            case TYPE_TEXT:
                float offset = (fontMetrics.descent - fontMetrics.ascent) / 2;
                canvas.drawText(mPercentage, mWidth >> 1, (mHeight >> 1) + offset / 2, mTextPaint);
                break;
            case TYPE_ICON:
                canvas.drawBitmap(mBitmap, (mWidth >> 1) - (mBitmap.getWidth() >> 1),
                        (mHeight >> 1) - (mBitmap.getHeight() >> 1), mPaint);
            default:
                break;
        }
    }


    private void initAnim() {
        mValueAnimator =new ValueAnimator();
        mValueAnimator.setDuration(200);
        mValueAnimator.setRepeatMode(ValueAnimator.RESTART);
        mValueAnimator.setRepeatCount(0);
        mValueAnimator.setInterpolator(new LinearInterpolator());
        mValueAnimator.addUpdateListener(animation -> {
            int value = (int) animation.getAnimatedValue();
            Log.d(TAG, "initAnim:  "+value);
            mAngle = getAngle(value);
            mPercentage = getPercentage(value);
            postInvalidate();
        });

        mValueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}

            @Override
            public void onAnimationEnd(Animator animation) {
                Log.d(TAG, "onAnimationEnd: ");
                mCurrentProgress = mTargetProgress;
            }

            @Override
            public void onAnimationCancel(Animator animation) {}

            @Override
            public void onAnimationRepeat(Animator animation) { }
        });

    }



    public int getCurrentProgress() {
        return mCurrentProgress;
    }

    public void setProgress(int progress) {
        Log.d(TAG, "setProgress: "+progress);
        Log.d(TAG, "mCurrentProgress: "+mCurrentProgress);
        if (progress == mCurrentProgress || progress > mMaxProgress || progress < 0) {
            return;
        }
        mTargetProgress = progress;
        mValueAnimator.setIntValues(mCurrentProgress,progress);
        mValueAnimator.start();
    }

    public int getAngle(int progress) {
        return  (int) (progress * mMaxAngle/ mMaxProgress);
    }

    private static final int CAP_ROUND = 1;
    private static final int CAP_SQUARE = 2;
    private Paint.Cap getStrokeCap(int value) {
        switch (value) {
            case CAP_ROUND:
                return Paint.Cap.ROUND;
            default:
                return Paint.Cap.SQUARE;
        }
    }

    public static Bitmap getBitmap(Resources res, int drawable, int width) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, drawable, options);
        options.inJustDecodeBounds = false;
        options.inDensity = options.outWidth;
        options.inTargetDensity = width;
        return BitmapFactory.decodeResource(res, drawable, options);
    }

    private DisplayMetrics getDisplayMetrics(){
        return getResources().getDisplayMetrics();
    }

    private String getPercentage(int progress) {
        return String.format(getResources().getString(R.string.progress_percent),progress);
    }

    /**
     * 测量
     * @param measureSpec
     * @param defaultSize
     * @return
     */
    private int measureHandler(int measureSpec,int defaultSize){

        int result = defaultSize;
        int measureMode = MeasureSpec.getMode(measureSpec);
        int measureSize = MeasureSpec.getSize(measureSpec);
        if(measureMode == MeasureSpec.EXACTLY){
            result = measureSize;
        }else if(measureMode == MeasureSpec.AT_MOST){
            result = Math.min(defaultSize,measureSize);
        }
        return result;
    }


}
