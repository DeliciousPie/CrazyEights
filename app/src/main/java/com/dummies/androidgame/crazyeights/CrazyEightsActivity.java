package com.dummies.androidgame.crazyeights;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class CrazyEightsActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //Setting this activity to a custom view
        TitleView tView = new TitleView( this );
        tView.setKeepScreenOn( true ); //forces screen to stay on. Screen will never auto timeout

        requestWindowFeature( Window.FEATURE_NO_TITLE); //tells activity not to show title of app
        //set window to full screen
        getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
       //Display view
        setContentView(tView);
    }
}
