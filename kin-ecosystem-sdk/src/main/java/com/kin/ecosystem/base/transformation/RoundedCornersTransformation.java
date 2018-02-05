package com.kin.ecosystem.base.transformation;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.annotation.IntDef;

import com.squareup.picasso.Transformation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.kin.ecosystem.base.transformation.RoundedCornersTransformation.CornerType.ALL;
import static com.kin.ecosystem.base.transformation.RoundedCornersTransformation.CornerType.BOTTOM;
import static com.kin.ecosystem.base.transformation.RoundedCornersTransformation.CornerType.BOTTOM_LEFT;
import static com.kin.ecosystem.base.transformation.RoundedCornersTransformation.CornerType.BOTTOM_RIGHT;
import static com.kin.ecosystem.base.transformation.RoundedCornersTransformation.CornerType.LEFT;
import static com.kin.ecosystem.base.transformation.RoundedCornersTransformation.CornerType.RIGHT;
import static com.kin.ecosystem.base.transformation.RoundedCornersTransformation.CornerType.TOP;
import static com.kin.ecosystem.base.transformation.RoundedCornersTransformation.CornerType.TOP_LEFT;
import static com.kin.ecosystem.base.transformation.RoundedCornersTransformation.CornerType.TOP_RIGHT;


public class RoundedCornersTransformation implements Transformation {


    @IntDef({ALL, TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT, TOP, BOTTOM, LEFT, RIGHT})
    @Retention(RetentionPolicy.SOURCE)
    @interface CornerType {
        int ALL = 0;
        int TOP_LEFT = 1;
        int TOP_RIGHT = 2;
        int BOTTOM_LEFT = 3;
        int BOTTOM_RIGHT = 4;
        int TOP = 5;
        int BOTTOM = 6;
        int LEFT = 7;
        int RIGHT = 8;
    }

    private int mRadius;
    private int mDiameter;
    private int mMargin;

    private @CornerType int mCornerType;

    public RoundedCornersTransformation(int radius, int margin) {
        this(radius, margin, CornerType.ALL);
    }

    public RoundedCornersTransformation(int radius, int margin, @CornerType int cornerType) {
        mRadius = radius;
        mDiameter = radius * 2;
        mMargin = margin;
        mCornerType = cornerType;
    }

    @Override
    public Bitmap transform(Bitmap source) {

        int width = source.getWidth();
        int height = source.getHeight();

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(new BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
        drawRoundRect(canvas, paint, width, height);
        source.recycle();

        return bitmap;
    }

    private void drawRoundRect(Canvas canvas, Paint paint, float width, float height) {
        float right = width - mMargin;
        float bottom = height - mMargin;

        switch (mCornerType) {
            case ALL:
                canvas.drawRoundRect(new RectF(mMargin, mMargin, right, bottom), mRadius, mRadius, paint);
                break;
            case TOP_LEFT:
                drawTopLeftRoundRect(canvas, paint, right, bottom);
                break;
            case TOP_RIGHT:
                drawTopRightRoundRect(canvas, paint, right, bottom);
                break;
            case BOTTOM_LEFT:
                drawBottomLeftRoundRect(canvas, paint, right, bottom);
                break;
            case BOTTOM_RIGHT:
                drawBottomRightRoundRect(canvas, paint, right, bottom);
                break;
            case TOP:
                drawTopRoundRect(canvas, paint, right, bottom);
                break;
            case BOTTOM:
                drawBottomRoundRect(canvas, paint, right, bottom);
                break;
            case LEFT:
                drawLeftRoundRect(canvas, paint, right, bottom);
                break;
            case RIGHT:
                drawRightRoundRect(canvas, paint, right, bottom);
                break;
            default:
                canvas.drawRoundRect(new RectF(mMargin, mMargin, right, bottom), mRadius, mRadius, paint);
                break;
        }
    }

    private void drawTopLeftRoundRect(Canvas canvas, Paint paint, float right, float bottom) {
        canvas.drawRoundRect(new RectF(mMargin, mMargin, mMargin + mDiameter, mMargin + mDiameter),
                mRadius, mRadius, paint);
        canvas.drawRect(new RectF(mMargin, mMargin + mRadius, mMargin + mRadius, bottom), paint);
        canvas.drawRect(new RectF(mMargin + mRadius, mMargin, right, bottom), paint);
    }

    private void drawTopRightRoundRect(Canvas canvas, Paint paint, float right, float bottom) {
        canvas.drawRoundRect(new RectF(right - mDiameter, mMargin, right, mMargin + mDiameter), mRadius,
                mRadius, paint);
        canvas.drawRect(new RectF(mMargin, mMargin, right - mRadius, bottom), paint);
        canvas.drawRect(new RectF(right - mRadius, mMargin + mRadius, right, bottom), paint);
    }

    private void drawBottomLeftRoundRect(Canvas canvas, Paint paint, float right, float bottom) {
        canvas.drawRoundRect(new RectF(mMargin, bottom - mDiameter, mMargin + mDiameter, bottom),
                mRadius, mRadius, paint);
        canvas.drawRect(new RectF(mMargin, mMargin, mMargin + mDiameter, bottom - mRadius), paint);
        canvas.drawRect(new RectF(mMargin + mRadius, mMargin, right, bottom), paint);
    }

    private void drawBottomRightRoundRect(Canvas canvas, Paint paint, float right, float bottom) {
        canvas.drawRoundRect(new RectF(right - mDiameter, bottom - mDiameter, right, bottom), mRadius,
                mRadius, paint);
        canvas.drawRect(new RectF(mMargin, mMargin, right - mRadius, bottom), paint);
        canvas.drawRect(new RectF(right - mRadius, mMargin, right, bottom - mRadius), paint);
    }

    private void drawTopRoundRect(Canvas canvas, Paint paint, float right, float bottom) {
        canvas.drawRoundRect(new RectF(mMargin, mMargin, right, mMargin + mDiameter), mRadius, mRadius,
                paint);
        canvas.drawRect(new RectF(mMargin, mMargin + mRadius, right, bottom), paint);
    }

    private void drawBottomRoundRect(Canvas canvas, Paint paint, float right, float bottom) {
        canvas.drawRoundRect(new RectF(mMargin, bottom - mDiameter, right, bottom), mRadius, mRadius,
                paint);
        canvas.drawRect(new RectF(mMargin, mMargin, right, bottom - mRadius), paint);
    }

    private void drawLeftRoundRect(Canvas canvas, Paint paint, float right, float bottom) {
        canvas.drawRoundRect(new RectF(mMargin, mMargin, mMargin + mDiameter, bottom), mRadius, mRadius,
                paint);
        canvas.drawRect(new RectF(mMargin + mRadius, mMargin, right, bottom), paint);
    }

    private void drawRightRoundRect(Canvas canvas, Paint paint, float right, float bottom) {
        canvas.drawRoundRect(new RectF(right - mDiameter, mMargin, right, bottom), mRadius, mRadius,
                paint);
        canvas.drawRect(new RectF(mMargin, mMargin, right - mRadius, bottom), paint);
    }

    @Override
    public String key() {
        return "RoundedTransformation(radius=" + mRadius + ", margin=" + mMargin + ", diameter="
                + mDiameter + ", cornerType=" + mCornerType + ")";
    }
}
