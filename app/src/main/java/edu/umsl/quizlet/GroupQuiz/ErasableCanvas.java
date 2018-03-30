package edu.umsl.quizlet.GroupQuiz;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.icu.text.DateFormat;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import edu.umsl.quizlet.R;

/**
 * Created by landon on 5/4/17.
 */

public class ErasableCanvas extends View {
    private static String EXTRA_STATE = "instance_state";
    private static String EXTRA_PATH_LIST = "path_list";
    public int width;
    public  int height;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Path mPath;
    private Paint mBitmapPaint;
    private Paint backPaint;
    Context context;
    private Paint mPaint;
    private boolean mErased;
    private ArrayList<Path> mPaths;
    private WeakReference<OnCustomClickListener> mClickListener;
    private boolean touchDisabled;

    public interface OnCustomClickListener {
        void OnClick(View v);
    }

    public ErasableCanvas(Context c) {
        super(c);

        mPaths = new ArrayList<>();

        setSaveEnabled(true);

        mErased = false;
        context=c;
        width = getMeasuredWidth();
        height = getMeasuredHeight();

        backPaint = new Paint();
        backPaint.setColor(0xaaaaaa);

        mPath = new Path();
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);

        mPaint = new Paint();
        mPaint.setColor(0xFFFFFFFF);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(getContext().getResources().getDimensionPixelSize(R.dimen.group_quiz_canvas_stroke_width));
    }

    public ErasableCanvas(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        mPaths = new ArrayList<>();

        setSaveEnabled(true);

        mErased = false;
        context=context;
        width = getMeasuredWidth();
        height = getMeasuredHeight();

        backPaint = new Paint();
        backPaint.setColor(0xaaaaaa);

        mPath = new Path();
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);

        mPaint = new Paint();
        mPaint.setColor(0xFFFFFFFF);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(getContext().getResources().getDimensionPixelSize(R.dimen.group_quiz_canvas_stroke_width));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (!mErased) {
            mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            mBitmap.eraseColor(Color.TRANSPARENT);
            mCanvas = new Canvas(mBitmap);
            mCanvas.drawColor(Color.rgb(200, 200, 200));
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawBitmap( mBitmap, 0, 0, mBitmapPaint);
        canvas.drawPath( mPath,  mPaint);
    }

    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;

    private void touch_start(float x, float y) {
        mErased = true;
        mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
    }

    private void touch_move(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
            mX = x;
            mY = y;
        }
    }

    private void touch_up() {
        mPath.lineTo(mX, mY);
        // commit the path to our offscreen
        mPaths.add(new Path(mPath));
        mCanvas.drawPath(mPath,  mPaint);
        // kill this so we don't double draw
        mPath.reset();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!touchDisabled) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                    getParent().getParent().requestDisallowInterceptTouchEvent(true);
                    getParent().getParent().getParent().requestDisallowInterceptTouchEvent(true);
                    touch_start(x, y);
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (!touchDisabled) {
                    touch_move(x, y);
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                if (!touchDisabled) {
                    touch_up();
                    invalidate();
                    getParent().requestDisallowInterceptTouchEvent(false);
                    getParent().getParent().requestDisallowInterceptTouchEvent(false);
                    getParent().getParent().getParent().requestDisallowInterceptTouchEvent(false);
                    if (mClickListener != null) {
                        mClickListener.get().OnClick(this);
                    }
                }
                break;
        }
        return true;
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Log.e("ONSAVE", "Save called");
        Bundle bundle = new Bundle();
        bundle.putParcelable(EXTRA_STATE, super.onSaveInstanceState());
        bundle.putSerializable(EXTRA_PATH_LIST, mPaths);
        return super.onSaveInstanceState();
    }



    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        Log.e("ONRESTORE", "Restore called");
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            super.onRestoreInstanceState(bundle.getParcelable(EXTRA_STATE));
            mPaths = (ArrayList<Path>)bundle.getSerializable(EXTRA_PATH_LIST);
            if (mPaths == null) {
                mPaths = new ArrayList<>();
            } else {
                for (Path path : mPaths) {
                    mCanvas.drawPath(mPath,  mPaint);
                }
            }
        } else {
            super.onRestoreInstanceState(state);
        }
    }

    public void setmClickListener(OnCustomClickListener mClickListener) {
        this.mClickListener = new WeakReference<OnCustomClickListener>(mClickListener);
    }

    public void setTouchDisabled(boolean touchDisabled) {
        this.touchDisabled = touchDisabled;
    }
}