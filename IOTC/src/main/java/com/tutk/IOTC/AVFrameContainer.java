//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.tutk.IOTC;

import java.util.LinkedList;

class AVFrameContainer {
    private volatile LinkedList<AVFrame> AVFrameList = new LinkedList();
    private volatile int AVFrameNums = 0;
    private volatile boolean isDrop = false;

    AVFrameContainer() {
    }

    public synchronized int getAVFrameNums() {
        return this.AVFrameNums;
    }

    public synchronized void addAVFrame(AVFrame node) {
        if (this.AVFrameNums > 1500) {
            for(boolean bFirst = true; !this.AVFrameList.isEmpty(); bFirst = false) {
                AVFrame frame = this.AVFrameList.get(0);
                if (bFirst) {
                    this.AVFrameList.removeFirst();
                    --AVFrameNums;
                } else {
                    if (frame.isIFrame()) {
                        break;
                    }

                    this.AVFrameList.removeFirst();
                    --AVFrameNums;
                }
            }
        }

        this.AVFrameList.addLast(node);
        ++this.AVFrameNums;
    }

    public synchronized AVFrame getFirstAVFrame() {
        if (this.AVFrameNums == 0) {
            return null;
        } else {
            AVFrame frame = this.AVFrameList.removeFirst();
            --this.AVFrameNums;
            return frame;
        }
    }

    public synchronized void clear() {
        if (!AVFrameList.isEmpty()) {
            AVFrameList.clear();
        }

        AVFrameNums = 0;
    }

    public synchronized boolean isEmpty() {
        return AVFrameList != null && !AVFrameList.isEmpty() && (AVFrameList.get(0)).isIFrame();
    }

    public synchronized boolean isDrop() {
        return this.isDrop;
    }

    public synchronized void setIsDrop(Boolean isDrop) {
        this.isDrop = isDrop;
    }
}
