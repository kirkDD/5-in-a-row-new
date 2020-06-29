package com.example.a5_in_a_row_app;

import android.graphics.Point;

import java.io.File;
import java.sql.PreparedStatement;
import java.util.Random;

public class NotAI extends Bot {
    NotAI(int playerID) {
        super(playerID);
    }

    Random RD = new Random();


    @Override
    Point makeMove(int[][] b) {
        // priority model
        // 1. check if i can win
        Point result = checkHorizontal4(b, MY_ID);
        if (result != null) return result;
        result = checkVertical4(b, MY_ID);
        if (result != null) return result;
        result = checkDiagonal4(b, MY_ID);
        if (result != null) return result;

        // 2. check if they will win in next turn
        result = checkHorizontal4(b, -MY_ID);
        if (result != null) return result;
        result = checkVertical4(b, -MY_ID);
        if (result != null) return result;
        result = checkDiagonal4(b, -MY_ID);
        if (result != null) return result;

        // 3. if I have free 3 ones
        System.out.println("NotAI: random move");
        return new Point(RD.nextInt(b.length), RD.nextInt(b[0].length));

        // 4. if they have free 3 ones


    }

    Point checkHorizontal4(int[][] b, int id) {
        for (int i = 0; i < b.length; i++) {
            int[] counts = new int[3];
            for (int j = 0; j < 5; j++) {
                counts[b[i][j] + 1]++;
            }
            if (counts[id + 1] == 4 && counts[1] == 1) {
                for (int j = 0; j < 5; j++) {
                    if (b[i][j] == 0) return new Point(i, j);
                }
            }
            for (int j = 5; j < b[0].length; j++) {
                counts[b[i][j] + 1]++;
                counts[b[i][j - 5] + 1]--;
                if (counts[id + 1] == 4 && counts[1] == 1) {
                    for (; j > 0; j--) {
                        if (b[i][j] == 0) return new Point(i, j);
                    }
                }
            }
        }
        return null;
    }

    Point checkVertical4(int[][] b, int id) {
        for (int i = 0; i < b.length; i++) {
            int[] counts = new int[3];
            for (int j = 0; j < 5; j++) {
                counts[b[j][i] + 1]++;
            }
            if (counts[id + 1] == 4 && counts[1] == 1) {
                for (int j = 0; j < 5; j++) {
                    if (b[j][i] == 0) return new Point(j, i);
                }
            }
            for (int j = 5; j < b[0].length; j++) {
                counts[b[j][i] + 1]++;
                counts[b[j - 5][i] + 1]--;
                if (counts[id + 1] == 4 && counts[1] == 1) {
                    for (; j > 4; j--) {
                        if (b[j][i] == 0) return new Point(j, i);
                    }
                }
            }
        }
        return null;
    }

    Point checkDiagonal4(int[][] b, int id) {
        Point result;
        for (int i = 0; i < b.length; i++) {
            result = checkBothDiagonal(b, i, 0, id);
            if (result != null) return result;
            result = checkBothDiagonal(b, i, b[0].length - 1, id);
            if (result != null) return result;
            result = checkBothDiagonal(b, 0, i, id);
            if (result != null) return result;
        }
        return null;
    }

    Point checkBothDiagonal(int[][] b, int i, int j, int id) {
        Point result = checkBothDiagonal_helper(b, i, j, 1, id);
        if (result != null) return result;
        return checkBothDiagonal_helper(b, i, j, -1, id);
    }

    // start top left
    Point checkBothDiagonal_helper(int[][] b, int i, int j, int dir, int id) {
        int count = 0;
        int[] window = new int[3];
        while (i < b.length && j < b[0].length && i > -1 && j > -1) {
            window[1 + b[i][j]]++;
            count++;
            if (count > 5) {
                count--;
                window[1 + b[i - 5][j - 5 * dir]]--;
            }
            // check
            if (window[id + 1] == 4 && window[1] == 1) {
                // got it
                for (int k = 0; k < 5; k++) {
                    if (b[i - k][j - k * dir] == 0) return new Point(i, j);
                }
            }
            // update
            i += 1;
            j += dir;
        }
        return null;
    }



    @Override
    String trashTalk(int[][] game) {
        return "dwdwfavklanwfia";
    }

    @Override
    String whatIsUrFileName() {
        return "nope";
    }

    @Override
    void loadTrainedModelFromFile(File f) {
        // f
    }
}
