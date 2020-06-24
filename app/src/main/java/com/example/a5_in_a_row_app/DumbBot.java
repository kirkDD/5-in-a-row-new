package com.example.a5_in_a_row_app;

import android.graphics.Point;

public class DumbBot extends Bot {
    int myId;

    DumbBot(int id) {
        super(id);
    }

    @Override
    public Point makeMove(int[][] b) {
        Point nextMove = new Point();
        return nextMove;
    }

    @Override
    public String trashTalk(int[][] b) {
        return "your turn";
    }
}
