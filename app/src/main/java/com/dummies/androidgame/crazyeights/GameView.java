package com.dummies.androidgame.crazyeights;
import android.content.Context;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;
/**
 * Created by Wes on 3/6/2015.
 */
public class GameView extends View
{
    public GameView( Context context )
    {
        super(context);
    }

    @Override
    protected void onDraw( Canvas canvas )
    {

    }

    public boolean onTouchEvent( MotionEvent event )
    {
        int eventaction = event.getAction();
        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (eventaction)
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
