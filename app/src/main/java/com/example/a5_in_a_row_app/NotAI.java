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
        // 1. win, 5 in a row
        Point result = generic(b, MY_ID, 5);
        if (result != null) return result;
        // they win
        result = generic(b, -MY_ID, 5);
        if (result != null) return result;

        // 2. 4 in a row
        result = generic(b, MY_ID, 4);
        if (result != null) return result;
        // they 4
        result = generic(b, -MY_ID, 4);
        if (result != null) return result;

        // 3. 3 in a row
        result = generic(b, MY_ID, 3);
        if (result != null) return result;
        // they 3
        result = generic(b, -MY_ID, 3);
        if (result != null) return result;

        // 3. 2 in a row
        result = generic(b, MY_ID, 2);
        if (result != null) return result;
        // they 2
        result = generic(b, -MY_ID, 2);
        if (result != null) return result;

        System.out.println("NotAI: random move");
        return new Point(RD.nextInt(b.length), RD.nextInt(b[0].length));


    }

    Point generic(int[][] b, int id, int num) {
        // horizontal
        for (int i = 0; i < b.length; i++) {
            int[] counts = new int[3];
            for (int j = 0; j < num - 1; j++) {
                counts[b[i][j] + 1]++;
            }
            for (int j = num - 1; j < b[0].length; j++) {
                counts[b[i][j] + 1]++;
                if (j > num - 1) counts[b[i][j - num] + 1]--;
                if (counts[id + 1] == num - 1 && counts[1] == 1) {
                    for (; j >= 0; j--) if (b[i][j] == 0) return new Point(i, j);
                }
            }
        }
        // vertical
        for (int j = 0; j < b[0].length; j++) {
            int[] w = new int[3];
            for (int i = 0; i < num - 1; i++) w[b[i][j] + 1]++;
            for (int i = num - 1; i < b.length; i++) {
                w[b[i][j] + 1]++;
                if (i > num - 1) w[b[i - num][j] + 1]--;
                // check
                if (w[id + 1] == num - 1 && w[1] == 1) {
                    for (; i >= 0; i--) if (b[i][j] == 0) return new Point(i, j);
                }
            }
        }
        // diagonal
        Point result;
        for (int i = 0; i < b.length; i++) {
            result = checkBothDiagonal(b, i, 0, id, num);
            if (result != null) return result;
            result = checkBothDiagonal(b, i, b[0].length - 1, id, num);
            if (result != null) return result;
            result = checkBothDiagonal(b, 0, i, id, num);
            if (result != null) return result;
        }
        return null;
    }

    Point checkHorizontal4(int[][] b, int id) {
        for (int i = 0; i < b.length; i++) {
            int[] counts = new int[3];
            for (int j = 0; j < 4; j++) {
                counts[b[i][j] + 1]++;
            }
            for (int j = 4; j < b[0].length; j++) {
                counts[b[i][j] + 1]++;
                if (j > 4) counts[b[i][j - 5] + 1]--;
                if (counts[id + 1] == 4 && counts[1] == 1) {
                    for (; j >= 0; j--) if (b[i][j] == 0) return new Point(i, j);
                }
            }
        }
        return null;
    }

    Point checkVertical4(int[][] b, int id) {
        System.out.println("v4: id=" + id);
        for (int j = 0; j < b[0].length; j++) {
            int[] w = new int[3];
            for (int i = 0; i < 4; i++) w[b[i][j] + 1]++;
            for (int i = 4; i < b.length; i++) {
                w[b[i][j] + 1]++;
                if (i > 4) w[b[i - 5][j] + 1]--;
                // check
                if (w[id + 1] == 4 && w[1] == 1) {
                    for (; i >= 0; i--) if (b[i][j] == 0) return new Point(i, j);
                }
            }
        }
        return null;
    }

    Point checkDiagonal(int[][] b, int id, int num) {
        Point result;
        for (int i = 0; i < b.length; i++) {
            result = checkBothDiagonal(b, i, 0, id, num);
            if (result != null) return result;
            result = checkBothDiagonal(b, i, b[0].length - 1, id, num);
            if (result != null) return result;
            result = checkBothDiagonal(b, 0, i, id, num);
            if (result != null) return result;
        }
        return null;
    }

    Point checkBothDiagonal(int[][] b, int i, int j, int id, int num) {
        Point result = checkBothDiagonal_helper(b, i, j, 1, id, num);
        if (result != null) return result;
        return checkBothDiagonal_helper(b, i, j, -1, id, num);
    }

    // start top left
    Point checkBothDiagonal_helper(int[][] b, int i, int j, int dir, int id, int num) {
        int count = 0;
        int[] window = new int[3];
        while (i < b.length && j < b[0].length && i > -1 && j > -1) {
            window[1 + b[i][j]]++;
            count++;
            if (count > num) {
                count--;
                window[1 + b[i - num][j - num * dir]]--;
            }
            // check
            if (window[id + 1] == num - 1 && window[1] == 1) {
                // got it
                for (int k = 0; k < num; k++) {
                    if (b[i - k][j - k * dir] == 0) return new Point(i - k, j - k * dir);
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
