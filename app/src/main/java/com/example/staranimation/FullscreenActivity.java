package com.example.staranimation;

import android.annotation.SuppressLint;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.drm.DrmStore;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends AppCompatActivity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (AUTO_HIDE) {
                        delayedHide(AUTO_HIDE_DELAY_MILLIS);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    view.performClick();
                    break;
                default:
                    break;
            }
            return false;
        }
    };

    //Declaration of fonts and widgets
    Typeface codystarFont;
    Typeface contrailFont;
    TextView txtTitle;
    Button btnDummy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen);

        //Set action bar to the center
        ActionBar myActionBar = getSupportActionBar();
        if (myActionBar != null) {
            myActionBar.setCustomView(R.layout.action_bar_center);
            myActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        }

        //Initialization of fonts and widgets
        codystarFont = Typeface.createFromAsset(getAssets(), "fonts/Codystar-Regular.ttf");
        contrailFont = Typeface.createFromAsset(getAssets(), "fonts/ContrailOne-Regular.ttf");
        txtTitle = (TextView) findViewById(R.id.txtTitle);
        btnDummy = (Button) findViewById(R.id.dummy_button);

        //Setup fonts
        txtTitle.setTypeface(contrailFont);
        btnDummy.setTypeface(codystarFont);

        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.fullscreen_content);

        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);
    }

    // Declaration of Variables
    AnimationDrawable starListAnimation;
    FrameLayout animationContainer;
    AnimationSet myAnimationSet;
    Animation fadeEffect;
    Animation rotateItself;
    Animation circleRotate;

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);

        // Setup star list animation
        ImageView starImage = (ImageView) findViewById(R.id.starImage);
        starImage.setBackgroundResource(R.drawable.star_animation_list);
        starListAnimation = (AnimationDrawable) starImage.getBackground();
        starListAnimation.start();

        //Initialization of the animation container
        animationContainer = (FrameLayout) findViewById(R.id.animationContainer);

        //Initialization of animations
        fadeEffect = AnimationUtils.loadAnimation(this, R.anim.fade_effect);
        rotateItself = AnimationUtils.loadAnimation(this, R.anim.rotate_itself);
        circleRotate = AnimationUtils.loadAnimation(this, R.anim.circle_rotate);

        //Initialize animation set and add animations in set
        myAnimationSet = new AnimationSet(true);
        myAnimationSet.addAnimation(fadeEffect);
        myAnimationSet.addAnimation(rotateItself);
        myAnimationSet.addAnimation(circleRotate);

        //Set durations for all animations added
        myAnimationSet.setDuration(1500);

        //Setup AnimationSet on starImage
        starImage.setAnimation(myAnimationSet);

        //Setup animation listener so that it loops infinitely
        myAnimationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                animation.reset();
                animation.start();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        //Start AnimationSet
        myAnimationSet.start();
    }

    private void toggle() {
        if (mVisible) {
            //Fullscreen - frame visible, but text invisible
            hide();
            mContentView.setAlpha(0);
            animationContainer.setVisibility(View.VISIBLE);
        } else {
            //Normal screen - frame invisible, but text visible
            show();
            mContentView.setAlpha(1);
            animationContainer.setVisibility(View.INVISIBLE);
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
}