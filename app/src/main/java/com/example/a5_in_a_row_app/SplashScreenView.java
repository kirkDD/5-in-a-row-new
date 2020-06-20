package com.example.a5_in_a_row_app;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import java.util.Random;

public class SplashScreenView extends View {

    Paint b;

    float W;
    float H;

    Drawable black;
    Drawable white;
    Drawable smile;
    boolean canSmile;
    ClipDrawable text;
    private OnClickListener click;

    Random r;
    int nextLevel;

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        click = l;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            text.setLevel(10000);
            blink = true;
            invalidate();
            new Handler().postDelayed(() -> click.onClick(this), 800);
        }
        return true;
    }

    public SplashScreenView(Context context) {
        super(context);
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        black = context.getDrawable(R.drawable.black_piece);
        white = context.getDrawable(R.drawable.white_piece);
        smile = context.getDrawable(R.drawable.smile);
        r = new Random();
        b = new Paint();
        // get pngs
        text = new ClipDrawable(context.getDrawable(R.drawable.splash_text2), 3, ClipDrawable.HORIZONTAL);
        nextLevel = 0;
        text.setLevel(nextLevel);
        setNextLevel();
    }

    void setNextLevel() {
        if (text.getLevel() >= 10000) return;
        nextLevel = r.nextInt(10000 - text.getLevel()) + text.getLevel();
        if (nextLevel > 9900) {
            nextLevel = 10000;
        }
        ValueAnimator va = ValueAnimator.ofInt(text.getLevel(), nextLevel);
        va.setDuration(2 * (nextLevel - text.getLevel()) / (r.nextInt(5) + 2));
        va.addUpdateListener(thing -> {
            int newV = (int) thing.getAnimatedValue();
            if (text.getLevel() < newV) {
                text.setLevel(newV);
                invalidate();
            }
        });
        va.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (nextLevel != 10000) {
                    setNextLevel();
                } else {
                    canSmile = true;
                    new Handler().postDelayed(() -> click.onClick(null), 800);
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        try {
            Thread.sleep(r.nextInt(600) + 100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        va.start();
    }

    void initDims() {
        W = Math.max(getWidth() - 200, 0);
        H = getHeight();
        // magic numbers
        float w = text.getIntrinsicWidth();
        float h = text.getIntrinsicHeight();
        float ratio = w / W;
        h = h / ratio;
        w = W;
        text.setBounds((int) (-w / 2), (int) (-h / 2), (int) w / 2, (int) h / 2);
        smile.setBounds((int) (-w / 2), (int) (-h / 1.2), (int) w / 2, (int) (h / 1.2));
        W = getWidth();
        float size = Math.min(W, H) / 4;
        black.setBounds((int) (-size * 1.5), (int) (-size / 2), (int) (-size * 0.5), (int) (size / 2));
        white.setBounds((int) (size * 1.5), (int) (-size / 2), (int) (size * 0.5), (int) (size / 2));
    }

    int blinkCounter = 0;
    boolean blink;
    @Override
    protected void onDraw(Canvas canvas) {
        if (W == 0) initDims();
        canvas.translate(W / 2, H / 1.5f);
        if (!canSmile) {
            text.draw(canvas);
        } else {
            smile.draw(canvas);
        }
        canvas.translate(-W / 2, -H / 1.5f);
        // draw the image
        canvas.translate(W / 2, H / 3);
        // blinking?
        if (blinkCounter > 4) {
            blinkCounter = 0;
            blink = r.nextInt(10000) + 100 < text.getLevel();
        } else {
            blinkCounter++;
        }
        if (blink) {
            black.draw(canvas);
            white.draw(canvas);
        }
        canvas.translate(- W / 2, - H / 3);
    }

}
