package com.example.rlakkh.pdr;

/**
 * Created by RLAKKH on 2018-04-27.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import static com.example.rlakkh.pdr.MainActivity.display;
/**
 * Created by RLAKKH on 2018-02-24.
 */
public class MyView extends View {
    static float interset = 22.82f;
    Canvas cvs;
    Bitmap bm;
    Bitmap bitmap;

    String str1 = "";
    String str2 = "";
    int stepcount = 0;

    Path path = new Path();
    boolean isFirstTouch = true;
    boolean isInit = false;
    float lastpointx = 0.0f, lastpointy = 0.0f;
    float px = 0, py = 0;
    float tmpx = 0, tmpy = 0;
    float xDistOld = 1, xDistNew = 1;
    float yDistOld = 1, yDistNew = 1;
    float scalefactor = 1.0f;
    float wscale = 1.0f, hscale = 1.0f;
    float wScale = 1.0f, hScale = 1.0f;
    float pxtmp = 0.0f, pytmp = 0.0f;
    float initX, initY;
    float pixelX = 0,pixelY = 0;
    int wid, hei;

    public MyView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyView(Context context) {
        super(context);
    }

    public void stepCount(int step) {
        stepcount = step;
    }

    public void scaleFactor(float factor) {
        scalefactor = factor;
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            tmpx = event.getX();
            tmpy = event.getY();
            return true;
        }

        if (event.getAction() == MotionEvent.ACTION_UP) {
            pxtmp = px;
            pytmp = py;
        }

        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            if (event.getPointerCount() == 1) {
                px = pxtmp + tmpx - event.getX();
                py = pytmp + tmpy - event.getY();
                if (px - (wid * wScale / 2) <= 0) {
                    px = (wid * wScale / 2);
                }
                if (py - (hei * wScale / 2) <= 0) {
                    py = (hei * wScale / 2);
                }
                if ((wid * wScale / 2) + px >= wid) {
                    px = wid - (wid * wScale / 2);
                }
                if ((hei * wScale / 2) + py >= hei) {
                    py = hei - (hei * wScale / 2);
                }
                invalidate();
            }
            if (event.getPointerCount() == 2) {
                if (isFirstTouch) {
                    xDistOld = Math.abs(event.getX(1) - event.getX(0));
                    yDistOld = Math.abs(event.getY(1) - event.getY(0));
                    isFirstTouch = false;
                } else {
                    xDistNew = Math.abs(event.getX(1) - event.getX(0));
                    yDistNew = Math.abs(event.getY(1) - event.getY(0));
                    wScale = wscale * xDistOld / xDistNew;
                    //hScale = hscale * yDistOld / yDistNew;
                    if (wScale < 0.05f) {
                        wScale = 0.05f;
                    }
                    if (wScale > 1.0f) {
                        wScale = 1.0f;
                    }
                    if (hScale < 0.05f) {
                        hScale = 0.05f;
                    }
                    if (hScale > 1.0f) {
                        hScale = 1.0f;
                    }
                }
                invalidate();
            }

            return true;
        }

        if (event.getAction() == MotionEvent.ACTION_POINTER_UP) {
            wscale = wScale;
            hscale = hScale;

            isFirstTouch = true;
        }

        return false;
    }

    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        final float interset = 90 / display;
        final int offsetx = 10, offsety = 10;

        Paint pnt = new Paint();
        pnt.setAntiAlias(true);

        if (bitmap != null) {
            if (isInit == false) {
                wid = bitmap.getWidth();
                hei = bitmap.getHeight();

                px = (wid * wScale / 2);
                py = (hei * wScale / 2);
                pxtmp = px;
                pytmp = py;
                isInit = true;
            }
            cvs = new Canvas(bm);
            cvs.drawColor(Color.WHITE);
            cvs.drawBitmap(bitmap, 0, 0, null);
            pnt.setColor(Color.BLUE);
            pnt.setStyle(Paint.Style.STROKE);
            cvs.drawPath(path, pnt);

            canvas.drawBitmap(bm, new Rect((int) px - (int) (wid * wScale / 2), (int) py - (int) (hei * wScale / 2),
                            (int) (wid * wScale / 2) + (int) px, (int) (hei * wScale / 2) + (int) py),
                    new Rect(offsetx, offsety, getWidth() - offsetx, getWidth() - offsety), pnt);
        }
        //*
        pnt.setColor(Color.BLACK);
        pnt.setTextSize(45.0f / display);
        canvas.drawText(str1, 1, 45 / display, pnt);
        canvas.drawText(str2, 1, 100 / display, pnt);
        /**/
        pnt.setStyle(Paint.Style.STROKE);
        canvas.drawRect(offsetx, offsety, getWidth() - offsetx, getWidth() - offsety, pnt);
        pnt.setColor(Color.GRAY);

        for (int i = 1; i < 10; i++) {
            canvas.drawLine(offsetx, (i * getWidth() - 2 * offsety) / 10, getWidth() - offsetx, (i * getWidth() - 2 * offsety) / 10, pnt);
            canvas.drawLine((i * getWidth() - 2 * offsetx) / 10, offsety, (i * getWidth() - 2 * offsetx) / 10, getWidth() - offsety, pnt);
        }

        pnt.setColor(Color.BLUE);
        pnt.setStyle(Paint.Style.FILL);
        canvas.drawCircle(0.5f * getWidth(), 0.5f * getWidth(), 10 / display, pnt);
    }

    public void getBitmap(Bitmap map) {
        bitmap = map;
        bm = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.RGB_565);
        isInit = false;
    }

    public void getString1(String s) {
        str1 = s;
    }

    public void getString2(String s) {
        str2 = s;
    }

    public void setPosition(){
        initX = (px - (wid * wScale / 2) + (wid * wScale / 2) + px)/2;
        initY = (py - (hei * wScale / 2) + (hei * wScale / 2) + py)/2;
        wScale=1;
        wscale=1;
        px = wid * wScale / 2;
        py = hei * wScale / 2;
        path.moveTo(initX,initY);
        pixelX = initX;
        pixelY = initY;
    }

    public void getPosition(float x, float y) {

        if (path.isEmpty()) {
            initX = (px - (wid * wScale / 2) + (wid * wScale / 2) + px)/2;
            initY = (py - (hei * wScale / 2) + (hei * wScale / 2) + py)/2;

            path.moveTo(initX, initY);
        }

        path.lineTo(initX - x * interset, initY - y * interset);
        pixelX = initX - x * interset;
        pixelY = initY - y * interset;
    }

    public float[] getPixelPosition(){
        float[] tmp = {0.0f,0.0f};
        tmp[0] = pixelX;
        tmp[1] = pixelY;
        return tmp;
    }
}