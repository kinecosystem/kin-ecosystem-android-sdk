package com.kin.ecosystem.history.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import com.kin.ecosystem.R;


public class KinDashView extends View {

    private static int ORIENTATION_HORIZONTAL = 0;
    private static int ORIENTATION_VERTICAL = 1;
    private Paint mPaint;
    private int orientation;

    public KinDashView(Context context, AttributeSet attrs) {
        super(context, attrs);
        int dashGap, dashLength, dashThickness;
        int color;
        TypedArray styledAttributes = context.getTheme().obtainStyledAttributes(attrs, R.styleable.KinDashView, 0, 0);

        try {
            dashGap = styledAttributes.getDimensionPixelSize(R.styleable.KinDashView_dashGap, 5);
            dashLength = styledAttributes.getDimensionPixelSize(R.styleable.KinDashView_dashLength, 5);
            dashThickness = styledAttributes.getDimensionPixelSize(R.styleable.KinDashView_dashThickness, 3);
            color = styledAttributes.getColor(R.styleable.KinDashView_color, 0xff000000);
            orientation = styledAttributes.getInt(R.styleable.KinDashView_orientation, ORIENTATION_HORIZONTAL);
        } finally {
            styledAttributes.recycle();
        }

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(color);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(dashThickness);
        mPaint.setPathEffect(new DashPathEffect(new float[]{dashLength, dashGap,}, 0));

        setLayerType(View.LAYER_TYPE_SOFTWARE, mPaint);
    }


    public KinDashView(Context context) {
        this(context, null);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (orientation == ORIENTATION_HORIZONTAL) {
            float center = getHeight() * .5f;
            canvas.drawLine(0, center, getWidth(), center, mPaint);
        } else {
            float center = getWidth() * .5f;
            canvas.drawLine(center, 0, center, getHeight(), mPaint);
        }
    }
}
