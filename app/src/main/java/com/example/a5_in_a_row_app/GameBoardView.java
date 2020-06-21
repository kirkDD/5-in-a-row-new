package com.example.a5_in_a_row_app;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class GameBoardView extends View {

    Paint brush;
    float numTileOneSide;
    float size;
    float tileSize;

    // from parent
    FiveInARowGame game;
    StackHistory history;

    Drawable BLACK_PNG;
    Drawable WHITE_PNG;

    // the current state of the board(selecting or not)
    State state;
    // the current location of selection on the board
    Pair<Integer, Integer> location;
    List<GameCompletedListener> gameCompletedListeners;
    /**
     * class which defines a listener to be called when the game is over(someone wins)
     */
    public interface GameCompletedListener {
        void onGameCompleted(int gameState);
    }
    public GameBoardView(Context context, FiveInARowGame game, StackHistory history) {

        super(context);
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        // initialize
        this.game = game;
        this.history = history;
        gameCompletedListeners = new ArrayList<>();
        brush = new Paint();
        brush.setStrokeWidth(3);
        brush.setColor(Color.DKGRAY);

        BLACK_PNG = getResources().getDrawable(R.drawable.black_piece, null);
        WHITE_PNG = getResources().getDrawable(R.drawable.white_piece, null);

        state = State.START;
        location = null;
    }

    void initDims() {
        size = Math.min(getWidth(), getHeight());
        numTileOneSide = game.getBoard().length;
        tileSize =  size / numTileOneSide;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //get location of the current selection on the board
        Pair<Integer, Integer> current = essentialGeometry(new PointF(event.getX(), event.getY()));
        switch(state) {
            case START:
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    state = State.SELECTING;
                    updateModel(current);
                    invalidate();
                    return true;
                }
            case SELECTING:
                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    updateModel(current);
                    invalidate();
                    return true;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    registerMove(event.getX(), event.getY());
                    invalidate();
                    state = State.START;
                    location = null;
                    return true;
                }
                break;
            default:
                break;
        }
        return false;
    }

    // updates the current location of selection
    private void updateModel(Pair<Integer, Integer> p) {
        if (p != location) {
            location = p;
        }
    }
    void registerMove(float xPos, float yPos) {
        int x = (int) (xPos / tileSize);
        int y = (int) (yPos / tileSize);
        if (x < numTileOneSide && y < numTileOneSide) {
            if (game.makeMove(x, y, game.nextPlayer()).equals("good")) {
                history.addAction(Pair.create(x, y));
            }
            if(game.getGameState() != 0) {
                invokeGameCompletedListeners(game.getGameState());
            }
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        if (size == 0) initDims();
        // how to draw a board?
        // game != null
        // center it horizontally
        canvas.translate((getWidth() - size) / 2f, 0);
        int[][] board = game.getBoard();
        brush.setStyle(Paint.Style.FILL);
        brush.setColor(Color.GRAY);
        canvas.drawRect(0, 0, size, size, brush);
        brush.setStyle(Paint.Style.STROKE);
        brush.setColor(Color.BLACK);
        canvas.drawRect(0, 0, size, size, brush);
        for (int i = 0; i < numTileOneSide; i++) {
            for (int j = 0; j < numTileOneSide; j++) {
                float x = i * tileSize;
                float y = j * tileSize;
                canvas.drawRect(x, y, x + tileSize, y + tileSize, brush);
                drawPiece(canvas, x, y, board[i][j]);
            }
        }
        // draw the piece that is being put
        try {
            if (location != null && board[location.first][location.second] == FiveInARowGame.EMPTY) {
                drawPiece(canvas, (float) (location.first * tileSize), (float) (location.second * tileSize), game.nextPlayer());
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("haha Y?");
        }
    }

    // draw piece at specific location on board
    // if playerId is neither, draw nothing
    void drawPiece(Canvas canvas, float x, float y, int playerId) {
        if (playerId == FiveInARowGame.BLACK) {
            BLACK_PNG.setBounds((int) x+1, (int) y+1, (int) (x + tileSize) - 1, (int) (y + tileSize) - 1);
            BLACK_PNG.draw(canvas);
        } else if (playerId == FiveInARowGame.WHITE) {
            WHITE_PNG.setBounds((int) x+1, (int) y+1, (int) (x + tileSize) - 1, (int) (y + tileSize) - 1);
            WHITE_PNG.draw(canvas);
        }
    }

    public Pair<Integer, Integer> essentialGeometry(PointF p) {
        return Pair.create((int) ((p.x - ((getWidth() - size) / 2f)) / tileSize), (int) (p.y / tileSize));
    }

    /**
     * Registers a new listener
     *
     * @param l New listener (should not be null).
     */
    public final void addGameCompletedListener(@NonNull GameCompletedListener l) {
        gameCompletedListeners.add(l);
    }

    /**
     * Removes a GameCompletedListener, if it exists
     *
     * @param l Listener that should be removed (should not be null).
     */
    public final void removeGameCompletedListener(GameCompletedListener l) {
        gameCompletedListeners.remove(l);
    }

    /**
     * Method that will notify all the registered listeners that the game has finished
     * @param gameState who wins : 1 -> black wins, -1 -> white wins, 2 -> draw
     */
    protected void invokeGameCompletedListeners(int gameState) {
        for (GameCompletedListener l : gameCompletedListeners) {
            l.onGameCompleted(gameState);
        }
    }
}
