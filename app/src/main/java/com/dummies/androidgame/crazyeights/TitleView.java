package com.dummies.androidgame.crazyeights;
import android.content.Context;
import android.content.Intent;
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
    private Bitmap playButtonUp;
    private Bitmap playButtonDown;
    private boolean playButtonPressed;
    private Context myContext; //current context to use with intents

    public TitleView( Context context )
    {
        super(context);
        myContext = context;
        /*This line loads the bitmap into memory so it can be drawn on the screen.
          BitmapFactory creates bitmaps from various sources i.e. from the file in the
            drawable directory.
            **REMEMBER** You never touch the R file. Whenever you place a file in one of your
            directories it creates a reference to it in the R file. This is how you can pass in the
            parameter that tells BitmapFactory what file to load: R.name_of_your_file
        */
        titleGraphic = BitmapFactory.decodeResource(getResources(), R.drawable.title_graphic);

        //load bitmap with the TitleView constructor
        playButtonUp = BitmapFactory.decodeResource(getResources(), R.drawable.play_button_up);

        playButtonDown = BitmapFactory.decodeResource(getResources(), R.drawable.play_button_down);
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
        //only draw the playButtonDown if the player is touching the button
        if( playButtonPressed)
        {
            canvas.drawBitmap(playButtonDown, (screenW - playButtonDown.getWidth()) / 2,
                    (int) (screenH * 0.7), null);
        }
        else {
            //draw playButtonUp
            canvas.drawBitmap(playButtonUp, (screenW - playButtonUp.getWidth()) / 2,
                    (int) (screenH * 0.7), null); //draw the top of the image at 70% screen height
        }

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
                /*
                This monstrosity checks to see whether the player is touching the screen within the
                bounds of the Play-button graphic. First two conditions check for horizontal bounds
                and the last two check for vertical bounds.
                THERE MUST BE A BETTER WAY THAN THIS
                 */
                if( x > (screenW - playButtonUp.getWidth()) /2 &&
                        x < ( ( screenW - playButtonUp.getWidth() ) /2 )+
                        playButtonUp.getWidth() &&
                        y > (int) (screenH * 0.7) &&
                        y < (int) (screenH * 0.7) +
                        playButtonUp.getHeight())
                {
                    playButtonPressed = true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                if( playButtonPressed )
                {
                    Intent gameIntent = new Intent(myContext, GameActivity.class);
                    myContext.startActivity(gameIntent);
                }
                playButtonPressed = false;
                break;
        }
        invalidate();
        return true;

    }

}
