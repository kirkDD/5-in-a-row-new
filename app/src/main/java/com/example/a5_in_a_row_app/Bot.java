package com.example.a5_in_a_row_app;

import android.graphics.Point;

import java.io.File;

public abstract class Bot {

    int MY_ID;
    Bot(int playerID) {
        MY_ID = playerID;
    }

    Point makeMove(int[][] gameBoard) {
        return null;
    }

    String trashTalk(int[][] game) {
        return "wat?";
    }

    abstract String whatIsUrFileName();

    abstract void loadTrainedModelFromFile(File f);

}
