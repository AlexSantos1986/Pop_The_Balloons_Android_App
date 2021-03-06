package com.alexsantos.gameappfirebase;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.animation.ValueAnimatorCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AnticipateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements Balloon.BalloonListener {


    public static final int MIN_ANIMATION_DELAY =500;
    public static final int MAX_ANIMATION_DELAY =1500;
    public static final int MIN_ANIMATION_DURATION =1000;
    public static final int MAX_ANIMATION_DURATION =8000;
    public static final int NUMBERS_OF_PINS=5;

    ViewGroup mViewGroup;

    private int[] mBallonColors = new int[3];
    int nextColor, mScreenWidth, mScreenHeight;
    private int mLevel;
    private int mScore;
    private int mPinsUsed;
    TextView mScoreDisplay, mLevelDisplay;
    private List<ImageView> mPinImages = new ArrayList<>();
    private List<Balloon> mBalloons = new ArrayList<>();
    private Button mGoButton;
    private boolean mPlaying;
    private boolean mGameStopped = true;


    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private int mBalloonsPopped;
    private int BALLOONS_PER_LEVEL = 3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBallonColors[0]= Color.argb(255, 255,0,0);
        mBallonColors[1]= Color.argb(255,0,255,0);
        mBallonColors[2]= Color.argb(255,0,0,255);

        getWindow().setBackgroundDrawableResource(R.drawable.city_background);

        mViewGroup = (ViewGroup) findViewById(R.id.activity_main);
        mScoreDisplay = (TextView) findViewById(R.id.score_display);
        mLevelDisplay = (TextView) findViewById(R.id.level_display);
        mPinImages.add((ImageView) findViewById(R.id.pushpin1));
        mPinImages.add((ImageView) findViewById(R.id.pushpin2));
        mPinImages.add((ImageView) findViewById(R.id.pushpin3));
        mPinImages.add((ImageView) findViewById(R.id.pushpin4));
        mPinImages.add((ImageView) findViewById(R.id.pushpin5));
        mGoButton= (Button) findViewById(R.id.go_button);

        updateDisplay();

        setToFullScreen();

        ViewTreeObserver viewTreeObserver =  mViewGroup.getViewTreeObserver();

        if(viewTreeObserver.isAlive()){

            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public void onGlobalLayout() {
                    mViewGroup.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    mScreenWidth = mViewGroup.getWidth();
                    mScreenHeight = mViewGroup.getHeight();
                }
            });
        }

        mViewGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setToFullScreen();
            }
        });




        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if(firebaseAuth.getCurrentUser() == null){

                    Intent registerIntent = new Intent(MainActivity.this, LoginActivity.class);
                    registerIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(registerIntent);

                }

            }
        };
    }
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    private void setToFullScreen(){
        ViewGroup root = (ViewGroup) findViewById(R.id.activity_main);

       root.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LOW_PROFILE
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setToFullScreen();
    }

    private void startGame(){

        setToFullScreen();
        mScore = 0;
        mLevel = 0;
        mPinsUsed = 0;

        for(ImageView pin : mPinImages){

            pin.setImageResource(R.drawable.pin);
        }
        mGameStopped = false;
        startLevel();

    }

    private void startLevel(){
        mLevel++;
        updateDisplay();
        BalloonLauncher laucher = new BalloonLauncher();
        laucher.execute(mLevel);
        mPlaying =true;
        mBalloonsPopped =0;
        mGoButton.setText("Stop Game");
    }

    private void finishLever(){

        Toast.makeText(this, String.format("You finished level %d",mLevel), Toast.LENGTH_SHORT).show();
        mPlaying=false;
        mGoButton.setText(String.format("Start level %d", mLevel +1));
    }

    public void goButton(View view) {

        if(mPlaying){

            gameOver(false);
        }else if(mGameStopped){

            startGame();
        }else{
            startLevel();
        }

    }

    @Override
    public void popBalloon(Balloon balloon, boolean userTouch) {

        mBalloonsPopped++;

        mViewGroup.removeView(balloon);
        mBalloons.remove(balloon);
        if(userTouch){

            mScore++;
        }else{
            mPinsUsed++;
            if(mPinsUsed <= mPinImages.size()){
                mPinImages.get(mPinsUsed -1).setImageResource(R.drawable.pin_off);

            }
            if(mPinsUsed == NUMBERS_OF_PINS){

                gameOver(true);
                return;
            }else{
                Toast.makeText(this, "Missed that one!!!", Toast.LENGTH_SHORT).show();

            }
        }
        updateDisplay();
        if(mBalloonsPopped == BALLOONS_PER_LEVEL){

            finishLever();

        }
    }

    private void gameOver(boolean b) {

        Toast.makeText(this, "Game Over!!!", Toast.LENGTH_SHORT).show();

        for(Balloon balloon : mBalloons){

            mViewGroup.removeView(balloon);
            balloon.setPopped(true);
        }
        mBalloons.clear();
        mPlaying = false;
        mGameStopped = true;
        mGoButton.setText("Start game!");


    }

    private void updateDisplay() {

        mScoreDisplay.setText(String.valueOf(mScore));
        mLevelDisplay.setText(String.valueOf(mLevel));

    }

    private class BalloonLauncher extends AsyncTask<Integer, Integer, Void> {

        @Override
        protected Void doInBackground(Integer... params) {

            if (params.length != 1) {
                throw new AssertionError(
                        "Expected 1 param for current level");
            }

            int level = params[0];
            int maxDelay = Math.max(MIN_ANIMATION_DELAY,
                    (MAX_ANIMATION_DELAY - ((level - 1) * 500)));
            int minDelay = maxDelay / 2;

            int balloonsLaunched = 0;
            while (mPlaying && balloonsLaunched < BALLOONS_PER_LEVEL) {

//              Get a random horizontal position for the next balloon
                Random random = new Random(new Date().getTime());
                int xPosition = random.nextInt(mScreenWidth - 200);
                publishProgress(xPosition);
                balloonsLaunched++;

//              Wait a random number of milliseconds before looping
                int delay = random.nextInt(minDelay) + minDelay;
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            return null;

        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            int xPosition = values[0];
            launchBalloon(xPosition);
        }

    }

    private void launchBalloon(int x) {

        Balloon balloon = new Balloon(this, mBallonColors[nextColor], 150);
        mBalloons.add(balloon);

        if (nextColor + 1 == mBallonColors.length) {
            nextColor = 0;
        } else {
            nextColor++;
        }

//      Set balloon vertical position and dimensions, add to container
        balloon.setX(x);
        balloon.setY(mScreenHeight + balloon.getHeight());
        mViewGroup.addView(balloon);

//      Let 'er fly
        int duration = Math.max(MIN_ANIMATION_DURATION, MAX_ANIMATION_DURATION - (mLevel * 1000));
        balloon.moveBalloon(mScreenHeight, duration);

    }


}
