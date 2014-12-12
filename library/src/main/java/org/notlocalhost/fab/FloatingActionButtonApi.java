package org.notlocalhost.fab;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.View;

/**
 * Created by pedlar on 11/29/14.
 *
 * This class is to help maintain API for the FloatingActionButton class.
 */
public interface FloatingActionButtonApi {
    public void attachToView(View view);
    public void setColor(int color);
    public int getColor();
    public void show();
    public void hide();
    public void setAttachToWindow(boolean attachToWindow);
    public void setSize(int size);
    public int getSize();
    public void setGravity(int gravity);
    public ActionButton getActionButton();
    public void setIcon(int resId);
    public void setIcon(Drawable drawable);
}