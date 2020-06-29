package com.example.a5_in_a_row_app;


import android.widget.TextView;

import java.lang.Math;
import java.lang.StringBuilder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class FiveInARowGame {

    static final int WHITE = -1;
    static final int BLACK = 1;
    static final int EMPTY = 0;


    // the game board
    private int[][] board;

    // game board as a list
    private Map<Integer, List<int[]>> boardAsList;

    // 1 - player A
    // -1 - player B
    // 0 - cannot play anymore, maybe a win?
    private int turn;

    // 0 -> playing
    // 1 -> 1 wins
    // -1 -> -1 wins
    // 2 -> draw
    private int gameState;


    /**
     * test main class
     */
    public static void main(String[] args) {
        FiveInARowGame game = new FiveInARowGame(10);
        System.out.println(game.makeMove(0, 0, 1));
        System.out.println(game.makeMove(5, 0, -1));
        System.out.println(game.makeMove(0, 1, 1));
        System.out.println(game.makeMove(5, 1, -1));
        System.out.println(game.makeMove(0, 2, 1));
        System.out.println(game.makeMove(5, 2, -1));
        System.out.println(game.makeMove(0, 3, 1));
        System.out.println(game.makeMove(5, 3, -1));
        System.out.println(game.makeMove(0, 4, 1));
        System.out.println(game.makeMove(5, 4, -1));
        System.out.println(game.getResult());
        System.out.println(game);
    }

    /**
     * initialize Five-in-a-row game board
     * player with id 1 goes first
     * @param  boardSize the size of board
     * @return           [description]
     */
    public FiveInARowGame(int boardSize) {
        board = new int[boardSize][boardSize];
        turn = 1;
        gameState = 0;
        boardAsList = new HashMap<Integer, List<int[]>>();
        boardAsList.put(1, new ArrayList<int[]>());
        boardAsList.put(-1, new ArrayList<int[]>());
    }

    /**
     * reset the game
     * only when game is
     */
    public String reset() {
        return hardReset();
    }
    private String hardReset() {
        // reset no matter what
        board = new int[board.length][board.length];
        turn = 1;
        gameState = 0;
        boardAsList.clear();
        boardAsList.put(1, new ArrayList<int[]>());
        boardAsList.put(-1, new ArrayList<int[]>());
        return "good";
    }


    /**
     * rep expose and return the board
     */
    public int[][] getBoard() {
        return this.board;
    }
    /**
     * rep expose and return the board as list
     */
    public Map<Integer, List<int[]>> getBoardAsList() {
        return this.boardAsList;
    }

    /**
     * return the gameState
     * @return int
     */
    public int getResult() {
        return this.gameState;
    }

    /**
     * make a move in game
     * @param  player int either 1 or -1
     * @param  x      [description]
     * @param  y      [description]
     * @return        a status code in string
     */
    public String makeMove(int x, int y, int player) {
        System.out.println("Game: player " + player + " making move " + x + ", " + y);
        if (turn == 0 || gameState != 0) {
            return "wrong";
        }
        if (x > board.length || y > board[0].length) {
            return "bad";
        }
        if (board[x][y] == EMPTY && turn == player) {
            board[x][y] = player;
            turn = -turn;
            boardAsList.get(player).add(new int[]{x, y});
//            System.out.println("move played");
            this.updateGameState();
            return "good";
        }
        return "bad";
    }

    /**
     * unmake a move in game
     * @param  x      [description]
     * @param  y      [description]
     */
    public void unmakeMove(int x, int y) {
        System.out.println("unmaking move " + x + ", " + y);
        if (turn == 0 || gameState != 0) {
            return;
        }
        if (x > board.length || y > board[0].length) {
            return;
        }
        if (board[x][y] != 0) {
            int player = board[x][y];
            board[x][y] = 0;
            turn = -turn;
            Objects.requireNonNull(boardAsList.get(player)).remove(new int[]{x, y});
            System.out.println("undo done");
        }
    }

    /**
     * see if someone wins somehow
     * @modifies gameState
     */
    private void updateGameState() {
        // check if -1 wins O(4n)
        if (this.gameState != 0) {
            return;
            // game is not going
        }

        if (checkGridFor(WHITE)) {
            this.gameState = WHITE;
        } else if (checkGridFor(BLACK)) {
            this.gameState = BLACK;
        } else {
            // check draw
            for (int i = 0; i < board.length; i++) {
                for (int j = 0; j < board[0].length; j++) {
                    if (board[i][j] == EMPTY) {
                        // there are empty space to play
                        return;
                    }
                }
            }
            this.gameState = 2;
        }
    }

    /**
     *  return the current game state
     */
    public int getGameState() {
        return this.gameState;
    }

    // see if player wins
    private boolean checkGridFor(int player) {
        // check vertical and horizontal
        for (int i = 0; i < board.length; i++) {	// rows?
            int row = 0;
            int col = 0;
            for (int j = 0; j < board[0].length; j++) {
                row = (board[i][j] == player) ? row + 1 : 0;
                col = (board[j][i] == player) ? col + 1 : 0;
                if (row == 5 || col == 5) {
                    return true;
                }
            }
        }
        // check both diagonal
        for (int index = board.length - 1; index > - board.length; index--) {
            int j = Math.max(0, -index);
            int i = (j == 0) ? index : 0;
            int countA = 0;
            int countB = 0;
            for (; i < board.length && j < board[0].length; i++, j++) {
                countA = (board[i][j] == player)? countA + 1 : 0;
                countB = (board[board.length - 1 - i][j] == player)? countB + 1 : 0;
                if (countA == 5 || countB == 5) {
                    return true;
                }
            }
        }
        return false;
    }




    ///////////////////
    // for debugging //
    ///////////////////
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();

        s.append("******************\n");
        // add info
        s.append("gameState: ").append(this.gameState).append("\n");
        s.append("turn: ").append(this.turn).append("\n");

        // add the board
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                switch (board[i][j]) {
                    case EMPTY:
                        s.append('_');
                        break;
                    case BLACK:
                        s.append('x');
                        break;
                    case WHITE:
                        s.append('o');
                        break;
                    default:
                        System.out.println("what the heck is no the board?");
                }
            }
            s.append("\n");
        }
        s.append("******************\n");
        return s.toString();
    }


    public int nextPlayer() {
        return this.turn;
    }


    void setBoard(int[] bx, int[] by, int[] wx, int[] wy) {
        hardReset();
        for (int i = 0;; i++) {
            // black go first
            if (i > bx.length - 1) return;
            makeMove(bx[i], by[i], nextPlayer());
            if (i > wx.length - 1) return;
            makeMove(wx[i], wy[i], nextPlayer());
        }
    }

}
