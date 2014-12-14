package org.notlocalhost.fab;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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

    int getDrawableSize() {
        int size = (int)(mSize == FloatingActionButton.SIZE_MINI ? getResources().getDimension(R.dimen.fab__size_mini) :
                getResources().getDimension(R.dimen.fab__size_normal));
        int shadowRadius = (int)getResources().getDimension(R.dimen.fab__shadow_radius);
        return size + 2 * shadowRadius;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getDrawableSize(), getDrawableSize());
    }

    void drawActionButton() {
        if(isInTransaction) return; // Don't make any changes yet if we're in a transactional state

        float size = (mSize == FloatingActionButton.SIZE_MINI ? getResources().getDimension(R.dimen.fab__size_mini) :
                getResources().getDimension(R.dimen.fab__size_normal));

        float shadowRadius = getResources().getDimension(R.dimen.fab__shadow_radius);
        float shadowOffset = getResources().getDimension(R.dimen.fab__shadow_offset);
        RectF circleRect = new RectF(shadowRadius, shadowRadius - shadowOffset, shadowRadius + size, (shadowRadius - shadowOffset) + size);
        Drawable background = createCircleDrawable(circleRect, mColor);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN)
            setBackgroundDrawable(background);
        else
            setBackground(background);

    }

    private Drawable createCircleDrawable(RectF circleRect, int color) {
        final Bitmap bitmap = Bitmap.createBitmap(getDrawableSize(), getDrawableSize(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap);

        float shadowRadius = getResources().getDimension(R.dimen.fab__shadow_radius);
        float shadowOffset = getResources().getDimension(R.dimen.fab__shadow_offset);

        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(color);
        paint.setShadowLayer(shadowRadius, 0f, shadowOffset, Color.argb(100, 0, 0, 0));

        canvas.drawOval(circleRect, paint);

        return new BitmapDrawable(getResources(), bitmap);
    }

    boolean needsShadow() {
        return true;//Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP;
    }
}
