package com.example.a5_in_a_row_app;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity{

    GameBoardView boardView;
    FiveInARowGame game;
    StackHistory history;
    Bot human;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("create");
        if (savedInstanceState != null) {
            // configuration changed instead of new activity
            startMain();
            game.setBoard(savedInstanceState.getIntArray("BX"), savedInstanceState.getIntArray("BY"),
                    savedInstanceState.getIntArray("WX"), savedInstanceState.getIntArray("WY"));
            return;
        }
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
        v.setOnClickListener(view -> startMain());
    }


    void startMain() {
        history = new StackHistory();
        setContentView(R.layout.activity_main);
        setUpBoardView();
        // experiment
        setUpStangeView();
        findViewById(R.id.undo_button).setOnClickListener((v) -> undo());
        findViewById(R.id.redo_button).setOnClickListener((v) -> redo());
        boardView.addGameCompletedListener(this::onGameCompleted);
    }

    void setUpBoardView() {
        // construct bot
        human = new DumbBot(FiveInARowGame.WHITE);

        game = new FiveInARowGame(15);
        boardView = new GameBoardView(this, game, history, human);
        LinearLayout ll = findViewById(R.id.game_board_area);
        ll.addView(boardView);
    }

    void setUpStangeView() {
        LinearLayout ll = findViewById(R.id.strange_view);
        ll.addView(new StrangeView(this));
    }

    /**
     *  Called when the game is completed
     * @param gameState : who wins : 1 -> black wins, -1 -> white wins, 2 -> draw
     */
    void onGameCompleted(int gameState) {
        setContentView(R.layout.activity_main);

        if (gameState == 1) {
            ConstraintLayout finishedScreen = findViewById(R.id.win_screen);
            TextView finishedText = findViewById(R.id.instructionTextViewWin);
            finishedText.setText("congratulations black wins");
            finishedScreen.setVisibility(View.VISIBLE);
            findViewById(R.id.play_again).setOnClickListener((v) -> reset());
            findViewById(R.id.return_home_win).setOnClickListener((v) -> quit());
        } else {
            ConstraintLayout finishedScreen = findViewById(R.id.loss_screen);
            TextView finishedText = findViewById(R.id.instructionTextViewLoss);
            finishedText.setText("congratulations white wins");
            finishedScreen.setVisibility(View.VISIBLE);
            findViewById(R.id.try_again).setOnClickListener((v) -> reset());
            findViewById(R.id.return_home_loss).setOnClickListener((v) -> quit());
        }
        findViewById(R.id.undo_button).setVisibility(View.INVISIBLE);
        findViewById(R.id.redo_button).setVisibility(View.INVISIBLE);
        findViewById(R.id.undo_button_text).setVisibility(View.INVISIBLE);
        findViewById(R.id.redo_button_text).setVisibility(View.INVISIBLE);
    }

    /**
     *  quit the game
     */
    void quit() {
        finish();
        System.exit(0);
    }

    /**
     *  restart the game
     */
    void reset() {
        startMain();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        System.out.println("config?");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        System.out.println("restore? Too late");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        System.out.println("save");
        // best it can do is to save some array primitives
        if (game == null) return;
        Map<Integer, List<int[]>> map = game.getBoardAsList();
        int[] black_x = new int[map.get(FiveInARowGame.BLACK).size()];
        int[] black_y = new int[map.get(FiveInARowGame.BLACK).size()];
        int[] white_x = new int[map.get(FiveInARowGame.WHITE).size()];
        int[] white_y = new int[map.get(FiveInARowGame.WHITE).size()];
        for (int i = 0; i < map.get(FiveInARowGame.BLACK).size(); i++) {
            black_x[i] = map.get(FiveInARowGame.BLACK).get(i)[0];
            black_y[i] = map.get(FiveInARowGame.BLACK).get(i)[1];
        }
        for (int i = 0; i < map.get(FiveInARowGame.WHITE).size(); i++) {
            white_x[i] = map.get(FiveInARowGame.WHITE).get(i)[0];
            white_y[i] = map.get(FiveInARowGame.WHITE).get(i)[1];
        }
        outState.putIntArray("BX", black_x);
        outState.putIntArray("BY", black_y);
        outState.putIntArray("WX", white_x);
        outState.putIntArray("WY", white_y);
        System.out.println("saved " + white_x.length);
    }

    /**
     * Called before the activity is destroyed.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (boardView != null) {
            boardView.removeGameCompletedListener(this::onGameCompleted);
            System.out.println("destroying");
        }
    }
}
