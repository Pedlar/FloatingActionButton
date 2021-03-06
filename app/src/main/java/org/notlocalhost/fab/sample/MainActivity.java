package org.notlocalhost.fab.sample;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.notlocalhost.fab.FloatingActionButton;
import org.notlocalhost.fab.FloatingActionMenuItem;
import org.notlocalhost.fab.MenuAnimation;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity implements View.OnClickListener {
    FloatingActionButton mFab;

    int[] mIcons = new int[]{
            android.R.drawable.ic_menu_camera,
            android.R.drawable.ic_menu_compass,
            android.R.drawable.ic_menu_help,
            android.R.drawable.ic_menu_crop,
            android.R.drawable.ic_menu_call
    };

    String[] mLabels = new String[] {
            "Camera",
            "Compass",
            "Help",
            "Crop",
            "Call"
    };

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_main);
        mFab = new FloatingActionButton(this, true);

        mFab.setGravity(Gravity.BOTTOM | Gravity.RIGHT);

        findViewById(R.id.top_left).setOnClickListener(this);
        findViewById(R.id.top_right).setOnClickListener(this);
        findViewById(R.id.bottom_left).setOnClickListener(this);
        findViewById(R.id.bottom_right).setOnClickListener(this);
        findViewById(R.id.bottom_center).setOnClickListener(this);
        findViewById(R.id.center_left).setOnClickListener(this);
        findViewById(R.id.center_right).setOnClickListener(this);
        findViewById(R.id.attach_to_view).setOnClickListener(this);
        findViewById(R.id.hide).setOnClickListener(this);
        findViewById(R.id.show).setOnClickListener(this);
        findViewById(R.id.menu_overshoot).setOnClickListener(this);
        findViewById(R.id.menu_scalein).setOnClickListener(this);

        List<FloatingActionMenuItem> menuItemList = new ArrayList<>();
        for(int i = 0; i < 5; i++) {
            FloatingActionMenuItem floatingActionMenuItem = new FloatingActionMenuItem();
            floatingActionMenuItem.setLabel(mLabels[i]);
            floatingActionMenuItem.setIcon(getResources().getDrawable(mIcons[i]));
            menuItemList.add(floatingActionMenuItem);
        }
        mFab.setMenu(menuItemList);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.top_left:
                mFab.setGravity(Gravity.TOP | Gravity.LEFT);
                break;
            case R.id.top_right:
                mFab.setGravity(Gravity.TOP | Gravity.RIGHT);
                break;
            case R.id.bottom_left:
                mFab.setGravity(Gravity.BOTTOM | Gravity.LEFT);
                break;
            case R.id.bottom_right:
                mFab.setGravity(Gravity.BOTTOM | Gravity.RIGHT);
                break;
            case R.id.bottom_center:
                mFab.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
                break;
            case R.id.center_left:
                mFab.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
                break;
            case R.id.center_right:
                mFab.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
                break;
            case R.id.attach_to_view:
                mFab.setAttachToWindow(false);
                mFab.attachToView(findViewById(R.id.attach_view));
                break;
            case R.id.hide:
                mFab.hide();
                break;
            case R.id.show:
                mFab.show();
                break;
            case R.id.menu_scalein:
                mFab.setMenuAnimation(MenuAnimation.SCALE_IN);
                break;
            case R.id.menu_overshoot:
                mFab.setMenuAnimation(MenuAnimation.OVERSHOOT);
                break;
        }
    }
}
