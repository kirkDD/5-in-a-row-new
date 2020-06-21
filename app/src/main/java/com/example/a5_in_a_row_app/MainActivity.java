package com.example.a5_in_a_row_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    GameBoardView boardView;
    FiveInARowGame game;
    StackHistory history;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        history = new StackHistory();
        playSplashScreen();

    }

    /**
     * Redoes the most recently undone action (if any).
     */
    protected void redo() {
        Pair<Integer, Integer> action = history.redo();
        if (action != null) {
            game.makeMove(action.first, action.second, game.nextPlayer());
        }
    }

    /**
     * Undoes the most recently (re)done action (if reversible).
     */
    protected void undo() {
        Pair<Integer, Integer> action = history.undo();
        if (action != null) {
            game.unmakeMove(action.first, action.second);
        }
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
        findViewById(R.id.redo_button).setOnClickListener((v) -> undo());
        findViewById(R.id.redo_button).setOnClickListener((v) -> redo());
    }

    void setUpBoardView() {
        game = new FiveInARowGame(15);
        boardView = new GameBoardView(this, game, history);
        LinearLayout ll = findViewById(R.id.game_board_area);
        ll.addView(boardView);
    }

    void setUpStangeView() {
        LinearLayout ll = findViewById(R.id.strange_view);
        ll.addView(new StrangeView(this));
    }
}
