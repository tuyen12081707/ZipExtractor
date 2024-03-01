package com.demo.zipextractor.utils;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;


public class TouchImageView extends ImageView implements GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {
    static final int CLICK = 3;
    static final int DRAG = 1;
    static final int NONE = 0;
    static final int ZOOM = 2;
    Context context;
    PointF last;
    float[] m;
    GestureDetector mGestureDetector;
    ScaleGestureDetector mScaleDetector;
    Matrix matrix;
    float maxScale;
    float minScale;
    int mode;
    int oldMeasuredHeight;
    int oldMeasuredWidth;
    protected float origHeight;
    protected float origWidth;
    float saveScale;
    PointF start;
    int viewHeight;
    int viewWidth;

    float getFixDragTrans(float f, float f2, float f3) {
        if (f3 <= f2) {
            return 0.0f;
        }
        return f;
    }

    float getFixTrans(float f, float f2, float f3) {
        float f4;
        float f5;
        if (f3 <= f2) {
            f5 = f2 - f3;
            f4 = 0.0f;
        } else {
            f4 = f2 - f3;
            f5 = 0.0f;
        }
        if (f < f4) {
            return (-f) + f4;
        }
        if (f > f5) {
            return (-f) + f5;
        }
        return 0.0f;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    public TouchImageView(Context context) {
        super(context);
        this.mode = 0;
        this.last = new PointF();
        this.start = new PointF();
        this.minScale = 1.0f;
        this.maxScale = 3.0f;
        this.saveScale = 1.0f;
        sharedConstructing(context);
    }

    public TouchImageView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mode = 0;
        this.last = new PointF();
        this.start = new PointF();
        this.minScale = 1.0f;
        this.maxScale = 3.0f;
        this.saveScale = 1.0f;
        sharedConstructing(context);
    }

    private void sharedConstructing(Context context) {
        super.setClickable(true);
        this.context = context;
        GestureDetector gestureDetector = new GestureDetector(context, this);
        this.mGestureDetector = gestureDetector;
        gestureDetector.setOnDoubleTapListener(this);
        this.mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        Matrix matrix = new Matrix();
        this.matrix = matrix;
        this.m = new float[9];
        setImageMatrix(matrix);
        setScaleType(ImageView.ScaleType.MATRIX);
        setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                TouchImageView.this.mScaleDetector.onTouchEvent(motionEvent);
                TouchImageView.this.mGestureDetector.onTouchEvent(motionEvent);
                PointF pointF = new PointF(motionEvent.getX(), motionEvent.getY());
                int action = motionEvent.getAction();
                if (action == 0) {
                    TouchImageView.this.last.set(pointF);
                    TouchImageView.this.start.set(TouchImageView.this.last);
                    TouchImageView.this.mode = 1;
                } else if (action == 1) {
                    TouchImageView.this.mode = 0;
                    int abs = (int) Math.abs(pointF.x - TouchImageView.this.start.x);
                    int abs2 = (int) Math.abs(pointF.y - TouchImageView.this.start.y);
                    if (abs < 3 && abs2 < 3) {
                        TouchImageView.this.performClick();
                    }
                } else if (action != 2) {
                    if (action == 6) {
                        TouchImageView.this.mode = 0;
                    }
                } else if (TouchImageView.this.mode == 1) {
                    float f = pointF.x - TouchImageView.this.last.x;
                    float f2 = pointF.y - TouchImageView.this.last.y;
                    TouchImageView touchImageView = TouchImageView.this;
                    float fixDragTrans = touchImageView.getFixDragTrans(f, touchImageView.viewWidth, TouchImageView.this.origWidth * TouchImageView.this.saveScale);
                    TouchImageView touchImageView2 = TouchImageView.this;
                    TouchImageView.this.matrix.postTranslate(fixDragTrans, touchImageView2.getFixDragTrans(f2, touchImageView2.viewHeight, TouchImageView.this.origHeight * TouchImageView.this.saveScale));
                    TouchImageView.this.fixTrans();
                    TouchImageView.this.last.set(pointF.x, pointF.y);
                }
                TouchImageView touchImageView3 = TouchImageView.this;
                touchImageView3.setImageMatrix(touchImageView3.matrix);
                TouchImageView.this.invalidate();
                return true;
            }
        });
    }

    public void setMaxZoom(float f) {
        this.maxScale = f;
    }

    @Override
    public boolean onDoubleTap(MotionEvent motionEvent) {
        Log.i("MAIN_TAG", "Double tap detected");
        float f = this.saveScale;
        float f2 = this.maxScale;
        if (f == f2) {
            f2 = this.minScale;
            this.saveScale = f2;
        } else {
            this.saveScale = f2;
        }
        float f3 = f2 / f;
        this.matrix.postScale(f3, f3, this.viewWidth / 2, this.viewHeight / 2);
        fixTrans();
        return false;
    }


    public class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        private ScaleListener() {
        }

        @Override

        public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {
            TouchImageView.this.mode = 2;
            return true;
        }

        @Override

        public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
            float f;
            float scaleFactor = scaleGestureDetector.getScaleFactor();
            float f2 = TouchImageView.this.saveScale;
            TouchImageView.this.saveScale *= scaleFactor;
            if (TouchImageView.this.saveScale > TouchImageView.this.maxScale) {
                TouchImageView touchImageView = TouchImageView.this;
                touchImageView.saveScale = touchImageView.maxScale;
                f = TouchImageView.this.maxScale;
            } else {
                if (TouchImageView.this.saveScale < TouchImageView.this.minScale) {
                    TouchImageView touchImageView2 = TouchImageView.this;
                    touchImageView2.saveScale = touchImageView2.minScale;
                    f = TouchImageView.this.minScale;
                }
                if (TouchImageView.this.origWidth * TouchImageView.this.saveScale > TouchImageView.this.viewWidth || TouchImageView.this.origHeight * TouchImageView.this.saveScale <= TouchImageView.this.viewHeight) {
                    TouchImageView.this.matrix.postScale(scaleFactor, scaleFactor, TouchImageView.this.viewWidth / 2, TouchImageView.this.viewHeight / 2);
                } else {
                    TouchImageView.this.matrix.postScale(scaleFactor, scaleFactor, scaleGestureDetector.getFocusX(), scaleGestureDetector.getFocusY());
                }
                TouchImageView.this.fixTrans();
                return true;
            }
            scaleFactor = f / f2;
            if (TouchImageView.this.origWidth * TouchImageView.this.saveScale > TouchImageView.this.viewWidth) {
            }
            TouchImageView.this.matrix.postScale(scaleFactor, scaleFactor, TouchImageView.this.viewWidth / 2, TouchImageView.this.viewHeight / 2);
            TouchImageView.this.fixTrans();
            return true;
        }
    }

    void fixTrans() {
        this.matrix.getValues(this.m);
        float[] fArr = this.m;
        float f = fArr[2];
        float f2 = fArr[5];
        float fixTrans = getFixTrans(f, this.viewWidth, this.origWidth * this.saveScale);
        float fixTrans2 = getFixTrans(f2, this.viewHeight, this.origHeight * this.saveScale);
        if (fixTrans == 0.0f && fixTrans2 == 0.0f) {
            return;
        }
        this.matrix.postTranslate(fixTrans, fixTrans2);
    }

    @Override
    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        this.viewWidth = View.MeasureSpec.getSize(i);
        int size = View.MeasureSpec.getSize(i2);
        this.viewHeight = size;
        int i3 = this.oldMeasuredHeight;
        int i4 = this.viewWidth;
        if ((i3 == i4 && i3 == size) || i4 == 0 || size == 0) {
            return;
        }
        this.oldMeasuredHeight = size;
        this.oldMeasuredWidth = i4;
        if (this.saveScale == 1.0f) {
            Drawable drawable = getDrawable();
            if (drawable == null || drawable.getIntrinsicWidth() == 0 || drawable.getIntrinsicHeight() == 0) {
                return;
            }
            int intrinsicWidth = drawable.getIntrinsicWidth();
            int intrinsicHeight = drawable.getIntrinsicHeight();
            Log.d("bmSize", "bmWidth: " + intrinsicWidth + " bmHeight : " + intrinsicHeight);
            float f = intrinsicWidth;
            float f2 = intrinsicHeight;
            float min = Math.min(this.viewWidth / f, this.viewHeight / f2);
            this.matrix.setScale(min, min);
            float f3 = (this.viewHeight - (f2 * min)) / 2.0f;
            float f4 = (this.viewWidth - (min * f)) / 2.0f;
            this.matrix.postTranslate(f4, f3);
            this.origWidth = this.viewWidth - (f4 * 2.0f);
            this.origHeight = this.viewHeight - (f3 * 2.0f);
            setImageMatrix(this.matrix);
        }
        fixTrans();
    }
}
