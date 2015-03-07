package com.dummies.androidgame.crazyeights;

import android.graphics.Bitmap;

/**
 * Created by Wes on 3/6/2015.
 */
public class Card
{
    private int id;
    private Bitmap bmp;

    public Card( int newId)
    {
        id = newId;
    }

    public void setBitmap( Bitmap newBitmap)
    {
        bmp = newBitmap;
    }

    public Bitmap getBitmap()
    {
        return bmp;
    }

    public int getId()
    {
        return id;
    }

}
