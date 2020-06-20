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

public class GameBoardView extends View {

    Paint brush;
    float numTileOneSide;
    float size;
    float tileSize;

    // from parent
    FiveInARowGame game;


    Drawable BLACK_PNG;
    Drawable WHITE_PNG;

    // the current state of the board(selecting or not)
    State state;
    // the current location of selection on the board
    Pair<Integer, Integer> location;

    public GameBoardView(Context context, FiveInARowGame game) {

        super(context);
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        // initialize
        this.game = game;
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
            game.makeMove(x, y, game.nextPlayer());
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
        brush.setColor(Color.DKGRAY);
        brush.setStyle(Paint.Style.STROKE);
        for (int i = 0; i < numTileOneSide; i++) {
            for (int j = 0; j < numTileOneSide; j++) {
                float x = i * tileSize;
                float y = j * tileSize;
                canvas.drawRect(x, y, x + tileSize, y + tileSize, brush);
                if (board[i][j] == FiveInARowGame.BLACK) {
                    BLACK_PNG.setBounds((int) x+1, (int) y+1, (int) (x + tileSize) - 1, (int) (y + tileSize) - 1);
                    BLACK_PNG.draw(canvas);
                } else if (board[i][j] == FiveInARowGame.WHITE) {
                    WHITE_PNG.setBounds((int) x+1, (int) y+1, (int) (x + tileSize) - 1, (int) (y + tileSize) - 1);
                    WHITE_PNG.draw(canvas);
                }
                if (location != null && i == location.first && j == location.second) {
                    if (game.nextPlayer() == FiveInARowGame.BLACK) {
                        BLACK_PNG.setBounds((int) x+1, (int) y+1, (int) (x + tileSize) - 1, (int) (y + tileSize) - 1);
                        BLACK_PNG.draw(canvas);
                    } else if (game.nextPlayer() == FiveInARowGame.WHITE) {
                        WHITE_PNG.setBounds((int) x+1, (int) y+1, (int) (x + tileSize) - 1, (int) (y + tileSize) - 1);
                        WHITE_PNG.draw(canvas);
                    }
                }
            }
        }
    }

    public Pair<Integer, Integer> essentialGeometry(PointF p) {
        return Pair.create((int) (p.x / tileSize), (int) (p.y / tileSize));
    }
}
