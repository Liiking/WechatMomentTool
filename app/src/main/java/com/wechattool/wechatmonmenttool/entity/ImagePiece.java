package com.wechattool.wechatmonmenttool.entity;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import java.io.Serializable;

public class ImagePiece implements Serializable, Comparable {
    private int index;
    private Bitmap bitmap;

    public ImagePiece(){
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public ImagePiece(Bitmap bitmap){
        this.bitmap = bitmap;
    }

    public Bitmap getBitmap(){
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap){
        this.bitmap = bitmap;
    }

    @Override
    public int compareTo(@NonNull Object o) {
        if (o instanceof ImagePiece) {
            return ((ImagePiece) o).index - index;
        }
        return 0;
    }
}