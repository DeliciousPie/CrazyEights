package com.dummies.androidgame.crazyeights;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;


/**
 * Created by Wes on 3/6/2015.
 */
public class TitleView extends View //extend view because I'm making my own custom view
{

    private Bitmap titleGraphic; //Bitmap object to hold the title screen image

    public TitleView( Context context )
    {
        super(context);
        /*This line loads the bitmap into memory so it can be drawn on the screen.
          BitmapFactory creates bitmaps from various sources i.e. from the file in the
            drawable directory.
            **REMEMBER** You never touch the R file. Whenever you place a file in one of your
            directories it creates a reference to it in the R file. This is how you can pass in the
            parameter that tells BitmapFactory what file to load: R.name_of_your_file
        */
        titleGraphic = BitmapFactory.decodeResource(getResources(), R.drawable.title_graphic);
    }

    @Override
    protected void onDraw( Canvas canvas) //Override onDraw so I can add my logic to add title
    {
        canvas.drawBitmap( titleGraphic, 0, 0, null);
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
