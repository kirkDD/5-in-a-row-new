package com.example.a5_in_a_row_app;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {

    GameBoardView boardView;
    FiveInARowGame game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        playSplashScreen();
    }

    void playSplashScreen() {
        View v = new SplashScreenView(this);
        setContentView(v);
        v.setOnClickListener(view -> {
            startMain();
        });
    }


    void startMain() {
        setContentView(R.layout.activity_main);
        setUpBoardView();
        // experiemnt
        setUpStangeView();
        StackHistory sh = new StackHistory();

    }

    void setUpBoardView() {
        game = new FiveInARowGame(15);
        boardView = new GameBoardView(this, game);
        LinearLayout ll = findViewById(R.id.game_board_area);
        ll.addView(boardView);
    }

    void setUpStangeView() {
        LinearLayout ll = findViewById(R.id.strange_view);
        ll.addView(new StrangeView(this));
    }
}
