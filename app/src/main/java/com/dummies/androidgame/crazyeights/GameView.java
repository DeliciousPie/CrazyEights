package com.dummies.androidgame.crazyeights;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by Wes on 3/6/2015.
 */
public class GameView extends View
{
    private Context myContext;
    private List<Card> deck = new ArrayList<Card>();
    private List<Card> myHand = new ArrayList<Card>();
    private List<Card> oppHand = new ArrayList<Card>();
    private List<Card> discardPile = new ArrayList<Card>();
    private int scaledCardW;
    private int scaledCardH;
    private int screenW;
    private int screenH;
    private float scale; //help scale UI elements
    private Paint whitePaint;
    private int oppScore; //opponents score
    private int myScore; //player's score
    private Bitmap cardBack;
    private boolean myTurn; //Controls whether certain logic will happen if it is the player's turn
    private int movingCardIdx = -1;
    private int movingX;
    private int movingY;
    private int validRank = 8; //initialized at 8 because 8 is always a valid play
    private int validSuit = 0;

    public GameView( Context context )
    {
        super(context);
        myContext = context;
        scale = myContext.getResources().getDisplayMetrics().density;
        //myTurn = new Random().nextBoolean(); //randomly determine who goes first
        myTurn = true; //this is only for testing. Remove after

        whitePaint = new Paint();
        whitePaint.setAntiAlias(true); //AA just like in game; smooth edges, no jaggies
        whitePaint.setColor(Color.WHITE); //text color is white
        whitePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        whitePaint.setTextAlign(Paint.Align.LEFT); //align text left of wherever it starts writing
        whitePaint.setTextSize( scale * 15 );
    }

    @Override
    public void onSizeChanged( int w, int h, int oldW, int oldH )
    {
        super.onSizeChanged(w, h, oldW, oldH);
        screenW = w;
        screenH = h;
        //load the cardback bitmap
        Bitmap tempBitmap = BitmapFactory.decodeResource(
                myContext.getResources(), R.drawable.card_back);
        scaledCardW = (int) (screenW / 8);
        scaledCardH = (int) (scaledCardW * 1.28);
        cardBack = Bitmap.createScaledBitmap(
                tempBitmap, scaledCardW, scaledCardH, false );
        initCards();
        dealCards();
        drawCard(discardPile); //move top card from draw pile to discardPile

        validSuit = discardPile.get(0).getSuit();
        validRank = discardPile.get(0).getRank();

    }


    @Override
    protected void onDraw( Canvas canvas )
    {
        /** Display scores **/
        //Ten pixels from the left, ten pixels from the top got by adding size of text to 10
        canvas.drawText("Computer Score: " + Integer.toString( oppScore ), 10,
                whitePaint.getTextSize() + 10, whitePaint);
        //same as above but 10 from bottom so subtract 10 pixels
        canvas.drawText("My Score: " + Integer.toString( myScore ), 10,
                screenH - whitePaint.getTextSize() - 10, whitePaint);

        /** Display player hand **/
        for( int i = 0; i < myHand.size(); i++ )
        {
            if( i < 7 )
            {
                canvas.drawBitmap(myHand.get(i).getBitmap(),
                        i * (scaledCardW + 5), screenH - scaledCardH -
                                whitePaint.getTextSize() - ( 50 * scale), null);
            }
        }
        /** Display opponent hand **/
        for( int i = 0; i < oppHand.size(); i++ )
        {
            canvas.drawBitmap( cardBack, i * (scale * 5),
                    whitePaint.getTextSize() + (50 * scale), null);
        }

        /** Represent the draw pile. Roughly centered on the screen. X = half screen minus cardW **/
        canvas.drawBitmap(cardBack, (screenW / 2) - cardBack.getWidth() - 10,
                (screenH / 2) - (cardBack.getHeight() / 2), null);

        /** Drawing the discard pile **/
        if( !discardPile.isEmpty() )
        {
            /* check to see if discard pile has any cards. If it does, display top card
                slightly to the right of the draw pile and at the same height.
             */
            canvas.drawBitmap( discardPile.get(0).getBitmap(),
                    (screenW / 2) + 10,
                    (screenH / 2) - (cardBack.getHeight() / 2), null);
        }

        //draw card as it is moved
        for( int i = 0; i < myHand.size(); i++ )
        {
            if( i == movingCardIdx)
            {
                canvas.drawBitmap(myHand.get(i).getBitmap(), movingX, movingY, null);
            }
            else
            {
                canvas.drawBitmap(myHand.get(i).getBitmap(), i * (scaledCardW + 5),
                        screenH - scaledCardH - whitePaint.getTextSize() - (50 * scale), null);
            }
        }
        invalidate();
    }

    public boolean onTouchEvent( MotionEvent event )
    {
        int eventaction = event.getAction();
        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (eventaction)
        {
            case MotionEvent.ACTION_DOWN:
                if(myTurn)
                {
                    //loop through player hand and see if player has touched the screen on a card
                    //  that's being displayed. If so, assign the index of that card to movingCardIdx
                    //      as well as to the current x and y positions to the movingX and movingY
                    for(int i = 0; i < 7; i++)
                    {
                        if(x > i * (scaledCardW + 5) && x < i * (scaledCardW + 5) + scaledCardW
                                && y > screenH - scaledCardH - whitePaint.getTextSize() -
                                (50 * scale))
                        {
                            movingCardIdx = i;
                            movingX = x - (int) (30 * scale);
                            movingY = y - (int) (70 * scale);
                        }
                    }
                }
                break;
            //keep track of x and y as player moves finger across screen. Use for drawing card bmp
            case MotionEvent.ACTION_MOVE:
                movingX = x - (int) (30 * scale);
                movingY = y - (int) (70 * scale);
                break;
            //reset movingCardIdx when player lifts finger from the screen
            case MotionEvent.ACTION_UP:
                if( movingCardIdx > -1 && x > (screenW / 2) - (100 * scale) &&
                        x < (screenW / 2) + (100 * scale) &&
                        y > (screenH / 2) - (100 * scale) &&
                        y < (screenH / 2) + (100 * scale) &&
                        (myHand.get( movingCardIdx).getRank() == 8 ||
                        myHand.get(movingCardIdx).getRank() == validRank) ||
                        myHand.get(movingCardIdx).getSuit() == validSuit)
                {
                    validRank = myHand.get(movingCardIdx).getRank();
                    validSuit = myHand.get(movingCardIdx).getSuit();
                    discardPile.add(0, myHand.get(movingCardIdx));
                    myHand.remove(movingCardIdx);
                }

                movingCardIdx = -1;
                break;
        }
        invalidate();
        return true;
    }

    //Build a deck
    private void initCards()
    {
        for( int i = 0; i < 4; i++ ) //loop through suits
        {
            for( int j = 102; j < 115; j++ ) //loop through ranks
            {
                int tempId = j + ( i * 100 ); //temp Id for card
                Card tempCard = new Card( tempId ); //temp card
                //get the resource id of image based on the filename and then load the bitmap
                //basically getting the image in the res/drawable directories
                int resourceId = getResources().getIdentifier("card" + tempId, "drawable",
                        myContext.getPackageName());
                Bitmap tempBitmap = BitmapFactory.decodeResource(myContext.getResources(),
                        resourceId);
                scaledCardW = (int) (screenW / 8 ); //THIS MIGHT NOT WORK
                scaledCardH = (int) (scaledCardW * 1.28);
                Bitmap scaledBitmap = Bitmap.createScaledBitmap( tempBitmap, scaledCardW,
                        scaledCardH, false);
                tempCard.setBitmap(scaledBitmap);
                deck.add(tempCard);

            }
        }
    }

    //Method to draw a card from the deck and add it to the hand
    private void drawCard( List<Card> handToDraw)
    {
        handToDraw.add(0, deck.get(0));
        deck.remove(0);
        if( deck.isEmpty() )
        {
            for( int i = discardPile.size() -1; i > 0; i--)
            {
                deck.add(discardPile.get(i));
                discardPile.remove(i);
                Collections.shuffle(deck, new Random()); //THIS METHOD OMG SO GOOD
            }
        }
    }

    //Method to deal the cards to each player
    private void dealCards()
    {
        Collections.shuffle( deck, new Random() );
        for( int i = 0; i < 7; i++ )
        {
            drawCard(myHand);
            drawCard(oppHand);
        }
    }

}
