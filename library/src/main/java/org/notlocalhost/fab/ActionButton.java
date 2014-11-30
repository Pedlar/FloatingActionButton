package org.notlocalhost.fab;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageButton;

/**
 * Created by pedlar on 11/29/14.
 *
 */
class ActionButton extends ImageButton {
    private int mColor;
    private int mSize;
    private GradientDrawable mMainBackground;
    private boolean isInTransaction;

    public ActionButton(Context context) {
        super(context);
    }

    public ActionButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ActionButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ActionButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    /*
        beginTransaction and endTransaction are here to make sure we don't try and resetup the background
        if we're going to make a bunch of changes at once.
     */
    void beginTransaction() {
        isInTransaction = true;
    }

    void commitChanges() {
        isInTransaction = false;
        drawActionButton();
        isInTransaction = true;
    }

    void endTransaction() {
        isInTransaction = false;
    }

    void setColor(int color) {
        mColor = color;
        drawActionButton();
    }

    void setSize(int size) {
        mSize = size;
        drawActionButton();
    }

    void drawActionButton() {
        if(isInTransaction) return; // Don't make any changes yet if we're in a transactional state
        int cicleResId;

        if(mSize == FloatingActionButton.SIZE_MINI) {
            cicleResId = needsShadow() ? R.drawable.fab__mini_shadow : R.drawable.fab__mini;
        } else {
            cicleResId = needsShadow() ? R.drawable.fab__normal_shadow : R.drawable.fab__normal;
        }

        Drawable background = getResources().getDrawable(cicleResId);
        if (background instanceof LayerDrawable) {
            LayerDrawable layers = (LayerDrawable) background;
            if (layers.getNumberOfLayers() == 2) {
                Drawable shadow = layers.getDrawable(0);
                Drawable circle = layers.getDrawable(1);

                if (shadow instanceof GradientDrawable) {
                    ((GradientDrawable) shadow.mutate()).setGradientRadius(getShadowRadius(shadow, circle));
                }

                if (circle instanceof GradientDrawable) {
                    setMainBackground(circle);
                }
            }
        } else if (background instanceof GradientDrawable) {
            setMainBackground(background);
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN)
            setBackgroundDrawable(background);
        else
            setBackground(background);
    }

    private void setMainBackground(Drawable drawable) {
        mMainBackground = (GradientDrawable)drawable.mutate();
        mMainBackground.setColor(mColor);
    }

    int getShadowRadius(Drawable shadow, Drawable circle) {
        int radius = 0;
        if (shadow != null && circle != null) {
            Rect rect = new Rect();
            radius = (circle.getIntrinsicWidth() + (shadow.getPadding(rect) ? rect.left + rect.right : 0)) / 2;
        }
        return Math.max(1, radius);
    }

    boolean needsShadow() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP;
    }
}
