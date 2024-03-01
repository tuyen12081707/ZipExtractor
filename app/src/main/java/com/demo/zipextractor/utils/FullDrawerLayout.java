package com.demo.zipextractor.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;

import androidx.drawerlayout.widget.DrawerLayout;


public class FullDrawerLayout extends DrawerLayout {
    private static final int MIN_DRAWER_MARGIN = 0;

    public FullDrawerLayout(Context context) {
        super(context);
    }

    public FullDrawerLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public FullDrawerLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    @Override
    protected void onMeasure(int i, int i2) {
        int mode = View.MeasureSpec.getMode(i);
        int mode2 = View.MeasureSpec.getMode(i2);
        int size = View.MeasureSpec.getSize(i);
        int size2 = View.MeasureSpec.getSize(i2);
        if (mode != 1073741824 || mode2 != 1073741824) {
            throw new IllegalArgumentException("DrawerLayout must be measured with MeasureSpec.EXACTLY.");
        }
        setMeasuredDimension(size, size2);
        int childCount = getChildCount();
        for (int i3 = 0; i3 < childCount; i3++) {
            View childAt = getChildAt(i3);
            if (childAt.getVisibility() != View.GONE) {
                DrawerLayout.LayoutParams layoutParams = (DrawerLayout.LayoutParams) childAt.getLayoutParams();
                if (isContentView(childAt)) {
                    childAt.measure(View.MeasureSpec.makeMeasureSpec((size - layoutParams.leftMargin) - layoutParams.rightMargin, MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec((size2 - layoutParams.topMargin) - layoutParams.bottomMargin, MeasureSpec.EXACTLY));
                } else if (isDrawerView(childAt)) {
                    getDrawerViewGravity(childAt);
                    childAt.measure(getChildMeasureSpec(i, layoutParams.leftMargin + 0 + layoutParams.rightMargin, layoutParams.width), getChildMeasureSpec(i2, layoutParams.topMargin + layoutParams.bottomMargin, layoutParams.height));
                } else {
                    throw new IllegalStateException("Child " + childAt + " at index " + i3 + " does not have a valid layout_gravity - must be Gravity.LEFT, Gravity.RIGHT or Gravity.NO_GRAVITY");
                }
            }
        }
    }

    boolean isContentView(View view) {
        return ((DrawerLayout.LayoutParams) view.getLayoutParams()).gravity == 0;
    }

    boolean isDrawerView(View view) {
        return (Gravity.getAbsoluteGravity(((DrawerLayout.LayoutParams) view.getLayoutParams()).gravity, view.getLayoutDirection()) & 7) != 0;
    }

    int getDrawerViewGravity(View view) {
        return Gravity.getAbsoluteGravity(((DrawerLayout.LayoutParams) view.getLayoutParams()).gravity, view.getLayoutDirection());
    }

    static String gravityToString(int i) {
        return (i & 3) == 3 ? "LEFT" : (i & 5) == 5 ? "RIGHT" : Integer.toHexString(i);
    }
}
