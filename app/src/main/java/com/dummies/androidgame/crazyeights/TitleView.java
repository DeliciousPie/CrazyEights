package com.dummies.androidgame.crazyeights;
import android.content.Context;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;


/**
 * Created by Wes on 3/6/2015.
 */
public class TitleView extends View //extend view because I'm making my own custom view
{
    public TitleView( Context context )
    {
        super(context);
    }

    @Override
    protected void onDraw( Canvas canvas) //Override onDraw so I can add my logic to add title
    {

    }

    // The logic for handling cases when the player touches the screen
    public boolean onTouchEvent( MotionEvent event )
    {
        int eventAction = event.getAction();
        int x = (int) event.getX();
        int y = (int) event.getY();

        switch ( eventAction )
        {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        invalidate();
        return true;

    }

}
