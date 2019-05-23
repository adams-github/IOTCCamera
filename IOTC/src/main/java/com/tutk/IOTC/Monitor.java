//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.tutk.IOTC;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.GestureDetector.OnGestureListener;
import android.view.SurfaceHolder.Callback;
import android.view.View.OnTouchListener;
import com.tutk.IOTC.AVIOCTRLDEFs.SMsgAVIoctrlPtzCmd;
import java.util.Collections;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@SuppressLint({"NewApi"})
public class Monitor extends SurfaceView implements OnGestureListener, Callback, OnTouchListener, IRegisterIOTCListener {
    /**
     *  渲染图像的paint
     */
    private Paint mPaint = new Paint();
    // 设置的屏幕宽高
    private int mScreenWidth = 0;
    private int mScreenHeight = 0;
    private int e = 0;
    private int f = 0;
    private Rect mScreenRect = new Rect();
    // 手指触碰屏幕的数目
    private int mNumPointTouch = 0;
    private PointF mCurrentPointF = new PointF();
    private PointF j = new PointF();
    private PointF k = new PointF();
    /**
     *  两个手指触点之间的距离
     */
    private float newDistIn2Point = 0.0F;
    /**
     *  进行scale操作的当前时间
     */
    private long mScaleTimeMillis;
    /**
     *  放大倍数
     */
    private float mScale = 1.0F;
    /**
     *  放大的最大倍数
     */
    private float mMaxScale = 2.0F;
    private GestureDetector mGestureDetector;
    private SurfaceHolder mHolder = null;
    /**
     *  渲染bitmap的rect的left top right bottom
     */
    private int mLeftRealRect;
    private int mTopRealRect;
    private int mRightRealRect;
    private int mBottomRealRect;
    /**
     *  播放的实际图像的Rect
     */
    private Rect mRealDrawRect = new Rect();
    /**
     *  整个播放Monitor的Rect
     */
    private Rect monitorRect = new Rect();
    private Bitmap mBitmap;
    private Lock y = new ReentrantLock();
    private Camera mCamera;
    private int mAvChanel = -1;
    // 保存的最新的屏幕宽高
    private int mCacheScreenWidth = 0;
    private int mCacheScreenHeight = 0;
    private Monitor.DrawThread mDrawThread = null;
    private boolean E = false;
    private int F = 0;
    private PointF G = new PointF();
    private List<MRegisterMonitiorListener> mRegisterMonitiorListenerList = Collections.synchronizedList(new Vector());
    int a = 100;
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                Monitor.this.E = true;
            } else if (msg.what == 1) {
                Monitor.this.E = false;
                if (Monitor.this.F <= 1) {
                    for(int i = 0; i < Monitor.this.mRegisterMonitiorListenerList.size(); ++i) {
                        MRegisterMonitiorListener listener = (MRegisterMonitiorListener)Monitor.this.mRegisterMonitiorListenerList.get(i);
                        listener.receiveMonitorInfo(1, 0, 1);
                    }
                } else {
                    Monitor.this.F = 0;
                }
            }

        }
    };

    public Monitor(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mHolder = this.getHolder();
        this.mHolder.addCallback(this);
        this.mGestureDetector = new GestureDetector(this);
        this.setOnTouchListener(this);
        this.setLongClickable(true);
    }

    public void setMaxZoom(float value) {
        this.mMaxScale = value;
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        synchronized(this) {
            this.monitorRect.set(0, 0, width, height);
            this.mRealDrawRect.set(0, 0, width, height);
            if (this.mCacheScreenWidth != 0 && this.mCacheScreenHeight != 0) {
                double ratio;
                if (this.monitorRect.bottom - this.monitorRect.top < this.monitorRect.right - this.monitorRect.left) {
                    MLog.i("IOTCamera", "Landscape layout");
                    ratio = (double)this.mCacheScreenWidth / (double)this.mCacheScreenHeight;
                    this.mRealDrawRect.right = (int)((double)this.monitorRect.bottom * ratio);
                    this.mRealDrawRect.offset((this.monitorRect.right - this.mRealDrawRect.right) / 2, 0);
                } else {
                    MLog.i("IOTCamera", "Portrait layout");
                    ratio = (double)this.mCacheScreenWidth / (double)this.mCacheScreenHeight;
                    this.mRealDrawRect.bottom = (int)((double)this.monitorRect.right / ratio);
                    this.mRealDrawRect.offset(0, (this.monitorRect.bottom - this.mRealDrawRect.bottom) / 2);
                }
            } else if (height < width) {
                this.mRealDrawRect.right = 4 * height / 3;
                this.mRealDrawRect.offset((width - this.mRealDrawRect.right) / 2, 0);
            } else {
                this.mRealDrawRect.bottom = 3 * width / 4;
                this.mRealDrawRect.offset(0, (height - this.mRealDrawRect.bottom) / 2);
            }

            this.mLeftRealRect = this.mRealDrawRect.left;
            this.mTopRealRect = this.mRealDrawRect.top;
            this.mRightRealRect = this.mRealDrawRect.right;
            this.mBottomRealRect = this.mRealDrawRect.bottom;
            this.mScale = 1.0F;
            this.setPointInCenterRect(this.j, (float)this.mLeftRealRect, (float)this.mTopRealRect, (float)this.mRightRealRect, (float)this.mBottomRealRect);
            this.setPointInCenterRect(this.k, (float)this.mLeftRealRect, (float)this.mTopRealRect, (float)this.mRightRealRect, (float)this.mBottomRealRect);
        }
    }

    public void surfaceCreated(SurfaceHolder holder) {
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    public void attachCamera(Camera camera, int avChannel) {
        this.mCamera = camera;
        this.mCamera.registerIOTCListener(this);
        this.mAvChanel = avChannel;
        if (this.mDrawThread == null) {
            this.mDrawThread = new Monitor.DrawThread();
            this.mDrawThread.start();
        }

    }

    public void deattachCamera() {
        this.mAvChanel = -1;
        if (this.mCamera != null) {
            this.mCamera.unregisterIOTCListener(this);
            this.mCamera = null;
        }

        if (this.mDrawThread != null) {
            this.mDrawThread.stopDraw();

            try {
                this.mDrawThread.join();
            } catch (InterruptedException var2) {
                var2.printStackTrace();
            }

            this.mDrawThread = null;
        }

    }

    public void receiveFrameData(Camera camera, int avChannel, Bitmap bmp) {
        if (this.mAvChanel == avChannel) {
            this.mBitmap = bmp;
            if (bmp.getWidth() > 0 && bmp.getHeight() > 0 && (bmp.getWidth() != this.mCacheScreenWidth || bmp.getHeight() != this.mCacheScreenHeight)) {
                this.mCacheScreenWidth = this.mScreenWidth;
                this.mCacheScreenHeight = this.mScreenHeight;
                this.mRealDrawRect.set(0, 0, this.monitorRect.right, this.monitorRect.bottom);
                double ratio;
                if (this.monitorRect.bottom - this.monitorRect.top < this.monitorRect.right - this.monitorRect.left) {
                    MLog.i("IOTCamera", "Landscape layout");
                    ratio = (double)this.mCacheScreenWidth / (double)this.mCacheScreenHeight;
                    this.mRealDrawRect.right = (int)((double)this.monitorRect.bottom * ratio);
                    this.mRealDrawRect.offset((this.monitorRect.right - this.mRealDrawRect.right) / 2, 0);
                } else {
                    MLog.i("IOTCamera", "Portrait layout");
                    ratio = (double)this.mCacheScreenWidth / (double)this.mCacheScreenHeight;
                    this.mRealDrawRect.bottom = (int)((double)this.monitorRect.right / ratio);
                    this.mRealDrawRect.offset(0, (this.monitorRect.bottom - this.mRealDrawRect.bottom) / 2);
                }

                this.mLeftRealRect = this.mRealDrawRect.left;
                this.mTopRealRect = this.mRealDrawRect.top;
                this.mRightRealRect = this.mRealDrawRect.right;
                this.mBottomRealRect = this.mRealDrawRect.bottom;
                this.mScale = 1.0F;
                this.setPointInCenterRect(this.j, (float)this.mLeftRealRect, (float)this.mTopRealRect, (float)this.mRightRealRect, (float)this.mBottomRealRect);
                this.setPointInCenterRect(this.k, (float)this.mLeftRealRect, (float)this.mTopRealRect, (float)this.mRightRealRect, (float)this.mBottomRealRect);
                MLog.i("IOTCamera", "Change canvas size (" + (this.mRealDrawRect.right - this.mRealDrawRect.left) + ", " + (this.mRealDrawRect.bottom - this.mRealDrawRect.top) + ")");
            }
        }

    }

    public void receiveFrameInfo(Camera camera, int sessionChannel, long bitRate, int frameRate, int onlineNm, int frameCount, int incompleteFrameCount) {
    }

    public void receiveChannelInfo(Camera camera, int sessionChannel, int resultCode) {
    }

    public void receiveSessionInfo(Camera camera, int resultCode) {
    }

    public void receiveIOCtrlData(Camera camera, int sessionChannel, int avIOCtrlMsgType, byte[] data) {
    }

    @SuppressLint({"NewApi"})
    public boolean onTouch(View view, MotionEvent event) {
        this.mGestureDetector.onTouchEvent(event);
        switch(event.getAction() & 0xff) {
            case MotionEvent.ACTION_DOWN:
                if (this.mRealDrawRect.left != this.mLeftRealRect || this.mRealDrawRect.top != this.mTopRealRect || this.mRealDrawRect.right != this.mRightRealRect || this.mRealDrawRect.bottom != this.mBottomRealRect) {
                    this.mNumPointTouch = 1;
                    this.mCurrentPointF.set(event.getX(), event.getY());
                }

                this.G.set(event.getX(), event.getY());
                this.handler.sendEmptyMessage(0);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                if (this.mScale == 1.0F) {
                    this.mNumPointTouch = 0;
                }

                if (Math.abs(event.getX() - this.G.x) < 20.0F && Math.abs(event.getY() - this.G.y) < 20.0F) {
                    this.handler.sendEmptyMessage(1);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                int maxWidth;
                int scaledWidth;
                int scaledHeight;
                if (this.mNumPointTouch == 1) {
                    if (System.currentTimeMillis() - this.mScaleTimeMillis < 33L) {
                        return true;
                    }

                    PointF currentPoint = new PointF();
                    currentPoint.set(event.getX(), event.getY());
                    int offsetX = (int)currentPoint.x - (int)this.mCurrentPointF.x;
                    maxWidth = (int)currentPoint.y - (int)this.mCurrentPointF.y;
                    this.mCurrentPointF = currentPoint;
                    Rect rect = new Rect();
                    rect.set(this.mRealDrawRect);
                    rect.offset(offsetX, maxWidth);
                    scaledWidth = rect.right - rect.left;
                    scaledHeight = rect.bottom - rect.top;
                    if (this.monitorRect.bottom - this.monitorRect.top > this.monitorRect.right - this.monitorRect.left) {
                        if (rect.left > this.monitorRect.left) {
                            rect.left = this.monitorRect.left;
                            rect.right = rect.left + scaledWidth;
                        }

                        if (rect.top > this.monitorRect.top) {
                            rect.top = this.mRealDrawRect.top;
                            rect.bottom = rect.top + scaledHeight;
                        }

                        if (rect.right < this.monitorRect.right) {
                            rect.right = this.monitorRect.right;
                            rect.left = rect.right - scaledWidth;
                        }

                        if (rect.bottom < this.monitorRect.bottom) {
                            rect.bottom = this.mRealDrawRect.bottom;
                            rect.top = rect.bottom - scaledHeight;
                        }
                    } else {
                        if (rect.left > this.monitorRect.left) {
                            rect.left = this.mRealDrawRect.left;
                            rect.right = rect.left + scaledWidth;
                        }

                        if (rect.top > this.monitorRect.top) {
                            rect.top = this.monitorRect.top;
                            rect.bottom = rect.top + scaledHeight;
                        }

                        if (rect.right < this.monitorRect.right) {
                            rect.right = this.mRealDrawRect.right;
                            rect.left = rect.right - scaledWidth;
                        }

                        if (rect.bottom < this.monitorRect.bottom) {
                            rect.bottom = this.monitorRect.bottom;
                            rect.top = rect.bottom - scaledHeight;
                        }
                    }

                    System.out.println("offset (" + offsetX + ", " + maxWidth + "), after offset rect = (" + rect.left + ", " + rect.top + ", " + rect.right + ", " + rect.bottom + ")");
                    this.mRealDrawRect.set(rect);
                } else if (this.mNumPointTouch == 2) {
                    if (System.currentTimeMillis() - this.mScaleTimeMillis < 33L) {
                        return true;
                    }

                    if (event.getPointerCount() == 1) {
                        return true;
                    }

                    float newDist = this.getDistancePoint1ToPoint2(event);
                    float scale = newDist / this.newDistIn2Point;
                    this.mScale *= scale;
                    this.newDistIn2Point = newDist;
                    if (this.mScale > this.mMaxScale) {
                        this.mScale = this.mMaxScale;
                        return true;
                    }

                    if (this.mScale < 1.0F) {
                        this.mScale = 1.0F;
                    }

                    System.out.println("newDist(" + newDist + ") / origDist(" + this.newDistIn2Point + ") = zoom scale(" + this.mScale + ")");
                    maxWidth = (this.mRightRealRect - this.mLeftRealRect) * 3;
                    int maxHeight = (this.mBottomRealRect - this.mTopRealRect) * 3;
                    scaledWidth = (int)((float)(this.mRightRealRect - this.mLeftRealRect) * this.mScale);
                    scaledHeight = (int)((float)(this.mBottomRealRect - this.mTopRealRect) * this.mScale);
                    int origWidth = this.mRightRealRect - this.mLeftRealRect;
                    int origHeight = this.mBottomRealRect - this.mTopRealRect;
                    int l = (int)((float)(this.monitorRect.width() / 2) - (float)(this.monitorRect.width() / 2 - this.mRealDrawRect.left) * scale);
                    int t = (int)((float)(this.monitorRect.height() / 2) - (float)(this.monitorRect.height() / 2 - this.mRealDrawRect.top) * scale);
                    int r = l + scaledWidth;
                    int b = t + scaledHeight;
                    if (scaledWidth > origWidth && scaledHeight > origHeight) {
                        if (scaledWidth >= maxWidth || scaledHeight >= maxHeight) {
                            l = this.mRealDrawRect.left;
                            t = this.mRealDrawRect.top;
                            r = l + maxWidth;
                            b = t + maxHeight;
                        }
                    } else {
                        l = this.mLeftRealRect;
                        t = this.mTopRealRect;
                        r = this.mRightRealRect;
                        b = this.mBottomRealRect;
                    }

                    this.mRealDrawRect.set(l, t, r, b);
                    System.out.println("zoom -> newDistIn2Point: " + l + ", mRightRealRect: " + t + ", mLeftRealRect: " + r + ", mPaint: " + b + ",  width: " + scaledWidth + ", height: " + scaledHeight);
                    this.mScaleTimeMillis = System.currentTimeMillis();
                }
            case 3:
            case 4:
            default:
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                float dist = this.getDistancePoint1ToPoint2(event);
                if (dist > 10.0F) {
                    this.mNumPointTouch = 2;
                    this.newDistIn2Point = dist;
                    System.out.println("Action_Pointer_Down -> origDist(" + this.newDistIn2Point + ")");
                }
        }

        return false;
    }

    public boolean onDown(MotionEvent e) {
        return false;
    }

    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (this.mRealDrawRect.left == this.mLeftRealRect && this.mRealDrawRect.top == this.mTopRealRect && this.mRealDrawRect.right == this.mRightRealRect && this.mRealDrawRect.bottom == this.mBottomRealRect) {
            System.out.println("velocityX: " + Math.abs(velocityX) + ", velocityY: " + Math.abs(velocityY));
            if (e1.getX() - e2.getX() > 100.0F && Math.abs(velocityX) > 0.0F) {
                if (this.mCamera != null && this.mAvChanel >= 0) {
                    this.mCamera.sendIOCtrl(this.mAvChanel, 4097, SMsgAVIoctrlPtzCmd.parseContent((byte)6, (byte)8, (byte)0, (byte)0, (byte)0, (byte)0));
                }
            } else if (e2.getX() - e1.getX() > 100.0F && Math.abs(velocityX) > 0.0F) {
                if (this.mCamera != null && this.mAvChanel >= 0) {
                    this.mCamera.sendIOCtrl(this.mAvChanel, 4097, SMsgAVIoctrlPtzCmd.parseContent((byte)3, (byte)8, (byte)0, (byte)0, (byte)0, (byte)0));
                }
            } else if (e1.getY() - e2.getY() > 100.0F && Math.abs(velocityY) > 0.0F) {
                if (this.mCamera != null && this.mAvChanel >= 0) {
                    this.mCamera.sendIOCtrl(this.mAvChanel, 4097, SMsgAVIoctrlPtzCmd.parseContent((byte)2, (byte)8, (byte)0, (byte)0, (byte)0, (byte)0));
                }
            } else if (e2.getY() - e1.getY() > 100.0F && Math.abs(velocityY) > 0.0F && this.mCamera != null && this.mAvChanel >= 0) {
                this.mCamera.sendIOCtrl(this.mAvChanel, 4097, SMsgAVIoctrlPtzCmd.parseContent((byte)1, (byte)8, (byte)0, (byte)0, (byte)0, (byte)0));
            }

            (new Handler()).postDelayed(new Runnable() {
                public void run() {
                    if (Monitor.this.mCamera != null && Monitor.this.mAvChanel >= 0) {
                        Monitor.this.mCamera.sendIOCtrl(Monitor.this.mAvChanel, 4097, SMsgAVIoctrlPtzCmd.parseContent((byte)0, (byte)8, (byte)0, (byte)0, (byte)0, (byte)0));
                    }

                }
            }, 1500L);
            return false;
        } else {
            return false;
        }
    }

    public void onLongPress(MotionEvent e) {
    }

    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return true;
    }

    public void onShowPress(MotionEvent e) {
    }

    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    /**
     *  获取两个手指之间的距离
     * @param event
     * @return
     */
    @SuppressLint({"FloatMath"})
    private float getDistancePoint1ToPoint2(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float)Math.sqrt((double)(x * x + y * y));
    }

    /**
     *  设置point在某一个rect的中点
     * @param point
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    private void setPointInCenterRect(PointF point, float left, float top, float right, float bottom) {
        point.set((left + right) / 2.0F, (top + bottom) / 2.0F);
    }

    public void receiveExtraInfo(Camera camera, int avChannel, int eventType, int recvFrame, int dispFrame) {
    }

    public void TurnToLeftOn() {
        if (this.mCamera != null && this.mAvChanel >= 0) {
            this.mCamera.sendIOCtrl(0, 4097, SMsgAVIoctrlPtzCmd.parseContent((byte)3, (byte)8, (byte)this.a, (byte)0, (byte)0, (byte)0));
        }

    }

    public void TurnToRightOn() {
        if (this.mCamera != null && this.mAvChanel >= 0) {
            this.mCamera.sendIOCtrl(0, 4097, SMsgAVIoctrlPtzCmd.parseContent((byte)6, (byte)8, (byte)this.a, (byte)0, (byte)0, (byte)0));
        }

    }

    public void TurnToUpOn() {
        if (this.mCamera != null && this.mAvChanel >= 0) {
            this.mCamera.sendIOCtrl(0, 4097, SMsgAVIoctrlPtzCmd.parseContent((byte)1, (byte)8, (byte)this.a, (byte)0, (byte)0, (byte)0));
        }

    }

    public void TurnToDownOn() {
        if (this.mCamera != null && this.mAvChanel >= 0) {
            this.mCamera.sendIOCtrl(0, 4097, SMsgAVIoctrlPtzCmd.parseContent((byte)2, (byte)8, (byte)this.a, (byte)0, (byte)0, (byte)0));
        }

    }

    public void PtzStop() {
        if (this.mCamera != null && this.mAvChanel >= 0) {
            this.mCamera.sendIOCtrl(0, 4097, SMsgAVIoctrlPtzCmd.parseContent((byte)0, (byte)8, (byte)this.a, (byte)0, (byte)0, (byte)0));
        }

    }

    public boolean registerMonitor(MRegisterMonitiorListener listener) {
        boolean result = false;
        if (!this.mRegisterMonitiorListenerList.contains(listener)) {
            this.mRegisterMonitiorListenerList.add(listener);
            result = true;
        }

        return result;
    }

    public boolean unregisterMonitor(MRegisterMonitiorListener listener) {
        boolean result = false;
        if (!this.mRegisterMonitiorListenerList.contains(listener)) {
            this.mRegisterMonitiorListenerList.remove(listener);
            result = true;
        }

        return result;
    }

    public void setScreenSize(int width, int height) {
        this.mScreenHeight = height;
        this.mScreenWidth = width;
        if (this.mScreenWidth == 0) {
            this.mScreenWidth = this.mCacheScreenWidth;
        }

        if (this.mScreenHeight == 0) {
            this.mScreenHeight = this.mCacheScreenHeight;
        }

        this.mScreenRect.set(0, 0, this.mScreenWidth, this.mScreenHeight);
        this.mRealDrawRect.set(0, 0, this.mScreenWidth, this.mScreenHeight);
        Log.i("IOTCamera", "setScreenSize w[" + this.mScreenWidth + "] mNumPointTouch[" + this.mScreenHeight + "]");
    }

    private class DrawThread extends Thread {
        private boolean isDrawing;
        private Object drawFlag;

        private DrawThread() {
            this.isDrawing = false;
            this.drawFlag = new Object();
        }

        public void stopDraw() {
            this.isDrawing = false;

            try {
                this.drawFlag.notify();
            } catch (Exception var2) {
                ;
            }

        }

        public void run() {
            this.isDrawing = true;
            Canvas videoCanvas = null;
            Monitor.this.mPaint.setDither(true);

            while(this.isDrawing) {
                if (Monitor.this.mBitmap != null && !Monitor.this.mBitmap.isRecycled()) {
                    try {
                        videoCanvas = Monitor.this.mHolder.lockCanvas();
                        if (videoCanvas != null && Monitor.this.mPaint != null) {
                            videoCanvas.drawColor(-16777216);
                            videoCanvas.drawBitmap(Monitor.this.mBitmap, (Rect)null, Monitor.this.mRealDrawRect, Monitor.this.mPaint);
                        }
                    } finally {
                        if (videoCanvas != null) {
                            Monitor.this.mHolder.unlockCanvasAndPost(videoCanvas);
                        }

                        videoCanvas = null;
                    }
                }

                try {
                    Object var2 = this.drawFlag;
                    synchronized(this.drawFlag) {
                        this.drawFlag.wait(33L);
                    }
                } catch (InterruptedException var7) {
                    var7.printStackTrace();
                }
            }

            System.out.println("===ThreadRender exit===");
        }
    }
}
