package org.notlocalhost.fab;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.internal.view.menu.MenuBuilder;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

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
    private boolean isShowing = true;
    private float mActionButtonX;
    private float mActionButtonY;

    private FloatingActionMenuView mActionMenuView;

    private View mMenuFog;
    private MenuAnimation mMenuAnimation;

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

        mMenuFog = new View(getContext());
        mMenuFog.setBackgroundColor(0xffeeeeee);
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.addRule(CENTER_IN_PARENT, 1);
        addView(mMenuFog, layoutParams);
        mMenuFog.setVisibility(View.GONE);

        setupActionButton();
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
        int size = (int)getResources().getDimension(mSize == SIZE_MINI ? R.dimen.fab__size_mini : R.dimen.fab__size_normal);

        RelativeLayout.LayoutParams actionButtonParams = new LayoutParams(size, size);

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

        if(mActionMenuView != null) {
            mActionMenuView.setLayoutParams(getActionMenuLayoutParams());
        }

        mActionButtonX = mActionButton.getX();
        mActionButtonY = mActionButton.getY();
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

        if(mActionButton != null) {
            mActionButtonX = mActionButton.getX();
            mActionButtonY = mActionButton.getY();
        }
    }

    private LayoutParams getActionMenuLayoutParams() {
        RelativeLayout.LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        if ((mButtonGravity & Gravity.BOTTOM) == Gravity.BOTTOM) {
            layoutParams.addRule(ABOVE, R.id.fab__action_button);
        } else if ((mButtonGravity & Gravity.TOP) == Gravity.TOP) {
            layoutParams.addRule(BELOW, R.id.fab__action_button);
        }

        if ((mButtonGravity & Gravity.LEFT) == Gravity.LEFT) {
            layoutParams.addRule(ALIGN_LEFT, R.id.fab__action_button);
        } else if ((mButtonGravity & Gravity.RIGHT) == Gravity.RIGHT) {
            layoutParams.addRule(ALIGN_RIGHT, R.id.fab__action_button);
        }

        return layoutParams;
    }

    private void setupFloatingActionMenu(List<FloatingActionMenuItem> menuItemList) {
        mActionMenuView = new FloatingActionMenuView(getContext(), mButtonGravity, menuItemList);
        if(mMenuAnimation != null) {
            mActionMenuView.setMenuAnimation(mMenuAnimation);
        }
        addView(mActionMenuView, getActionMenuLayoutParams());
        if(mActionButton != null) {
            mActionButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!mActionMenuView.isVisible()) {
                        expandMenu();
                    } else {
                        collapeMenu();
                    }
                }
            });
        }
    }

    private void expandMenu() {
        mActionMenuView.toggle();

        mMenuFog.setAlpha(0f);
        mMenuFog.setVisibility(View.VISIBLE);
        mMenuFog.animate().alpha(.7f).setDuration(200).setListener(null);
    }

    private void collapeMenu() {
        mActionMenuView.toggle();
        mMenuFog.animate().alpha(0f).setDuration(200).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mMenuFog.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
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
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            mAttachedView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }
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
        if(!isShowing && mActionButton != null) {
            float x = mActionButton.getX();
            float y = mActionButton.getY();

            if((mButtonGravity & Gravity.BOTTOM) == Gravity.BOTTOM
                    || (mButtonGravity & Gravity.TOP) == Gravity.TOP) {
                y = mActionButtonY;
            } else {
                x = mActionButtonX;
            }
            mActionButton.animate().x(x).y(y).setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    isShowing = true;
                }
                @Override public void onAnimationStart(Animator animation) { }
                @Override public void onAnimationCancel(Animator animation) { }
                @Override public void onAnimationRepeat(Animator animation) { }
            });
        }
    }

    @Override
    public void hide() {
        if(isShowing && mActionButton != null) {
            float x = mActionButtonX = mActionButton.getX();
            float y = mActionButtonY = mActionButton.getY();


            if((mButtonGravity & Gravity.BOTTOM) == Gravity.BOTTOM) {
                y = getHeight() + mActionButton.getHeight();
            } else if ((mButtonGravity & Gravity.TOP) == Gravity.TOP) {
                y = -1 * mActionButton.getHeight();
            } else if ((mButtonGravity & Gravity.CENTER_VERTICAL) == Gravity.CENTER_VERTICAL
                    && (mButtonGravity & Gravity.RIGHT) == Gravity.RIGHT) {
                x = getWidth() + mActionButton.getWidth();
            } else if((mButtonGravity & Gravity.CENTER_VERTICAL) == Gravity.CENTER_VERTICAL
                    && (mButtonGravity & Gravity.LEFT) == Gravity.LEFT) {
                x = -1 * mActionButton.getWidth();
            }

            mActionButton.animate().x(x).y(y).setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    isShowing = false;
                }
                @Override public void onAnimationStart(Animator animation) { }
                @Override public void onAnimationCancel(Animator animation) { }
                @Override public void onAnimationRepeat(Animator animation) { }
            });
        }
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
        if(mActionMenuView != null) {
            mActionMenuView.setGravity(mButtonGravity);
        }
    }

    @Override
    public ActionButton getActionButton() {
        return mActionButton;
    }

    @Override
    public void setIcon(int resId) {
        if(mActionButton != null) {
            mActionButton.setImageResource(resId);
        }
    }

    @Override
    public void setIcon(Drawable drawable) {
        if(mActionButton != null) {
            mActionButton.setImageDrawable(drawable);
        }
    }

    @Override
    public void setMenu(int menuResId) {
        MenuInflater menuInflater = new MenuInflater(getContext());
        Menu menu = new MenuBuilder(getContext());
        menuInflater.inflate(menuResId, menu);

        List<FloatingActionMenuItem> menuItems = new ArrayList<FloatingActionMenuItem>();
        for(int i = 0; i < menu.size(); i++) {
            FloatingActionMenuItem menuItem = new FloatingActionMenuItem();
            menuItem.setLabel(menu.getItem(i).getTitle().toString());
            menuItem.setIcon(menu.getItem(i).getIcon());
        }
        setupFloatingActionMenu(menuItems);
    }

    @Override
    public void setMenu(List<FloatingActionMenuItem> menuItemList) {
        setupFloatingActionMenu(menuItemList);
    }

    @Override
    public void setMenuAnimation(MenuAnimation menuAnimation) {
        mMenuAnimation = menuAnimation;
        if(mActionMenuView != null) {
            mActionMenuView.setMenuAnimation(mMenuAnimation);
        }
    }

    public void setMenuItemClickListener() {

    }
}
