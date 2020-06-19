package com.example.a5_in_a_row_app;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {

    GameBoardView boardView;
    FiveInARowGame game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpBoardView();

        // experiemnt
        setUpStangeView();
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
