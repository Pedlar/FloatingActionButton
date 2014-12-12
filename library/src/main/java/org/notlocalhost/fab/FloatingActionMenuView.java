package org.notlocalhost.fab;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by mkoenig on 12/12/14.
 */
public class FloatingActionMenuView extends ViewGroup {
    List<FloatingActionMenuItem> mMenuItemList;

    public FloatingActionMenuView(Context context, List<FloatingActionMenuItem> menuItemList) {
        super(context);
        mMenuItemList = menuItemList;

        addViews();
    }

    private void addViews() {
        for(FloatingActionMenuItem item : mMenuItemList) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.fab__menu_item, this, false);
            ((TextView)view.findViewById(R.id.fab__menu_label)).setText(item.getLabel());

            ActionButton button = (ActionButton) view.findViewById(R.id.fab__menu_icon);
            button.setColor(Color.RED); // TODO: Derive this color from the main action button
            button.setSize(FloatingActionButton.SIZE_MINI);
            if(item.getIcon() != null) {
                button.setImageDrawable(item.getIcon());
            }

            LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            addView(view, layoutParams);
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
        int nextY = b - t;

        for(int i = 0; i < getChildCount(); i++) {
            final View childView = getChildAt(i);

            int childY = nextY - childView.getMeasuredHeight();
            int childX = 0;

            childView.layout(childX, childY, childX + childView.getMeasuredWidth(), childY + childView.getMeasuredHeight());

            nextY = childY;
        }
    }


}
