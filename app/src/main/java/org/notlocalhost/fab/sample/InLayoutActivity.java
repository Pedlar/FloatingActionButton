package org.notlocalhost.fab.sample;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

/**
 * Created by mkoenig on 12/19/14.
 */
public class InLayoutActivity extends Activity implements View.OnClickListener  {

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_inlayout);
    }

    @Override
    public void onClick(View v) {

    }
}
