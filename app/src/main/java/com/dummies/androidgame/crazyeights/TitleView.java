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
    private int screenW; //variable to keep track of screen width
    private int screenH; //Variable to keep track of the screen height

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
    /*
        Overriding the onSizeChanged method which is called by a view after the constructor but
        before anything is drawn. All this is doing is grabbing the values of the width and
        height of the screen.
     */
    @Override
    public void onSizeChanged ( int w, int h, int oldW, int oldH )
    {
        super.onSizeChanged( w, h , oldW, oldH );
        screenW = w;
        screenH = h;
    }


    @Override
    protected void onDraw( Canvas canvas) //Override onDraw so I can add my logic to add title
    {
        /*
            This is replacing the X position with some math. First, subtract the width of the
            graphic from the screen width to find out how much space is available. Then divide that
            by 2 to put equal amounts of space on either side of your graphic.
         */
        canvas.drawBitmap( titleGraphic, (screenW - titleGraphic.getWidth() ) /2, 0, null);
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
