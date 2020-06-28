package com.example.a5_in_a_row_app;

import android.graphics.Point;

import java.io.File;
import java.util.Random;
import java.util.Scanner;

public class DumbBot extends Bot {
    String nextSpil = "your turn";
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

    @Override
    String whatIsUrFileName() {
        return "i.am.not.dumb";
    }

    void loadTrainedModelFromFile(File f) {
        try {
            Scanner s = new Scanner(f);
            while (s.hasNextLine()) System.out.println("Reading: " + s.nextLine());
        } catch (Exception e) {
            nextSpil = "wtf, gimme my file";
        }
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
