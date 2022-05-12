package com.example.myapplication.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.ColorRes;
import androidx.annotation.DimenRes;
import androidx.recyclerview.widget.RecyclerView;

public class DefaultItemDecoration extends RecyclerView.ItemDecoration {
    private int mDivideHeight;
    private int mPaddingLeft;
    private int mPaddingRight;
    private Paint paint;

    public DefaultItemDecoration(Context context, @DimenRes int divideHeight, @ColorRes int divideColor) {
        this(context, divideHeight, divideColor, 0, 0);
    }

    public DefaultItemDecoration(Context context, @DimenRes int divideHeight, @ColorRes int divideColor, @DimenRes int paddingLeft, @DimenRes int paddingRight) {
        mDivideHeight = context.getResources().getDimensionPixelSize(divideHeight);
        if (paddingRight != 0) {
            mPaddingRight = context.getResources().getDimensionPixelSize(paddingRight);
        }

        if (paddingLeft != 0) {
            mPaddingLeft = context.getResources().getDimensionPixelSize(paddingLeft);
        }

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(context.getResources().getColor(divideColor));
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        int childCount = parent.getChildCount();
        int width = parent.getWidth();

        int top;
        int bottom;
        for (int i = 0; i < childCount; i++) {
            View childAt = parent.getChildAt(i);
            top = childAt.getBottom();
            bottom = top + mDivideHeight;
            c.drawRect(mPaddingLeft, top, width - mPaddingRight, bottom, paint);
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.bottom = mDivideHeight;
    }
}

