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

        //Test using custom view
        //CrazyEightsView myView = new CrazyEightsView( this );
        TitleView tView = new TitleView( this );
        tView.setKeepScreenOn( true ); //forces screen to stay on. Screen will never auto timeout
        setContentView(tView);

        //ACTUAL GAME VIEW
//        TitleView tView = new TitleView(this);
//        tView.setKeepScreenOn(true);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//        					 WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        setContentView(tView);
    }
}
