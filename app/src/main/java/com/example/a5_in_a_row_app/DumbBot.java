package com.example.a5_in_a_row_app;

import android.graphics.Point;

import androidx.annotation.NonNull;

import java.util.Random;

public class DumbBot extends Bot {
    int myId;
    Random R = new Random();

    DumbBot(int id) {
        super(id);
    }

    @Override
    public Point makeMove(int[][] b) {
        Point nextMove = new Point(R.nextInt(b.length), R.nextInt(b[0].length));
        return nextMove;
    }

    @Override
    public String trashTalk(int[][] b) {
        return "your turn";
    }

    void loadTrainedModelFromString(String s) {

    }

    @NonNull
    @Override
    public String toString() {
        return super.toString();
    }
}
