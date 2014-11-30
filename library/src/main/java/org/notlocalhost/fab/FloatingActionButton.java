package org.notlocalhost.fab;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.RelativeLayout;

/**
 * Created by pedlar on 11/29/14.
 *
 */
public class FloatingActionButton extends RelativeLayout implements FloatingActionButtonApi {
    public static final int SIZE_NORMAL = 0;
    public static final int SIZE_MINI = 1;

    private Window mWindow;
    private View mAttachedView;
    private boolean mAttachToWindow;
    private ActionButton mActionButton;
    private int mSize = SIZE_NORMAL;
    private int mColor = Color.RED;
    private int mButtonGravity = Gravity.BOTTOM | Gravity.RIGHT;

    public FloatingActionButton(Activity context) {
        super(context);
        init(context, false);
    }

    public FloatingActionButton(Activity context, boolean attachToWindow) {
        super(context);
        init(context, attachToWindow);
    }

    private void init(Context context, boolean attachToWindow) {
        setId(R.id.fab__main_container);

        initThemeAttrs();

        setupActionButton();

        if(context instanceof Activity) {
            Activity activity = (Activity)context;
            mWindow = activity.getWindow();
        }

        mAttachToWindow = attachToWindow;
        
        ViewGroup.LayoutParams lParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        setLayoutParams(lParams);

        ((ViewGroup)mWindow.getDecorView()).addView(this);
        bringToFront();
        if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            mWindow.getDecorView().requestLayout();
            mWindow.getDecorView().invalidate();
        }
    }

    private void initThemeAttrs() {

    }

    private void setupActionButton() {
        if(mActionButton == null) {
            mActionButton = new ActionButton(getContext());
            mActionButton.setId(R.id.fab__action_button);
        }
        mActionButton.beginTransaction();

        mActionButton.setSize(mSize);
        mActionButton.setColor(mColor);

        mActionButton.commitChanges();
        mActionButton.endTransaction();

        int buttonMargin = Math.round(getResources().getDimension(mSize == SIZE_MINI ? R.dimen.fab__mini_margin : R.dimen.fab__normal_margin));

        RelativeLayout.LayoutParams actionButtonParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        if(mAttachToWindow) {
            if ((mButtonGravity & Gravity.BOTTOM) == Gravity.BOTTOM) {
                actionButtonParams.addRule(ALIGN_PARENT_BOTTOM, 1);
                actionButtonParams.bottomMargin = buttonMargin;
            } else if ((mButtonGravity & Gravity.TOP) == Gravity.TOP) {
                actionButtonParams.addRule(ALIGN_PARENT_TOP, 1);
                actionButtonParams.topMargin = buttonMargin;
            } else if ((mButtonGravity & Gravity.CENTER_VERTICAL) == Gravity.CENTER_VERTICAL) {
                actionButtonParams.addRule(CENTER_VERTICAL, 1);
            }

            if ((mButtonGravity & Gravity.LEFT) == Gravity.LEFT) {
                actionButtonParams.addRule(ALIGN_PARENT_LEFT, 1);
                actionButtonParams.leftMargin = buttonMargin;
            } else if ((mButtonGravity & Gravity.RIGHT) == Gravity.RIGHT) {
                actionButtonParams.addRule(ALIGN_PARENT_RIGHT, 1);
                actionButtonParams.rightMargin = buttonMargin;
            } else if ((mButtonGravity & Gravity.CENTER_HORIZONTAL) == Gravity.CENTER_HORIZONTAL) {
                actionButtonParams.addRule(CENTER_HORIZONTAL, 1);
            }
        } else {
            positionActionButton();
        }

        if(mActionButton.equals(findViewById(R.id.fab__action_button))) {
            mActionButton.setLayoutParams(actionButtonParams);
        } else{
            addView(mActionButton, actionButtonParams);
        }
    }

    private void positionActionButton() {
        if(mAttachedView != null) {
            int l[] = new int[2];
            mAttachedView.getLocationOnScreen(l);

            Rect viewRect = new Rect(l[0], l[1], l[0] + mAttachedView.getWidth(), l[1] + mAttachedView.getHeight());

            if (mActionButton != null) {
                int buttonMargin = Math.round(getResources().getDimension(mSize == SIZE_MINI ? R.dimen.fab__mini_margin : R.dimen.fab__normal_margin));
                int halfWidth = mActionButton.getWidth() / 2;
                if ((mButtonGravity & Gravity.BOTTOM) == Gravity.BOTTOM) {
                    mActionButton.setY(viewRect.bottom - halfWidth);
                } else if ((mButtonGravity & Gravity.TOP) == Gravity.TOP) {
                    mActionButton.setY(viewRect.top - halfWidth);
                }

                if ((mButtonGravity & Gravity.LEFT) == Gravity.LEFT) {
                    mActionButton.setX(viewRect.left + buttonMargin);
                } else if ((mButtonGravity & Gravity.RIGHT) == Gravity.RIGHT) {
                    mActionButton.setX((viewRect.right - buttonMargin) - mActionButton.getWidth());
                } else if ((mButtonGravity & Gravity.CENTER_HORIZONTAL) == Gravity.CENTER_HORIZONTAL) {
                    mActionButton.setX(viewRect.exactCenterX() - halfWidth);
                }
            }
        }
    }

    @Override
    public void attachToView(View view) {
        mAttachedView = view;
        positionActionButton();
        mAttachedView.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        positionActionButton();
                        mAttachedView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                });

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            mAttachedView.addOnLayoutChangeListener(new OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    positionActionButton();
                }
            });
        }
    }

    @Override
    public void setColor(int color) {
        mColor = color;
        if(mActionButton != null) {
            mActionButton.setColor(color);
        }
    }

    @Override
    public int getColor() {
        return mColor;
    }

    @Override
    public void show() {
        // TODO: STUB!
    }

    @Override
    public void hide() {
        // TODO: STUB!
    }

    @Override
    public void setAttachToWindow(boolean attachToWindow) {
        mAttachToWindow = attachToWindow;
        if(mAttachToWindow) {
            setupActionButton();
        }
    }

    @Override
    public void setSize(int size) {
        mSize = size;
        setupActionButton();
    }

    @Override
    public int getSize() {
        return mSize;
    }

    @Override
    public void setGravity(int gravity) {
        mButtonGravity = gravity;
        setupActionButton();
    }

    @Override
    public ActionButton getActionButton() {
        return mActionButton;
    }
}
