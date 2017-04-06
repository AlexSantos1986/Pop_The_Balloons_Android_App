package com.alexsantos.gameappfirebase;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.CycleInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.alexsantos.gameappfirebase.utilities.PixelBalloon;


/**
 * Created by Alex on 06/04/2017.
 */

public class Balloon extends android.support.v7.widget.AppCompatImageView implements Animator.AnimatorListener, ValueAnimator.AnimatorUpdateListener {

    ValueAnimator mAnimator;
    private BalloonListener mListener;
    private boolean mPopped;

    public Balloon(Context context){
        super(context);
    }

    public Balloon(Context context, int color, int height){
        super(context);

        mListener = (BalloonListener) context;

        this.setImageResource(R.drawable.balloon);
        this.setColorFilter(color);

        int width = height / 2;
        int dpHeight = PixelBalloon.pixelToBalloons(height, context);
        int dpWidth = PixelBalloon.pixelToBalloons(width , context);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(dpWidth, dpHeight);
    }
    public void moveBalloon(int screenHeight , int duration){

        mAnimator = new ValueAnimator();
        mAnimator.setDuration(duration);
        mAnimator.setFloatValues(screenHeight, 0f);
        mAnimator.setInterpolator(new LinearInterpolator());
        mAnimator.setTarget(this);
        mAnimator.addListener(this);
        mAnimator.addUpdateListener(this);
        mAnimator.start();

    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {

        setY((Float) animation.getAnimatedValue());
    }

    @Override
    public void onAnimationStart(Animator animation) {

    }

    @Override
    public void onAnimationEnd(Animator animation) {

        if(!mPopped){
            mListener.popBalloon(this, false);
        }

    }

    @Override
    public void onAnimationCancel(Animator animation) {

    }

    @Override
    public void onAnimationRepeat(Animator animation) {


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if(!mPopped && event.getAction() == MotionEvent.ACTION_DOWN){
            mListener.popBalloon(this, true);
            mPopped =true;
            mAnimator.cancel();

        }
        return super.onTouchEvent(event);
    }

    public void setPopped(boolean b) {
        mPopped = b;
        if(b){
            mAnimator.cancel();
        }
    }

    public interface BalloonListener{
        void popBalloon(Balloon balloon, boolean userTouch);
    }
}
