package com.example.a5_in_a_row_app;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Shader;
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

    LinearGradient gradient = new LinearGradient(0, 0, 100, 100, Color.RED
            , Color.BLUE, Shader.TileMode.MIRROR);

    // the current state of the board(selecting or not)
    State state;
    // the current location of selection on the board
    Pair<Integer, Integer> location;
    List<GameCompletedListener> gameCompletedListeners;
    // highlight the last played location
    Pair<Integer, Integer> lastMove;
    int lastMoveTimer;
    //bot
    Bot bot;
    boolean botThinking;
    /**
     * class which defines a listener to be called when the game is over(someone wins)
     */
    public interface GameCompletedListener {
        void onGameCompleted(int gameState);
    }
    public GameBoardView(Context context, FiveInARowGame game, StackHistory history, Bot bot) {

        super(context);
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        // initialize
        this.game = game;
        this.history = history;
        gameCompletedListeners = new ArrayList<>();
        // bot is white
        this.bot = bot;
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
        // skip if it is bot's move
        if (game.nextPlayer() == bot.MY_ID) return true;
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
        int x = (int) ((xPos - ((getWidth() - size) / 2f)) / tileSize);
        int y = (int) (yPos / tileSize);
        if (x < numTileOneSide && y < numTileOneSide) {
            if (game.makeMove(x, y, game.nextPlayer()).equals("good")) {
                history.addAction(Pair.create(x, y));
                botThinking = true;
                new Thread(() -> {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    while (!registerMove(bot.makeMove(game.getBoard()))) {
                        System.out.println("bot is crazy");
                    }
                    botThinking = false;
                    postInvalidate();
                }).start();
            }
            if(game.getGameState() != 0) {
                invokeGameCompletedListeners(game.getGameState());
            }
        }
    }

    // the bot calls this to make a move
    boolean registerMove(Point p) {
        if (p.x < numTileOneSide && p.y < numTileOneSide) {
            if (game.makeMove(p.x, p.y, game.nextPlayer()).equals("good")) {
                history.addAction(Pair.create(p.x, p.y));
                lastMove = new Pair<>(p.x, p.y);
                lastMoveTimer = 200;
            } else {
                return false;
            }
            if(game.getGameState() != 0) {
                invokeGameCompletedListeners(game.getGameState());
            }
            return true;
        } else {
            return false;
        }
    }

    float delta = 0;

    @Override
    protected void onDraw(Canvas canvas) {
        if (size == 0) initDims();
        // how to draw a board?
        // game != null
        // center it horizontally
        canvas.translate((getWidth() - size) / 2f, 0);
        int[][] board = game.getBoard();

        brush.setShader(gradient);
        brush.setStyle(Paint.Style.FILL);
        canvas.drawRect(0, 0, size, size, brush);

        if (game.nextPlayer() == FiveInARowGame.BLACK && delta < 1000) {
            postInvalidate();
            delta += 100;
            gradient = new LinearGradient(0 + delta, 0 + delta, 400 + delta, 400 + delta,
                    Color.BLACK, Color.WHITE, Shader.TileMode.CLAMP);
        } else if (game.nextPlayer() == FiveInARowGame.WHITE && delta > -400) {
            postInvalidate();
            delta -= 100;
            gradient = new LinearGradient(0 + delta, 0 + delta, 400 + delta, 400 + delta,
                    Color.BLACK, Color.WHITE, Shader.TileMode.CLAMP);
        }
        brush.setShader(null);

        brush.setColor(Color.GRAY);
        brush.setStyle(Paint.Style.FILL);
        for (int i = 0; i < numTileOneSide; i++) {
            for (int j = 0; j < numTileOneSide; j++) {
                float x = i * tileSize;
                float y = j * tileSize;
                canvas.drawRect(x + 2, y + 2, x + tileSize - 2, y + tileSize - 2, brush);
                drawPiece(canvas, x, y, board[i][j]);
            }
        }
        // draw the piece that is being put
        try {
            if (location != null && board[location.first][location.second] == FiveInARowGame.EMPTY) {
                drawPiece(canvas, location.first * tileSize, (location.second * tileSize), game.nextPlayer());
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("haha Y?");
            // the if check somehow throw this
        }
        // animate last move by bot
        if (lastMove != null && lastMoveTimer > 0) {
            float x = lastMove.first * tileSize;
            float y = lastMove.second * tileSize;
            brush.setColor(Color.BLACK);
            brush.setStyle(Paint.Style.FILL);
            brush.setAlpha((Math.min(lastMoveTimer, 255)));
            canvas.drawRect(x, y, x + tileSize, y + tileSize, brush);
            lastMoveTimer--;
            postInvalidate();
        }
    }

    // draw piece at specific location on board
    // if playerId is neither, draw nothing
    void drawPiece(Canvas canvas, float x, float y, int playerId) {
        if (playerId == FiveInARowGame.BLACK) {
            BLACK_PNG.setBounds((int) x + 3, (int) y + 3, (int) (x + tileSize) - 3, (int) (y + tileSize) - 3);
            BLACK_PNG.draw(canvas);
        } else if (playerId == FiveInARowGame.WHITE) {
            WHITE_PNG.setBounds((int) x + 3, (int) y + 3, (int) (x + tileSize) - 3, (int) (y + tileSize) - 3);
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
