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
    private Paint b = new Paint();
    private int c = 0;
    private int d = 0;
    private int e = 0;
    private int f = 0;
    private Rect g = new Rect();
    private int h = 0;
    private PointF i = new PointF();
    private PointF j = new PointF();
    private PointF k = new PointF();
    private float l = 0.0F;
    private long m;
    private float n = 1.0F;
    private float o = 2.0F;
    private GestureDetector p;
    private SurfaceHolder q = null;
    private int r;
    private int s;
    private int t;
    private int u;
    private Rect v = new Rect();
    private Rect w = new Rect();
    private Bitmap x;
    private Lock y = new ReentrantLock();
    private Camera z;
    private int A = -1;
    private int B = 0;
    private int C = 0;
    private Monitor.a D = null;
    private boolean E = false;
    private int F = 0;
    private PointF G = new PointF();
    private List<MRegisterMonitiorListener> H = Collections.synchronizedList(new Vector());
    int a = 100;
    private Handler I = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                Monitor.this.E = true;
            } else if (msg.what == 1) {
                Monitor.this.E = false;
                if (Monitor.this.F <= 1) {
                    for(int i = 0; i < Monitor.this.H.size(); ++i) {
                        MRegisterMonitiorListener listener = (MRegisterMonitiorListener)Monitor.this.H.get(i);
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
        this.q = this.getHolder();
        this.q.addCallback(this);
        this.p = new GestureDetector(this);
        this.setOnTouchListener(this);
        this.setLongClickable(true);
    }

    public void setMaxZoom(float value) {
        this.o = value;
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        synchronized(this) {
            this.w.set(0, 0, width, height);
            this.v.set(0, 0, width, height);
            if (this.B != 0 && this.C != 0) {
                double ratio;
                if (this.w.bottom - this.w.top < this.w.right - this.w.left) {
                    MLog.i("IOTCamera", "Landscape layout");
                    ratio = (double)this.B / (double)this.C;
                    this.v.right = (int)((double)this.w.bottom * ratio);
                    this.v.offset((this.w.right - this.v.right) / 2, 0);
                } else {
                    MLog.i("IOTCamera", "Portrait layout");
                    ratio = (double)this.B / (double)this.C;
                    this.v.bottom = (int)((double)this.w.right / ratio);
                    this.v.offset(0, (this.w.bottom - this.v.bottom) / 2);
                }
            } else if (height < width) {
                this.v.right = 4 * height / 3;
                this.v.offset((width - this.v.right) / 2, 0);
            } else {
                this.v.bottom = 3 * width / 4;
                this.v.offset(0, (height - this.v.bottom) / 2);
            }

            this.r = this.v.left;
            this.s = this.v.top;
            this.t = this.v.right;
            this.u = this.v.bottom;
            this.n = 1.0F;
            this.a(this.j, (float)this.r, (float)this.s, (float)this.t, (float)this.u);
            this.a(this.k, (float)this.r, (float)this.s, (float)this.t, (float)this.u);
        }
    }

    public void surfaceCreated(SurfaceHolder holder) {
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    public void attachCamera(Camera camera, int avChannel) {
        this.z = camera;
        this.z.registerIOTCListener(this);
        this.A = avChannel;
        if (this.D == null) {
            this.D = new Monitor.a((Monitor.a)null);
            this.D.start();
        }

    }

    public void deattachCamera() {
        this.A = -1;
        if (this.z != null) {
            this.z.unregisterIOTCListener(this);
            this.z = null;
        }

        if (this.D != null) {
            this.D.a();

            try {
                this.D.join();
            } catch (InterruptedException var2) {
                var2.printStackTrace();
            }

            this.D = null;
        }

    }

    public void receiveFrameData(Camera camera, int avChannel, Bitmap bmp) {
        if (this.A == avChannel) {
            this.x = bmp;
            if (bmp.getWidth() > 0 && bmp.getHeight() > 0 && (bmp.getWidth() != this.B || bmp.getHeight() != this.C)) {
                this.B = this.c;
                this.C = this.d;
                this.v.set(0, 0, this.w.right, this.w.bottom);
                double ratio;
                if (this.w.bottom - this.w.top < this.w.right - this.w.left) {
                    MLog.i("IOTCamera", "Landscape layout");
                    ratio = (double)this.B / (double)this.C;
                    this.v.right = (int)((double)this.w.bottom * ratio);
                    this.v.offset((this.w.right - this.v.right) / 2, 0);
                } else {
                    MLog.i("IOTCamera", "Portrait layout");
                    ratio = (double)this.B / (double)this.C;
                    this.v.bottom = (int)((double)this.w.right / ratio);
                    this.v.offset(0, (this.w.bottom - this.v.bottom) / 2);
                }

                this.r = this.v.left;
                this.s = this.v.top;
                this.t = this.v.right;
                this.u = this.v.bottom;
                this.n = 1.0F;
                this.a(this.j, (float)this.r, (float)this.s, (float)this.t, (float)this.u);
                this.a(this.k, (float)this.r, (float)this.s, (float)this.t, (float)this.u);
                MLog.i("IOTCamera", "Change canvas size (" + (this.v.right - this.v.left) + ", " + (this.v.bottom - this.v.top) + ")");
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
        this.p.onTouchEvent(event);
        switch(event.getAction() & 255) {
            case 0:
                if (this.v.left != this.r || this.v.top != this.s || this.v.right != this.t || this.v.bottom != this.u) {
                    this.h = 1;
                    this.i.set(event.getX(), event.getY());
                }

                this.G.set(event.getX(), event.getY());
                this.I.sendEmptyMessage(0);
                break;
            case 1:
            case 6:
                if (this.n == 1.0F) {
                    this.h = 0;
                }

                if (Math.abs(event.getX() - this.G.x) < 20.0F && Math.abs(event.getY() - this.G.y) < 20.0F) {
                    this.I.sendEmptyMessage(1);
                }
                break;
            case 2:
                int maxWidth;
                int scaledWidth;
                int scaledHeight;
                if (this.h == 1) {
                    if (System.currentTimeMillis() - this.m < 33L) {
                        return true;
                    }

                    PointF currentPoint = new PointF();
                    currentPoint.set(event.getX(), event.getY());
                    int offsetX = (int)currentPoint.x - (int)this.i.x;
                    maxWidth = (int)currentPoint.y - (int)this.i.y;
                    this.i = currentPoint;
                    Rect rect = new Rect();
                    rect.set(this.v);
                    rect.offset(offsetX, maxWidth);
                    scaledWidth = rect.right - rect.left;
                    scaledHeight = rect.bottom - rect.top;
                    if (this.w.bottom - this.w.top > this.w.right - this.w.left) {
                        if (rect.left > this.w.left) {
                            rect.left = this.w.left;
                            rect.right = rect.left + scaledWidth;
                        }

                        if (rect.top > this.w.top) {
                            rect.top = this.v.top;
                            rect.bottom = rect.top + scaledHeight;
                        }

                        if (rect.right < this.w.right) {
                            rect.right = this.w.right;
                            rect.left = rect.right - scaledWidth;
                        }

                        if (rect.bottom < this.w.bottom) {
                            rect.bottom = this.v.bottom;
                            rect.top = rect.bottom - scaledHeight;
                        }
                    } else {
                        if (rect.left > this.w.left) {
                            rect.left = this.v.left;
                            rect.right = rect.left + scaledWidth;
                        }

                        if (rect.top > this.w.top) {
                            rect.top = this.w.top;
                            rect.bottom = rect.top + scaledHeight;
                        }

                        if (rect.right < this.w.right) {
                            rect.right = this.v.right;
                            rect.left = rect.right - scaledWidth;
                        }

                        if (rect.bottom < this.w.bottom) {
                            rect.bottom = this.w.bottom;
                            rect.top = rect.bottom - scaledHeight;
                        }
                    }

                    System.out.println("offset (" + offsetX + ", " + maxWidth + "), after offset rect = (" + rect.left + ", " + rect.top + ", " + rect.right + ", " + rect.bottom + ")");
                    this.v.set(rect);
                } else if (this.h == 2) {
                    if (System.currentTimeMillis() - this.m < 33L) {
                        return true;
                    }

                    if (event.getPointerCount() == 1) {
                        return true;
                    }

                    float newDist = this.a(event);
                    float scale = newDist / this.l;
                    this.n *= scale;
                    this.l = newDist;
                    if (this.n > this.o) {
                        this.n = this.o;
                        return true;
                    }

                    if (this.n < 1.0F) {
                        this.n = 1.0F;
                    }

                    System.out.println("newDist(" + newDist + ") / origDist(" + this.l + ") = zoom scale(" + this.n + ")");
                    maxWidth = (this.t - this.r) * 3;
                    int maxHeight = (this.u - this.s) * 3;
                    scaledWidth = (int)((float)(this.t - this.r) * this.n);
                    scaledHeight = (int)((float)(this.u - this.s) * this.n);
                    int origWidth = this.t - this.r;
                    int origHeight = this.u - this.s;
                    int l = (int)((float)(this.w.width() / 2) - (float)(this.w.width() / 2 - this.v.left) * scale);
                    int t = (int)((float)(this.w.height() / 2) - (float)(this.w.height() / 2 - this.v.top) * scale);
                    int r = l + scaledWidth;
                    int b = t + scaledHeight;
                    if (scaledWidth > origWidth && scaledHeight > origHeight) {
                        if (scaledWidth >= maxWidth || scaledHeight >= maxHeight) {
                            l = this.v.left;
                            t = this.v.top;
                            r = l + maxWidth;
                            b = t + maxHeight;
                        }
                    } else {
                        l = this.r;
                        t = this.s;
                        r = this.t;
                        b = this.u;
                    }

                    this.v.set(l, t, r, b);
                    System.out.println("zoom -> l: " + l + ", t: " + t + ", r: " + r + ", b: " + b + ",  width: " + scaledWidth + ", height: " + scaledHeight);
                    this.m = System.currentTimeMillis();
                }
            case 3:
            case 4:
            default:
                break;
            case 5:
                float dist = this.a(event);
                if (dist > 10.0F) {
                    this.h = 2;
                    this.l = dist;
                    System.out.println("Action_Pointer_Down -> origDist(" + this.l + ")");
                }
        }

        return false;
    }

    public boolean onDown(MotionEvent e) {
        return false;
    }

    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (this.v.left == this.r && this.v.top == this.s && this.v.right == this.t && this.v.bottom == this.u) {
            System.out.println("velocityX: " + Math.abs(velocityX) + ", velocityY: " + Math.abs(velocityY));
            if (e1.getX() - e2.getX() > 100.0F && Math.abs(velocityX) > 0.0F) {
                if (this.z != null && this.A >= 0) {
                    this.z.sendIOCtrl(this.A, 4097, SMsgAVIoctrlPtzCmd.parseContent((byte)6, (byte)8, (byte)0, (byte)0, (byte)0, (byte)0));
                }
            } else if (e2.getX() - e1.getX() > 100.0F && Math.abs(velocityX) > 0.0F) {
                if (this.z != null && this.A >= 0) {
                    this.z.sendIOCtrl(this.A, 4097, SMsgAVIoctrlPtzCmd.parseContent((byte)3, (byte)8, (byte)0, (byte)0, (byte)0, (byte)0));
                }
            } else if (e1.getY() - e2.getY() > 100.0F && Math.abs(velocityY) > 0.0F) {
                if (this.z != null && this.A >= 0) {
                    this.z.sendIOCtrl(this.A, 4097, SMsgAVIoctrlPtzCmd.parseContent((byte)2, (byte)8, (byte)0, (byte)0, (byte)0, (byte)0));
                }
            } else if (e2.getY() - e1.getY() > 100.0F && Math.abs(velocityY) > 0.0F && this.z != null && this.A >= 0) {
                this.z.sendIOCtrl(this.A, 4097, SMsgAVIoctrlPtzCmd.parseContent((byte)1, (byte)8, (byte)0, (byte)0, (byte)0, (byte)0));
            }

            (new Handler()).postDelayed(new Runnable() {
                public void run() {
                    if (Monitor.this.z != null && Monitor.this.A >= 0) {
                        Monitor.this.z.sendIOCtrl(Monitor.this.A, 4097, SMsgAVIoctrlPtzCmd.parseContent((byte)0, (byte)8, (byte)0, (byte)0, (byte)0, (byte)0));
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

    @SuppressLint({"FloatMath"})
    private float a(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float)Math.sqrt((double)(x * x + y * y));
    }

    private void a(PointF point, float left, float top, float right, float bottom) {
        point.set((left + right) / 2.0F, (top + bottom) / 2.0F);
    }

    public void receiveExtraInfo(Camera camera, int avChannel, int eventType, int recvFrame, int dispFrame) {
    }

    public void TurnToLeftOn() {
        if (this.z != null && this.A >= 0) {
            this.z.sendIOCtrl(0, 4097, SMsgAVIoctrlPtzCmd.parseContent((byte)3, (byte)8, (byte)this.a, (byte)0, (byte)0, (byte)0));
        }

    }

    public void TurnToRightOn() {
        if (this.z != null && this.A >= 0) {
            this.z.sendIOCtrl(0, 4097, SMsgAVIoctrlPtzCmd.parseContent((byte)6, (byte)8, (byte)this.a, (byte)0, (byte)0, (byte)0));
        }

    }

    public void TurnToUpOn() {
        if (this.z != null && this.A >= 0) {
            this.z.sendIOCtrl(0, 4097, SMsgAVIoctrlPtzCmd.parseContent((byte)1, (byte)8, (byte)this.a, (byte)0, (byte)0, (byte)0));
        }

    }

    public void TurnToDownOn() {
        if (this.z != null && this.A >= 0) {
            this.z.sendIOCtrl(0, 4097, SMsgAVIoctrlPtzCmd.parseContent((byte)2, (byte)8, (byte)this.a, (byte)0, (byte)0, (byte)0));
        }

    }

    public void PtzStop() {
        if (this.z != null && this.A >= 0) {
            this.z.sendIOCtrl(0, 4097, SMsgAVIoctrlPtzCmd.parseContent((byte)0, (byte)8, (byte)this.a, (byte)0, (byte)0, (byte)0));
        }

    }

    public boolean registerMonitor(MRegisterMonitiorListener listener) {
        boolean result = false;
        if (!this.H.contains(listener)) {
            this.H.add(listener);
            result = true;
        }

        return result;
    }

    public boolean unregisterMonitor(MRegisterMonitiorListener listener) {
        boolean result = false;
        if (!this.H.contains(listener)) {
            this.H.remove(listener);
            result = true;
        }

        return result;
    }

    public void setScreenSize(int width, int height) {
        this.d = height;
        this.c = width;
        if (this.c == 0) {
            this.c = this.B;
        }

        if (this.d == 0) {
            this.d = this.C;
        }

        this.g.set(0, 0, this.c, this.d);
        this.v.set(0, 0, this.c, this.d);
        Log.i("IOTCamera", "setScreenSize w[" + this.c + "] h[" + this.d + "]");
    }

    private class a extends Thread {
        private boolean b;
        private Object c;

        private a() {
            this.b = false;
            this.c = new Object();
        }

        public void a() {
            this.b = false;

            try {
                this.c.notify();
            } catch (Exception var2) {
                ;
            }

        }

        public void run() {
            this.b = true;
            Canvas videoCanvas = null;
            Monitor.this.b.setDither(true);

            while(this.b) {
                if (Monitor.this.x != null && !Monitor.this.x.isRecycled()) {
                    try {
                        videoCanvas = Monitor.this.q.lockCanvas();
                        if (videoCanvas != null && Monitor.this.b != null) {
                            videoCanvas.drawColor(-16777216);
                            videoCanvas.drawBitmap(Monitor.this.x, (Rect)null, Monitor.this.v, Monitor.this.b);
                        }
                    } finally {
                        if (videoCanvas != null) {
                            Monitor.this.q.unlockCanvasAndPost(videoCanvas);
                        }

                        videoCanvas = null;
                    }
                }

                try {
                    Object var2 = this.c;
                    synchronized(this.c) {
                        this.c.wait(33L);
                    }
                } catch (InterruptedException var7) {
                    var7.printStackTrace();
                }
            }

            System.out.println("===ThreadRender exit===");
        }
    }
}
