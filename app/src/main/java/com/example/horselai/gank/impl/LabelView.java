package com.example.horselai.gank.impl;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.TextView;

import com.example.horselai.gank.R;

/**
 * Created by horseLai on 2017/8/7.
 */

public class LabelView extends TextView
{

    private Paint mPaint;
    private int mRectColor;
    private String mTextToDraw;
    private float mTextSize;
    private int mTextColor;
    private float mBottomRightCorner;
    private float mBottomLeftCorner;
    private float mTopRightCorner;
    private float mTopLeftCorner;
    private RectF mRect;
    private float mRectWidthDimen;
    private float mRectHeightDimen;
    private int mCenterX;
    private int mCenterY;

    public int getRectColor()
    {
        return mRectColor;
    }

    public void setRectColor(int rectColor)
    {
        this.mRectColor = rectColor;
    }

    public LabelView(Context context)
    {
        this(context, null);
    }

    public LabelView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public LabelView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }


    public static final int DEFAULT_TEXT_SIZE = 14;
    public static final int DEFAULT_CORNER_RADIUS = 10;
    public static final int DEFAULT_RECT_HEIGHT = 50;
    public static final int DEFAULT_RECT_WIDTH = 50;

    private void init(Context context, AttributeSet attrs)
    {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mRect = new RectF();


        final TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.LabelView);
        //color
        mRectColor = attributes.getColor(R.styleable.LabelView_rectColor, Color.WHITE);
        mTextColor = attributes.getColor(R.styleable.LabelView_textColor, Color.BLACK);

        //size
        mTextSize = attributes.getFloat(R.styleable.LabelView_textSize, DEFAULT_TEXT_SIZE);
        mRectHeightDimen = attributes.getDimension(R.styleable.LabelView_rectHeight, DEFAULT_RECT_HEIGHT);
        mRectWidthDimen = attributes.getDimension(R.styleable.LabelView_rectWidth, DEFAULT_RECT_WIDTH);
        //content
        mTextToDraw = attributes.getString(R.styleable.LabelView_text);
        if (mTextToDraw == null) mTextToDraw = "未知";

        // corner radius
        mTopLeftCorner = attributes.getFloat(R.styleable.LabelView_topLeftCornerRadius, DEFAULT_CORNER_RADIUS);
        mTopRightCorner = attributes.getFloat(R.styleable.LabelView_topRightCornerRadius, DEFAULT_CORNER_RADIUS);
        mBottomLeftCorner = attributes.getFloat(R.styleable.LabelView_bottomLeftCornerRadius, DEFAULT_CORNER_RADIUS);
        mBottomRightCorner = attributes.getFloat(R.styleable.LabelView_bottomRightCornerRadius, DEFAULT_CORNER_RADIUS);


        attributes.recycle();

    }

    @Override protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);

        mCenterX = w / 2;
        mCenterY = h / 2;
    }

    @Override protected void onDraw(Canvas canvas)
    {
        canvas.translate(mCenterX, mCenterY);

        final float halfRectH = mRectHeightDimen / 2.0F;
        final float halfRectW = mRectWidthDimen / 2.0F;

        canvas.drawColor(Color.TRANSPARENT);

        mRect.set(-mCenterX, -mCenterY, mCenterX, mCenterY);
        mPaint.setColor(mRectColor);
        canvas.drawRoundRect(mRect, mTopLeftCorner, mBottomRightCorner, mPaint);

       /* mPaint.setColor(mTextColor);
        canvas.drawText(mTextToDraw, 0, 0, mPaint);
*/
        super.onDraw(canvas);

    }
}
