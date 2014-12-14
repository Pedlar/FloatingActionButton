package org.notlocalhost.fab;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by mkoenig on 12/12/14.
 */
public class FloatingActionMenuView extends ViewGroup {
    List<FloatingActionMenuItem> mMenuItemList;
    boolean isExpanded = false;

    AnimatorSet mExpandAnimatorSet = new AnimatorSet().setDuration(200);
    AnimatorSet mCollapseAnimatorSet = new AnimatorSet().setDuration(200);
    OvershootInterpolator mOvershootInterpolator = new OvershootInterpolator();
    DecelerateInterpolator mDecelerateInterpolator = new DecelerateInterpolator(2f);
    LinearInterpolator mLinearInterpolator= new LinearInterpolator();

    int mGravity;

    MenuAnimation mMenuAnimation = MenuAnimation.OVERSHOOT;

    List<Animator> mExpandSequintialAnimators = new ArrayList<Animator>();
    List<Animator> mCollapseSequintialAnimators = new ArrayList<Animator>();

    public FloatingActionMenuView(Context context, int gravity, List<FloatingActionMenuItem> menuItemList) {
        super(context);
        mMenuItemList = menuItemList;
        mGravity = gravity;
        addViews();
    }

    public FloatingActionMenuView(Context context, int gravity, MenuAnimation menuAnimation, List<FloatingActionMenuItem> menuItemList) {
        super(context);
        mMenuItemList = menuItemList;
        mGravity = gravity;
        mMenuAnimation = menuAnimation;
        addViews();
    }

    private void addViews() {
        for(FloatingActionMenuItem item : mMenuItemList) {
            View view = LayoutInflater
                    .from(getContext())
                    .inflate(isRight() ? R.layout.fab__menu_item_right : R.layout.fab__menu_item_left,
                            this, false);
            ((TextView)view.findViewById(R.id.fab__menu_label)).setText(item.getLabel());

            ActionButton button = (ActionButton) view.findViewById(R.id.fab__menu_icon);
            button.setColor(Color.RED); // TODO: Derive this color from the main action button
            button.setSize(FloatingActionButton.SIZE_MINI);
            if(item.getIcon() != null) {
                button.setImageDrawable(item.getIcon());
            }

            if (mMenuAnimation == MenuAnimation.SCALE_IN) {
                view.findViewById(R.id.fab__menu_icon).setScaleX(0f);
                view.findViewById(R.id.fab__menu_icon).setScaleY(0f);

                view.findViewById(R.id.fab__menu_label_container).setAlpha(0f);
            }

            addView(view, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureChildren(widthMeasureSpec, heightMeasureSpec);

        int width = 0;
        int height = 0;

        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);

            width = Math.max(width, child.getMeasuredWidth());
            height += child.getMeasuredHeight();
        }

        height = adjustForOvershoot(height);

        setMeasuredDimension(width, height);
    }

    private int adjustForOvershoot(int dimension) {
        return dimension * 12 / 10;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int nextY = isBottom() ? b - t : 0;

        if(mMenuAnimation == MenuAnimation.SCALE_IN) {
            mExpandSequintialAnimators.clear();
            mCollapseSequintialAnimators.clear();
        }

        for(int i = 0; i < getChildCount(); i++) {
            final View childView = getChildAt(i);
            int baseY = isBottom() ? b - t + childView.getMeasuredHeight() : 0 - childView.getMeasuredHeight();

            int childY = isBottom() ? nextY - childView.getMeasuredHeight() : nextY;
            int childX = 0;

            childView.layout(childX, childY, childX + childView.getMeasuredWidth(), childY + childView.getMeasuredHeight());

            float fromY = (baseY - childView.getMeasuredHeight()) - childY;
            if(mMenuAnimation == MenuAnimation.OVERSHOOT) {
                childView.setTranslationY(isExpanded ? 0f : fromY);
            }

            ((LayoutParams)childView.getLayoutParams()).setupAnimations(childView, fromY);

            nextY = isBottom() ? childY : childY + childView.getMeasuredHeight();
        }

        if(mMenuAnimation == MenuAnimation.SCALE_IN
                && mExpandAnimatorSet.getChildAnimations().size() == 0) {
            mExpandAnimatorSet.playSequentially(mExpandSequintialAnimators);
        }

        if(mMenuAnimation == MenuAnimation.SCALE_IN
                && mCollapseAnimatorSet.getChildAnimations().size() == 0) {
            Collections.reverse(mCollapseSequintialAnimators);
            mCollapseAnimatorSet.playSequentially(mCollapseSequintialAnimators);
        }
    }

    public void setGravity(int gravity) {
        if(gravity != mGravity) {
            mGravity = gravity;
            reset();
        }

    }

    private boolean isBottom() {
        return (mGravity & Gravity.BOTTOM) == Gravity.BOTTOM;
    }

    private boolean isRight() {
        return (mGravity & Gravity.RIGHT) == Gravity.RIGHT;
    }

    public boolean isVisible() {
        return isExpanded;
    }

    public void setMenuAnimation(MenuAnimation menuAnimation) {
        mMenuAnimation = menuAnimation;
        reset();
    }

    private void reset() {
        mExpandAnimatorSet = new AnimatorSet().setDuration(200);
        mCollapseAnimatorSet = new AnimatorSet().setDuration(200);
        removeAllViews();
        addViews();
    }

    private class LayoutParams extends ViewGroup.LayoutParams {
        final ObjectAnimator expandAnimator = new ObjectAnimator();
        final ObjectAnimator collapseAnimator = new ObjectAnimator();
        final ObjectAnimator labelExpandAnimator = new ObjectAnimator();
        final ObjectAnimator labelCollapseAnimator = new ObjectAnimator();

        public LayoutParams(int wrapContent, int wrapContent1) {
            super(wrapContent, wrapContent1);
        }

        public void setupAnimations(View target, float fromY) {

            switch(mMenuAnimation) {
                case OVERSHOOT:
                    expandAnimator.setInterpolator(mOvershootInterpolator);
                    expandAnimator.setProperty(View.TRANSLATION_Y);
                    expandAnimator.setFloatValues(fromY, 0f);
                    expandAnimator.setTarget(target);

                    collapseAnimator.setInterpolator(mDecelerateInterpolator);
                    collapseAnimator.setProperty(View.TRANSLATION_Y);
                    collapseAnimator.setFloatValues(0f, fromY);
                    collapseAnimator.setTarget(target);

                    mExpandAnimatorSet.play(expandAnimator);
                    mCollapseAnimatorSet.play(collapseAnimator);
                    break;
                case SCALE_IN:
                    expandAnimator.setInterpolator(mDecelerateInterpolator);
                    expandAnimator.setValues(PropertyValuesHolder.ofFloat(View.SCALE_X, 1.0f), PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.0f));
                    expandAnimator.setTarget(target.findViewById(R.id.fab__menu_icon));

                    collapseAnimator.setInterpolator(mDecelerateInterpolator);
                    collapseAnimator.setValues(PropertyValuesHolder.ofFloat(View.SCALE_X, 0.0f), PropertyValuesHolder.ofFloat(View.SCALE_Y, 0.0f));
                    collapseAnimator.setTarget(target.findViewById(R.id.fab__menu_icon));

                    labelExpandAnimator.setInterpolator(mLinearInterpolator);
                    labelExpandAnimator.setProperty(View.ALPHA);
                    labelExpandAnimator.setFloatValues(0.0f, 1.0f);
                    labelExpandAnimator.setTarget(target.findViewById(R.id.fab__menu_label_container));

                    labelCollapseAnimator.setInterpolator(mLinearInterpolator);
                    labelCollapseAnimator.setProperty(View.ALPHA);
                    labelCollapseAnimator.setFloatValues(1.0f, 0.0f);
                    labelCollapseAnimator.setTarget(target.findViewById(R.id.fab__menu_label_container));

                    mExpandSequintialAnimators.add(expandAnimator);
                    mExpandSequintialAnimators.add(labelExpandAnimator);
                    mCollapseSequintialAnimators.add(collapseAnimator);
                    mCollapseSequintialAnimators.add(labelCollapseAnimator);

                    mExpandAnimatorSet.setDuration(75);
                    mCollapseAnimatorSet.setDuration(75);
                    break;
            }
        }
    }

    public void toggle() {
        if(isExpanded) {
            collapse();
        } else {
            expand();
        }
    }

    public void collapse() {
        if(isExpanded) {
            isExpanded = false;
            mExpandAnimatorSet.cancel();
            mCollapseAnimatorSet.start();
        }
    }

    public void expand() {
        if(!isExpanded) {
            isExpanded = true;
            mCollapseAnimatorSet.cancel();
            mExpandAnimatorSet.start();
        }
    }
}
