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
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity{

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
            boardView.invalidate();
        }
    }

    /**
     * Undoes the most recently (re)done action (if reversible).
     */
    protected void undo() {
        Pair<Integer, Integer> action = history.undo();
        if (action != null) {
            game.unmakeMove(action.first, action.second);
            boardView.invalidate();
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
        // experiment
        setUpStangeView();
        findViewById(R.id.undo_button).setOnClickListener((v) -> undo());
        findViewById(R.id.redo_button).setOnClickListener((v) -> redo());
        boardView.addGameCompletedListener(this::onGameCompleted);
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

    /**
     *  Called when the game is completed
     */
    void onGameCompleted(int gameState) {
        setContentView(R.layout.activity_main);
        ConstraintLayout finishedScreen = findViewById(R.id.finished_screen);
        TextView finishedText = findViewById(R.id.instructionTextView);
        finishedText.setText("Somebody wins!!");
        finishedScreen.setVisibility(View.VISIBLE);
    }

    /**
     * Called before the activity is destroyed.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        boardView.removeGameCompletedListener(this::onGameCompleted);
    }
}
