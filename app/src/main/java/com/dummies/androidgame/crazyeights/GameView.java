package com.dummies.androidgame.crazyeights;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

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
    private Bitmap nextCardButton; //button to cycle through hand when it has > 7 cards
    private ComputerPlayer computerPlayer = new ComputerPlayer();

    public GameView( Context context )
    {
        super(context);
        myContext = context;
        scale = myContext.getResources().getDisplayMetrics().density;

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
                tempBitmap, scaledCardW, scaledCardH, false);
        nextCardButton = BitmapFactory.decodeResource(
                myContext.getResources(), R.drawable.arrow_next);

        initCards();
        dealCards();
        drawCard(discardPile); //move top card from draw pile to discardPile

        validSuit = discardPile.get(0).getSuit();
        validRank = discardPile.get(0).getRank();
        myTurn = new Random().nextBoolean(); //randomly determine who goes first
        if( !myTurn )
        {
            makeComputerPlay();
        }

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
        if( myHand.size() > 7 )
        {
            canvas.drawBitmap(nextCardButton, screenW - nextCardButton.getWidth() - (30 * scale),
                    screenH - nextCardButton.getHeight() - scaledCardH - (90 * scale), null);
        }
        for(int i = 0; i < myHand.size(); i++)
        {
            if(i == movingCardIdx)
            {
                canvas.drawBitmap(myHand.get(i).getBitmap(), movingX, movingY, null);
            }
            else
            {
                if(i < 7)
                {
                    canvas.drawBitmap(myHand.get(i).getBitmap(), i * (scaledCardW + 5),
                            screenH - scaledCardH - whitePaint.getTextSize() - (50 * scale), null);
                }
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
        int X = (int) event.getX();
        int Y = (int) event.getY();

        switch (eventaction)
        {
            case MotionEvent.ACTION_DOWN:
                if(myTurn)
                {
                    //loop through player hand and see if player has touched the screen on a card
                    //  that's being displayed. If so, assign the index of that card to movingCardIdx
                    //      as well as to the current X and Y positions to the movingX and movingY
                    for(int i = 0; i < 7; i++)
                    {
                        if(X > i * (scaledCardW + 5) && X < i * (scaledCardW + 5) + scaledCardW
                                && Y > screenH - scaledCardH - whitePaint.getTextSize() -
                                (50 * scale))
                        {
                            movingCardIdx = i;
                            movingX = X - (int) (30 * scale);
                            movingY = Y - (int) (70 * scale);
                        }
                    }
                }
                break;
            //keep track of X and Y as player moves finger across screen. Use for drawing card bmp
            case MotionEvent.ACTION_MOVE:
                movingX = X - (int) (30 * scale);
                movingY = Y - (int) (70 * scale);
                break;
            //reset movingCardIdx when player lifts finger from the screen
            case MotionEvent.ACTION_UP:
                if (movingCardIdx > -1 &&
                        X > (screenW/2)-(100*scale) &&
                        X < (screenW/2)+(100*scale) &&
                        Y > (screenH/2)-(100*scale) &&
                        Y < (screenH/2)+(100*scale) &&
                        (myHand.get(movingCardIdx).getRank() == 8 ||
                                myHand.get(movingCardIdx).getRank() == validRank ||
                                myHand.get(movingCardIdx).getSuit() == validSuit))
                {
                    validRank = myHand.get(movingCardIdx).getRank();
                    validSuit = myHand.get(movingCardIdx).getSuit();
                    discardPile.add(0, myHand.get(movingCardIdx));
                    myHand.remove(movingCardIdx);
                    if (myHand.isEmpty()) {
                        //endHand();
                    } else {
                        if (validRank == 8) {
                            showChooseSuitDialog();
                        } else {
                            myTurn = false;
                            makeComputerPlay();
                        }
                    }
                }
                if (movingCardIdx == -1 && myTurn &&
                        X > (screenW/2)-(100*scale) &&
                        X < (screenW/2)+(100*scale) &&
                        Y > (screenH/2)-(100*scale) &&
                        Y < (screenH/2)+(100*scale))
                {
                    if (checkForValidDraw()) {
                        drawCard(myHand);
                    } else {
                        Toast.makeText(myContext, "You have a valid play.", Toast.LENGTH_SHORT).show();
                    }
                }
                if (myHand.size() > 7 &&
                        X > screenW-nextCardButton.getWidth()-(30*scale) &&
                        Y > screenH-nextCardButton.getHeight()-scaledCardH-(90*scale) &&
                        Y < screenH-nextCardButton.getHeight()-scaledCardH-(60*scale)) {
                    Collections.rotate(myHand, 1);
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

    //Method to show the dialog box to choose suits when an 8 is played
    private void showChooseSuitDialog()
    {
        final Dialog chooseSuitDialog = new Dialog(myContext);
        chooseSuitDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        chooseSuitDialog.setContentView(R.layout.choose_suit_dialog);

        final Spinner suitSpinner = (Spinner) chooseSuitDialog.findViewById(R.id.suitSpinner);

        ArrayAdapter<CharSequence> adapter =
                ArrayAdapter.createFromResource(myContext, R.array.suits,
                        android.R.layout.simple_spinner_dropdown_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        suitSpinner.setAdapter(adapter);

        Button okButton =
                (Button) chooseSuitDialog.findViewById(R.id.okButton);
        okButton.setOnClickListener(new OnClickListener()
        {
            public void onClick(View view)
            {
                validSuit = (suitSpinner.getSelectedItemPosition() + 1) * 100;
                String suitText = "";
                if( validSuit == 100)
                {
                    suitText = "Diamonds";
                }
                else if (validSuit == 200 )
                {
                    suitText = "Clubs";
                }
                else if (validSuit == 300)
                {
                    suitText = "Hearts";
                }
                else
                {
                    suitText = "Spades";
                }

                chooseSuitDialog.dismiss();
                Toast.makeText(myContext, "You chose " + suitText, Toast.LENGTH_SHORT ).show();
                myTurn = false;
                makeComputerPlay();
            }
        });
        chooseSuitDialog.show();
    }

    private boolean checkForValidDraw()
    {
        boolean canDraw = true;
        for( int i = 0; i < myHand.size(); i++)
        {
            int tempId = myHand.get(i).getId();
            int tempRank = myHand.get(i).getRank();
            int tempSuit = myHand.get(i).getSuit();

            if(validSuit == tempSuit || validRank == tempRank ||
                    tempId == 108 || tempId == 208 || tempId == 308 || tempId == 408)
            {
                canDraw = false;
            }
        }
        return canDraw;

    }

    private void makeComputerPlay()
    {
        int tempPlay = 0;
        while( tempPlay ==0)
        {
            tempPlay = computerPlayer.makePlay(oppHand, validSuit, validRank);
            if(tempPlay == 0)
            {
                drawCard(oppHand);
            }
        }
        if( tempPlay == 108 || tempPlay == 208 || tempPlay == 308 || tempPlay == 408)
        {
            validRank = 8;
            validSuit = computerPlayer.chooseSuit(oppHand);
            String suitText = "";
            if(validSuit == 100)
            {
                suitText = "Diamonds";
            }
            else if(validSuit == 200)
            {
                suitText = "Clubs";
            }
            else if(validSuit == 300)
            {
                suitText = "Hearts";
            }
            else
            {
                suitText = "Spades";
            }
            Toast.makeText(myContext, "Computer chose " + suitText, Toast.LENGTH_SHORT).show();
        }
        else
        {
            validSuit = Math.round( (tempPlay / 100) * 100);
            validRank = tempPlay - validSuit;
        }
        for(int i = 0; i < oppHand.size(); i++)
        {
            Card tempCard = oppHand.get(i);
            if(tempPlay == tempCard.getId())
            {
                discardPile.add(0, oppHand.get(i));
                oppHand.remove(i);
            }
        }
        myTurn = true;
    }

}
